package com.xl.example.sts.redis.service.async;

import java.util.concurrent.CompletableFuture;

public class FutureCtrl {
	private Stopper stopper;
	private CompletableFuture<Object> future;
	
	public Stopper getStopper() {
		return stopper;
	}
	public void setStopper(Stopper stopper) {
		this.stopper = stopper;
	}
	public CompletableFuture<Object> getFuture() {
		return future;
	}
	public void setFuture(CompletableFuture<Object> future) {
		this.future = future;
	}
}
