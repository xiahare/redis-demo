package com.xl.example.sts.redis.service.async;

public class Stopper {
	private boolean stopping = false;

	public boolean isStopping() {
		return stopping;
	}

	public void setStopping(boolean stopping) {
		this.stopping = stopping;
	}
	
}
