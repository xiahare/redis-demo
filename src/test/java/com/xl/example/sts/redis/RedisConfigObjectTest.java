package com.xl.example.sts.redis;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisConfigObjectTest {

	@Autowired
	@Qualifier("redisObjectTemplate")
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void set(){
    	
        redisTemplate.opsForValue().set("name","echo");
        System.out.println(redisTemplate.opsForValue().get("name"));
    }
}