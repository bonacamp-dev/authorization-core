package com.bonacamp.authorization.core.redis.service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public void setValue(String key, String data, Duration duration) {
        redisTemplate.opsForValue().set(key, data, duration);
    }
    
    @Transactional(readOnly = true)
    @Override
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    @Override
    public void expire(String key, int timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

}