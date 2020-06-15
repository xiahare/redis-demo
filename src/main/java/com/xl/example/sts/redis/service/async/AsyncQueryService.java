package com.xl.example.sts.redis.service.async;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncQueryService {

	@Async
	public CompletableFuture<Object> asyncQuery(String taskId, Integer alivetime) {
		
		try {
			System.out.println(taskId + "  -  Starting sleep : " + alivetime + " seconds!" );
			Thread.sleep(alivetime*1000);
			System.out.println(taskId + "  -  Ended sleep : " + alivetime + " seconds!" );
		} catch (InterruptedException e) {
			System.out.println(e);
		}
		
		return CompletableFuture.completedFuture(alivetime);
	}
}
