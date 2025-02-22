package com.ptitB22CN539.LaptopShop.Service.User;

import com.ptitB22CN539.LaptopShop.DTO.User.RefreshTokenRequest;
import com.ptitB22CN539.LaptopShop.DTO.User.ResidentRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.User.UserLogin;
import com.ptitB22CN539.LaptopShop.DTO.User.UserRegister;
import com.ptitB22CN539.LaptopShop.DTO.User.UserSocialLogin;
import com.ptitB22CN539.LaptopShop.Domains.JwtEntity;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.web.PagedModel;

public interface IUserService {
    JwtEntity login(UserLogin userLogin);
    UserEntity register(UserRegister user);
    void logout(HttpServletRequest request);
    JwtEntity loginSocial(UserSocialLogin userSocialLogin);
    JwtEntity refreshToken(RefreshTokenRequest refreshToken);
    UserEntity getUserByEmail(String email);
    UserEntity getUserById(String id);
    PagedModel<UserEntity> getAllResident(ResidentRequestDTO residentRequestDTO);
}
