package com.gaiay.base.widget.listview;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

public class BitmapConfig {
	public Map<Integer, Integer[]> imgs = new HashMap<Integer, Integer[]>();
	/**
	 * 默认图片resId<br>
	 * 注：如果defaultImageRes和defaultImageBitmap都进行了设置，以defaultImageBitmap为主
	 */
	public int defaultImageRes;
	/**
	 * 默认图片Bitmap<br>
	 * 注：如果defaultImageRes和defaultImageBitmap都进行了设置，以defaultImageBitmap为主
	 */
	public Bitmap defaultImageBitmap;
	/**
	 * 图片缓存目录
	 */
	public String cacheFolder;
	
	/**
	 * 设置图片的尺寸
	 * 
	 * @param resId
	 * @param width
	 * @param height
	 */
	public void setImage(int resId, int width, int height) {
		imgs.put(resId, new Integer[] { width, height });
	}
}
