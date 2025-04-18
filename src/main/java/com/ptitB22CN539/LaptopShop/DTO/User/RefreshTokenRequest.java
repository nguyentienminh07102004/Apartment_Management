package com.ptitB22CN539.LaptopShop.DTO.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RefreshTokenRequest {
    @NotNull(message = "TOKEN_INVALID")
    @NotBlank(message = "TOKEN_INVALID")
    private String refreshToken;
}
