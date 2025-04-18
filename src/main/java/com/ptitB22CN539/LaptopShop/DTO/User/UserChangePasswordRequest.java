package com.ptitB22CN539.LaptopShop.DTO.User;

import jakarta.validation.constraints.Size;
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
public class UserChangePasswordRequest {
    @Size(min = 8, message = "PASSWORD_LENGTH_NOT_CORRECT")
    private String oldPassword;
    @Size(min = 8, message = "PASSWORD_LENGTH_NOT_CORRECT")
    private String newPassword;
    @Size(min = 8, message = "PASSWORD_LENGTH_NOT_CORRECT")
    private String confirmPassword;
}
