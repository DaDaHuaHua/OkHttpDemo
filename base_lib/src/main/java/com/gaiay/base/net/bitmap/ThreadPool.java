package com.gaiay.base.net.bitmap;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 */
public class ThreadPool {
	private static ThreadPool pool;
	private static ThreadPoolExecutor executor;
	
	private ThreadPool() {}
	
	public static ThreadPool getInstance() {
		if (pool == null) {
			pool = new ThreadPool();
		}
		return pool;
	}
	
	public ThreadPoolExecutor getPool() {
		if (executor == null || executor.isShutdown()) {
			executor = new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS, 
					new ArrayBlockingQueue<Runnable>(5), new ThreadPoolExecutor.DiscardOldestPolicy());
		}
		return executor;
	}
	
	public void setCorePoolSize(int corePoolSize) {
		getPool().setCorePoolSize(corePoolSize);
	}
	
	public void shutdown() {
		if (executor != null) {
			executor.shutdown();
			executor = null;
		}
	}
}
