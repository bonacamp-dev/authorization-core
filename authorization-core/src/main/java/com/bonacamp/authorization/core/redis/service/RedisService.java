package com.bonacamp.authorization.core.redis.service;

import java.time.Duration;

public interface RedisService {
	
	void setValue(String key, String data, Duration duration);

    Object getValue(String key);

    void delete(String key);

    void expire(String key, int timeout);

    boolean hasKey(String key);
	
}
