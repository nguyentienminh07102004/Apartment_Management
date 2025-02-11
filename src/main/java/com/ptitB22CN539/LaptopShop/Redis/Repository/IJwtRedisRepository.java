package com.ptitB22CN539.LaptopShop.Redis.Repository;

import com.ptitB22CN539.LaptopShop.Redis.Entity.JwtRedisEntity;

import java.util.List;

public interface IJwtRedisRepository {
    void setJwt(JwtRedisEntity jwt);
    List<JwtRedisEntity> getJwt(String userId);
    void deleteJwt(String userId);
    void deleteJwt(String userId, String jwtId);
    boolean hasExists(String userEmail, String jwtId);
}
