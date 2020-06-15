package com.xl.example.sts.redis;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.xl.example.sts.redis.model.AsyncTaskResult;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisAsyncTaskResultTest {

	@Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void set(){
    	
    	AsyncTaskResult asyncTaskResult = new AsyncTaskResult();
    	
        redisTemplate.opsForValue().set("asyncTaskResult",asyncTaskResult);
        System.out.println(redisTemplate.opsForValue().get("asyncTaskResult"));
    }

}