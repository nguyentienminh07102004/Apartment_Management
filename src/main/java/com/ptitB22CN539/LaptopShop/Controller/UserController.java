package com.ptitB22CN539.LaptopShop.Controller;

import com.ptitB22CN539.LaptopShop.DTO.APIResponse;
import com.ptitB22CN539.LaptopShop.DTO.User.UserLogin;
import com.ptitB22CN539.LaptopShop.Service.SendEmail.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final IEmailService emailService;

    @PostMapping(value = "/verify-email")
    public ResponseEntity<APIResponse> login(@RequestBody UserLogin userLogin) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("code", (long) (Math.random() * 1000000));
        emailService.sendEmail(userLogin.getEmail(), "Forgot Password", "ForgotPassword", properties);
        return null;
    }
}
