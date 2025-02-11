package com.ptitB22CN539.LaptopShop.Redis.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtRedisEntity implements Serializable {
    private String id;
    private String userEmail;
    private String token;
    private Long expires;
}