package com.gaiay.base.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import com.gaiay.base.BaseApplication;
import com.gaiay.base.BaseConstants;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class NetworkUtil {
	private static final String TAG = "Gaiay_NetUtil";

	/**
	 * 标识APN的类型
	 * 
	 * @author iMuto
	 * 
	 */
	public enum APNType {
		CMWAP, CMNET, Unknow, CTWAP, CTNET, _3GNET, _3GWAP;
	}

	/**
	 * 移动wap网代理地址
	 */
	public static final String CMPROXY = "10.0.0.172";
	/**
	 * 联通wap网代理地址
	 */
	public static final String CTPROXY = "10.0.0.200";
	/**
	 * 使用的端口号
	 */
	public static final String PROT = "80";
	/**
	 * APN的存放URI
	 */
	public static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

	/**
	 * 服务器连接超时(3秒)
	 */
	public static final int TIME_OUT_SHORT = 10 * 1000;

	/**
	 * 服务器连接超时(6秒)
	 */
	public static final int TIME_OUT_LONG = 20 * 1000;

	/**
	 * 获取当前网络的APN类型
	 * 
	 * @return 当前网络的APN类型
	 */
	public static APNType getCurrentUsedAPNType() {
		return getCurrentUsedAPNType(BaseApplication.app);
	}

	/**
	 * 获取当前网络的APN类型
	 * 
	 * @param cxt
	 *            上下文参数
	 * @return 当前网络的APN类型
	 */
	public static APNType getCurrentUsedAPNType(Context cxt) {
		try {
			ContentResolver cr = cxt.getContentResolver();
			Cursor cursor = cr.query(PREFERRED_APN_URI, new String[] { "_id", "name", "apn", "proxy", "port" }, null,
					null, null);

			cursor.moveToFirst();
			if (cursor.isAfterLast()) {
				return APNType.Unknow;
			}
			String id = cursor.getString(0);
			String name = cursor.getString(1);
			String apn = cursor.getString(2);
			String proxy = cursor.getString(3);
			String _prot = cursor.getString(4);
			Log.d(TAG, id + " proxy:" + proxy + " prot:" + _prot + " apn:" + apn);
			cursor.close();
			if ((("CTWAP".equals(apn.toUpperCase()) || "CTWAP".equals(name.toUpperCase())) && !StringUtil.isBlank(proxy)
					&& !StringUtil.isBlank(_prot)))
				return APNType.CTWAP;
			else if ((("CTNET".equals(apn.toUpperCase()) || "CTNET".equals(name.toUpperCase()))
					&& StringUtil.isBlank(proxy) && StringUtil.isBlank(_prot)))
				return APNType.CTNET;
			else if (("CMWAP".equals(apn.toUpperCase()) || "CMWAP".equals(name.toUpperCase()))
					&& !StringUtil.isBlank(proxy) && !StringUtil.isBlank(_prot))
				return APNType.CMWAP;
			else if (("CMNET".equals(apn.toUpperCase()) || "CMNET".equals(name.toUpperCase()))
					&& StringUtil.isBlank(proxy) && StringUtil.isBlank(_prot))
				return APNType.CMNET;
			else if (("3GWAP".equals(apn.toUpperCase()) || "3GWAP".equals(name.toUpperCase()))
					&& !StringUtil.isBlank(proxy) && !StringUtil.isBlank(_prot))
				return APNType._3GWAP;
			else if (("3GNET".equals(apn.toUpperCase()) || "3GNET".equals(name.toUpperCase()))
					&& StringUtil.isBlank(proxy) && StringUtil.isBlank(_prot))
				return APNType._3GNET;
			else
				return APNType.Unknow;
		} catch (Exception ep) {
			Log.e(TAG, "getCurrentUsedAPNTypeException");
			return APNType.Unknow;
		}
	}

	public static boolean isWifi() {
		return isWifi(BaseApplication.app);
	}

	public static boolean isWifi(Context cxt) {
		return getNetworkType(cxt).equals("WIFI");
	}

	public static boolean is2G(Context cxt) {
		return getNetworkType(cxt).equals("2G");
	}

	public static boolean is3G(Context cxt) {
		return getNetworkType(cxt).equals("3G");
	}

	public static boolean is4G(Context cxt) {
		return getNetworkType(cxt).equals("4G");
	}

	public static String getNetworkType(Context context) {
		String strNetworkType = "";
		NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				strNetworkType = "WIFI";
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				String _strSubTypeName = networkInfo.getSubtypeName();
				// TD-SCDMA networkType is 17
				int networkType = networkInfo.getSubtype();
				switch (networkType) {
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by 11
					strNetworkType = "2G";
					break;
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by 14
				case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by 12
				case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by 15
					strNetworkType = "3G";
					break;
				case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by 13
					strNetworkType = "4G";
					break;
				default:
					// http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
					if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA")
							|| _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
						strNetworkType = "3G";
					} else {
						strNetworkType = _strSubTypeName;
					}

					break;
				}
			}
		}
		return strNetworkType;
	}

	/**
	 * 使用给定的conn重新设置,连接指定url
	 * 
	 * @param url
	 *            指定连接的url地址
	 * @param conn
	 *            指定的conn
	 * @throws IOException
	 *             可能出现的Exception
	 */
	public static HttpURLConnection toConn(URL url) throws IOException {
		return toConn(url, BaseApplication.app);
	}

	/**
	 * 使用给定的conn重新设置,连接指定url
	 * 
	 * @param url
	 *            指定连接的url地址
	 * @param conn
	 *            指定的conn
	 * @param cxt
	 *            上下文参数
	 * @throws IOException
	 *             可能出现的Exception
	 */
	public static HttpURLConnection toConn(URL url, Context cxt) throws IOException {
		APNType apnType = getCurrentUsedAPNType();
		HttpURLConnection conn;
		Proxy proxy = null;
		if (!NetworkUtil.isWifi(cxt)) {
			if (APNType.CTWAP.equals(apnType)) {
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(CTPROXY, Integer.parseInt(PROT)));
			} else if (APNType.CMWAP.equals(apnType)) {
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(CMPROXY, Integer.parseInt(PROT)));
			}
		}
		if (proxy != null) {
			conn = (HttpURLConnection) url.openConnection(proxy);
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}
		conn.setConnectTimeout(TIME_OUT_SHORT);
		conn.setReadTimeout(TIME_OUT_LONG);
		return conn;
	}

	/**
	 * 创建Client
	 * 
	 * @param ctx
	 *            上下文参数
	 * @return 创建成功后的Client
	 */
	public static DefaultHttpClient buildClient() {
		return buildClient(BaseApplication.app);
	}

	/**
	 * 创建Client
	 * 
	 * @param ctx
	 *            上下文参数
	 * @return 创建成功后的Client
	 */
	public static DefaultHttpClient buildClient(Context cxt) {

		HttpHost proxy = null;
		APNType apnType = getCurrentUsedAPNType();
		if (APNType.CTWAP.equals(apnType)) {
			proxy = new HttpHost(CTPROXY, Integer.parseInt(PROT));
		} else if (APNType.CMWAP.equals(apnType)) {
			proxy = new HttpHost(CMPROXY, Integer.parseInt(PROT));
		}
		HttpParams httpParameters = new BasicHttpParams();
		HttpProtocolParams.setUserAgent(httpParameters, BaseConstants.USER_AGENT);
		int timeoutConnection = 60 * 1000;
		int timeoutSocket = 60 * 1000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		HttpConnectionParams.setStaleCheckingEnabled(httpParameters, false);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		HttpRequestRetryHandler retry = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int count, HttpContext context) {
				if (count >= 5) {
					return false;
				}
				if (exception instanceof NoHttpResponseException) {
					return true;
				}
				if (exception instanceof SSLHandshakeException) {
					return false;
				}
				HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
				if (idempotent) {
					return true;
				} else {
					return false;
				}
			}
		};
		httpclient.setHttpRequestRetryHandler(retry);
		if (!isWifi()) {
			if (proxy != null) {
				httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
		}

		return httpclient;
	}

	public static void buildUserAgent(HttpRequestBase http) {
		http.setHeader("User-Agent", BaseConstants.USER_AGENT);
	}

	/**
	 * 检查网络是否可用
	 * 
	 * @return true可用,false不可用
	 */
	public static boolean isNetworkValidate(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()
					&& cm.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) {
				return cm.getActiveNetworkInfo().isAvailable();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取手机Wifi的mac地址
	 * 
	 * @return mac地址,获取失败返回null
	 */
	public static String getWifiMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifi.getConnectionInfo().getMacAddress();
	}

}
