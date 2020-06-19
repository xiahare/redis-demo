package com.xl.example.sts.redis.service.async;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingRuntimeException  extends RuntimeException {

	private static final long serialVersionUID = 11L;
	
	Logger logger = LogManager.getLogger(LoggingRuntimeException.class);
	
	public LoggingRuntimeException() {
		super();
	}
	
	public LoggingRuntimeException(String msg) {
		super(msg);
		logger.error(msg);
	}
	
	public LoggingRuntimeException(String msg, Throwable th) {
		super(msg, th);
		logger.error(msg, th);
	}
}
