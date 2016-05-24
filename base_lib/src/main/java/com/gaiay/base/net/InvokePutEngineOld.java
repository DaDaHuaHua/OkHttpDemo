package com.gaiay.base.net;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.gaiay.base.common.CommonCode;
import com.gaiay.base.common.ErrorMsg;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;
import com.gaiay.base.util.ToastUtil;

public class InvokePutEngineOld extends NetEngine {

	DefaultHttpClient httpclient = null;
	HttpPut http = null;

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
			Log.e("model.url:" + model.url);
			http = new HttpPut(URI.create(getUrlRequest()));
//			http = new HttpPut(new URI(model.url));
//			List<NameValuePair> nvps = getHttpParams(model.requestValues);
//			http.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			rsp = httpclient.execute(this.http);
			cookies = httpclient.getCookieStore().getCookies();
			if (rsp.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(rsp.getEntity());
				return strResult.trim();
			} else {
				throw new ErrorMsg(CommonCode.ERROR_LINK_FAILD, rsp
					
						 .getStatusLine().toString());
			}
		} catch (ConnectTimeoutException e) {
			throw new ErrorMsg(CommonCode.ERROR_TIME_OUT,
					CommonCode.ERROR_TIME_OUT_MSG);
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
		NetEngine engine = new InvokePutEngineOld();
		engine.model = model;
		return engine;
	}

}
