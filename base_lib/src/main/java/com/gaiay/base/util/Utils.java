package com.gaiay.base.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.gaiay.base.BaseApplication;
import com.gaiay.base.net.NetworkUtil;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类,用于存放常用工具
 * 
 * @author iMuto
 */
public class Utils {
	/**
	 * 短时间的展示一个给定消息的Toast
	 * 
	 * @param msg
	 *            消息
	 */
	public static void toast(Context context, String msg) {
		if (msg == null) {
			msg = "";
		}
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 短时间的展示一个给定消息的Toast
	 * 
	 * @param resId
	 */
	public static void toast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 查看给定Service是否正在运行
	 * 
	 * @param context
	 *            运行上下文
	 * @return true正在运行,否则没有运行
	 */
	public static boolean isServiceRun(Context context, String service) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = am.getRunningServices(30);
		for (RunningServiceInfo info : list) {
			if (info.service.getClassName().equals(service)) {
				return true;
			}
		}
		return false;
	}

	public static void hideSoftInput(Activity activity) {
		if (activity.getCurrentFocus() != null) {
			((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 弹起软键盘
	 * 
	 * @param activity
	 * @param view
	 */
	public static void showSoftInput(Activity activity, View view) {
		view.requestFocus();
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param activity
	 * @param view
	 */
	public static void hideSoftInput(Activity activity, View view) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 隐藏软键盘强制
	 * 
	 * @param activity
	 * @param view
	 */
	public static void hideSoftInputAlways(Activity activity, View view) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将px转换为sp
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp转换为px
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 获取指定位数的随机不重复字母数字组合
	 * 
	 * @param length
	 *            随机字母数字长度
	 * @return 随机后的数据
	 */
	public static String getSoleRandom(int length) {
		String re = "";
		try {

			re = NetworkUtil.getWifiMacAddress(BaseApplication.app).replace(":", "");
			int len = length - re.length();
			if (len > 0) {
				String para = System.currentTimeMillis() + "";
				if (para.length() - len >= 0) {
					re = re + para.substring(para.length() - len, para.length());
				} else {
					re = re + para;
					re = re + getRandom(length - re.length());
				}
			} else {
				re.substring(0, re.length() - length);
			}
		} catch (Exception e) {
			re = System.currentTimeMillis() + "";
			re = re + getRandom(length - re.length());
		}
		return re;
	}

	/**
	 * 获取指定长度的随机数
	 * 
	 * @param length
	 *            要获取的随机数长度
	 * @return 获取的结果
	 */
	public static String getRandom(int length) {
		int tmp = 1;
		if (length <= 0) {
			return "";
		}
		for (int i = 0; i < length; i++) {
			tmp = tmp * 10;
		}
		int temp = (int) (tmp * Math.random());
		while (temp == 0 || temp == 100) {
			temp = (int) (tmp * Math.random());
		}
		return temp + "";
	}

	/**
	 * 重新计算ListView高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}
		try {
			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				if (listItem != null) {
					listItem.measure(0, 0);
					totalHeight += listItem.getMeasuredHeight();
				}
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			listView.setLayoutParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断一个String是否为数字
	 * 
	 * @param str
	 *            要判断的String串
	 * @return 是数字返回true，否则返回false;
	 */
	public static boolean isNum(String str) {
		if (StringUtil.isBlank(str)) {
			return false;
		}
		try {
			Pattern p = Pattern.compile("[0-9]+(\\.[0-9]+)?");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断一个String是否为int数据
	 * 
	 * @param str
	 *            要判断的String串
	 * @return 是数字返回true，否则返回false;
	 */
	public static boolean isInteger(String str) {
		if (StringUtil.isBlank(str)) {
			return false;
		}
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static int getInt(String num) {
		int res = 0;
		try {
			res = Integer.parseInt(num);
		} catch (Exception e) {
			res = 0;
		}
		return res;
	}

	public static String getString(String str) {
		return str == null ? "" : str;
	}

	/**
	 * 返回一个小数点后p位的string
	 */
	public static String getNum(double d, int p) {
		if (p < 0) {
			p = 0;
		}
		return String.format("%." + p + "f", d);
	}

	/**
	 * 判断一个String是否为小数
	 * 
	 * @param str
	 *            要判断的String串
	 * @return 是小数返回true，否则返回false;
	 */
	public static boolean isDecimal(String str) {
		if (StringUtil.isBlank(str)) {
			return false;
		}
		try {
			Pattern p = Pattern.compile("[0-9]+\\.{1}[0-9]+");
			Matcher m = p.matcher(str);
			if (m.matches()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 去除字符串中的空格、回车、换行符、制表符
	 */
	public static String getStringWitchOutEnter(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	public static String returnDayHourMinSec(String mill) {
		String str = null;
		if (StringUtil.isBlank(mill)) {
			str = "团购已结束";
		}
		long data = 0;
		try {
			data = Long.parseLong(mill);
			long curr = System.currentTimeMillis();
			if (data < curr) {
				str = "团购已结束";
			} else {
				data = data - curr;
				int day = (int) (data / 1000 / 60 / 60 / 24);
				long d1 = (day * 24L * 60L * 60L * 1000L);
				int hour = (int) ((data - d1) / 1000 / 60 / 60);
				int min = (int) ((data - (hour * 60 * 60 * 1000) - d1) / 1000 / 60);
				/*
				 * str = "团购剩余时间：<font color=\"#ffffff\">" + day + "</font>天<font color=\"#ffffff\">" + hour +
				 * "</font>小时<font color=\"#ffffff\">" + min + "</font>分";
				 */
				str = day + "天" + hour + "小时" + min + "分";
			}
		} catch (Exception e) {
			str = "团购已结束";
		}
		return str;
	}

	public static String MSToTime(long ms) {
		if (ms < 0) {
			return "00:00";
		}
		int hour = (int) (ms / 1000 / 60 / 60);
		int min = (int) ((ms - hour * 60 * 60 * 1000) / 1000 / 60);
		int second = (int) ((ms - hour * 60 * 60 * 1000 - min * 60 * 1000) / 1000);
		String strH = "";
		String strM = "";
		String strS = "";
		if (hour > 0) {
			strH = hour + ":";
		}
		if (min < 10) {
			strM = "0" + min + ":";
		} else {
			strM = min + ":";
		}
		if (second < 10) {
			strS = "0" + second;
		} else {
			strS = second + "";
		}

		return strH + strM + strS;
	}

	/**
	 * 计算折扣
	 * 
	 * @param x
	 *            原价
	 * @param y
	 *            折扣价
	 * @return
	 */
	public static int calculateRebate(float x, float y) {
		Log.d("x=" + x + "; y=" + y);
		return (int) ((y / x) * 10);
	}

	/**
	 * 获取字符串字节长度
	 * 
	 * @param s
	 * @return
	 */
	public static int getWordCount(String s) {
		s = s.replaceAll("[^\\x00-\\xff]", "**");
		int length = s.length();
		return length;
	}

	public static void telNum(String num, Activity act) {
		Uri uri = Uri.parse("tel:" + num);
		Intent it = new Intent(Intent.ACTION_DIAL, uri);
		act.startActivity(it);
	}

	public static boolean isVersionHigher11() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean isVersionHigher12() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean isVersionHigher13() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
	}

	public static boolean isVersionHigher14() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static boolean isVersionHigher15() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
	}

	public static boolean isVersionHigher16() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean isVersionHigher17() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	public static boolean isVersionHigher18() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
	}

	public static boolean isVersionHigher19() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static int getScreenWidth(Context cxt) {
		int windowW;
		WindowManager wm = (WindowManager) cxt.getSystemService(Context.WINDOW_SERVICE);
		try {
			if (Utils.isVersionHigher13()) {
				Point size = new Point();
				wm.getDefaultDisplay().getSize(size);
				windowW = size.x;
			} else {
				windowW = wm.getDefaultDisplay().getWidth();
			}
		} catch (Exception e) {
			windowW = wm.getDefaultDisplay().getWidth();
		}
		return windowW;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static void setClipBoard(CharSequence content) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) BaseApplication.app
					.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("label", content);
			clipboard.setPrimaryClip(clip);
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) BaseApplication.app
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(content);
		}
	}

	/**
	 * 实现粘贴功能
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static CharSequence paste(Context context) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			return clipboard.getText();
		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			return clipboard.getText();
		}
	}

	/**
	 * 把Map转化成字符串，方便打印调试<br>
	 * 该方法只是简单的进行了实现，key只能解析基本类型，value可以解析基本类型和实体，不支持集合
	 */
	public static String mapToString(Map<?, ?> map) {
		StringBuilder sb = new StringBuilder("--------- start map to String\n");
		if (map != null) {
			for (Map.Entry entry : map.entrySet()) {
				sb.append("key: " + entry.getKey() + ", value: ");
				Object obj = entry.getValue();
				if (obj instanceof Number || obj instanceof CharSequence) {
					sb.append(obj + "\n");
				} else {
					sb.append(entityToString(obj, null) + "\n");
				}
			}
		} else {
			sb.append("map is null\n");
		}
		sb.append("--------- end\n");
		return sb.toString();
	}

	/**
	 * 把List转化成字符串，方便打印调试<br>
	 * 该方法只是简单的进行了实现，可以解析基本类型和实体，不支持集合
	 */
	public static String listToString(List list, String[] names) {
		StringBuilder sb = new StringBuilder("--------- start list to String\n");
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Object obj = list.get(i);
				if (obj instanceof Number || obj instanceof CharSequence) {
					sb.append(obj + "\n");
				} else {
					sb.append(entityToString(obj, names) + "\n");
				}
			}
		} else {
			sb.append("list is null\n");
		}
		sb.append("--------- end\n");
		return sb.toString();
	}

	/**
	 * 把实体转化成字符串，方便打印调试<br>
	 * 该方法只是简单的进行了实现，假如某字段也是实体，将会直接进行toString，而不会进一步进行解析
	 */
	public static String entityToString(Object obj, String[] names) {
		try {
			Class clazz = obj.getClass();
			StringBuilder sb = new StringBuilder(clazz.getSimpleName() + " [");
			if (names != null && names.length > 0) {
				for (int j = 0; j < names.length; j++) {
					Field f = null;
					try {
						f = clazz.getDeclaredField(names[j]);
						f.setAccessible(true);
						if (j == 0) {
							sb.append(f.getName() + ": " + f.get(obj));
						} else {
							sb.append(", " + f.getName() + ": " + f.get(obj));
						}
					} catch (NoSuchFieldException e) {
						if (j == 0) {
							sb.append("no Field named '" + names[j] + "'");
						} else {
							sb.append(", no Field named '" + names[j] + "'");
						}
					}
				}
			} else {
				Field[] fields = clazz.getDeclaredFields();
				for (int j = 0; j < fields.length; j++) {
					Field f = fields[j];
					f.setAccessible(true);
					if (j == 0) {
						sb.append(f.getName() + ": " + f.get(obj));
					} else {
						sb.append(", " + f.getName() + ": " + f.get(obj));
					}
				}
			}
			sb.append("]");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "exception, please check";
		}
	}

	/**
	 * 获取显示的 activity 是否是 传入的activity
	 * 
	 * @param activity
	 * @return true 是传入的activity ，false 不是传入 的activity
	 */
	public static boolean getTopActivity(Activity activity) {
		String packageName = activity.getClass().getName();
		ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 应用是否从前台进入后台（如按HOME键）
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isApplicationBroughtToBackground(Context context) {
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasks = am.getRunningTasks(1);
			if (tasks != null && !tasks.isEmpty()) {
				ComponentName topActivity = tasks.get(0).topActivity;
				if (!topActivity.getPackageName().equals(context.getPackageName())) {
					return true;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static byte[] bmpToByteArray(Bitmap paramBitmap, boolean paramBoolean) {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		paramBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
		if (paramBoolean) {
			paramBitmap.recycle();
		}
		byte[] array = localByteArrayOutputStream.toByteArray();
		try {
			localByteArrayOutputStream.close();
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return array;
	}

}
