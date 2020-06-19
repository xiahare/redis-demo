package com.xl.example.sts.redis.service.async;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
public class TaskStopException extends LoggingRuntimeException {
	
	private static final long serialVersionUID = 1L;

	static public final String PREFIX_STOP_ERROR_MSG = "Facet Task was stopped";

	// stop exception
	static public boolean isStopException( Throwable th) {
		return (th!=null && TaskStopException.class.isInstance(th)) 
				|| (th!=null && TaskStopException.class.isInstance(th.getCause()) )
				|| ( th.getMessage()!=null && th.getMessage().contains(TaskStopException.PREFIX_STOP_ERROR_MSG) ) ;
	}
	
	public TaskStopException(String taskId) {
		super(PREFIX_STOP_ERROR_MSG+ " : " + taskId);
	};
}

