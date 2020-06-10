package com.xl.example.sts.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@ResponseBody
public class RedisController {


	@Autowired
	@Qualifier("redisObjectTemplate")
    private RedisTemplate<String,Object> redisTemplate;
	
	@RequestMapping(value="hello", method=RequestMethod.GET,produces="application/json")
	public String hello(String name) {
    	
        redisTemplate.opsForValue().set("name",name);
        System.out.println(redisTemplate.opsForValue().get("name"));
        
        return name;
	}
}
