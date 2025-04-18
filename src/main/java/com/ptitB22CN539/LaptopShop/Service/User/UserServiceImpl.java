package com.ptitB22CN539.LaptopShop.Service.User;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.ptitB22CN539.LaptopShop.Config.ConstantConfig;
import com.ptitB22CN539.LaptopShop.Config.JwtGenerator;
import com.ptitB22CN539.LaptopShop.Config.UserStatus;
import com.ptitB22CN539.LaptopShop.DTO.CodeVerify.CodeVerifyForgotPasswordRequest;
import com.ptitB22CN539.LaptopShop.DTO.User.RefreshTokenRequest;
import com.ptitB22CN539.LaptopShop.DTO.User.ResidentRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.User.UserChangePasswordRequest;
import com.ptitB22CN539.LaptopShop.DTO.User.UserLogin;
import com.ptitB22CN539.LaptopShop.DTO.User.UserRegister;
import com.ptitB22CN539.LaptopShop.DTO.User.UserSendEmailForgotPasswordRequest;
import com.ptitB22CN539.LaptopShop.DTO.User.UserSocialLogin;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentEntity_;
import com.ptitB22CN539.LaptopShop.Domains.ApartmentUserEntity_;
import com.ptitB22CN539.LaptopShop.Domains.JwtEntity;
import com.ptitB22CN539.LaptopShop.Domains.PermissionEntity;
import com.ptitB22CN539.LaptopShop.Domains.RoleEntity;
import com.ptitB22CN539.LaptopShop.Domains.RoleEntity_;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity_;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import com.ptitB22CN539.LaptopShop.Mapper.User.UserMapper;
import com.ptitB22CN539.LaptopShop.Redis.Entity.CodeVerifyChangePassword;
import com.ptitB22CN539.LaptopShop.Redis.Repository.ICodeVerifyRepository;
import com.ptitB22CN539.LaptopShop.Repository.JwtRepository;
import com.ptitB22CN539.LaptopShop.Repository.PermissionRepository;
import com.ptitB22CN539.LaptopShop.Repository.RoleRepository;
import com.ptitB22CN539.LaptopShop.Repository.UserRepository;
import com.ptitB22CN539.LaptopShop.Service.SendEmail.IEmailService;
import com.ptitB22CN539.LaptopShop.Service.Upload.IUploadService;
import com.ptitB22CN539.LaptopShop.Utils.GeneratedRandomCode;
import com.ptitB22CN539.LaptopShop.Utils.PageableUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final JwtGenerator jwtGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtRepository jwtRepository;
    private final PageableUtils pageableUtils;
    private final IUploadService uploadService;
    private final ICodeVerifyRepository codeVerifyRepository;
    private final IEmailService emailService;

    @Value(value = "${maxLoginDevice}")
    private Integer maxLoginDevice;
    @Value(value = "${refreshTokenDuration}")
    private Long refreshTokenDuration;

    @Override
    @Transactional
    public JwtEntity login(UserLogin userLogin) {
        try {
            UserEntity user = this.getUserByEmail(userLogin.getEmail());
            if ((userLogin.getIsSocial() == null || !userLogin.getIsSocial())
                    && !passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
                throw new DataInvalidException(ExceptionVariable.EMAIL_PASSWORD_NOT_CORRECT);
            }
            List<JwtEntity> jwtEntities = jwtRepository.findByUser_Email(user.getEmail());
            if (jwtEntities.size() >= maxLoginDevice) {
                String jit = null;
                for (JwtEntity jwtRedisEntity : jwtEntities) {
                    JWTClaimsSet jwtClaimsSet = jwtGenerator.getSignedJWT(jwtRedisEntity.getToken()).getJWTClaimsSet();
                    if (jwtClaimsSet.getExpirationTime().before(new Date(System.currentTimeMillis()))) {
                        jit = jwtClaimsSet.getJWTID();
                        break;
                    }
                }
                if (jit != null) {
                    // nếu có 1 token hết hạn
                    jwtRepository.deleteById(jit);
                } else {
                    throw new DataInvalidException(ExceptionVariable.ACCOUNT_LOGIN_MAX_DEVICE);
                }
            }
            JwtEntity jwt = jwtGenerator.jwtGenerator(user);
            user.getListJwt().add(jwt);
            userRepository.save(user);
            return jwt;
        } catch (ParseException | JOSEException exception) {
            return null;
        }
    }

    @Override
    @Transactional
    public UserEntity register(UserRegister user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DataInvalidException(ExceptionVariable.EMAIL_EXISTS);
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new DataInvalidException(ExceptionVariable.PASSWORD_CONFIRM_PASSWORD_NOT_MATCH);
        }
        UserEntity userEntity = userMapper.registerToEntity(user);
        RoleEntity role;
        if (user.getRoleId() != null) {
            role = roleRepository.findById(user.getRoleId())
                    .orElseThrow(() -> new DataInvalidException(ExceptionVariable.ROLE_NOT_FOUND));
        } else {
            role = roleRepository.findByName(ConstantConfig.USER_ROLE);
        }
        if (user.getPermissionIds() != null && !user.getPermissionIds().isEmpty()) {
            List<PermissionEntity> listPermission = new ArrayList<>();
            for (String permissionId : user.getPermissionIds()) {
                PermissionEntity permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new DataInvalidException(ExceptionVariable.PERMISSION_NOT_FOUND));
                listPermission.add(permission);
            }
            userEntity.setPermissions(listPermission);
        } else {
            userEntity.setPermissions(permissionRepository.findAllByNameIn(ConstantConfig.PERMISSION_DEFAULT_USER));
        }
        userEntity.setRole(role);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setStatus(UserStatus.ACTIVE);
        return userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request) {
        try {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            JWTClaimsSet jwtClaimsSet = jwtGenerator.getSignedJWT(token.substring(7)).getJWTClaimsSet();
            jwtRepository.deleteById(jwtClaimsSet.getJWTID());
        } catch (ParseException | JOSEException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    @Transactional
    @SuppressWarnings(value = "rawtypes")
    public JwtEntity loginSocial(UserSocialLogin userSocialLogin) {
        MultiValueMap<String, String> properties = new LinkedMultiValueMap<>();
        if (!userSocialLogin.getName().equals("discord")) {
            properties.add(OAuth2ParameterNames.CLIENT_ID, userSocialLogin.getClientId());
            properties.add(OAuth2ParameterNames.CLIENT_SECRET, userSocialLogin.getClientSecret());
        }
        properties.add(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
        properties.add(OAuth2ParameterNames.REDIRECT_URI, userSocialLogin.getRedirectUri());
        properties.add(OAuth2ParameterNames.CODE, userSocialLogin.getCode());
        WebClient webClient = WebClient.builder()
                .baseUrl(userSocialLogin.getAccessTokenUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        WebClient.RequestHeadersSpec<?> headersSpec = webClient.method(userSocialLogin.getAccessTokenMethod())
                .body(BodyInserters.fromFormData(properties));
        if (userSocialLogin.getName().equals("discord")) {
            String headerAuth = "Basic " + Base64.getEncoder().encodeToString((String.join(":", userSocialLogin.getClientId(),
                    userSocialLogin.getClientSecret())).getBytes());
            headersSpec.header(HttpHeaders.AUTHORIZATION, headerAuth);
        }
        Mono<Map> response = headersSpec.retrieve().bodyToMono(Map.class);
        Map responseMap = response.block();
        if (responseMap == null) throw new DataInvalidException(ExceptionVariable.SERVER_ERROR);
        String accessToken = responseMap.get(OAuth2ParameterNames.ACCESS_TOKEN).toString();
        webClient = WebClient.builder()
                .baseUrl(userSocialLogin.getUserInfoUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, ConstantConfig.AUTHORIZATION_PREFIX + accessToken)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        WebClient.RequestBodySpec requestBodySpec = webClient.method(userSocialLogin.getUserInfoMethod());
        response = requestBodySpec.retrieve().bodyToMono(Map.class);
        responseMap = response.block();
        if (responseMap == null) throw new DataInvalidException(ExceptionVariable.SERVER_ERROR);
        String email = responseMap.get("email").toString();
        if (userRepository.existsByEmail(email)) {
            return this.login(new UserLogin(email, null, true));
        }
        UserEntity user = UserEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .status(UserStatus.ACTIVE)
                .role(roleRepository.findByName(ConstantConfig.USER_ROLE))
                .permissions(permissionRepository.findAllByNameIn(ConstantConfig.PERMISSION_DEFAULT_USER))
                .build();
        JwtEntity jwt = jwtGenerator.jwtGenerator(user);
        List<JwtEntity> jwtEntities = user.getListJwt();
        if (jwtEntities == null) {
            jwtEntities = new ArrayList<>();
        }
        jwtEntities.add(jwt);
        user.setListJwt(jwtEntities);
        userRepository.save(user);
        return jwt;
    }

    @Override
    @Transactional
    public JwtEntity refreshToken(RefreshTokenRequest refreshToken) {
        try {
            JwtEntity jwt = jwtRepository.findByRefreshToken(refreshToken.getRefreshToken())
                    .orElseThrow(() -> new DataInvalidException(ExceptionVariable.UNAUTHORIZED));
            // check jwt hết hạn
            UserEntity user = jwt.getUser();
            JWTClaimsSet jwtClaimsSet = jwtGenerator.getSignedJWT(jwt.getToken()).getJWTClaimsSet();
            if (new Date(jwtClaimsSet.getIssueTime().getTime() + refreshTokenDuration * 1000).before(new Date(System.currentTimeMillis()))) {
                throw new DataInvalidException(ExceptionVariable.UNAUTHORIZED);
            }
            jwtRepository.deleteById(jwtClaimsSet.getJWTID());
            return jwtRepository.save(jwtGenerator.jwtGenerator(user));
        } catch (ParseException | JOSEException exception) {
            throw new DataInvalidException(ExceptionVariable.UNAUTHORIZED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new DataInvalidException(ExceptionVariable.EMAIL_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataInvalidException(ExceptionVariable.USER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedModel<UserEntity> getAllResident(ResidentRequestDTO residentRequestDTO) {
        Specification<UserEntity> specification = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(UserEntity_.ROLE).get(RoleEntity_.NAME), ConstantConfig.USER_ROLE);
            if (StringUtils.hasText(residentRequestDTO.getName())) {
                predicate = builder.and(builder.like(root.get(UserEntity_.FULL_NAME),
                        String.join("", "%", residentRequestDTO.getName(), "%")));
            }
            if (StringUtils.hasText(residentRequestDTO.getId())) {
                predicate = builder.and(builder.like(root.get(UserEntity_.ID),
                        String.join("", "%", residentRequestDTO.getId(), "%")));
            }
            if (StringUtils.hasText(residentRequestDTO.getApartmentId())) {
                predicate = builder.and(builder.like(root.get(UserEntity_.APARTMENT_USERS).get(ApartmentUserEntity_.APARTMENT).get(ApartmentEntity_.ID),
                        String.join("", "%", residentRequestDTO.getApartmentId(), "%")));
            }
            return predicate;
        };
        return new PagedModel<>(userRepository.findAll(specification,
                pageableUtils.getPageable(residentRequestDTO.getPage(), residentRequestDTO.getLimit())));

    }

    @Override
    @Transactional
    public UserEntity uploadAvatar(MultipartFile avatar, String userid) {
        try {
            if (avatar == null || avatar.isEmpty()) {
                throw new DataInvalidException(ExceptionVariable.FILE_EMPTY);
            }
            File file = File.createTempFile("PTITB22CN539-", null);
            avatar.transferTo(file);
            String url = uploadService.upload(file);
            UserEntity user = this.getUserById(userid);
            if (StringUtils.hasText(user.getAvatar())) {
                uploadService.delete(user.getAvatar());
            }
            user.setAvatar(url);
            return userRepository.save(user);
        } catch (Exception exception) {
            throw new DataInvalidException(ExceptionVariable.SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public UserEntity getMyInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.getUserByEmail(email);
    }

    @Override
    @Transactional
    public void sendEmailForgotPassword(UserSendEmailForgotPasswordRequest userSendEmailForgotPasswordRequest) {
        this.getUserByEmail(userSendEmailForgotPasswordRequest.getEmail());
        String code = GeneratedRandomCode.generateRandomCode(6);
        CodeVerifyChangePassword codeVerifyChangePassword = CodeVerifyChangePassword.builder()
                .code(code)
                .email(userSendEmailForgotPasswordRequest.getEmail())
                .build();
        codeVerifyRepository.setCodeVerify(codeVerifyChangePassword);
        this.emailService.sendEmail(userSendEmailForgotPasswordRequest.getRevisedEmail(), "Forgot Password", "ForgotPassword", Map.of("code", GeneratedRandomCode.generateRandomCode(6)));
    }

    @Override
    @Transactional
    public void verifyCodeForgotPassword(CodeVerifyForgotPasswordRequest codeVerifyForgotPasswordRequest) {
        String code = codeVerifyForgotPasswordRequest.getCode();
        CodeVerifyChangePassword codeVerifyChangePassword = this.codeVerifyRepository.getCodeVerify(code);
        if (codeVerifyChangePassword == null) {
            throw new DataInvalidException(ExceptionVariable.TOKEN_INVALID);
        }
        String email = codeVerifyChangePassword.getEmail();
        UserEntity user = this.getUserByEmail(email);
        if (!codeVerifyForgotPasswordRequest.getPassword().equals(codeVerifyForgotPasswordRequest.getConfirmPassword())) {
            throw new DataInvalidException(ExceptionVariable.PASSWORD_CONFIRM_PASSWORD_NOT_MATCH);
        }
        if (passwordEncoder.matches(codeVerifyForgotPasswordRequest.getPassword(), user.getPassword())) {
            throw new DataInvalidException(ExceptionVariable.OLD_PASSWORD_NEW_PASSWORD_MATCH);
        }
        user.setPassword(passwordEncoder.encode(codeVerifyForgotPasswordRequest.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(UserChangePasswordRequest userChangePasswordRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = this.getUserByEmail(email);
        if (passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new DataInvalidException(ExceptionVariable.OLD_PASSWORD_NEW_PASSWORD_MATCH);
        }
        if (!userChangePasswordRequest.getConfirmPassword().equals(userChangePasswordRequest.getNewPassword())) {
            throw new DataInvalidException(ExceptionVariable.PASSWORD_CONFIRM_PASSWORD_NOT_MATCH);
        }
        user.setPassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    @Scheduled(cron = "@daily")
    @Transactional
    protected void deleteAllRefreshTokenExpired() {
        jwtRepository.deleteAllByRefreshTokenExpiatedDateBefore(new Date(System.currentTimeMillis()));
    }
}
