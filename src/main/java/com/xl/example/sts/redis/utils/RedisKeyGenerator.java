package com.xl.example.sts.redis.utils;

import java.util.UUID;

public class RedisKeyGenerator {
	public static String generateRedisKey(String type) {
		return type + UUID.randomUUID().toString();
	}
}
