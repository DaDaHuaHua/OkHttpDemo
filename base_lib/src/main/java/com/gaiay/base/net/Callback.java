package com.gaiay.base.net;

/**
 * 用来连网等更新操作的回调对象
 */
public interface Callback {
	/**
	 * 用于处理请求结果
	 * @param resultCode 请求结果码，详情请见{@link com.gaiay.base.common.CommonCode}类
	 * 					 <br>请求成功码为{@link com.gaiay.base.common.CommonCode#SUCCESS}
	 */
	void onGetResult(int resultCode);
	
	/**
	 * 请求过程的进度更新
	 */
	void updateProgress(int progress, String desc);
}
