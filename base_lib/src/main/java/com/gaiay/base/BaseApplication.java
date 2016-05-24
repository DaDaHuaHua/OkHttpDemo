package com.gaiay.base;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.util.Xml;

import com.gaiay.base.model.BaseModel;
import com.gaiay.base.util.FileUtil;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

public class BaseApplication extends Application {
	public static BaseApplication app;
	CrashHandler crashHandler;

	@Override
	public void onCreate() {
		super.onCreate();
		synchronized (this) {
			app = this;
		}
		if (isConfigCrashHandle()) {
			crashHandler = CrashHandler.getInstance();
			crashHandler.init(getApplicationContext());
		}
		Log.e("系统允许的最大内存为：" + (float) Runtime.getRuntime().maxMemory() / 1024
				/ 1024 + "M");
	}

	public void createIconOnLuncher(int iconResId, String name, Class<?> clss) {
		SharedPreferences preferences = getSharedPreferences("first",
				Context.MODE_PRIVATE);
		boolean isFirst = preferences.getBoolean("isfrist", true);
		if (isFirst) {
			clearLuncherIcon(name, clss);
			// 创建快捷方式的Intent
			Intent shortcutIntent = new Intent(
					"com.android.launcher.action.INSTALL_SHORTCUT");
			// 不允许重复创建
			shortcutIntent.putExtra("duplicate", false);
			// 需要现实的名称
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

			// 快捷图片
			Parcelable icon = Intent.ShortcutIconResource.fromContext(
					getApplicationContext(), iconResId);

			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);

			Intent intent = new Intent(getApplicationContext(), clss);
			// 下面两个属性是为了当应用程序卸载时桌面 上的快捷方式会删除
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.LAUNCHER");
			// 点击快捷图片，运行的程序主入口
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
			// 发送广播。OK
			sendBroadcast(shortcutIntent);
		}
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("isfrist", false);
		editor.commit();
	}

	public void clearLuncherIcon(String appName, Class<?> clss) {
		Intent intent = new Intent(
				"com.android.launcher.action.UNINSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
		Intent in = new Intent(getApplicationContext(), clss);
		in.setAction("android.intent.action.MAIN");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, in);
		sendBroadcast(intent);
	}

	public boolean isDebug() {
		return true;
	}

	public String getCacheFolder() {
		Log.e("========================》没有设置默认缓存路径！");
		return "";
	}

	public String getDefaultUrl() {
		Log.e("========================》没有设置默认Url地址！");
		return "";
	}

	public int getZWSJLayoutId() {
		Log.e("========================》没有设置默认无数据LayoutId！");
		return 0;
	}

	public int getWaitLayoutId() {
		Log.e("========================》没有设置默认无数据LayoutId！");
		return 0;
	}

	public int getFilletSize() {
		Log.e("========================》没有设置默认圆角大小，使用系统默认的5px！");
		return 5;
	}

	public String getUpdateUrl() {
		Log.e("========================》没有设置默认检查更新URL！");
		return getDefaultUrl();
	}

	public Class<Activity> getUpdateClass() {
		Log.e("========================》没有设置默认检查更新URL！");
		return null;
	}

	public String getUpdateMethod() {
		Log.e("========================》没有设置默认检查更新方法！");
		return "";
	}

	public String getAppName() {
		return getString(R.string.app_name);
	}

	public int getDowningLayoutId() {
		return 0;
	}

	public String getLogPath() {
		return null;
	}

	public BaseModel parseGeneralInfo(String str) {
		BaseModel model = new BaseModel();
		if (StringUtil.isBlank(str)) {
			return model;
		}
		XmlPullParser parser = Xml.newPullParser();
		try {
			try {
				parser.setInput(FileUtil.convertStringToStream(str), "utf-8");
				int event = parser.getEventType();
				while (event != XmlPullParser.END_DOCUMENT) {
					String name = parser.getName();
					switch (event) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if ("rc".equals(name)) {
							model.rc = parser.nextText();
						} else if ("rm".equals(name)) {
							model.rm = parser.nextText();
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					}
					event = parser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	public interface OnCrashHandleListener {
		public Class<?> getReStartClass();

		public boolean processCrash(Throwable ex);
	}
	
	public boolean isConfigCrashHandle() {
		return true;
	}

	public void setOnCrashHandleListener(OnCrashHandleListener l) {
		if (crashHandler != null) {
			crashHandler.setOnCrashHandleListener(l);
		}
	}

}
