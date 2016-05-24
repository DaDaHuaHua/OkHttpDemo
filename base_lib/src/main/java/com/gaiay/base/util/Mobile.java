package com.gaiay.base.util;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

public class Mobile {
	private static String imei = null;

	/**
	 * 获取手机IMEI
	 * 
	 * @return 获取成功返回IMEI,获取失败返回null;
	 */
	public static String getIMEI(Context context) {
		if (StringUtil.isBlank(imei)) {
			try {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);
				imei = tm.getDeviceId();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return imei;
	}

	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e("Exception：" + e);
		}
		return versionName;
	}

	/**
	 * 返回当前程序版本号
	 */
	public static int getAppVersionCode(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (Exception e) {
			Log.e("Exception：" + e);
		}
		return 1;
	}

	private static String imsi = null;

	/**
	 * 获取手机IMSI
	 * 
	 * @return 获取成功返回imsi,获取失败返回null;
	 */
	public static String getIMSI(Context context) {
		if (StringUtil.isBlank(imsi)) {
			try {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);
				imsi = tm.getSimSerialNumber();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return imsi;
	}

	private static String phoneNum = null;

	/**
	 * 获取手机号码
	 * 
	 * @return 获取手机号码,成功返回手机号,失败返回null
	 */
	public static String getPhoneNumber(Context context) {
		if (StringUtil.isBlank(phoneNum)) {
			try {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);
				phoneNum = tm.getLine1Number();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return phoneNum;
	}

	/**
	 * 获取sim卡类型
	 * 
	 * @return 1为中国移动,2为中国联通,3为中国电信,4为其他
	 */
	public static int getSimType(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telManager.getSimOperator();
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002")) {
				return 1;
			} else if (operator.equals("46001")) {
				return 2;
			} else if (operator.equals("46003")) {
				return 3;
			}
		}
		return 4;
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param activity
	 *            要获取屏幕宽度的Activity
	 * @return 屏幕的宽度
	 */
	public static int getScreenWidth(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param activity
	 *            要获取屏幕高度的Activity
	 * @return 屏幕的高度
	 */
	public static int getScreenHeight(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}

	/**
	 * 获取系统固件版本,如1.5
	 */
	public static String getOSVersion() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取api版本,如1.5对应的3
	 */
	public static String getSDKVersion() {
		return Build.VERSION.SDK;
	}

	/**
	 * 获取手机型号
	 */
	public static String getMobileModel() {
		return Build.MODEL.replaceAll(" ", "");
	}
}
