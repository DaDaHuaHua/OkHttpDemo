package com.gaiay.base.request;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.gaiay.base.BaseApplication;
import com.gaiay.base.common.CommonCode;
import com.gaiay.base.common.ErrorMsg;
import com.gaiay.base.model.BaseModel;
import com.gaiay.base.net.NetAsynTask.CacheHelper;
import com.gaiay.base.util.FileUtil;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

/**
 * 连网请求的数据封装父类,所有的连网结果都应由其子类进行处理
 * 
 * @author iMuto
 */
@Deprecated
public abstract class ARequest {

	private static final String TAG = "Gaiay_ARequest";
	protected XmlPullParser parser;
	
	public int pageNo = 1;
	public int pageSize = 20;
	protected int totalCount;
	CacheHelper cacheHelper;
	public BaseModel dataGeneralInfo;
	private String url;
	
	public ARequest() {
		parser = Xml.newPullParser();
	}
	public ARequest(CacheHelper cache) {
		parser = Xml.newPullParser();
		cacheHelper = cache;
	}
	public int getTotalCount(){
		return totalCount;
	}
	
	public int read(String result, int what, boolean isFromCache) throws ErrorMsg {
		try {
			Log.e("==================================");
			Log.e(result);
			Log.e("==================================");
			parseGeneralInfo(result);
			if (StringUtil.isNotBlank(result)) {
				result = result.replaceAll("<br />", "");
				result = result.replaceAll("<br/>", "");
			}
			if (isFromCache) {
				return parseSelfInfo(FileUtil.convertStringToStream(result), what);
			}
			if (isToParse(result)) {
				return parseSelfInfo(FileUtil.convertStringToStream(result), what);
			} else {
				return CommonCode.SUCCESS;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return CommonCode.ERROR_PARSE_DATA;
		}
	}
	
	/**
	 * @param result  inputstream 形式的数据(从服务器得到)
	 * @param what  类型标记
	 * @return
	 */
	protected int parseSelfInfo(InputStream result, int what) {
		return CommonCode.SUCCESS;
	}

	/**
	 * 用于解析公用变量(只用在必要情况,一般情况不用)
	 * @param result
	 */
	protected void parseGeneralInfo(String result) {
		dataGeneralInfo = BaseApplication.app.parseGeneralInfo(result);
	}
	
	public abstract boolean hasData();
	
	public boolean isToParse(String result) {
		return true;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
