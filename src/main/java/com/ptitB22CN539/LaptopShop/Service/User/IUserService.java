package com.ptitB22CN539.LaptopShop.Service.User;

import com.ptitB22CN539.LaptopShop.DTO.User.UserLogin;
import com.ptitB22CN539.LaptopShop.DTO.User.UserRegister;
import com.ptitB22CN539.LaptopShop.DTO.User.UserSocialLogin;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity;
import com.ptitB22CN539.LaptopShop.Redis.Entity.JwtRedisEntity;
import jakarta.servlet.http.HttpServletRequest;

public interface IUserService {
    JwtRedisEntity login(UserLogin userLogin);
    UserEntity register(UserRegister user);
    void logout(HttpServletRequest request);
    JwtRedisEntity loginSocial(UserSocialLogin userSocialLogin);
}
