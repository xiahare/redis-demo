package com.xl.example.sts.redis.service.async;

import com.xl.example.sts.redis.model.AsyncTaskResult;

public class AsyncTaskResultHelper {

	// runninging
	public static boolean isRunning(AsyncTaskResult asyncTaskResult) {
		return asyncTaskResult!=null && AsyncTaskResult.TASK_STATUS_RUNNING.equals( asyncTaskResult.getTaskStatus() )  ;
	}
	
	// stopping
	static public boolean isStopping(AsyncTaskResult asyncTaskResult) {
		
		return asyncTaskResult!=null && AsyncTaskResult.TASK_STATUS_STOPPING.equals( asyncTaskResult.getTaskStatus() ) ;
				
	}
	
	// stopped
	static public boolean isStopped(AsyncTaskResult asyncTaskResult) {
		
		return asyncTaskResult!=null && AsyncTaskResult.TASK_STATUS_STOPPED.equals( asyncTaskResult.getTaskStatus() )  ;
				
	}
	
	// stopping and stopped
	static public boolean isStop(AsyncTaskResult asyncTaskResult) {
		
		return isStopping(asyncTaskResult) || isStopped(asyncTaskResult) ;
				
	}
		
	static public void progressComplete( AsyncTaskResult asyncTaskResult) {
		if( asyncTaskResult!=null) {
			
			asyncTaskResult.setProgress(1.0);
		}
	}

}
