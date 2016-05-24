package com.gaiay.base.net;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.gaiay.base.request.ARequest;
import com.gaiay.base.request.BaseRequest;

/**
 * 用于异步连网的封装类
 * 
 * @author iMuto
 */
public final class NetAsynTask {
	
	NetEngine engine;
	public static final int request_type_get = 1;
	public static final int request_type_post = 2;
	public static final int request_type_delete = 3;
	public static final int request_type_put = 4;
	public static final int request_type_put_new = 5;
	
	public static NetAsynTask connectByGet(String url,
			Map<String, String> requestValues, NetCallback callback, ARequest req) {
		NetAsynTask task = new NetAsynTask(url, requestValues, callback, request_type_get);
		task.execute(req);
		return task;
	}
	public static NetAsynTask connectByPost(String url,
			Map<String, String> requestValues, NetCallback callback, ARequest req) {
		NetAsynTask task = new NetAsynTask(url, requestValues, callback, request_type_post);
		task.execute(req);
		return task;
	}
	public static NetAsynTask connectByDelete(String url,
			Map<String, String> requestValues, NetCallback callback, ARequest req) {
		NetAsynTask task = new NetAsynTask(url, requestValues, callback, request_type_delete);
		task.execute(req);
		return task;
	}
	public static NetAsynTask connectByPut(String url,
			Map<String, String> requestValues, NetCallback callback, ARequest req) {
		NetAsynTask task = new NetAsynTask(url, requestValues, callback, request_type_put);
		task.execute(req);
		return task;
	}
	public static NetAsynTask connectByPutNew(String url,
			Map<String, String> requestValues, NetCallback callback, ARequest req) {
		NetAsynTask task = new NetAsynTask(url, requestValues, callback, request_type_put_new);
		task.execute(req);
		return task;
	}
	public static NetAsynTask connect(String url, String method,
			Map<String, String> requestValues, Context cxt, NetCallback callback, ARequest req) {
		NetAsynTask task = new NetAsynTask(url, method, requestValues, cxt, callback);
		task.execute(req);
		return task;
	}
	public static NetAsynTask connect(String url, String method,
			Map<String, String> requestValues, NetCallback callback, ARequest req) {
		return new NetAsynTask(url, method, requestValues, null, callback).execute(req);
	}

	@Deprecated
	public static NetAsynTask upload(String url, String method,
			Map<String, String> requestValues, NetCallback callback, ARequest req, Map<String, byte[]> data) {
		NetAsynTask task = new NetAsynTask(url, method, requestValues, callback, req, data);
		task.execute();
		return task;
	}
	public static NetAsynTask upload(String url,
			Map<String, String> requestValues, NetCallback callback, BaseRequest<?> req, List<ModelUpload> data) {
		NetAsynTask task = new NetAsynTask(url, requestValues, callback, req, data);
		task.execute();
		return task;
	}
	@Deprecated
	public static NetAsynTask upload(String url,
			Map<String, String> requestValues, NetCallback callback, ARequest req, Map<String, byte[]> data) {
		NetAsynTask task = new NetAsynTask(url, requestValues, callback, req, data);
		task.execute();
		return task;
	}
	@Deprecated
	public static NetAsynTask upload(String url,
			Map<String, String> requestValues, Map<String, Bitmap> bmps, NetCallback callback, ARequest req) {
		NetAsynTask task = new NetAsynTask(url, requestValues, bmps, req, callback);
		task.execute();
		return task;
	}
	@Deprecated
	public static NetAsynTask upload(String url, String method,
			Map<String, String> requestValues, NetCallback callback, ARequest req, Map<String, byte[]> data, boolean isUseDefName) {
		NetAsynTask task = new NetAsynTask(url, method, requestValues, callback, req, data, isUseDefName);
		task.execute();
		return task;
	}
	@Deprecated
	public static NetAsynTask upload(String url, String method,
			Map<String, String> requestValues, NetCallback callback, ARequest req, LinkedHashMap<String, String> data) {
		NetAsynTask task = new NetAsynTask(url, method, requestValues, callback, req, data);
		task.execute();
		return task;
	}
	@Deprecated
	public static NetAsynTask upload(String url, String method,
			Map<String, String> requestValues, NetCallback callback, ARequest req, List<Bitmap> data) {
		NetAsynTask task = new NetAsynTask(url, method, requestValues, callback, req, data);
		task.execute();
		return task;
	}

	/**
	 * 异步请求连接,访问数据 <br>
	 * 使用参数what分辨不同的请求,请求完成后what参数将分发给对应的Request进行处理 <br>
	 * 处理完成后回调callback.onGetResult(boolean),连网成功boolean为true,否者为false
	 * 
	 * @param method
	 *            请求的方法名称
	 * @param requestValues
	 *            请求的参数map
	 * @param cxt
	 *            上下文参数
	 * @param callback
	 *            Ui更新部分
	 */
	public NetAsynTask(String url, String method,
			Map<String, String> requestValues, Context cxt, Callback callback) {
		this(url, method, requestValues, cxt, callback, null);
	}
	
