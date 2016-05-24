package com.gaiay.base.framework.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.gaiay.base.util.ToastUtil;
import com.gaiay.base.util.Utils;

@Deprecated
public abstract class BaseActivity extends Activity implements OnClickListener, OnItemClickListener {
	
	public BaseActivity mCon;
	public String setId;
	public LayoutInflater inflater;
	boolean noRoot = true;
	public final static int MSG_BITMAP_DEF = 19880119;
	public boolean isDestroy = false;
	FinalBitmap fb;
	public int defRes = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCon = this;
		setId = this.getClass().getName() + Utils.getSoleRandom(15);
		inflater = getLayoutInflater();
		appIdName = mCon.getString(R.string.app_id_name);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e(getLogMsg() + ":onActivityResult");
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.e(getLogMsg() + ":onLowMemory");
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
	
	public boolean isInvoking = false;
	String appIdName =  null;
	
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
	
	protected String msg_no_data = null;
	
	public void doNetMsg(int resultCode, boolean hasData, ARequest aReq) {
		if (isDestroy) {
			return;
		}
		if (resultCode == CommonCode.SUCCESS) {
			if (hasData) {
			} else if (aReq.pageNo > 1){
				ToastUtil.showMessage(mCon.getString(R.string.alert_no_more));
				aReq.pageNo --;
			} else {
				ToastUtil.showMessage(mCon.getString(R.string.alert_no_data));
				showZWSJ(mCon.getString(R.string.alert_no_data));
			}
		} else {
			if (aReq.pageNo > 1) {
				aReq.pageNo --;
			}
			if (resultCode == CommonCode.ERROR_NO_NETWORK) {
				ToastUtil.showMessage(CommonCode.ERROR_NO_NETWORK_MSG);
				showZWSJ(CommonCode.ERROR_NO_NETWORK_MSG);
			} else {
				ToastUtil.showMessage(mCon.getString(R.string.alert_get_failed));
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
			mFramWarn = (FrameLayout) findViewById(layoutId);
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
			mProgress = this.findViewById(BaseApplication.app.getWaitLayoutId());
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
	
	@Override
	public void onClick(View v) {
	}
	
	public void onItemClick(AdapterView<?> adpter, View view, int position, long rowId) {
	}
	
	protected void recycle(String url) {
		BitmapManager.recycleSpecil(url, setId);
	}
	
	public void destroySelf() {
		stopTaskImg();
		isDestroy = true;
	}
	
}
