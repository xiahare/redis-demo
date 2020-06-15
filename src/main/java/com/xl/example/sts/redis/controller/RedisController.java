package com.xl.example.sts.redis.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.xl.example.sts.redis.model.AsyncTaskResult;
import com.xl.example.sts.redis.service.QueryService;

@RestController
@RequestMapping("/redis/task")
@ResponseBody
public class RedisController {
	
//	@Autowired
////	@Qualifier("redisObjectTemplate")
//    private RedisTemplate<String,Object> redisTemplate;

	@Autowired
	private QueryService queryService;
	
//	@RequestMapping(value="hello", method=RequestMethod.GET,produces="application/json")
//	public String hello(String name) {
//    	
//        redisTemplate.opsForValue().set("name",name);
//        System.out.println(redisTemplate.opsForValue().get("name"));
//        
//        return name;
//	}
	
	@RequestMapping(value="start", method=RequestMethod.POST,produces="application/json")
	public String start(Integer alivetime) {
        
        return queryService.start(alivetime);
	}
	
	@RequestMapping(value="fetch", method=RequestMethod.POST,produces="application/json")
	public AsyncTaskResult fetch(String taskId) {
		
        return queryService.fetchResult(taskId);
	}
	
	@RequestMapping(value="cancel", method=RequestMethod.POST,produces="application/json")
	public String cancel(String taskId) {
    	
        // TODO
        
        return "Success";
	}
	
	@RequestMapping(value="list", method=RequestMethod.POST,produces="application/json")
	public List<String> list() {
    	
        return queryService.listRunningTasks("TestHA");
	}
}
