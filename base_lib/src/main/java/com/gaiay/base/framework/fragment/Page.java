package com.gaiay.base.framework.fragment;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalBitmap.OnCompleteListener;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaiay.base.BaseApplication;
import com.gaiay.base.R;
import com.gaiay.base.common.CommonCode;
import com.gaiay.base.net.Callback;
import com.gaiay.base.net.NetAsynTask;
import com.gaiay.base.net.NetCallback;
import com.gaiay.base.net.bitmap.BitmapAsynTask;
import com.gaiay.base.net.bitmap.BitmapManager;
import com.gaiay.base.request.ARequest;
import com.gaiay.base.util.FileUtil;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;
import com.gaiay.base.util.Utils;

public abstract class Page extends Fragment implements OnClickListener, OnItemClickListener {
	
	private View root;

	public RootContainer mCon;
	public String setId;
	public LayoutInflater inflater;
	boolean noRoot = true;
	public final static int MSG_BITMAP_DEF = 19880119;
	public boolean isDestroy = false;
	boolean isFirstIn = true;
	FinalBitmap fb;
	public int defRes = 0;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCon = (RootContainer) getActivity();
		setId = this.getClass().getName() + Utils.getSoleRandom(15);
		inflater = getActivity().getLayoutInflater();
		appIdName = mCon.getString(R.string.app_id_name);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		boolean isNightMode = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("isNightMode", false);
//		if (isNightMode) {
//			getActivity().setTheme(R.style.night_threme);
//		}else {
//			getActivity().setTheme(R.style.default_threme);
//		}
//		Log.e(getLogMsg() + ":onCreate");
	}
	
	/**
	 * 这个方法用于初始化你的界面
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (root == null) {
			isFirstIn = true;
			root = inflater.inflate(setLayoutId(), container, false);
		} else {
			isFirstIn = false;
			((ViewGroup)root.getParent()).removeAllViews();
		}
		root.bringToFront();
		root.setOnClickListener(this);
		setFullScreen(isFullScreen());
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e(getLogMsg() + ":onActivityCreated");
		if (noRoot) {
			noRoot = false;
			initView();
		}
		beforeShowView();
	}
	
	@Override
	public void onDestroy() {
		Log.e(getClass().getName() + " : onDestroy");
		if (fb != null) {
			fb.onDestroy();
		}
		destroySelf();
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		Log.e(getClass().getName() + " : onDetach");
		super.onDetach();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e(getLogMsg() + ":onActivityResult");
	}
	
	Animation in;
	Animation out;
	Animation in2;
	Animation out2;
	
	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
		Log.e(getLogMsg() + ":onCreateAnimationL:" + enter + " isBack:" + isBack);
		initAnim();
		if (!isBack) {
			if (enter) {
				if (!isEnterAnim()) {
					return null;
				}
				return in;
			} else {
				return out2;
			}
		} else {
			if (enter) {
				return in2;
			} else {
				return out;
			}
		}
		
	}
	
	public void setIsBack(boolean isBack) {
		this.isBack = isBack;
	}
	
	public boolean isEnterAnim() {
		return true;
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.e(getLogMsg() + ":onLowMemory");
	}
	
	public void setFullScreen(boolean isFull) {
		Log.e("isFull:" + isFull);
		if (isFull) {
			mCon.fullScreenNoAnim();
		} else {
			mCon.fullScreenCloseNoAnim();
		}
	}
	
	public void beforeShowView() {
		
	}
	
	public View getCurrentRootView() {
		return root;
	}
	
	/**
	 * 返回默认是否全屏显示
	 * @return
	 */
	public boolean isFullScreen() {
		return false;
	}
	Handler mHandlerAnim = new Handler();
	
	Runnable mRunAnim = new Runnable() {
		@Override
		public void run() {
			onPageEnterEnd();
			if (isFirstIn) {
				onPageFirstTimeEnterEnd();
			}
		}
	};
	
	private void initAnim() {
		if (in == null) {
			in = AnimationUtils.loadAnimation(getActivity(), R.anim.base_page_in);
			in.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {
				}
				@Override
				public void onAnimationRepeat(Animation arg0) {
				}
				@Override
				public void onAnimationEnd(Animation arg0) {
					mHandlerAnim.removeCallbacks(mRunAnim);
					mHandlerAnim.postDelayed(mRunAnim, 100);
				}
			});
		}
		if (out == null) {
			out = AnimationUtils.loadAnimation(getActivity(), R.anim.base_page_out);
			out.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {
				}
				@Override
				public void onAnimationRepeat(Animation arg0) {
				}
				@Override
				public void onAnimationEnd(Animation arg0) {
					onPageOutEnd();
				}
			});
		}
		if (in2 == null) {
			in2 = AnimationUtils.loadAnimation(getActivity(), R.anim.base_page_in2);
			in2.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {
				}
				@Override
				public void onAnimationRepeat(Animation arg0) {
				}
				@Override
				public void onAnimationEnd(Animation arg0) {
					mHandlerAnim.removeCallbacks(mRunAnim);
					mHandlerAnim.postDelayed(mRunAnim, 100);
				}
			});
		}
		if (out2 == null) {
			out2 = AnimationUtils.loadAnimation(getActivity(), R.anim.base_page_out2);
			out2.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {
				}
				@Override
				public void onAnimationRepeat(Animation arg0) {
				}
				@Override
				public void onAnimationEnd(Animation arg0) {
					onPageOutEnd();
				}
			});
		}
	}
	/**
	 * 当page进入最上层界面，动画结束后，调用该方法
	 */
	public void onPageEnterEnd() {
		if (mCon != null) {
//			MobileProbe.onResume(mCon);
			Log.e(getLogMsg() + ":onPageEnterEnd");
		}
	}
	/**
	 * 当page第一次进入最上层界面，动画结束后，调用该方法
	 */
	public void onPageFirstTimeEnterEnd() {
	}
	public void onPageOutEnd() {
	}
	
	public abstract int setLayoutId();
	public abstract void initView();
	
	public View findView(int id) {
		if (root != null) {
			return root.findViewById(id);
		} else {
			return getView().findViewById(id);
		}
	}
	
	Toast toast;
	
	public void toast(String msg) {
		if (isDestroy) {
			return;
		}
		if (toast == null) {
			toast = Toast.makeText(mCon, msg, Toast.LENGTH_SHORT);
		}
		toast.setText(msg);
		toast.show();
	}
	public void toast(int msgId) {
		if (isDestroy) {
			return;
		}
		if (toast == null) {
			toast = Toast.makeText(mCon, msgId, Toast.LENGTH_SHORT);
		}
		toast.setText(msgId);
		toast.show();
	}
	boolean isBack = false;
	public void startPage(Page page) {
		mCon.startPage(page);
	}
	public void startPageClearBacks(Page page, int clears) {
		mCon.startPageClearBacks(page, clears);
	}
	
	boolean isForResult = false;
	protected boolean isRequestResult = false;
	protected int requestCode = -1024;
	
	public void startPageForResult(Page page) {
		if (page != null) {
			isForResult = true;
			page.isRequestResult = true;
			mCon.startPage(page);
		}
	}
	public void startPageForResult(Page page, int requestCode) {
		if (page != null) {
			isForResult = true;
			page.isRequestResult = true;
			page.requestCode = requestCode;
			mCon.startPage(page);
		}
	}
	
	Bundle bun;
	
	public void setBundle(Bundle bun) {
		if (bun == null) {
			return;
		}
		this.bun = new Bundle(bun);
	}
	
	public void putString(String key, String value) {
		if (bun == null) {
			bun = new Bundle();
		}
		bun.putString(key, value);
	}
	
	public void putSerializable(String key, Serializable value) {
		if (bun == null) {
			bun = new Bundle();
		}
		bun.putSerializable(key, value);
	}
	
	public void putInt(String key, int value) {
		if (bun == null) {
			bun = new Bundle();
		}
		bun.putInt(key, value);
	}
	
	public void putBoolean(String key, boolean value) {
		if (bun == null) {
			bun = new Bundle();
		}
		bun.putBoolean(key, value);
	}
	
	public Bundle getBundle() {
		if (bun == null) {
			bun = new Bundle();
		}
		return bun;
	}
	
	public void onPageResult(boolean isSuccess, Bundle bun) {
		isForResult = false;
	}
	
	public void onPageResult(boolean isSuccess, int requestCode, Bundle bun) {
		onPageResult(isSuccess, bun);
	}
	
	Bundle bunResult;
	boolean isResultSuc = false;
	public void setResult(Bundle bun, boolean isSuc) {
//		mCon.setResult(bun);
		this.bunResult = bun;
		isResultSuc = isSuc;
	}
	
	public boolean isResult() {
		return isResultSuc;
	}
	
	public Bundle getResult() {
		return bunResult;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
	
	public String getLogMsg() {
		return this.getClass().getSimpleName();
	}
	
	public Map<String, BitmapAsynTask> tasks_img = new HashMap<String, BitmapAsynTask>();
	
	public void stopTaskImg() {
		if (tasks_img != null) {
			for (Entry<String, BitmapAsynTask> entry : tasks_img.entrySet()) {
				if (entry != null && entry.getValue() != null) {
					entry.getValue().cancel(true);
				}
			}
			tasks_img.clear();
		}
	}
	
	public void getBitmap(String url, Handler handler, int what, int index) {
		Log.e("url:" + url + " con:" + mCon);
		if (tasks_img.containsKey(url)) {
			return;
		}
		BitmapAsynTask task = BitmapManager.getBitmap(url, mCon, handler, what, setId, index, BaseApplication.app.getCacheFolder());
		if (task != null) {
			tasks_img.put(url, task);
		}
	}
	public void getBitmap(String url, Context context, Handler handler, int what, String si, int index, String cacheFolder) {
		BitmapAsynTask task = BitmapManager.getBitmap(url, context, handler, what, si, index, cacheFolder);
		if (task != null) {
			tasks_img.put(url, task);
		}
	}
	public void getBitmap(ImageView img, String url) {
		if (fb == null) {
			if (StringUtil.isBlank(BaseApplication.app.getCacheFolder())) {
				fb = FinalBitmap.create(mCon);
			} else {
				fb = FinalBitmap.create(mCon, BaseApplication.app.getCacheFolder());
			}
			fb.configLoadingImage(defRes);
			fb.configLoadfailImage(defRes);
		}
		fb.configBitmapFilter(null);
		fb.display(img, url);
	}
	public void getBitmap(ImageView img, String url, int defImg) {
		if (fb == null) {
			if (StringUtil.isBlank(BaseApplication.app.getCacheFolder())) {
				fb = FinalBitmap.create(mCon);
			} else {
				fb = FinalBitmap.create(mCon, BaseApplication.app.getCacheFolder());
			}
		}
		fb.configBitmapFilter(null);
		fb.configLoadingImage(defImg);
		fb.configLoadfailImage(defImg);
		fb.display(img, url);
	}
	public void getBitmap(ImageView img, String url, Bitmap defImg) {
		if (fb == null) {
			if (StringUtil.isBlank(BaseApplication.app.getCacheFolder())) {
				fb = FinalBitmap.create(mCon);
			} else {
				fb = FinalBitmap.create(mCon, BaseApplication.app.getCacheFolder());
			}
		}
		fb.configBitmapFilter(null);
		fb.configLoadingImage(defImg);
		fb.configLoadfailImage(defImg);
		fb.display(img, url);
	}
	public void getBitmap(ImageView img, String url, int defImg, OnCompleteListener l) {
		if (fb == null) {
			if (StringUtil.isBlank(BaseApplication.app.getCacheFolder())) {
				fb = FinalBitmap.create(mCon);
			} else {
				fb = FinalBitmap.create(mCon, BaseApplication.app.getCacheFolder());
			}
		}
		fb.configBitmapFilter(null);
		fb.configLoadingImage(defImg);
		fb.configLoadfailImage(defImg);
		fb.configOnCompleteListener(l);
		fb.display(img, url);
	}
	
	public void getBitmapFillet(ImageView img, String url) {
		if (fb == null) {
			if (StringUtil.isBlank(BaseApplication.app.getCacheFolder())) {
				fb = FinalBitmap.create(mCon);
			} else {
				fb = FinalBitmap.create(mCon, BaseApplication.app.getCacheFolder());
			}
			fb.configLoadingImage(defRes);
			fb.configLoadfailImage(defRes);
		}
		fb.configBitmapFilter(null);
		fb.display(img, url);
	}
	public void getBitmapFillet(ImageView img, String url, int defImg, int fillet) {
		if (fb == null) {
			if (StringUtil.isBlank(BaseApplication.app.getCacheFolder())) {
				fb = FinalBitmap.create(mCon);
			} else {
				fb = FinalBitmap.create(mCon, BaseApplication.app.getCacheFolder());
			}
			fb.configLoadingImage(defRes);
			fb.configLoadfailImage(defRes);
		}
		fb.configBitmapFilter(null);
		fb.configLoadingImage(defImg);
		fb.configLoadfailImage(defImg);
		fb.display(img, url);
	}
	public void getBitmapFillet(ImageView img, String url, int defImg) {
		if (fb == null) {
			if (StringUtil.isBlank(BaseApplication.app.getCacheFolder())) {
				fb = FinalBitmap.create(mCon);
			} else {
				fb = FinalBitmap.create(mCon, BaseApplication.app.getCacheFolder());
			}
			fb.configLoadingImage(defRes);
			fb.configLoadfailImage(defRes);
		}
		fb.configBitmapFilter(null);
		fb.configLoadingImage(defImg);
		fb.configLoadfailImage(defImg);
		fb.display(img, url);
	}
	public void getBitmapWithFilter(ImageView img, String url, int defImg, BitmapDisplayConfig.OnFilterBitmap filter) {
		if (fb == null) {
			if (StringUtil.isBlank(BaseApplication.app.getCacheFolder())) {
				fb = FinalBitmap.create(mCon);
			} else {
				fb = FinalBitmap.create(mCon, BaseApplication.app.getCacheFolder());
			}
			fb.configLoadingImage(defRes);
			fb.configLoadfailImage(defRes);
		}
		fb.configBitmapFilter(filter);
		fb.configLoadingImage(defImg);
		fb.configLoadfailImage(defImg);
		fb.display(img, url);
	}
	
	public Bitmap getLocBitmap(String uri) {
		if (StringUtil.isBlank(uri)) {
			return null;
		}
		if (fb == null) {
			if (StringUtil.isBlank(BaseApplication.app.getCacheFolder())) {
				fb = FinalBitmap.create(mCon);
			} else {
				fb = FinalBitmap.create(mCon, BaseApplication.app.getCacheFolder());
			}
			fb.configLoadingImage(defRes);
			fb.configLoadfailImage(defRes);
		}
		return fb.getBitmapFromCache(uri);
	}
	
	public void isStopLoadImg(boolean isStopLoad) {
		if (fb != null) {
			if (isStopLoad) {
				fb.pauseWork(true);
			} else {
				fb.pauseWork(false);
			}
		}
	}
	
	public void setDefRes(int id) {
		defRes = id;
	}
	
	public NetAsynTask task;
	public boolean isInvoking = false;
	String appIdName =  null;
	
	public void invoke(String method, Map<String, String> map, CallbackFilter callback) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		task = new NetAsynTask(BaseApplication.app.getDefaultUrl(), method, map, mCon, callback);
		task.execute(callback.mReq);
	}
	
	public void invoke(String method, Map<String, String> map, CallbackFilter callback, NetAsynTask.CacheHelper cacheHelper) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		task = new NetAsynTask(BaseApplication.app.getDefaultUrl(), method, map, mCon, callback, cacheHelper);
		task.execute(callback.mReq);
	}
	
	public void invoke(String method, Map<String, String> map, CallbackFilter callback, NetAsynTask.CacheHelper cacheHelper, boolean isPriorCache) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		task = new NetAsynTask(BaseApplication.app.getDefaultUrl(), method, map, mCon, callback, cacheHelper, isPriorCache);
		task.execute(callback.mReq);
	}
	public void invoke(String url, String method, Map<String, String> map, ARequest mReq, Callback callback) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		task = new NetAsynTask(url, method, map, mCon, callback);
		task.execute(mReq);
	}
	public void invoke(String method, Map<String, String> map, ARequest mReq, Callback callback) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		task = new NetAsynTask(BaseApplication.app.getDefaultUrl(), method, map, mCon, callback);
		task.execute(mReq);
	}
	public void invoke(String method, Map<String, String> map, ARequest mReq, Callback callback, NetAsynTask.CacheHelper cacheHelper) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		task = new NetAsynTask(BaseApplication.app.getDefaultUrl(), method, map, mCon, callback, cacheHelper);
		task.execute(mReq);
	}
	
	public void invoke(String method, Map<String, String> map, ARequest mReq, boolean isPriorCache, Callback callback, NetAsynTask.CacheHelper cacheHelper) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		task = new NetAsynTask(BaseApplication.app.getDefaultUrl(), method, map, mCon, callback, cacheHelper, isPriorCache);
		task.execute(mReq);
	}
	
	public void invoke(String url, String method, Map<String, String> map, CallbackFilter callback) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		task = new NetAsynTask(url, method, map, mCon, callback);
		task.execute(callback.mReq);
	}
	public void invoke(String url, String cId, String method, Map<String, String> map, CallbackFilter callback) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, cId);
		task = new NetAsynTask(url, method, map, mCon, callback);
		task.execute(callback.mReq);
	}
	public void invokeBase(String url, String method, Map<String, String> map, CallbackFilter callback) {
		isInvoking = true;
		task = new NetAsynTask(url, method, map, mCon, callback);
		task.execute(callback.mReq);
	}
	
	public void invoke(String method, Map<String, String> map, NetCallbackFilter callback) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.connect(BaseApplication.app.getDefaultUrl(), method, map, mCon, callback, callback.aRequest);
	}
	
	public void invoke(String url, String method, Map<String, String> map, NetCallbackFilter callback) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.connect(url, method, map, mCon, callback, callback.aRequest);
	}
	public void invoke(URL url, Map<String, String> map, NetCallbackFilter callback) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.connect(url.toString(), null, map, mCon, callback, callback.aRequest);
	}
	public void invoke(String url, String method, boolean isAdd, Map<String, String> map, NetCallbackFilter callback) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		map.put("pageSize", callback.aRequest.pageSize + "");
		if (isAdd) {
			callback.aRequest.pageNo ++;
		} else {
			callback.aRequest.pageNo = 1;
			showWaitView();
		}
		map.put("pageNo", callback.aRequest.pageNo + "");
		NetAsynTask.connect(url, method, map, mCon, callback, callback.aRequest);
	}
	public void invoke(String method, boolean isAdd, Map<String, String> map, NetCallbackFilter callback) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		map.put("pageSize", callback.aRequest.pageSize + "");
		if (isAdd) {
			callback.aRequest.pageNo ++;
		} else {
			callback.aRequest.pageNo = 1;
			showWaitView();
		}
		map.put("pageNo", callback.aRequest.pageNo + "");
		NetAsynTask.connect(BaseApplication.app.getDefaultUrl(), method, map, mCon, callback, callback.aRequest);
	}
	
	public void invoke(URL url, Map<String, String> map, NetCallbackFilter callback, Map<String, byte[]> data) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url.toString(), null, map, callback, callback.aRequest, data);
	}
	public void invoke(URL url, Map<String, String> map, NetCallbackFilter callback, List<Bitmap> data) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url.toString(), null, map, callback, callback.aRequest, data);
	}
	public void invoke(String url, Map<String, String> map, NetCallbackFilter callback, List<Bitmap> data) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url, null, map, callback, callback.aRequest, data);
	}
	public void invoke(String url, Map<String, String> map, NetCallbackFilter callback, String file) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		if (!FileUtil.isFileExists(file)) {
			return;
		}
		LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
		data.put("file", file);
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url, null, map, callback, callback.aRequest, data);
	}
	public void invoke(String url, Map<String, String> map, NetCallbackFilter callback, String file, String fileType) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		if (!FileUtil.isFileExists(file)) {
			return;
		}
		LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
		data.put(fileType + "1", file);
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url, null, map, callback, callback.aRequest, data);
	}
	public void invoke(String url, Map<String, String> map, NetCallbackFilter callback, String[] file, String fileType) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		if (file == null || file.length <= 0) {
			return;
		}
		LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
		for (int i = 0; i < file.length; i++) {
			data.put(fileType + (i + 1), file[i]);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url, null, map, callback, callback.aRequest, data);
	}
	public void invoke(String url, Map<String, String> map, NetCallbackFilter callback, LinkedHashMap<String, String> files) {
		isInvoking = true;
		if(map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		if (files == null || files.size() <= 0) {
			return;
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url, null, map, callback, callback.aRequest, files);
	}
	
	public abstract class NetCallbackFilter extends NetCallback {
		
		public ARequest aRequest;
		
		public NetCallbackFilter(ARequest mReq) {
			this.aRequest = mReq;
		}
		
		@Override
		public void onGetFaild() {
		}

		@Override
		public void onGetError() {
		}
		
		public boolean isUseMsg() {
			return true;
		}
		
		@Override
		public void onComplete() {
			
		}
		
		@Override
		public void onGetResult(int resultCode) {
			super.onGetResult(resultCode);
			isInvoking = false;
			if (isDestroy || !isUseMsg()) {
				return;
			}
			disWaitView();
			doNetMsg(resultCode, aRequest.hasData(), aRequest);
		}
	}
	
	public abstract class CallbackFilter implements Callback {
		
		boolean isUseMsg = true;
		public ARequest mReq;
		
		public CallbackFilter(ARequest mReq) {
			this.mReq = mReq;
		}
		
		/**
		 * 构造方法，其中参数用来标识是否显示系统提示，默认显示
		 * @param isUseMsg true显示，false不显示
		 */
		public CallbackFilter(ARequest mReq, boolean isUseMsg) {
			this.isUseMsg = isUseMsg;
			this.mReq = mReq;
		}
		
		/**
		 * 用于解析结果的类，这里返回的boolean类型用来标识是否有数据。
		 * @param what 用来区分成功与否
		 * @return true 有数据，false 没有数据。
		 */
		public abstract void onResult(int what);
		@Override
		public void updateProgress(int progress, String desc) {
		}

		@Override
		public void onGetResult(int resultCode) {
			if (isDestroy) {
				return;
			}
			isInvoking = false;
			onResult(resultCode);
			doNetMsg(resultCode, mReq.hasData(), mReq);
		}
	}
	
//	public abstract class NetCallbackFilter extends NetCallback {
//		
//		boolean isUseMsg = true;
//		public ARequest mReq;
//		
//		public NetCallbackFilter(ARequest mReq) {
//			this.mReq = mReq;
//		}
//		
//		@Override
//		public void onComplete() {
//			
//		}
//		
//		@Override
//		public void onGetResult(int resultCode) {
//			super.onGetResult(resultCode);
//			if (isDestroy) {
//				return;
//			}
//			doNetMsg(resultCode, mReq.hasData(), mReq);
//		}
//	}
	
	protected String msg_no_data = null;
	
	public void doNetMsg(int resultCode, boolean hasData, ARequest aReq) {
		if (isDestroy) {
			return;
		}
		if (resultCode == CommonCode.SUCCESS) {
			if (hasData) {
//				toast(getString(R.string.toast_get_success));
			} else if (aReq.pageNo > 1){
				toast(mCon.getString(R.string.alert_no_more));
				aReq.pageNo --;
			} else {
				toast(mCon.getString(R.string.alert_no_data));
				showZWSJ(mCon.getString(R.string.alert_no_data));
			}
		} else {
			if (aReq.pageNo > 1) {
				aReq.pageNo --;
			}
			if (resultCode == CommonCode.ERROR_NO_NETWORK) {
				toast(CommonCode.ERROR_NO_NETWORK_MSG);
				showZWSJ(CommonCode.ERROR_NO_NETWORK_MSG);
			} else {
				toast(mCon.getString(R.string.alert_get_failed));
				showZWSJ(mCon.getString(R.string.alert_get_failed));
			}
		}
	}
	
	FrameLayout mFramWarn;
	int layoutId;
	
	public void showZWSJ(String msg) {
		if (isDestroy) {
			return;
		}
		if (layoutId <= 0) {
			return;
		}
		layoutId = BaseApplication.app.getZWSJLayoutId();
		if (layoutId <= 0) {
			return;
		}
		if (mFramWarn == null) {
			mFramWarn = (FrameLayout) findView(layoutId);
		}
		if (mFramWarn != null) {
			if (mFramWarn.getChildCount() <= 0) {
				mFramWarn.addView(inflater.inflate(R.layout.common_fram_warn, null));
			}
			mFramWarn.setVisibility(View.VISIBLE);
			((TextView)mFramWarn.findViewById(R.id.common_txt)).setText(msg);
			mFramWarn.bringToFront();
			Log.e("showZWSJ");
		}
	}
	
	public void setZWSJLayoutId(int id) {
		layoutId = id;
	}
	
	protected void disTSFram() {
		if (isDestroy) {
			return;
		}
		Log.e("mFramWarn:" + 1);
		if (mFramWarn != null) {
			Log.e("mFramWarn:" + 2);
			mFramWarn.removeAllViews();
			mFramWarn.setVisibility(View.GONE);
		}
	}
	
	View mProgress;
	public void showWaitView() {
		if (mProgress == null) {
			mProgress = this.findView(BaseApplication.app.getWaitLayoutId());
		}
		if (mProgress == null) {
			return;
		}
		mProgress.setVisibility(View.VISIBLE);
		mProgress.bringToFront();
	}
	public void disWaitView() {
		if (mProgress == null) {
			return;
		}
		mProgress.setVisibility(View.INVISIBLE);
	}
	
	ProgressDialog progress;
	public void showWaitDialog(String msg) {
		if (isDestroy) {
			return;
		}
		if (progress == null) {
			progress = new ProgressDialog(mCon);
		}
		if (progress.isShowing()) {
			return;
		}
		progress.setOnCancelListener(null);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(false);
		progress.setCanceledOnTouchOutside(false);
		progress.setMessage(msg);
		progress.setMax(1000);
		progress.setProgress(1);
		progress.show();
	}
	
	public void dismisWaitDialog() {
		if (isDestroy) {
			return;
		}
		if (progress != null && progress.isShowing()) {
			progress.cancel();
		}
	}
	
	public void finish() {
		mCon.onBackPressed();
	}
	
	@Override
	public void onClick(View v) {
		// Page.getSimpleName()_点击的View.getSimpleName()_IDxxxxxx onClick
//		MobileProbe.onEvent(mCon, this.getClass().getSimpleName() + "_" + v.getClass().getSimpleName() + "_ID" + v.getId() + " onClick", 1);
	}
	
	public void onItemClick(AdapterView<?> adpter, View view, int position, long rowId) {
		// Page.getSimpleName()_List_IDxxxxxx_positionx onItemClick
//		MobileProbe.onEvent(mCon, this.getClass().getSimpleName() + "_List_ID" + view.getId() + "_position" + position + " onItemClick", 1);
	}
	
	protected void recycle(String url) {
		BitmapManager.recycleSpecil(url, setId);
	}
	
	public boolean onBackPressed() {
//		MobileProbe.onExit(mCon);
		return false;
	}
	
	public void destroySelf() {
		if (task != null) {
			task.cancel(true);
			task = null;
		}
		stopTaskImg();
		isDestroy = true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mCon != null) {
//			MobileProbe.onResume(mCon);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mCon != null) {
//			MobileProbe.onPause(mCon);
		}
	}
	
}
