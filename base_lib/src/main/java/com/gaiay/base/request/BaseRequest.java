package com.gaiay.base.request;

import java.io.InputStream;

import org.json.JSONException;

import com.gaiay.base.common.CommonCode;
import com.gaiay.base.common.ErrorMsg;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

@SuppressWarnings("deprecation")
public abstract class BaseRequest<T> extends ARequest {
	public T t;
	public T getData() {
		return t;
	}
	public void setT(T t) {
		this.t = t;
	}
	@Override
	public int read(String result, int what, boolean isFromCache) throws ErrorMsg {
		try {
			t = null;
			Log.e(result);
			if (StringUtil.isBlank(result)) {
				return CommonCode.ERROR_PARSE_DATA;
			}
			parseJSON(result);
			parseJsonCommon(result);
			return parseJson(result);
		} catch (Exception e) {
			e.printStackTrace();
			return CommonCode.ERROR_PARSE_DATA;
		}
	}
	/**
	 * 解析JSON数据
	 * @param paramString
	 * @return
	 * @throws JSONException
	 */
	@Deprecated
	public void parseJSON(String paramString) throws JSONException {
		
	}
	public int parseJson(String paramString) throws JSONException {
		return CommonCode.SUCCESS;
	}
	
	/**
	 * 用来解析公共变量
	 * @param paramString
	 * @throws JSONException
	 */
	public void parseJsonCommon(String paramString) throws JSONException {
		parseGeneralInfo(paramString);
	}
	
	@Override
	protected int parseSelfInfo(InputStream result, int what) {
		return 0;
	}
	
	@Override
	public boolean hasData() {
		return t != null;
	}
}
