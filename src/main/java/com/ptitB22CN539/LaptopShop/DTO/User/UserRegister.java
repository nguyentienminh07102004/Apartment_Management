package com.ptitB22CN539.LaptopShop.DTO.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegister {
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;
    @Size(min = 8, message = "PASSWORD_LENGTH_NOT_CORRECT")
    private String password;
    @Size(min = 8, message = "PASSWORD_LENGTH_NOT_CORRECT")
    private String confirmPassword;
    private String phone;
    private String fullName;
    private String address;
    private String roleId;
    private List<String> permissionIds;
}
