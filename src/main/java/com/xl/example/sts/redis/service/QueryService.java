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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xl.example.sts.redis.model.AsyncTaskResult;
import com.xl.example.sts.redis.service.async.AsyncFutureResultCache;
import com.xl.example.sts.redis.service.async.AsyncQueryService;
import com.xl.example.sts.redis.service.async.AsyncRunningNativeCtrlCache;
import com.xl.example.sts.redis.service.async.AsyncRunningTaskCache;
import com.xl.example.sts.redis.service.async.AsyncTaskResultHelper;
import com.xl.example.sts.redis.service.async.FutureCtrl;
import com.xl.example.sts.redis.service.async.Stopper;
import com.xl.example.sts.redis.service.async.TaskStopException;
import com.xl.example.sts.redis.utils.LocalHostUtil;

@Service
@EnableScheduling
public class QueryService {
	
	protected final Logger logger = LoggerFactory.getLogger(QueryService.class);
	
	@Autowired
	private AsyncQueryService asyncQueryService;
	
	@Autowired
	protected AsyncFutureResultCache asyncFutureResultCache;

	@Autowired
	protected AsyncRunningNativeCtrlCache asyncRunningFutureCtrlCache;
	
	@Autowired
	private AsyncRunningTaskCache asyncRunningTaskCache;
	
	static private Integer seq = 0;
	
	public String start(Integer alivetime) {
		String taskId = generateTaskId();
		// init task status
		AsyncTaskResult asyncTaskResult = new AsyncTaskResult();
		asyncTaskResult.setTaskId(taskId);
		asyncTaskResult.setTaskType("TestHA");
		asyncTaskResult.setTaskStatus(AsyncTaskResult.TASK_STATUS_INIT);
		
		asyncFutureResultCache.put(asyncTaskResult.getTaskId(),asyncTaskResult);
		
		FutureCtrl futureCtrl = new FutureCtrl();
		Stopper stopper = new Stopper();
		futureCtrl.setStopper(stopper);
		// Start an async job
		CompletableFuture<Object> completableFuture = asyncQueryService.asyncQuery(taskId, alivetime, stopper);
		futureCtrl.setFuture(completableFuture);
		asyncRunningFutureCtrlCache.put(taskId, futureCtrl);
				
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
				
			} else if( TaskStopException.isStopException( taskError ) ) {
				// Stopped
				asyncTaskResultOld.setTaskStatus(AsyncTaskResult.TASK_STATUS_STOPPED);
				
			} else {
				logger.error(taskError.getMessage(),taskError);
				// set errors : taskError
				asyncTaskResultOld.setTaskStatus(AsyncTaskResult.TASK_STATUS_FAIL);
				asyncTaskResultOld.setTaskError(taskError.getMessage());
				
			}
			
			asyncFutureResultCache.put(taskId, asyncTaskResultOld);
			asyncRunningFutureCtrlCache.remove(taskId); // Complete
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
		return taskResult;
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
		AsyncTaskResult result = asyncFutureResultCache.get(taskId);
		if( result!=null ) {
			result.setTaskStatus(AsyncTaskResult.TASK_STATUS_STOPPING);
			asyncFutureResultCache.put(taskId, result);
		}
		
		/* FutureCtrl removedFuture = asyncRunningFutureCache.remove(taskId);
		 if( removedFuture!=null && removedFuture.getFuture()!=null ) {
			 removedFuture.getFuture().cancel(true);
		 }*/
		 
//		return asyncFutureResultCache.remove(taskId);
		 return asyncFutureResultCache.get(taskId);
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
	
	@Scheduled(cron = "0/30 * * * * *")
	public void refreshExpire() {
		asyncRunningTaskCache.refreshExpire();
		
		// stop
		List<String> taskIdRunningSet = asyncRunningFutureCtrlCache.getRunningNativeCtrlMap();
		if( taskIdRunningSet!=null ) {
			for(String taskId : taskIdRunningSet) {
				AsyncTaskResult taskResult = asyncFutureResultCache.get(taskId);
				if( AsyncTaskResult.TASK_STATUS_STOPPING.equals( taskResult.getTaskStatus() ) ) {
					FutureCtrl futureCrl = asyncRunningFutureCtrlCache.get(taskId);
					if(futureCrl!=null ) {
						if(futureCrl.getStopper()!=null) {
							futureCrl.getStopper().setStopping(true);
						}
//						if(futureCrl.getFuture()!=null) {
//							futureCrl.getFuture().cancel(true);
//						}
					}
				}
			}
		}
	}
}
