package com.gaiay.base.net;

import com.gaiay.base.common.CommonCode;
import com.gaiay.base.request.BaseRequest;

/**
 * 用来连网等更新操作的回调对象
 */
public abstract class NetCallback implements Callback {
	
	BaseRequest<?> mReq;
	
	public void setReq(BaseRequest<?> mReq) {
		this.mReq = mReq;
	}
	
	public BaseRequest<?> getReq() {
		return mReq;
	}
	
	/**
	 * 获取成功
	 */
	public abstract void onGetSucc();
	/**
	 * 请求完成
	 */
	public abstract void onComplete();
	
	/**
	 * 获取成功，但是没有获取到想要得到的数据
	 */
	public abstract void onGetFaild();
	
	/**
	 * 获取失败，网络错误等其他原因
	 */
	public abstract void onGetError();

	@Override
	public void onGetResult(int resultCode) {
		onComplete();
		switch (resultCode) {
		case CommonCode.SUCCESS:
			onGetSucc();
			break;
		case CommonCode.ERROR_PARSE_DATA:
			onGetFaild();
			break;
		case CommonCode.ERROR_LINK_FAILD:
		case CommonCode.ERROR_NO_NETWORK:
		case CommonCode.ERROR_TIME_OUT:
		case CommonCode.ERROR_OTHER:
			onGetError();
			break;
		default:
			break;
		}
	}

	@Override
	public void updateProgress(int progress, String desc) {
		
	}
	
}
