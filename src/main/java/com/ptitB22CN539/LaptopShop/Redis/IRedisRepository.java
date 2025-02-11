package com.ptitB22CN539.LaptopShop.Redis;

import java.util.Map;

public interface IRedisRepository {
    Object get(String key);
    void set(String key, Object value);
    void del(String key);
    void del(String key, String field);
    void setTimeToLive(String key, Long timeToLive);
    Long getTimeToLive(String key);
    void hashSet(String key, String field, Object value);
    Map<String, Object> hashGetAll(String key);
    Object hashGet(String key, String field);
}
