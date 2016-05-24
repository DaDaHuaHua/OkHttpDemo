package com.gaiay.support.update;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gaiay.base.BaseApplication;

public class NetworkUtil {
	
	public static boolean isWifi() {
		return isWifi(BaseApplication.app);
	}
	public static boolean isWifi(Context cxt) {
		ConnectivityManager mConnectivity = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		int netType = info.getType();
		if (ConnectivityManager.TYPE_WIFI == netType) {
			return info.isConnectedOrConnecting();
		}
		return false;
	}
	
	/**
	 * 检查网络是否可用
	 * 
	 * @return true可用,false不可用
	 */
	public static boolean isNetworkValidate(Context context) {
		try{
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm.getActiveNetworkInfo() != null
					&& cm.getActiveNetworkInfo().isConnected()
					&& cm.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) {
				return cm.getActiveNetworkInfo().isAvailable();
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
		
	}
	
}
