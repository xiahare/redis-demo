package com.xl.example.sts.redis;
import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisObjectTest {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void set(){
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setKeySerializer(RedisSerializer.string());
    	
        redisTemplate.opsForValue().set("name","echo");
        System.out.println(redisTemplate.opsForValue().get("name"));
    }

}