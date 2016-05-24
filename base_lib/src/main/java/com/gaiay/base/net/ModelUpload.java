package com.gaiay.base.net;

import android.graphics.Bitmap;

public class ModelUpload {

	/**
	 * 指明上传的类型，1为path,2为bmp,3为data
	 */
	public int type = 0;
	
	public String path;
	public String contentType = null;
	public Bitmap bmp;
	public byte[] data;
	public String name;
	
	/**
	 * 当为图片的时候    1为  FILE_rWsxHszWlxHl 2为FILE_rWxH 3为FILE_{url}
	 */
	public int picType;
	public int w;
	public int h;
	public String url;
}
