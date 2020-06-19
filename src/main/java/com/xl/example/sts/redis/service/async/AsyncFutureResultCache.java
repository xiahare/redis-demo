package com.xl.example.sts.redis.service.async;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;

import com.xl.example.sts.redis.model.AsyncTaskResult;

@Component
@EnableCaching
@CacheConfig(cacheNames="asyncFutureResultCache")
public class AsyncFutureResultCache {

	protected final Logger logger = LoggerFactory.getLogger(AsyncFutureResultCache.class);
	
	@Autowired
	private AsyncRunningTaskCache asyncRunningTaskCache;
	
	@Cacheable(key="#p0", unless="#result == null")
	public AsyncTaskResult get(String tid) {

		AsyncTaskResult runningTask = asyncRunningTaskCache.get(tid);
		if( runningTask == null ) {
			logger.info("======== Task ID is not found! Maybe it is removed or expired or NO Cache configurations !  .....");
		}
		
		return runningTask;
	}
	
	@CachePut(key="#p0")
	public AsyncTaskResult put(String tid, AsyncTaskResult asyncTaskResult) {
		
		if( AsyncTaskResultHelper.isRunning(asyncTaskResult) ) {
			asyncRunningTaskCache.put(tid, asyncTaskResult);
		} else {
			asyncRunningTaskCache.remove(tid);
		}
		
		return asyncTaskResult;
	}
	
	@CacheEvict(key="#p0")
	public AsyncTaskResult remove(String tid) {
		asyncRunningTaskCache.remove(tid);
		return null;
	}
	
	public Map<String, AsyncTaskResult> getRunningTasks() {
		return asyncRunningTaskCache.getAllRunningTasks();
	}
	
	public AsyncTaskResult getRunningTask(String tid) {
		return asyncRunningTaskCache.getFromAll(tid);
	}

}
