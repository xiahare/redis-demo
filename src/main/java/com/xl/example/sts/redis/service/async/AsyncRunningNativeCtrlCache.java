package com.xl.example.sts.redis.service.async;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class AsyncRunningNativeCtrlCache {

	private Hashtable<String, FutureCtrl> futureMap = new Hashtable<String, FutureCtrl>();
	
	public FutureCtrl get(String tid) {
		return futureMap.get(tid);
	}
	
	public void put(String tid, FutureCtrl futureCtrl) {
		futureMap.put(tid, futureCtrl);
	}
	
	public FutureCtrl remove(String tid) {
		System.out.println("FutureCtrl removed :" + tid);
		return futureMap.remove(tid);
	}
	
	public List<String> getRunningNativeCtrlMap(){
		
		return new ArrayList<String> (futureMap.keySet());
	}
}
