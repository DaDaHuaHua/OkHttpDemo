package com.gaiay.base.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadToolManager {
	private static ThreadToolManager instance;
	private ExecutorService executorService;
	public final Map<String, ITask> runTaskMap = new HashMap<String, ITask>();

	private ThreadToolManager() {
		executorService = Executors.newCachedThreadPool();
	}

	public static ThreadToolManager getInstance() {
		if (instance == null) {
			instance = new ThreadToolManager();
		}
		return instance;
	}

	/**
	 * 添加一个下载任务
	 * 
	 * @param task
	 */
	public void addDownloadTask(ITask task) {
		if (runTaskMap.containsKey(task.getId())) {
			return;
		}
		runTaskMap.put(task.getId(), task);
		executorService.execute(task);
	}

	/**
	 * 取消一个下载任务
	 * 
	 * @param taskId
	 */
	public synchronized void cancelTask(String taskId) {

		ITask task = runTaskMap.get(taskId);
		if (task != null) {
			task.cancel();
		}
		runTaskMap.remove(taskId);
	}

	/**
	 * 取消所有下载任务
	 * 
	 * @param taskName
	 */
	public synchronized void cancelAllTask() {
		Iterator<Entry<String, ITask>> iterator = runTaskMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, ITask> entry = iterator.next();
			iterator.remove();
			entry.getValue().cancel();
		}
	}

	/**
	 * 得到下载任务个数
	 * 
	 * @return
	 */
	public int getDownloadTaskCount() {
		return runTaskMap.size();
	}

	interface ITask extends Runnable {
		public static final int PENDING = 0xA0;
		public static final int RUNNING = 0xA1;
		public static final int OVER = 0xA2;
		public static final int DONE = 0xA3;
		public static final int ERROR = 0xA4;

		public String getId();

		public int getState();

		public void cancel();

		public void released();
	}
}
