package com.ptitB22CN539.LaptopShop.DTO.CodeVerify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeVerifyForgotPasswordRequest {
    private String code;
    private String password;
    private String confirmPassword;
}