	/**
	 * 异步请求连接,访问数据 <br>
	 * 使用参数what分辨不同的请求,请求完成后what参数将分发给对应的Request进行处理 <br>
	 * 处理完成后回调callback.onGetResult(boolean),连网成功boolean为true,否者为false
	 * 
	 * @param method
	 *            请求的方法名称
	 * @param requestValues
	 *            请求的参数map
	 * @param cxt
	 *            上下文参数
	 * @param callback
	 *            Ui更新部分
	 * @param ch
	 *            数据缓存帮助类
	 */
	public NetAsynTask(String url, String method,
			Map<String, String> requestValues, Context cxt, Callback callback,
			CacheHelper ch) {
		this(url, method, requestValues, cxt, callback, null, false);
	}
	/**
	 * 异步请求连接,访问数据 <br>
	 * 使用参数what分辨不同的请求,请求完成后what参数将分发给对应的Request进行处理 <br>
	 * 处理完成后回调callback.onGetResult(boolean),连网成功boolean为true,否者为false
	 * 
	 * @param method
	 *            请求的方法名称
	 * @param requestValues
	 *            请求的参数map
	 * @param cxt
	 *            上下文参数
	 * @param callback
	 *            Ui更新部分
	 * @param ch
	 *            数据缓存帮助类
	 */
	public NetAsynTask(String url, String method,
			Map<String, String> requestValues, Context cxt, Callback callback,
			CacheHelper ch, boolean isPriorCache) {

		ModelEngine model = new ModelEngine();
		model.cacheHelper = ch;
		model.callback = callback;
		model.isPriorCache = isPriorCache;
		model.method = method;
//		model.req = ;
		model.requestValues = requestValues;
		model.url = url;
		model.what = 0;
		engine = InvokePostEngineOld.getEngine(model);
	}
	/**
	 * 异步请求连接,访问数据 <br>
	 * 使用参数what分辨不同的请求,请求完成后what参数将分发给对应的Request进行处理 <br>
	 * 处理完成后回调callback.onGetResult(boolean),连网成功boolean为true,否者为false
	 * 
	 * @param method
	 *            请求的方法名称
	 * @param requestValues
	 *            请求的参数map
	 * @param cxt
	 *            上下文参数
	 * @param callback
	 *            Ui更新部分
	 * @param ch
	 *            数据缓存帮助类
	 */
	public NetAsynTask(String url,
			Map<String, String> requestValues, Callback callback, int requestType) {
		
		ModelEngine model = new ModelEngine();
		model.callback = callback;
		model.requestValues = requestValues;
		model.url = url;
		model.what = 0;
		if (requestType == request_type_get) {
			engine = InvokeGetEngine.getEngine(model);
		} else if(requestType == request_type_post) {
			engine = InvokePostEngine.getEngine(model);
		} else if(requestType == request_type_put) {
			engine = InvokePutEngineOld.getEngine(model);
		} else if(requestType == request_type_delete) {
			engine = InvokeDeleteEngine.getEngine(model);
		} else if(requestType == request_type_put_new) {
			engine = InvokePutEngine.getEngine(model);
		} else {
			engine = InvokePostEngine.getEngine(model);
		}
	}
	/**
	 * 异步请求连接,访问数据 <br>
	 * 使用参数what分辨不同的请求,请求完成后what参数将分发给对应的Request进行处理 <br>
	 * 处理完成后回调callback.onGetResult(boolean),连网成功boolean为true,否者为false
	 * 
	 * @param method
	 *            请求的方法名称
	 * @param requestValues
	 *            请求的参数map
	 * @param cxt
	 *            上下文参数
	 * @param callback
	 *            Ui更新部分
	 * @param what
	 *            分辨连网请求
	 */
	public NetAsynTask(String url, String method,
			Map<String, String> requestValues, Context cxt, Callback callback,
			int what) {
		ModelEngine model = new ModelEngine();
		model.callback = callback;
		model.method = method;
//		model.req = ;
		model.requestValues = requestValues;
		model.url = url;
		model.what = 0;
		engine = InvokePostEngineOld.getEngine(model);
	}

