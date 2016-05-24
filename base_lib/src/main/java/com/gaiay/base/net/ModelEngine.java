package com.gaiay.base.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;

import com.gaiay.base.net.NetAsynTask.CacheHelper;
import com.gaiay.base.request.ARequest;
import com.gaiay.base.util.BitmapUtil;
import com.gaiay.base.util.FileUtil;
import com.gaiay.base.util.StringUtil;

/**
 * 连网实例
 * 
 * @author imuto
 * 
 */
public class ModelEngine {
	
	/**
	 * 请求方法
	 */
	public String method;
	/**
	 * 回调对象
	 */
	public Callback callback;
	/**
	 * 
	 */
	public int what;
	/**
	 * url地址
	 */
	public String url;
	/**
	 * 参数
	 */
	public Map<String, String> requestValues;
	/**
	 * 是否优先缓存
	 */
	public boolean isPriorCache = false;
	/**
	 * 缓存帮助对象
	 */
	public CacheHelper cacheHelper;
	/**
	 * 数据解析类
	 */
	public ARequest req;
	
	
	public boolean isUseDefFileParamName = true;
	
	public String contentType = null;
	
	public List<ModelUpload> dataUpload;
	
	public void setUploadBitmap(String name, Bitmap data) {
		if (data == null) {
			return;
		}
		setUploadData(name, BitmapUtil.convertBitmapToBytes(data));
	}
	public void setUploadStream(String name, InputStream data) {
		if (data == null) {
			return;
		}
		setUploadData(name, FileUtil.convertInToByte(data));
	}
	public void setUploadDataPath(String name, String data) throws IOException {
		if (StringUtil.isBlank(data)) {
			return;
		}
		setUploadData(name, FileUtil.getByteFromFile(data));
	}
	public void setUploadData(String name, byte[] data) {
		if (data != null && StringUtil.isNotBlank(name)) {
			if (dataUpload == null) {
				dataUpload = new ArrayList<ModelUpload>();
			}
			ModelUpload m = new ModelUpload();
			m.name = name;
			m.data = data;
			m.type = 3;
			dataUpload.add(m);
		}
	}
	public void setUploadData(String name, List<Bitmap> data) {
		if (data != null && StringUtil.isNotBlank(name)) {
			if (dataUpload == null) {
				dataUpload = new ArrayList<ModelUpload>();
			}
			dataUpload.clear();
			for (int i = 0; i < data.size(); i++) {
				ModelUpload m = new ModelUpload();
				m.name = name + i;
				m.data = BitmapUtil.convertBitmapToBytes(data.get(i));
				m.type = 3;
				dataUpload.add(m);
			}
		}
	}
	public void setUploadData(List<Bitmap> data) {
		if (data != null) {
			if (dataUpload == null) {
				dataUpload = new ArrayList<ModelUpload>();
			}
			dataUpload.clear();
			for (int i = 0; i < data.size(); i++) {
				ModelUpload m = new ModelUpload();
				m.name = "file" + i;
				m.data = BitmapUtil.convertBitmapToBytes(data.get(i));
				m.type = 3;
				dataUpload.add(m);
			}
		}
	}
	public void setUploadData(Map<String, Bitmap> data) {
		if (data != null) {
			if (dataUpload == null) {
				dataUpload = new ArrayList<ModelUpload>();
			}
			dataUpload.clear();
			for (Map.Entry<String, Bitmap> entry : data.entrySet()) {
				ModelUpload m = new ModelUpload();
				m.name = entry.getKey();
				m.data = BitmapUtil.convertBitmapToBytes(entry.getValue());
				m.type = 3;
				dataUpload.add(m);
			}
		}
	}
	public void setUploadBytesData(List<byte[]> data) {
		if (data != null) {
			if (dataUpload == null) {
				dataUpload = new ArrayList<ModelUpload>();
			}
			dataUpload.clear();
			for (int i = 0; i < data.size(); i++) {
				ModelUpload m = new ModelUpload();
				m.name = "file" + i;
				m.data = data.get(i);
				m.type = 3;
				dataUpload.add(m);
			}
		}
	}
	public void setUploadBytesData(Map<String, byte[]> data) {
		if (data != null) {
			if (dataUpload == null) {
				dataUpload = new ArrayList<ModelUpload>();
			}
			dataUpload.clear();
			for (Map.Entry<String, byte[]> entry : data.entrySet()) {
				ModelUpload m = new ModelUpload();
				m.name = entry.getKey();
				m.data = entry.getValue();
				m.type = 3;
				dataUpload.add(m);
			}
		}
	}
	public void setUploadFilesData(Map<String, String> data) {
		if (data != null) {
			if (dataUpload == null) {
				dataUpload = new ArrayList<ModelUpload>();
			}
			dataUpload.clear();
			for (Map.Entry<String, String> entry : data.entrySet()) {
				ModelUpload m = new ModelUpload();
				m.name = entry.getKey();
				m.path = entry.getValue();
				m.type = 1;
				dataUpload.add(m);
			}
		}
	}
}
