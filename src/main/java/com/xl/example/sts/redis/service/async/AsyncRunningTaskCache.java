package com.xl.example.sts.redis.service.async;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.xl.example.sts.redis.model.AsyncTaskResult;

@Component
@EnableCaching
@CacheConfig(cacheNames="asyncRunningTaskCache")
public class AsyncRunningTaskCache {
	
	protected final Logger logger = LoggerFactory.getLogger(AsyncRunningTaskCache.class);
	
	@Autowired
    private RedisTemplate<String,Object> redisTemplate;
	
	private final String RUNNING_TASK_UUID = UUID.randomUUID().toString();
	
	public AsyncTaskResult get(String tid) {

		AsyncTaskResult runningTask = (AsyncTaskResult)redisTemplate.opsForHash().get(runningTaskHashMapKey(), tid);
		
		refreshExpire();
		
		return runningTask;
	}
	
	public AsyncTaskResult getFromAll(String tid) {
		
		Set<String> runningTaskHashMapkeys = redisTemplate.keys(runningTaskHashMapKeyPattern());
		if(CollectionUtils.isNotEmpty( runningTaskHashMapkeys ) ) {
			for(String runningTaskHashMapkey : runningTaskHashMapkeys) {
				AsyncTaskResult runningTask = (AsyncTaskResult)redisTemplate.opsForHash().get(runningTaskHashMapkey, tid);
				if( runningTask!=null ) {
					return runningTask;
				}
				
			}

		}
		
		return null;
	}
	
	public AsyncTaskResult put(String tid, AsyncTaskResult asyncTaskResult) {
		
		redisTemplate.opsForHash().put(runningTaskHashMapKey(), tid, asyncTaskResult);
		
		refreshExpire();
		return asyncTaskResult;
	}
	
	public AsyncTaskResult remove(String tid) {
		
		Set<String> runningTaskHashMapkeys = redisTemplate.keys(runningTaskHashMapKeyPattern());
		if(CollectionUtils.isNotEmpty( runningTaskHashMapkeys ) ) {
			for(String runningTaskHashMapkey : runningTaskHashMapkeys) {
				AsyncTaskResult runningTask = (AsyncTaskResult)redisTemplate.opsForHash().get(runningTaskHashMapkey, tid);
				if( runningTask!=null ) {
					redisTemplate.opsForHash().delete(runningTaskHashMapKey(), tid);
					return null;
				}
				
			}

		}
		
		refreshExpire();
		return null;
	}
	
	public Map<String, AsyncTaskResult> getNativeRunningTasks() {
		Map<String, AsyncTaskResult> asyncTaskResultMap = new HashMap<String, AsyncTaskResult>();
		
		Map<Object, Object> map = redisTemplate.opsForHash().entries(runningTaskHashMapKey());
		if( map!=null ) {
			for (Entry<Object, Object> entry : map.entrySet()) {
				asyncTaskResultMap.put((String)entry.getKey(), (AsyncTaskResult)entry.getValue());
			}
		}
		refreshExpire();
		return asyncTaskResultMap;
	}
	
	public Map<String, AsyncTaskResult> getAllRunningTasks() {
		Map<String, AsyncTaskResult> asyncTaskResultMap = new HashMap<String, AsyncTaskResult>();
		
		Set<String> runningTaskHashMapkeys = redisTemplate.keys(runningTaskHashMapKeyPattern());
		if(CollectionUtils.isNotEmpty( runningTaskHashMapkeys ) ) {
			for(String runningTaskHashMapkey : runningTaskHashMapkeys) {
				Map<Object, Object> map = redisTemplate.opsForHash().entries(runningTaskHashMapkey);
				if( map!=null ) {
					for (Entry<Object, Object> entry : map.entrySet()) {
						asyncTaskResultMap.put((String)entry.getKey(), (AsyncTaskResult)entry.getValue());
					}
				}
			}
		}

		refreshExpire();
		return asyncTaskResultMap;
	}
	
	public void refreshExpire() {
		
		try {
			redisTemplate.expire(runningTaskHashMapKey(), 30*4, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.error("runningTaskHashMapKey refreshExpire Error:" , e);
		}
		logger.info("RunningTaskHashMapKey refreshExpire CALLED!" );
	}
	
	private String runningTaskHashMapKey() {
		return runningTaskHashMapKeyPrefix() + RUNNING_TASK_UUID;
	}
	
	private String runningTaskHashMapKeyPattern() {
		return runningTaskHashMapKeyPrefix() + "*";
	}
	
	private String runningTaskHashMapKeyPrefix() {
		return "fazconnector::running::";
	}
}
