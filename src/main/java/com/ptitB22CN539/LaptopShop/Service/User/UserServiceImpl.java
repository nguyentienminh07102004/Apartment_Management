package com.ptitB22CN539.LaptopShop.Service.User;

import com.ptitB22CN539.LaptopShop.Config.ConstantConfig;
import com.ptitB22CN539.LaptopShop.Config.JwtGenerator;
import com.ptitB22CN539.LaptopShop.Config.UserStatus;
import com.ptitB22CN539.LaptopShop.DTO.User.UserLogin;
import com.ptitB22CN539.LaptopShop.DTO.User.UserRegister;
import com.ptitB22CN539.LaptopShop.DTO.User.UserSocialLogin;
import com.ptitB22CN539.LaptopShop.Domains.JwtEntity;
import com.ptitB22CN539.LaptopShop.Domains.PermissionEntity;
import com.ptitB22CN539.LaptopShop.Domains.RoleEntity;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import com.ptitB22CN539.LaptopShop.Mapper.User.UserMapper;
import com.ptitB22CN539.LaptopShop.Repository.JwtRepository;
import com.ptitB22CN539.LaptopShop.Repository.PermissionRepository;
import com.ptitB22CN539.LaptopShop.Repository.RoleRepository;
import com.ptitB22CN539.LaptopShop.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    private final JwtRepository jwtRepository;
    private final UserMapper userMapper;

    @Value(value = "${maxLoginDevice}")
    private Integer maxLoginDevice;

    @Override
    @Transactional
    public JwtEntity login(UserLogin userLogin) {
        UserEntity user = userRepository.findByEmail(userLogin.getEmail())
                .orElseThrow(() -> new DataInvalidException(ExceptionVariable.EMAIL_NOT_FOUND));
        if ((userLogin.getIsSocial() == null || !userLogin.getIsSocial()) && !passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
            throw new DataInvalidException(ExceptionVariable.EMAIL_PASSWORD_NOT_CORRECT);
        }
        List<JwtEntity> jwtEntities = user.getJwts();
        if (jwtEntities.size() >= maxLoginDevice) {
            throw new DataInvalidException(ExceptionVariable.ACCOUNT_LOGIN_MAX_DEVICE);
        }
        JwtEntity jwt = jwtGenerator.jwtGenerator(user);
        jwtRepository.save(jwt);
        jwtEntities.add(jwt);
        user.setJwts(jwtEntities);
        userRepository.save(user);
        return jwt;
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
    public void logout(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || !token.startsWith(ConstantConfig.AUTHORIZATION_PREFIX)) {
            throw new DataInvalidException(ExceptionVariable.TOKEN_INVALID);
        }
        jwtRepository.deleteByToken(token.substring(ConstantConfig.AUTHORIZATION_PREFIX.length()));
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
        WebClient webClient;
        webClient = WebClient.builder()
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
        // Huỷ access token hiện tại đi
        if (userSocialLogin.getName().equals("discord")) {
            webClient = WebClient.builder()
                    .baseUrl("https://discord.com/api/v10/oauth2/token/revoke")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(String.join(":", userSocialLogin.getClientId(), userSocialLogin.getClientSecret()).getBytes()))
                    .build();
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("token", accessToken);
            parameters.add("token_type_hint", OAuth2ParameterNames.ACCESS_TOKEN);
            webClient.method(HttpMethod.POST)
                    .body(BodyInserters.fromFormData(parameters))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
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
        user.setJwts(List.of(jwt));
        userRepository.save(user);
        return jwt;
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void deleteJwtExp() {
        jwtRepository.deleteAllByExpiredDateBefore(new Date(System.currentTimeMillis()));
    }
}
