package com.gaiay.base.framework.fragment;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalBitmap.OnCompleteListener;
import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaiay.base.BaseApplication;
import com.gaiay.base.R;
import com.gaiay.base.net.NetAsynTask;
import com.gaiay.base.net.NetCallback;
import com.gaiay.base.request.ARequest;
import com.gaiay.base.util.FileUtil;
import com.gaiay.base.util.StringUtil;

@SuppressWarnings("deprecation")
public abstract class SimpleActivity extends FragmentActivity implements OnClickListener, OnItemClickListener {
	public SimpleActivity mCon;
	public LayoutInflater inflater;
	public boolean isDestroy = false;
	FinalBitmap fb;
	public int defRes = 0;
	protected View mWarnView;
	public boolean isUseFinishAnimation = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCon = this;
		inflater = getLayoutInflater();
		appIdName = mCon.getString(R.string.app_id_name);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		if (mWarnView == null) {
			mWarnView = findViewById(R.id.warn);
			if (mWarnView != null) {
				mWarnView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mWarnView.getVisibility() == View.VISIBLE
								&& mWarnView.findViewById(R.id.refresh).getVisibility() == View.VISIBLE) {
							onClicknRefresh();
						}
					}
				});
			}
		}
	}

	public void onClicknRefresh() {
	};

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.page_in, R.anim.page_out);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.page_in, R.anim.page_out);
	}

	@Override
	public void finish() {
		super.finish();
		if (isUseFinishAnimation) {
			overridePendingTransition(R.anim.page_in_finish, R.anim.page_out_finish);
		}
	}

	public void finishNoAnim() {
		super.finish();
	}

	@Override
	public void onDestroy() {
		if (fb != null) {
			fb.onDestroy();
		}
		isDestroy = true;
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	public String getLogMsg() {
		return this.getClass().getSimpleName();
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

	String appIdName = null;

	public void invoke(String method, Map<String, String> map, NetCallback callback, ARequest mReq) {
		if (map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.connect(BaseApplication.app.getDefaultUrl(), method, map, mCon, callback, mReq);
	}

	public void invoke(String url, String method, Map<String, String> map, NetCallback callback, ARequest mReq) {
		if (map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.connect(url, method, map, mCon, callback, mReq);
	}

	public void invoke(URL url, Map<String, String> map, NetCallback callback, ARequest mReq) {
		if (map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.connect(url.toString(), null, map, mCon, callback, mReq);
	}

	public void invoke(URL url, Map<String, String> map, NetCallback callback, ARequest mReq, Map<String, byte[]> data) {
		if (map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url.toString(), null, map, callback, mReq, data);
	}

	public void invoke(URL url, Map<String, String> map, NetCallback callback, ARequest mReq, List<Bitmap> data) {
		if (map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url.toString(), null, map, callback, mReq, data);
	}

	public void invoke(String url, Map<String, String> map, NetCallback callback, ARequest mReq, List<Bitmap> data) {
		if (map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url, null, map, callback, mReq, data);
	}

	public void invoke(String url, Map<String, String> map, NetCallback callback, ARequest mReq, String file) {
		if (map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		if (!FileUtil.isFileExists(file)) {
			return;
		}
		LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
		data.put("file", file);
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url, null, map, callback, mReq, data);
	}

	public void invoke(String url, Map<String, String> map, NetCallback callback, ARequest mReq, String file,
			String fileType) {
		if (map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		if (!FileUtil.isFileExists(file)) {
			return;
		}
		LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
		data.put(fileType + "1", file);
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url, null, map, callback, mReq, data);
	}

	public void invoke(String url, Map<String, String> map, NetCallback callback, ARequest mReq, String[] file,
			String fileType) {
		if (map.containsKey(appIdName)) {
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
		NetAsynTask.upload(url, null, map, callback, mReq, data);
	}

	public void invoke(String url, Map<String, String> map, NetCallback callback, ARequest mReq,
			LinkedHashMap<String, String> files) {
		if (map.containsKey(appIdName)) {
			map.remove(appIdName);
		}
		if (files == null || files.size() <= 0) {
			return;
		}
		map.put(appIdName, mCon.getString(R.string.app_cid));
		NetAsynTask.upload(url, null, map, callback, mReq, files);
	}

	ProgressDialog progress;

	public void showWaitDialog(String msg) {
		if (isDestroy) {
			return;
		}
		if (progress == null) {
			progress = new ProgressDialog(mCon);
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

	/**
	 * 加载出错时显示
	 */
	public void showWarn() {
		if (mWarnView != null) {
			mWarnView.setVisibility(View.VISIBLE);
			mWarnView.findViewById(R.id.refresh).setVisibility(View.VISIBLE);
			mWarnView.findViewById(R.id.progress).setVisibility(View.GONE);
			mWarnView.findViewById(R.id.warn_anim).setVisibility(View.GONE);
			mWarnView.findViewById(R.id.img).setVisibility(View.VISIBLE);
			ImageView img = (ImageView) mWarnView.findViewById(R.id.img);
			img.setImageResource(R.drawable.warn_img_data);
		}
	}

	/**
	 * 暂无数据
	 */
	public void showWarn(String errorMsg) {
		if (mWarnView != null) {
			mWarnView.setVisibility(View.VISIBLE);
			mWarnView.findViewById(R.id.refresh).setVisibility(View.VISIBLE);
			mWarnView.findViewById(R.id.progress).setVisibility(View.GONE);
			mWarnView.findViewById(R.id.warn_anim).setVisibility(View.GONE);
			mWarnView.findViewById(R.id.img).setVisibility(View.VISIBLE);
			TextView prompt = (TextView) mWarnView.findViewById(R.id.refresh);
			prompt.setText(TextUtils.isEmpty(errorMsg) ? "" : errorMsg);
		}
	}

	/**
	 * 正在加载数据时显示
	 */
	public void showLoading() {
		if (mWarnView != null) {
			mWarnView.setVisibility(View.VISIBLE);
			mWarnView.findViewById(R.id.refresh).setVisibility(View.GONE);
			mWarnView.findViewById(R.id.progress).setVisibility(View.GONE);
			mWarnView.findViewById(R.id.img).setVisibility(View.GONE);
			mWarnView.findViewById(R.id.warn_anim).setVisibility(View.VISIBLE);
			AnimationDrawable ad = (AnimationDrawable) ((ImageView) mWarnView.findViewById(R.id.warn_anim))
					.getDrawable();
			ad.start();
		}
	}

	/**
	 * 加载完成时
	 */
	public void showLoadingDone() {
		if (mWarnView != null) {
			mWarnView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
	}

	public void onItemClick(AdapterView<?> adpter, View view, int position, long rowId) {
	}
}
