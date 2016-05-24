package com.gaiay.base.net;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.gaiay.base.common.CommonCode;
import com.gaiay.base.common.ErrorMsg;
import com.gaiay.base.util.StringUtil;

public class InvokeGetEngine extends NetEngine {

	DefaultHttpClient httpclient = null;
	HttpGet http = null;

	@Override
	public void shutDownConnect() {
		if (http != null) {
			http.abort();
			http = null;
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
			http = new HttpGet(URI.create(getUrlRequest()));
			NetworkUtil.buildUserAgent(http);
			rsp = httpclient.execute(this.http);
			cookies = httpclient.getCookieStore().getCookies();
			if (rsp.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(rsp.getEntity());
				return strResult.trim();
			} else {
				throw new ErrorMsg(CommonCode.ERROR_LINK_FAILD, rsp.getStatusLine().toString());
			}
		} catch (ConnectTimeoutException e) {
			throw new ErrorMsg(CommonCode.ERROR_TIME_OUT, CommonCode.ERROR_TIME_OUT_MSG);
		} finally {
			if (httpclient != null) {
				httpclient.getConnectionManager().shutdown();
			}
		}
	}

	public List<Cookie> cookies = null;

	public List<Cookie> getHttpClientCookie() {
		return cookies;
	}

	public static NetEngine getEngine(ModelEngine model) {
		NetEngine engine = new InvokeGetEngine();
		engine.model = model;
		return engine;
	}

}
