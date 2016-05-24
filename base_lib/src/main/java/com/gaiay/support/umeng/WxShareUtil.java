package com.gaiay.support.umeng;

import com.gaiay.base.util.BitmapUtil;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.ToastUtil;
import com.gaiay.base.util.Utils;
import com.tencent.mm.sdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage.IMediaObject;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 用来处理微信的的分享到好友和到朋友圈
 * 
 * @author Administrator
 *
 */
public class WxShareUtil {

	private static WxShareUtil instance = null;
	public IWXAPI api;
	public boolean isInitWx = false;
	IMediaObject wxObj = null;
	WXMediaMessage msg = null;
	String appId;
	Bitmap bmp;
	private final int THUMB_SIZE = 150;
	private int THUMB_W = 150;
	private int THUMB_H = 150;

	private WxShareUtil() {
	};

	public static WxShareUtil getInstance(String wxAppID) {
		if (instance == null) {
			synchronized (ShareUtil.class) {
				if (instance == null) {
					instance = new WxShareUtil();
				}
			}
		}
		instance.appId = wxAppID;
		return instance;
	}

	public void initWx(Context context) {
		if (isInitWx) {
			return;
		}
		isInitWx = true;
		api = WXAPIFactory.createWXAPI(context, appId, true);
		api.registerApp(appId);
		if (!api.isWXAppInstalled()) {
			Toast.makeText(context, "你还没有安装微信", Toast.LENGTH_SHORT).show();
			return;
		} else if (!api.isWXAppSupportAPI()) {
			Toast.makeText(context, "你安装的微信版本不支持当前API", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	public void openShare(Context context, boolean toCircle) {
		THUMB_W = THUMB_SIZE;
		THUMB_H = THUMB_SIZE;
		initWx(context);
		sendByWX(toCircle);
	}

	public void openShare(Context context, boolean toCircle, int w, int h) {
		THUMB_W = w;
		THUMB_H = h;
		initWx(context);
		sendByWX(toCircle);
	}

	public void openShareFromWx(Context context, Bundle b) {
		THUMB_W = THUMB_SIZE;
		THUMB_H = THUMB_SIZE;
		initWx(context);
		sendFromWX(b);
	}

	public void openShareFromWx(Context context, int w, int h, Bundle b) {
		THUMB_W = w;
		THUMB_H = h;
		initWx(context);
		sendFromWX(b);
	}

	private void sendByWX(boolean toCircle) {

		Log.e("sendByWX");

		if (bmp != null && !bmp.isRecycled()) {
			Log.e("sendByWX2");
			msg.thumbData = processBitmap(bmp, THUMB_W, THUMB_H); // 设置缩略图
		}
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = toCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}

	private void sendFromWX(Bundle b) {
		Log.e("sendFromWX");
		if (bmp != null && !bmp.isRecycled()) {
			Log.e("sendFromWX2");
			msg.thumbData = processBitmap(bmp, THUMB_W, THUMB_H); // 设置缩略图
		}
		GetMessageFromWX.Resp req = new GetMessageFromWX.Resp();
		req.transaction = new GetMessageFromWX.Req(b).transaction;
		req.message = msg;
		api.sendResp(req);
	}

	public static byte[] processBitmap(Bitmap bmp, int w, int h) {
		Log.e("bmp:" + bmp + "  " + BitmapUtil.getBitmapSize(bmp));
		if (bmp == null) {
			return null;
		}
		float a = 1.0f;
		byte[] b = Utils.bmpToByteArray(bmp, true);
		while (b.length > 32 * 1024) {
			w = (int) (w * a);
			h = (int) (h * a);
			if (bmp != null && !bmp.isRecycled()) {
				bmp.recycle();
				bmp = null;
			}
			bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
			int width = bmp.getWidth();
			int height = bmp.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap thumbBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			b = Utils.bmpToByteArray(thumbBmp, true);
			a = a - 0.05f;
			Log.e("a:" + a + " b:" + b.length);
		}
		return b;
	}
	// public static byte[] processBitmap(Bitmap bmp, int w, int h) {
	// Log.e("bmp:" + bmp + " " + BitmapUtil.getBitmapSize(bmp));
	// if (bmp == null) {
	// return null;
	// }
	// float a = 1.0f;
	// byte[] b = Util.bmpToByteArray(bmp, true);
	// while (b.length > 32 * 1024) {
	// bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
	// Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, ((int)(w * a)),
	// ((int)(h * a)), true);
	// b = Util.bmpToByteArray(thumbBmp, true);
	// a = a - 0.05f;
	// Log.e("a:" + a + " b:" + b.length);
	// }
	// return b;
	// }

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	/**
	 * 设置分享内容, 当支持微信分享时必须使用此方法
	 * 
	 * @param des
	 *            分享描述信息
	 * @param img
	 *            要分享的图片， img 只能是Bitmap, 和String UIL类型不需要分享图片时可以传入null
	 */
	public void setContentNormal(String title, String des, String url, Bitmap img) {
		wxObj = new WXWebpageObject();
		msg = new WXMediaMessage(wxObj);
		if (!TextUtils.isEmpty(title)) {
			msg.title = title;
		}
		if (!TextUtils.isEmpty(des)) {
			msg.description = des;
		}
		if (!TextUtils.isEmpty(url)) {
			((WXWebpageObject) wxObj).webpageUrl = url;
		}
		bmp = img;
	}

	/**
	 * 设置分享内容, 当支持微信音频分享时必须使用此方法。当为视频时，在微信分享中sharurl将不被使用。当为其他分享时，url将不被使用。
	 * 
	 * @param title
	 *            名称
	 * @param des
	 *            描述
	 * @param url
	 *            多媒体数据地址
	 * @param sharUrl
	 *            多媒体网页地址
	 * @param img
	 *            图片
	 */
	public void setContentAudio(String title, String des, String url, String sharUrl, Bitmap img) {
		msg = new WXMediaMessage();
		if (!TextUtils.isEmpty(title)) {
			msg.title = title;
		}
		if (!TextUtils.isEmpty(url)) {
			wxObj = new WXMusicObject();
			((WXMusicObject) wxObj).musicDataUrl = url;
			((WXMusicObject) wxObj).musicLowBandDataUrl = url;
			((WXMusicObject) wxObj).musicUrl = sharUrl;
			((WXMusicObject) wxObj).musicLowBandUrl = sharUrl;
			msg.mediaObject = wxObj;
			msg.description = des;
			bmp = img;
		} else {
			ToastUtil.showMessage("您的分享内容不正确，请稍候再试.");
		}
	}

	/**
	 * 设置分享内容, 当支持微信视频分享时必须使用此方法。
	 * 
	 * @param title
	 *            名称
	 * @param des
	 *            描述
	 * @param url
	 *            多媒体数据地址
	 * @param img
	 *            图片
	 */
	public void setContentVideo(String title, String des, String url, String sharUrl, Bitmap img) {
		msg = new WXMediaMessage();
		if (!TextUtils.isEmpty(title)) {
			msg.title = title;
		}
		if (!TextUtils.isEmpty(url)) {
			wxObj = new WXVideoObject();
			((WXVideoObject) wxObj).videoLowBandUrl = url;
			((WXVideoObject) wxObj).videoUrl = url;
			msg.mediaObject = wxObj;
			msg.description = des;
			bmp = img;
		} else {
			ToastUtil.showMessage("您的分享内容不正确，请稍候再试.");
		}
	}

	public void setContentImg(Bitmap img) {
		if (img != null) {
			wxObj = new WXImageObject(img);
			msg = new WXMediaMessage();
			msg.mediaObject = wxObj;
			bmp = img;
		} else {
			ToastUtil.showMessage("您的分享内容不正确，请稍候再试.");
		}
	}

	public void setContentImg(Bitmap img, Bitmap img2) {
		if (img != null) {
			wxObj = new WXImageObject(img2);
			msg = new WXMediaMessage();
			msg.mediaObject = wxObj;
			bmp = img;
		} else {
			ToastUtil.showMessage("您的分享内容不正确，请稍候再试.");
		}
	}
}