	/**
	 * 异步请求连接,访问数据 <br>
	 * 使用参数what分辨不同的请求,请求完成后what参数将分发给对应的Request进行处理 <br>
	 * 处理完成后回调callback.onGetResult(boolean),连网成功boolean为true,否者为false
	 * 
	 * @param method
	 *            请求的方法名称
	 * @param json
	 *            请求的参数,以json格式存储
	 * @param cxt
	 *            上下文参数
	 * @param callback
	 *            Ui更新部分
	 * @param what
	 *            分辨连网请求
	 */
	public NetAsynTask(String url, String method, String json, Context cxt,
			Callback callback, int what) {
		ModelEngine model = new ModelEngine();
		model.callback = callback;
		model.method = method;
//		model.req = ;
		model.url = url;
		model.what = 0;
		engine = InvokePostEngineOld.getEngine(model);
	}
	
	public NetAsynTask(String url, String method,
			Map<String, String> requestValues, NetCallback callback, ARequest req, Map<String, byte[]> data) {
		ModelEngine model = new ModelEngine();
		model.setUploadBytesData(data);
		model.callback = callback;
		model.method = method;
		model.req = req;
		model.requestValues = requestValues;
		model.url = url;
		model.what = 0;
		engine = UploadEngine.getEngine(model);
	}
	public NetAsynTask(String url,
			Map<String, String> requestValues, NetCallback callback, ARequest req, List<ModelUpload> data) {
		ModelEngine model = new ModelEngine();
		model.dataUpload = data;
		model.callback = callback;
		model.req = req;
		model.requestValues = requestValues;
		model.url = url;
		model.what = 0;
		engine = UploadEngine.getEngine(model);
	}
	public NetAsynTask(String url,
			Map<String, String> requestValues, NetCallback callback, ARequest req, Map<String, byte[]> data) {
		ModelEngine model = new ModelEngine();
		model.setUploadBytesData(data);
		model.callback = callback;
		model.req = req;
		model.requestValues = requestValues;
		model.isUseDefFileParamName = false;
		model.url = url;
		model.what = 0;
		engine = UploadEngine.getEngine(model);
	}
	public NetAsynTask(String url,
			Map<String, String> requestValues, Map<String, Bitmap> bmps, ARequest req, NetCallback callback) {
		ModelEngine model = new ModelEngine();
		model.setUploadData(bmps);
		model.callback = callback;
		model.req = req;
		model.requestValues = requestValues;
		model.isUseDefFileParamName = false;
		model.url = url;
		model.what = 0;
		engine = UploadEngine.getEngine(model);
	}
	public NetAsynTask(String url, String method,
			Map<String, String> requestValues, NetCallback callback, ARequest req, Map<String, byte[]> data, boolean isUseDefName) {
		ModelEngine model = new ModelEngine();
		model.setUploadBytesData(data);
		model.callback = callback;
		model.method = method;
		model.isUseDefFileParamName = isUseDefName;
		model.req = req;
		if (!TextUtils.isEmpty(method) && requestValues != null) {
			requestValues.put("method", method);
		}
		model.requestValues = requestValues;
		model.url = url;
		model.what = 0;
		engine = UploadEngine.getEngine(model);
	}
	public NetAsynTask(String url, String method,
			Map<String, String> requestValues, NetCallback callback, ARequest req, LinkedHashMap<String, String> data) {
		ModelEngine model = new ModelEngine();
		model.setUploadFilesData(data);
		model.callback = callback;
		model.method = method;
		model.req = req;
		model.requestValues = requestValues;
		model.url = url;
		model.what = 0;
		engine = UploadEngine.getEngine(model);
	}
	public NetAsynTask(String url, String method,
			Map<String, String> requestValues, NetCallback callback, ARequest req, List<Bitmap> data) {
		ModelEngine model = new ModelEngine();
		model.setUploadData("imgs", data);
		model.callback = callback;
		model.method = method;
		model.req = req;
		if (!TextUtils.isEmpty(method) && requestValues != null) {
			requestValues.put("method", method);
		}
		model.requestValues = requestValues;
		model.url = url;
		model.what = 0;
		engine = UploadEngine.getEngine(model);
	}

	public void cancel(boolean isCancel) {
		engine.cancel(true);
	}

	public boolean isCancelled() {
		return true;
	}
	
	public NetAsynTask execute(ARequest req) {
		engine.getEngineModel().req = req;
		engine.execute();
		return this;
	}
	public NetAsynTask execute() {
		engine.execute();
		return this;
	}

	public List<Cookie> getHttpClientCookie() {
		return engine.getHttpClientCookie();
	}
	
	public interface CacheHelper {
		/**
		 * 获取缓存数据<br>
		 * 如果需要读取缓存数据，需要重写该方法
		 * 
		 * @param requestValues
		 * @return
		 */
		public String getCacheData(Map<String, String> requestValues);

		/**
		 * 将result缓存到本地<br>
		 * 如果需要对数据进行缓存，需要重写该方法
		 * 
		 * @param requestValues
		 * @param result
		 */
		public void cacheData(Map<String, String> requestValues, String result);
	}
	
}
