package com.gaiay.base.net.bitmap;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

/**
 * 用于缓存图片的,管理图片的封装对象
 * @author iMuto
 */
public class BitmapModel {
	/**
	 * url地址
	 */
	private String url;
	/**
	 * bitmap
	 */
	private WeakReference<Bitmap> bitmap;
	/**
	 * bitmap大小
	 */
	private int size;
	/**
	 * bitmapModel的组ID
	 */
	private List<String> SetId = new ArrayList<String>();

	/**
	 * 获取图片的URL
	 * @return 图片URL或者Null
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * 设置此model的图片URL
	 * @param url 图片URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 图片的Bitmap
	 * @return Bitmap或者NUll
	 */
	public Bitmap getBitmap() {
		if (bitmap != null) {
			return bitmap.get();
		}
		return null;
	}
	/**
	 * 设置图片的Bitmap
	 * @param bitmap bitmap参数
	 */
	public void setBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			this.bitmap = null;
			return;
		}
		this.bitmap = new WeakReference<Bitmap>(bitmap);
	}
	/**
	 * 获取此Model中包裹的Bitmap大小
	 * @return Bitmap大小
	 */
	public int getSize() {
		return size;
	}
	/**
	 * 设置此Model中包裹的bitmap大小
	 * @param size Bitmap大小
	 */
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * 设置此Model的组ID
	 * @param setId
	 */
	public void setSetId(String setId) {
		Log.e("setId:" + setId);
		if (StringUtil.isBlank(setId)) {
			return;
		}
		SetId.add(setId);
	}
	
	public boolean recycle(String sd) {
		if (bitmap == null) {
			bitmap = null;
			SetId.clear();
			Log.e("id:" + sd);
			return true;
		}
		if (bitmap != null) {
			Bitmap bmp = bitmap.get();
			if (bmp == null || bmp.isRecycled()) {
				bitmap = null;
				SetId.clear();
				Log.e("id2:" + sd);
				return true;
			}
		}
		if (StringUtil.isBlank(sd)) {
			SetId.clear();
			Bitmap bmp = bitmap.get();
			if (bmp != null) {
				bmp.recycle();
				bitmap.clear();
				bmp = null;
			}
			Log.e("id3:" + sd);
			return true;
		}
		SetId.remove(sd);
		if (SetId.size() <= 0) {
			SetId.clear();
			if (bitmap != null) {
				Bitmap bmp = bitmap.get();
				if (bmp != null) {
					bmp.recycle();
				}
				bitmap.clear();
				bmp = null;
			}
			Log.e("id4:" + sd);
			return true;
		}
		return false;
	}
}
