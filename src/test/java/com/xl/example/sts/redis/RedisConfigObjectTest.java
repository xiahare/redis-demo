package com.xl.example.sts.redis;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
//	@Qualifier("redisObjectTemplate")
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void testExpire(){
    	
        redisTemplate.opsForValue().set("name","echo");
        System.out.println(redisTemplate.opsForValue().get("name"));
        printExpire("name","0");
        redisTemplate.expire("name", 60, TimeUnit.SECONDS);
        printExpire("name","1");
        redisTemplate.opsForValue().set("name","echo2");
        printExpire("name","2");
    }
    
    @Test
    public void testHash(){
    	
    	String rkey = "running:query:10001:host1uuid";
    	
        redisTemplate.opsForHash().put(rkey, "task12345", "taskResultxxx");
        Map<Object, Object> map = redisTemplate.opsForHash().entries(rkey);
        System.out.println( map );
        System.out.println( redisTemplate.opsForHash().get(rkey, "task12345") );
    }
    
    private void printExpire(String key, String msg) {
    	Long expire = redisTemplate.getExpire("name", TimeUnit.SECONDS);
        System.out.println(msg + ":" +expire);
    }
}