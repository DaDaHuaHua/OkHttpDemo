package com.gaiay.base.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gaiay.base.common.CommonCode;
import com.gaiay.base.common.ErrorMsg;
import com.gaiay.base.net.NetAsynTask;
import com.gaiay.base.net.NetCallback;
import com.gaiay.base.request.ARequest;

/**
 * <p>
 * HttpClient 工具类
 * </p>
 * 
 * @author lys 2013-05-07
 * @version 1.0
 */

public class ShortDomainUtil {
	/**
	 * url 和 接口
	 */
	private static String openAPIUrl = "http://api.weibo.com/2/short_url/shorten.json";
	/**
	 * appKey
	 */
	private static String appKey = "1661250408";
	
	private String shortUrl = "";

	/**
	 * 转换长域名为短域名
	 * 
	 * @param openAPIUrl
	 * @param longUrl
	 * @return
	 */
	public String convertDomainToShort(String longUrl, NetCallback callback) {
		if (StringUtil.isBlank(longUrl)) {
			return "";
		}
		try {
			longUrl = URLEncoder.encode(longUrl, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String shortDomain = longUrl;

		StringBuffer url = new StringBuffer();
		url.append(openAPIUrl).append("?source=").append(appKey).append("&url_long=").append(longUrl);
		
		ARequest req = new ARequest() {
			
			boolean isOk = false;
			@Override
			public int read(String result, int what, boolean isFromCache)
					throws ErrorMsg {
				
				Log.e(result);
				try {
					JSONObject jb = new JSONObject(result);
					if (jb.isNull("urls")) {
						return CommonCode.ERROR_OTHER;
					}
					JSONArray ja = jb.getJSONArray("urls");
					JSONObject obj = ja.getJSONObject(0);
					if (!obj.getBoolean("result")) {
						return CommonCode.ERROR_OTHER;
					}
					shortUrl = obj.getString("url_short");
					isOk = true;
					return CommonCode.SUCCESS;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return CommonCode.ERROR_OTHER;
			}
			@Override
			protected int parseSelfInfo(InputStream result, int what) {
				return 0;
			}
			@Override
			public boolean hasData() {
				return isOk;
			}
		};
		
		NetAsynTask.connectByGet(url.toString(), null, callback, req);
		return shortDomain;
	}
	
	public String getShortUrl() {
		return shortUrl;
	}

	// 接口调用返回结果示例
	/*
	 * {"request":"/sinaurl/public/shorten.json","error_code":"21504","error":"Error: Wrong arguments!"
	 * }{"urls":[{"result":true,"url_short":"http://t.cn/SSh2h7","url_long":
	 * "http://www.gaiay.cn","type":0}]}
	 */

	public static String getOpenAPIUrl() {
		return openAPIUrl;
	}

	public static void setOpenAPIUrl(String openAPIUrl) {
		ShortDomainUtil.openAPIUrl = openAPIUrl;
	}

	public static String getAppKey() {
		return appKey;
	}

	public static void setAppKey(String appKey) {
		ShortDomainUtil.appKey = appKey;
	}
}
