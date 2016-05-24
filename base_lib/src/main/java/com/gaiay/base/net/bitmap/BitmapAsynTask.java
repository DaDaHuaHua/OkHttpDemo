package com.gaiay.base.net.bitmap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;

import com.gaiay.base.common.CommonCode;
import com.gaiay.base.net.NetworkUtil;
import com.gaiay.base.util.BitmapUtil;
import com.gaiay.base.util.FileUtil;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

/**
 * 用于异步下载图片的封装类
 * @author iMuto
 */
public final class BitmapAsynTask extends AsyncTask<Void, Object, Integer> {

	Context cxt;
	Handler handler;
	int what;
	String url;
	Bitmap bitmap;
	String setId;
	int id;
	BitmapReqModel model;
	
	String path;
	String cacheFolder;
	
	/**
	 * 图片下载工具类的构造方法,根据url参数下载图片到指定的Bitmap中进行更新
	 * @param url 图片的url地址
	 * @param bitmap 要存放图片的bitmap
	 * @param cxt 上下文参数
	 * @param callback 用户监听下载完成的对象
	 */
	public BitmapAsynTask(String url, Context cxt, Handler handler, int what, String setId, int id, String cacheFolder) {
		this.cxt = cxt;
		this.handler = handler;
		this.what = what;
		this.url = url;
		this.setId = setId;
		this.id = id;
		this.cacheFolder = cacheFolder;
	}
	/**
	 * 图片下载工具类的构造方法,根据url参数下载图片到指定的Bitmap中进行更新
	 * @param url 图片的url地址
	 * @param bitmap 要存放图片的bitmap
	 * @param cxt 上下文参数
	 * @param callback 用户监听下载完成的对象
	 */
	public BitmapAsynTask(Context cxt, Handler handler, String setId, String cacheFolder, BitmapReqModel model) {
		this.cxt = cxt;
		this.handler = handler;
		this.what = model.what;
		this.url = model.url;
		this.setId = setId;
		this.id = model.id;
		this.model = model;
		this.cacheFolder = cacheFolder;
	}
	
	boolean isComp = true;
	
	public boolean isComplete() {
		return isComp;
	}
	
	HttpURLConnection conn = null;
	@Override
	protected Integer doInBackground(Void... params) {
		isComp = false;
		if (NetworkUtil.isNetworkValidate(cxt)) {
			if (StringUtil.isBlank(url) || cxt == null || handler == null) {
				return CommonCode.ERROR_PARAMETER;
			}
			InputStream is = null;
			Log.e(url);
			try {
				URL url = new URL(this.url);
				int i = 0;
				while (i < 3) {
					i ++;
					conn = null;
					conn = NetworkUtil.toConn(url);
					if (conn == null) {
						continue;
					}
					conn.connect();
					if ((is = conn.getInputStream()) != null) {
						break;
					}
					conn.disconnect();
					conn = null;
				}
				if (conn == null) {
					return CommonCode.ERROR_OTHER;
				}
				if (is != null) {
					if (FileUtil.isSDCardFree()) {
						path = FileUtil.saveLocalFile(BitmapUtil.getImgLocName(this.url), BitmapManager.cacheFolder, is);
						if (StringUtil.isBlank(path)) {
							bitmap = BitmapFactory.decodeStream(is);
							return CommonCode.ERROR_SAVE_FAILD;
						}
					} else {
						bitmap = BitmapFactory.decodeStream(is);
						return CommonCode.ERROR_SAVE_FAILD;
					}
					return CommonCode.SUCCESS;
				}
				return CommonCode.ERROR_OTHER;
			} catch (Exception e) {
				e.printStackTrace();
				return CommonCode.ERROR_OTHER;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					is = null;
				}
			}
		}
		return CommonCode.ERROR_NO_NETWORK;
	}
	
	@Override
	protected void onCancelled() {
		if (conn != null) {
			conn.disconnect();
		}
		Log.e("ing_onCancelled");
		isComp = true;
		super.onCancelled();
	}
	
//	public AsyncTask<?, ?, ?> execute() {
////		return this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{});
//		return this.execute();
//	}
	
	@Override
	protected void onPostExecute(Integer code) {
		this.url = BitmapUtil.getImgLocName(url);
		Log.e("path:" + path);
		switch (code) {
		case CommonCode.SUCCESS:
			if (StringUtil.isBlank(path)) {
				if (model == null) {
					if (bitmap == null) {
						handler.sendMessage(handler.obtainMessage(what, CommonCode.ERROR_BITMAP_FAILD, 0, "获取失败"));
					} else {
						handler.sendMessage(handler.obtainMessage(what, CommonCode.SUCCESS_BITMAP, id, bitmap));
					}
				} else {
					if (bitmap == null) {
						model.resultCode = CommonCode.ERROR_BITMAP_FAILD;
						model.msg = "获取失败";
					} else {
						model.resultCode = CommonCode.SUCCESS_BITMAP;
						model.bmp = bitmap;
					}
					
					handler.sendMessage(handler.obtainMessage(what, model));
				}
				break;
			}
			BitmapManager.saveLocImgMsg(url);
			if (bitmap == null || bitmap.isRecycled()) {
				bitmap = BitmapManager.createBitmapFromFile(this.url);
			}
			if (BitmapManager.cacheBitmap(bitmap, url, setId)) {
				if (model == null) {
					handler.sendMessage(handler.obtainMessage(what, CommonCode.SUCCESS_BITMAP, id, bitmap));
				} else {
					model.resultCode = CommonCode.SUCCESS_BITMAP;
					model.id = id;
					model.bmp = bitmap;
					handler.sendMessage(handler.obtainMessage(what, model));
				}
			} else {
				if (model == null) {
					handler.sendMessage(handler.obtainMessage(what, CommonCode.ERROR_BITMAP_FAILD, 0, "加载失败"));
				} else {
					model.resultCode = CommonCode.ERROR_BITMAP_FAILD;
					model.msg = "加载失败";
					handler.sendMessage(handler.obtainMessage(what, model));
				}
			}
			break;
		case CommonCode.ERROR_SAVE_FAILD:
			BitmapManager.cacheBitmap(bitmap, url, setId);
			if (model == null) {
				handler.sendMessage(handler.obtainMessage(what, CommonCode.SUCCESS_BITMAP, id, bitmap));
			} else {
				model.resultCode = CommonCode.SUCCESS_BITMAP;
				model.id = id;
				model.bmp = bitmap;
				handler.sendMessage(handler.obtainMessage(what, model));
			}
			break;
		default:
			if (model == null) {
				handler.sendMessage(handler.obtainMessage(what, CommonCode.ERROR_BITMAP_FAILD, 0, "无网络连接"));
			} else {
				model.resultCode = CommonCode.ERROR_BITMAP_FAILD;
				model.msg = "无网络连接";
				handler.sendMessage(handler.obtainMessage(what, model));
			}
			break;
		}
		isComp = true;
	}
}
