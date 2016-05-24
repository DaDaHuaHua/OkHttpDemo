package com.gaiay.base.net;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import android.os.SystemClock;

import com.gaiay.base.util.BitmapUtil;
import com.gaiay.base.util.FileUtil;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

public class UploadEngine extends NetEngine {

	HttpClient httpclient = null;
	HttpPost post = null;

	@Override
	public void shutDownConnect() {
		if (post != null) {
			post.abort();
			post = null;
		}
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
			httpclient = null;
		}
	}

	@Override
	public String getRequest() throws Throwable {
		httpclient = NetworkUtil.buildClient();
		this.post = new HttpPost(model.url);
		// post.setHeader("Accept", "image/jpeg");
		// post.setHeader("Content-Type", "multipart/form-data");
		MultipartEntity multipart = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		if (model.requestValues != null) {
			for (Map.Entry<String, String> entry : model.requestValues
					.entrySet()) {
				Log.e("map:" + entry.getKey() + ":" + entry.getValue());
				multipart.addPart(
						entry.getKey(),
						new StringBody(entry.getValue(), Charset
								.forName("utf-8")));
			}
		}
		if (model.requestValues.containsKey("clientId")) {
			String name = SystemClock.currentThreadTimeMillis()+".jpg";
			if (model.dataUpload != null) {
				for (int i = 0; i < model.dataUpload.size(); i++) {
					ModelUpload m = model.dataUpload.get(i);
					switch (m.picType) {
					case 0:
						multipart.addPart("FILE", new FileBody(new File(m.path)));
						break;
					case 1:   // FILE_rWsxHszWlxHl
						multipart.addPart("FILE_r"+ m.w+"x"+  m.w+"z"+  m.h+"x"+ m.h, new FileBody(new File(m.path), name ,"",""));
						break;
					case 2://2为FILE_rWxH
						multipart.addPart("FILE_r"+ m.w+"x"+ m.h, new FileBody(new File(m.path)));
						break;
					case 3://FILE_{url}
						multipart.addPart("FILE_"+m.url , new FileBody(new File(m.path)));
						break;
					}
				}
			}
		}else{
		if (model.dataUpload != null) {
			for (int i = 0; i < model.dataUpload.size(); i++) {
				ModelUpload m = model.dataUpload.get(i);
				ContentBody body = null;
				switch (m.type) {
				case 1:
					if (StringUtil.isBlank(m.path)
							|| !FileUtil.isFileExists(m.path)) {
						continue;
					}
					File file = new File(m.path);
					if (StringUtil.isBlank(m.contentType)) {
						body = new FileBody(file);
					} else {
						body = new FileBody(file, m.contentType);
					}
					multipart
							.addPart(m.name, body);
					Log.e("files:" + m.name + "  " + file.length());
					break;
				case 2:
					if (m.bmp == null || m.bmp.isRecycled()) {
						continue;
					}
					if (StringUtil.isBlank(m.contentType)) {
						body = new ByteArrayBody(
								BitmapUtil.convertBitmapToBytes(m.bmp), "file2");
					} else {
						body = new ByteArrayBody(
								BitmapUtil.convertBitmapToBytes(m.bmp),
								m.contentType, "file2");
					}
					multipart.addPart(model.isUseDefFileParamName ? "image" + i
							: m.name, body);
					i++;
					Log.e("model(i：" + i + "  " + m.name + "):" + m.bmp);
					break;
				case 3:
					if (m.data == null) {
						continue;
					}
					if (StringUtil.isBlank(m.contentType)) {
						body = new ByteArrayBody(m.data, "file2");
					} else {
						body = new ByteArrayBody(m.data, m.contentType, "file2");
					}
					multipart.addPart(model.isUseDefFileParamName ? "image" + i
							: m.name, body);
					i++;
					Log.e("model(i：" + i + "  " + m.name + "):" + m.data.length);
					break;
				default:
					break;
				}
			}
		}
		}
		post.setEntity(multipart);
		Log.e("Upload Start!!!!!");
		HttpResponse response = httpclient.execute(post);
		if (response.getStatusLine().getStatusCode() == 200) {
			return EntityUtils.toString(response.getEntity());
		}
		return null;
	}

	public static NetEngine getEngine(ModelEngine model) {
		UploadEngine engine = new UploadEngine();
		engine.model = model;
		return engine;
	}

}
