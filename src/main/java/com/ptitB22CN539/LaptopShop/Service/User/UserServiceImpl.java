package com.ptitB22CN539.LaptopShop.Service.User;

import com.ptitB22CN539.LaptopShop.Config.ConstantConfig;
import com.ptitB22CN539.LaptopShop.Config.JwtGenerator;
import com.ptitB22CN539.LaptopShop.Config.UserStatus;
import com.ptitB22CN539.LaptopShop.DTO.User.UserRegister;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public JwtEntity login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataInvalidException(ExceptionVariable.EMAIL_NOT_FOUND));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new DataInvalidException(ExceptionVariable.EMAIL_PASSWORD_NOT_CORRECT);
        }
        List<JwtEntity> jwtEntities = user.getJwts();
        if (jwtEntities.size() >= maxLoginDevice) {
            throw new DataInvalidException(ExceptionVariable.ACCOUNT_LOGIN_MAX_DEVICE);
        }
        JwtEntity jwt = jwtGenerator.jwtGenerator(user);
        jwtEntities.add(jwt);
        user.setJwts(jwtEntities);
        userRepository.save(user);
        return jwt;
    }

    @Override
    public UserEntity register(UserRegister user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DataInvalidException(ExceptionVariable.EMAIL_EXISTS);
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new DataInvalidException(ExceptionVariable.PASSWORD_CONFIRM_PASSWORD_NOT_MATCH);
        }
        RoleEntity role = roleRepository.findById(user.getRoleId())
                .orElseThrow(() -> new DataInvalidException(ExceptionVariable.ROLE_NOT_FOUND));
        List<PermissionEntity> listPermission = new ArrayList<>();
        for (String permissionId : user.getPermissionIds()) {
            PermissionEntity permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new DataInvalidException(ExceptionVariable.PERMISSION_NOT_FOUND));
            listPermission.add(permission);
        }
        UserEntity userEntity = userMapper.registerToEntity(user);
        userEntity.setPermissions(listPermission);
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
}
