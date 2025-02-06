package com.ptitB22CN539.LaptopShop.Service.User;

import com.ptitB22CN539.LaptopShop.DTO.User.UserRegister;
import com.ptitB22CN539.LaptopShop.Domains.JwtEntity;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity;
import jakarta.servlet.http.HttpServletRequest;

public interface IUserService {
    JwtEntity login(String email, String password);
    UserEntity register(UserRegister user);
    void logout(HttpServletRequest request);
}
