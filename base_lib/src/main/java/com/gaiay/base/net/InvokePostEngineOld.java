package com.gaiay.base.net;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.gaiay.base.common.CommonCode;
import com.gaiay.base.common.ErrorMsg;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

public class InvokePostEngineOld extends NetEngine {

	private InvokePostEngineOld() {

	}

	DefaultHttpClient httpclient = null;
	HttpPost post = null;
	HttpGet get = null;

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
		if (StringUtil.isBlank(model.url)) {
			throw new ErrorMsg(CommonCode.ERROR_URL, "");
		}
		String strResult = null;
		HttpResponse rsp = null;
		try {
			httpclient = NetworkUtil.buildClient();
			URL url = new URL(model.url);
			int port = url.getPort();
			if (port == -1)
				port = 80;
			this.post = new HttpPost(new URI(model.url));
			Log.e("url === "+ url.toString());
			StringEntity entity = new StringEntity(getJSONRequest(model.method,
					model.requestValues), "utf-8");
			this.post.setEntity(entity);
			rsp = httpclient.execute(this.post);
			cookies = httpclient.getCookieStore().getCookies();
			if (rsp.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(rsp.getEntity());
				return strResult.trim();
			} else {
				throw new ErrorMsg(CommonCode.ERROR_LINK_FAILD, rsp
						.getStatusLine().toString());
			}
		} catch (ConnectTimeoutException e) {
			Log.e("错误信息== " +e.toString());
			throw new ErrorMsg(CommonCode.ERROR_TIME_OUT,
					CommonCode.ERROR_TIME_OUT_MSG);
		} finally {
			if (httpclient != null) {
				httpclient.getConnectionManager().shutdown();
			}
		}
	}

	public List<Cookie> cookies = null;

	public static NetEngine getEngine(ModelEngine model) {
		NetEngine engine = new InvokePostEngineOld();
		engine.model = model;
		return engine;
	}

	public List<Cookie> getHttpClientCookie() {
		return cookies;
	}

}
