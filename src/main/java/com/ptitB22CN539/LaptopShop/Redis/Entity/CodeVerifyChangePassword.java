package com.ptitB22CN539.LaptopShop.Redis.Entity;

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
public class CodeVerifyChangePassword {
    private String code;
    private String email;
}
