package com.ptitB22CN539.LaptopShop.Redis.Repository;

import com.ptitB22CN539.LaptopShop.Redis.Configuration.RedisRepositoryImpl;
import com.ptitB22CN539.LaptopShop.Redis.Entity.CodeVerifyChangePassword;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class CodeVerifyRepositoryImpl extends RedisRepositoryImpl implements ICodeVerifyRepository {
    public CodeVerifyRepositoryImpl(RedisTemplate<String, Object> redisTemplate,
                                    HashOperations<String, String, Object> hashOperations) {
        super(redisTemplate, hashOperations);
    }

    @Value(value = "${codeVerifyExpired}")
    private Long expired;

    @Override
    public void setCodeVerify(CodeVerifyChangePassword codeVerifyChangePassword) {
        String key = this.getKey(codeVerifyChangePassword.getEmail());
        this.hashSet(key, "code", codeVerifyChangePassword.getCode());
        this.hashSet(key, "email", codeVerifyChangePassword.getEmail());
        this.setTimeToLive(key, this.expired);
    }

    @Override
    public CodeVerifyChangePassword getCodeVerify(String code) {
        Map<String, Object> data = this.hashGetAll(code);
        return CodeVerifyChangePassword.builder()
                .code(String.valueOf(data.get("code")))
                .email(String.valueOf(data.get("email")))
                .build();
    }

    private String getKey(String code) {
        return "codeVerify:%s".formatted(code);
    }
}
