package com.gaiay.base.net;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.gaiay.base.BaseApplication;
import com.gaiay.base.common.CommonCode;
import com.gaiay.base.common.ErrorMsg;
import com.gaiay.base.request.ARequest;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

import android.os.Handler;
import android.os.Message;

public abstract class NetEngine {

	MyHandler hand = null;
	NetTask runTask;
	ModelEngine model;

	public NetEngine() {
		hand = new MyHandler(this);
	}

	public abstract void shutDownConnect();

	public abstract String getRequest() throws Throwable;

	public void execute() {
		Log.e(model.url);
		runTask = new NetTask();
		runTask.execute();
	}

	static class MyHandler extends Handler {
		NetEngine mActivity;

		MyHandler(NetEngine activity) {
			mActivity = activity;
		}

		@Override
		public void handleMessage(Message msg) {
			if (mActivity != null && mActivity.runTask != null && mActivity.runTask.isRun) {
				mActivity.handResult(msg.what);
			}
		}
	}

	private class NetTask {

		boolean isRun = true;

		Thread thread = new Thread() {
			public void run() {
				if (NetworkUtil.isNetworkValidate(BaseApplication.app) && !model.isPriorCache) {
					hand.sendEmptyMessage(doNet(model.req));
				} else {
					if (model.cacheHelper != null) {
						try {
							String result = model.cacheHelper.getCacheData(model.requestValues);
							if (StringUtil.isNotBlank(result)) {
								hand.sendEmptyMessage(model.req.read(result, model.what, true));
							} else {
								hand.sendEmptyMessage(doNet(model.req));
							}
						} catch (ErrorMsg e) {
							e.printStackTrace();
						}
					} else {
						hand.sendEmptyMessage(doNet(model.req));
					}
				}
			};

			public void interrupt() {
				isRun = false;
			};
		};

		public void cancel() {
			thread.interrupt();
			shutDownConnect();
			handResult(CommonCode.ERROR_TASK_CANCEL);
		}

		public void execute() {
			thread.start();
		}
	}

	private void handResult(int what) {
		if (model.callback == null) {
			return;
		}
		model.callback.onGetResult(what);
	}

	public void cancel(boolean isCancel) {
		if (isCancel && runTask != null) {
			runTask.cancel();
		}
	}

	public boolean isCancelled() {
		if (runTask != null) {
			return !runTask.isRun;
		}
		return true;
	}

	Integer doNet(ARequest request) {
		// String tag = "请求服务器";
		try {
			// if (model.method != null && !model.method.equals("")) {
			// tag += " method: " + model.method;
			// }
			// MobileProbe.onEventBegin(null, tag);

			if (!NetworkUtil.isNetworkValidate(BaseApplication.app)) {
				return CommonCode.ERROR_NO_NETWORK;
			}
			if (request == null) {
				return CommonCode.ERROR_AREQUEST;
			}
			if (model.url == null) {
				return CommonCode.ERROR_URL;
			}
			try {
				String result = getRequest();
				if (StringUtil.isBlank(result)) {
					return CommonCode.ERROR_OTHER;
				}
				if (model.cacheHelper != null) {
					model.cacheHelper.cacheData(model.requestValues, result);
				}
				request.setUrl(model.url);
				return request.read(result, model.what, false);
			} catch (Throwable e) {
				e.printStackTrace();
				if (model.cacheHelper != null) {
					try {
						String result = model.cacheHelper.getCacheData(model.requestValues);
						if (StringUtil.isNotBlank(result)) {
							return request.read(result, model.what, true);
						}
					} catch (ErrorMsg e2) {
						e2.printStackTrace();
					}
				} else {
					if (e instanceof ErrorMsg) {
						return ((ErrorMsg) e).getCode();
					} else if (e instanceof SocketException) {
						return CommonCode.ERROR_TIME_OUT;
					} else {
						return CommonCode.ERROR_OTHER;
					}
				}
			}
			return CommonCode.ERROR_OTHER;
		} finally {
			// MobileProbe.onEventEnd(null, tag);
		}
	}

	protected String getJSONRequest(String method, Map<String, String> req) {
		JSONObject json = new JSONObject();
		try {
			if (!StringUtil.isBlank(method)) {
				json.put("method", method);
			}
			for (Map.Entry<String, String> entry : req.entrySet()) {
				json.put(entry.getKey(), entry.getValue());
			}
			Log.e("-------------------------  参数  ------------------------------");
			Log.e(json.toString());
			Log.e("-------------------------------------------------------------");
			return json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected String getUrlRequest() {
		if (model == null) {
			return null;
		}
		if (model.requestValues == null || model.requestValues.size() <= 0) {
			return model.url;
		}
		String url = model.url;
		if (url.contains("?")) {
			url = url + "&";
		} else {
			url = url + "?";
		}
		for (Map.Entry<String, String> entry : model.requestValues.entrySet()) {
			if (entry.getValue() != null) {
				url = url + entry.getKey() + "=" + entry.getValue() + "&";
			}
		}
		url = url.substring(0, url.length() - 1);
		Log.e(url);
		return url;
	}

	protected String getRequestParams() {
		if (model == null) {
			return null;
		}
		String url = "";
		for (Map.Entry<String, String> entry : model.requestValues.entrySet()) {
			if (entry.getValue() != null) {
				url = url + entry.getKey() + "=" + entry.getValue() + "&";
			}
		}
		url = url.substring(0, url.length() - 1);
		Log.e("put:" + url);
		return url;
	}

	/**
	 * 设置请求参数
	 * 
	 * @param map
	 * @return
	 */
	public List<NameValuePair> getHttpParams(Map<String, String> map) {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		Set<Map.Entry<String, String>> set = map.entrySet();
		Log.e("post:");
		for (Map.Entry<String, String> entry : set) {
			if (entry.getValue() != null) {
				Log.e("key: " + entry.getKey() + "; value: " + entry.getValue());
				formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		return formparams;
	}

	public ModelEngine getEngineModel() {
		return model;
	}

	public List<Cookie> getHttpClientCookie() {
		return null;
	}
}
