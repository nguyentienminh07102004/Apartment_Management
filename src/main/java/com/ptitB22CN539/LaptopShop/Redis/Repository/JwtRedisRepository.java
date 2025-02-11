package com.ptitB22CN539.LaptopShop.Redis.Repository;

import com.ptitB22CN539.LaptopShop.Redis.Entity.JwtRedisEntity;
import com.ptitB22CN539.LaptopShop.Redis.RedisRepositoryImpl;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class JwtRedisRepository extends RedisRepositoryImpl implements IJwtRedisRepository {

    public JwtRedisRepository(RedisTemplate<String, Object> redisTemplate,
                              HashOperations<String, String, Object> hashOperations) {
        super(redisTemplate, hashOperations);
    }

    @Override
    public void setJwt(JwtRedisEntity jwt) {
        this.hashSet(jwt.getUserEmail(), jwt.getId(), jwt);
        this.setTimeToLive(jwt.getUserEmail(), jwt.getExpires());
    }

    @Override
    public List<JwtRedisEntity> getJwt(String userEmail) {
        Map<String, Object> redis = this.hashGetAll(userEmail);
        List<JwtRedisEntity> jwtList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : redis.entrySet()) {
            JwtRedisEntity jwt = (JwtRedisEntity) entry.getValue();
            jwtList.add(jwt);
        }
        return jwtList;
    }

    @Override
    public void deleteJwt(String userId) {
        this.del(userId);
    }

    @Override
    public void deleteJwt(String userId, String jwtId) {
        this.del(userId, jwtId);
    }

    @Override
    public boolean hasExists(String userEmail, String jwtId) {
        Map<String, Object> redis = this.hashGetAll(userEmail);
        return redis.containsKey(jwtId);
    }
}
