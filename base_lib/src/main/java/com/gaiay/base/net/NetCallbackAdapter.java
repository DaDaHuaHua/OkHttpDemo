package com.gaiay.base.net;

/**
 * NetCallback一个空的实现类，用于不想重写4个方法的情况，实例化这个类，然后重写对应的方法即可
 */
public abstract class NetCallbackAdapter extends NetCallback {

	@Override
	public void onGetSucc() {}

	@Override
	public void onComplete() {}

	@Override
	public void onGetFaild() {}

	@Override
	public void onGetError() {}

}
