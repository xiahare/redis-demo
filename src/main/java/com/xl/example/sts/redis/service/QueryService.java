package com.xl.example.sts.redis.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xl.example.sts.redis.model.AsyncTaskResult;
import com.xl.example.sts.redis.service.async.AsyncFutureResultCache;
import com.xl.example.sts.redis.service.async.AsyncQueryService;
import com.xl.example.sts.redis.service.async.AsyncTaskResultHelper;
import com.xl.example.sts.redis.utils.LocalHostUtil;

@Service
public class QueryService {
	
	protected final Logger logger = LoggerFactory.getLogger(QueryService.class);

	@Autowired
	private AsyncQueryService asyncQueryService;
	
	@Autowired
	protected AsyncFutureResultCache asyncFutureResultCache;
	
	static private Integer seq = 0;
	
	public String start(Integer alivetime) {
		String taskId = generateTaskId();
		// init task status
		AsyncTaskResult asyncTaskResult = new AsyncTaskResult();
		asyncTaskResult.setTaskId(taskId);
		asyncTaskResult.setTaskType("TestHA");
		asyncTaskResult.setTaskStatus(AsyncTaskResult.TASK_STATUS_INIT);
		
		asyncFutureResultCache.put(asyncTaskResult.getTaskId(),asyncTaskResult);
		
		// Start an async job
		CompletableFuture<Object> completableFuture = asyncQueryService.asyncQuery(taskId, alivetime);
				
		// update status running
		asyncTaskResult = asyncFutureResultCache.get(taskId);
		if(asyncTaskResult!=null) {
			asyncTaskResult.setTaskStatus(AsyncTaskResult.TASK_STATUS_RUNNING);
		}
		
		asyncFutureResultCache.put(asyncTaskResult.getTaskId(),asyncTaskResult);
		
		// task ends
		completableFuture.whenComplete((result, taskError)->{
			
			AsyncTaskResult asyncTaskResultOld = asyncFutureResultCache.get(taskId);
			if(asyncTaskResultOld==null) {
				asyncTaskResultOld = new AsyncTaskResult();
			}
			// finish  
			if( null == taskError ) 
			{
				// set result : taskResult
				asyncTaskResultOld.setTaskStatus(AsyncTaskResult.TASK_STATUS_SUCCESS);
				asyncTaskResultOld.setTaskResult(result);
				
//			} else if( TaskStopException.isStopException( taskError ) ) {
//				// Stopped
//				asyncTaskResultCache.setTaskStatus(AsyncTaskResult.TASK_STATUS_STOPPED);
//				
			} else {
				logger.error(taskError.getMessage());
				// set errors : taskError
				asyncTaskResultOld.setTaskStatus(AsyncTaskResult.TASK_STATUS_FAIL);
				asyncTaskResultOld.setTaskError(taskError.getMessage());
				
			}
			
			asyncFutureResultCache.put(taskId, asyncTaskResultOld);
		});
		
		return taskId;
	}
	
	public AsyncTaskResult fetchResult(String taskId) {
		AsyncTaskResult taskResult = asyncFutureResultCache.get(taskId);
		if ( AsyncTaskResultHelper.isRunning(taskResult) ) {
			if( asyncFutureResultCache.getRunningTask(taskId)==null ) {
				// cluster executing node exceptions
				return null;
			}
		}
		return asyncFutureResultCache.get(taskId);
	}
	
	public Map<String, AsyncTaskResult> listRunningFutureResultsMap(String taskType) {
		Map<String, AsyncTaskResult> ret;
		
		Map<String, AsyncTaskResult> runningTasks = asyncFutureResultCache.getRunningTasks();
		
		if( taskType==null ) {
			ret = runningTasks;
		} else {
			
			ret = new HashMap<String, AsyncTaskResult>();
			for ( Entry<String, AsyncTaskResult> entry : runningTasks.entrySet()) {
				AsyncTaskResult taskResult = entry.getValue();
				if(taskResult.getTaskType().equalsIgnoreCase(taskType)) {
					ret.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return ret;
	}
	
	public AsyncTaskResult cancel(String taskId) {
		return asyncFutureResultCache.remove(taskId);
	}
	
	public List<String> listRunningTasks(String taskType) {
		Map<String, AsyncTaskResult> runningTasksMap = listRunningFutureResultsMap(taskType);
		return new ArrayList<String>(runningTasksMap.keySet());
	}
	
	static private String generateTaskId() {
		String address = "";
		try {
			address = LocalHostUtil.getHostName() + "-" + LocalHostUtil.getLocalIP();
		} catch (UnknownHostException e) {
			
		}
		return "task-" + address + "-" + seq++;
	}
}
