package com.ptitB22CN539.LaptopShop.Controller;

import com.ptitB22CN539.LaptopShop.DTO.APIResponse;
import com.ptitB22CN539.LaptopShop.DTO.User.RefreshTokenRequest;
import com.ptitB22CN539.LaptopShop.DTO.User.ResidentRequestDTO;
import com.ptitB22CN539.LaptopShop.DTO.User.UserLogin;
import com.ptitB22CN539.LaptopShop.DTO.User.UserRegister;
import com.ptitB22CN539.LaptopShop.Domains.JwtEntity;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity;
import com.ptitB22CN539.LaptopShop.Service.User.IUserService;
import com.ptitB22CN539.LaptopShop.Utils.GetInfoSocialLogin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/${api.prefix}/users")
public class UserController {
    private final IUserService userService;
    private final GetInfoSocialLogin getInfoSocialLogin;

    @PostMapping(value = "/login")
    public ResponseEntity<APIResponse> login(@Valid @RequestBody UserLogin userLogin) {
        JwtEntity jwtEntity = userService.login(userLogin);
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(jwtEntity)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<APIResponse> registerUser(@Valid @RequestBody UserRegister userRegister) {
        UserEntity user = userService.register(userRegister);
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.CREATED.value())
                .message("Success")
                .data(user)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/login/{social}")
    public ResponseEntity<APIResponse> loginSocial(@Param(value = "code") @RequestParam String code, @Param(value = "social") @PathVariable String social) {
        JwtEntity jwt = userService.loginSocial(getInfoSocialLogin.getInfoSocialLogin(code).get(social));
        APIResponse response = APIResponse.builder()
                .message("Success")
                .code(HttpStatus.OK.value())
                .data(jwt)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<APIResponse> logout(HttpServletRequest request) {
        userService.logout(request);
        APIResponse response = APIResponse.builder()
                .message("Success")
                .code(HttpStatus.OK.value())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<APIResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        JwtEntity jwtRedisEntity = userService.refreshToken(refreshTokenRequest);
        APIResponse response = APIResponse.builder()
                .message("Success")
                .code(HttpStatus.OK.value())
                .data(jwtRedisEntity)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/resident")
    public ResponseEntity<APIResponse> getAllUser(@ModelAttribute ResidentRequestDTO residentRequestDTO) {
        APIResponse response = APIResponse.builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(userService.getAllResident(residentRequestDTO))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
