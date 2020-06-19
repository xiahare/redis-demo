package com.xl.example.sts.redis.service.async;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncQueryService {

	@Async
	public CompletableFuture<Object> asyncQuery(String taskId, Integer alivetime, Stopper stopper) {
		
		try {
			System.out.println(taskId + "  -  Starting sleep : " + alivetime + " seconds!" );
			for (int i = 0; i < alivetime; i++) {
				if(stopper.isStopping()) {
					System.out.println("Task is stopped:" + taskId);
					throw new TaskStopException(taskId);
				} else {
					Thread.sleep(1000); // 1 second	
					
				}
			}
			
			System.out.println(taskId + "  -  Ended sleep : " + alivetime + " seconds!" );
		} catch (InterruptedException e) {
			System.out.println("Stopped Task:" + taskId);
		}
		
		return CompletableFuture.completedFuture(alivetime);
	}
}
