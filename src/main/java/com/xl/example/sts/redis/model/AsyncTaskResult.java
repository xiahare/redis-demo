package com.xl.example.sts.redis.model;

import java.io.Serializable;

public class AsyncTaskResult implements Serializable {
	
	public static final String TASK_TYPE_FACET_RUN = "facet_run";
	public static final String TASK_TYPE_FACET_RETRY = "facet_retry";
	public static final String TASK_TYPE_FACET_ONDEMAND = "facet_ondemand";
	public static final String TASK_TYPE_LOGTYPE_AVAILABLE = "logtype_available";

	public static final String TASK_STATUS_INIT = "init";
	public static final String TASK_STATUS_RUNNING = "running";
	public static final String TASK_STATUS_SUCCESS = "success";
	public static final String TASK_STATUS_FAIL = "fail";
	public static final String TASK_STATUS_STOPPING = "stopping";
	public static final String TASK_STATUS_STOPPED = "stopped";
	
	private String taskId;
	
	private String taskType;
	
	private String taskStatus;

	private Object taskResult;

	private String taskError;
	
	private Double progress;
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Object getTaskResult() {
		return taskResult;
	}

	public void setTaskResult(Object taskResult) {
		this.taskResult = taskResult;
	}

	public String getTaskError() {
		return taskError;
	}

	public void setTaskError(String taskError) {
		this.taskError = taskError;
	}

	public Double getProgress() {
		return progress;
	}

	public void setProgress(Double progress) {
		this.progress = progress;
	}

	@Override
	public String toString() {
		return "AsyncTaskResult [taskId=" + taskId + ", taskType=" + taskType + ", taskStatus=" + taskStatus
				+ ", taskResult=" + taskResult + ", taskError=" + taskError + ", progress=" + progress + "]";
	}
}
