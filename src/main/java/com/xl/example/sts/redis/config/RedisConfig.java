package com.xl.example.sts.redis.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class RedisConfig {

	@Bean
	public RedisTemplate<String, Object> redisObjectTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		RedisSerializer<String> redisSerializer = new StringRedisSerializer();

		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);

		template.setConnectionFactory(factory);
		// key序列化方式
		template.setKeySerializer(redisSerializer);
		// value序列化
		template.setValueSerializer(jackson2JsonRedisSerializer);
		// value hashmap序列化
		template.setHashValueSerializer(jackson2JsonRedisSerializer);
		return template;
	}
	
//	@Bean("redisObjectTemplate")
//	public RedisTemplate<String, Object> redisObjectTemplate(RedisConnectionFactory factory) {
//        RedisTemplate<String,Object>template=new RedisTemplate<>();
//        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
//        
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
// 
//        template.setConnectionFactory(factory);
//        //key序列化方式
//        template.setKeySerializer(redisSerializer);
//        //value序列化
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//        //value hashmap序列化
//        template.setHashValueSerializer(jackson2JsonRedisSerializer);
//        return template;
//	}
}
