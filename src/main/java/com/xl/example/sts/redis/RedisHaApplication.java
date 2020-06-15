package com.xl.example.sts.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

import cn.gjing.core.EnableSwagger;

@SpringBootApplication
@EnableSwagger
@EnableCaching
@EnableAsync
public class RedisHaApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisHaApplication.class, args);
	}

}
