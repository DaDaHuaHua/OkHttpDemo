package com.gaiay.support.umeng;

import org.json.JSONObject;

import com.gaiay.base.BaseApplication;
import com.gaiay.base.BaseConstants;
import com.gaiay.base.R;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;
import com.gaiay.base.util.ToastUtil;
import com.gaiay.base.util.Utils;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage.IMediaObject;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.bean.CustomPlatform;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMInfoAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.OnCustomPlatformClickListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMediaObject;
import com.umeng.socialize.media.UMediaObject.MediaType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 微薄分享
 * 
 */
public class ShareUtil {
	public static final int SHARE_TYPE_TXT = 1;
	public static final int SHARE_TYPE_WEB = 2;
	public static final int SHARE_TYPE_AUDIO = 3;
	public static final int SHARE_TYPE_VIDIO = 4;
	private static final int THUMB_SIZE = 150;
	private static ShareUtil instance = null;
	public UMSocialService controller = null;
	private Context mContext;
	private SocializeConfig socializeConfig;
	public IWXAPI api;
	public static Tencent mTencent;
	public boolean isInitWx = false;
	IMediaObject wxObj = null;
	WXMediaMessage msg = null;
	private Activity mActivity;
	private Bundle bundle;

	public interface OnLoginEndListener {
		public void onLoginSuc(String name, String gender, String openId, String[] imgs, String token, JSONObject json);

		public void onLoginError(String json);

		public void onLogining();
	}

	private ShareUtil() {
		mContext = BaseApplication.app;
		controller = UMServiceFactory.getUMSocialService("http://www.gaiay.cn", RequestType.SOCIAL);

		socializeConfig = SocializeConfig.getSocializeConfig();
		socializeConfig.setShareMail(false);
		socializeConfig.setShareSms(false);
		socializeConfig.setPlatforms(SHARE_MEDIA.TENCENT, SHARE_MEDIA.SINA, SHARE_MEDIA.QZONE);
		controller.setConfig(socializeConfig);
	};

	public void setShareListener(SnsPostListener lis) {
		controller.registerListener(lis);
	}

	public boolean checkAuth(Activity act, SHARE_MEDIA plm) {
		return UMInfoAgent.isOauthed(act, plm);
	}

	public void doAuth(Activity act, SHARE_MEDIA plm, UMAuthListener lis) {
		controller.doOauthVerify(act, plm, lis);
	}

	public static void clearAuth(Activity act, SHARE_MEDIA plm) {
		UMInfoAgent.removeOauth(act, plm);
	}

	public void shareBg(String msg, Context cxt, SHARE_MEDIA plm) {
		controller.setShareContent(msg);
		controller.getConfig().closeToast();
		controller.directShare(cxt, plm, new SnsPostListener() {
			@Override
			public void onStart() {
				Log.e("开始分享..");
			}

			@Override
			public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
				controller.getConfig().openToast();
				Log.e("分享完成:" + arg0 + "  " + arg1 + "  " + arg2);
			}
		});
	}

	public void loginQQ(Activity context, final OnLoginEndListener lis) {
		mTencent = Tencent.createInstance(BaseConstants.QQ_APP_ID, context);
		if (!mTencent.isSessionValid()) {
			mTencent.logout(context);
		}
		// IUiListener listener = new IUiListener() {
		//
		// @Override
		// public void onCancel() {
		// Log.e("mTencent:cancel");
		// if (lis != null) {
		// lis.onLoginError("取消登录");
		// }
		// }
		//
		// @Override
		// public void onComplete(JSONObject arg0) {
		// Log.e("mTencent:" + arg0);
		// if (lis != null) {
		// lis.onLogining();
		// }
		// mTencent.requestAsync(Constants.GRAPH_USER_INFO, null,
		// Constants.HTTP_GET, new IRequestListener() {
		// @Override
		// public void onUnknowException(Exception arg0,
		// Object arg1) {
		// lis.onLoginError("登录失败，请稍候..");
		// }
		//
		// @Override
		// public void onSocketTimeoutException(
		// SocketTimeoutException arg0, Object arg1) {
		// lis.onLoginError("您的网络环境不稳定，登录失败，请稍候..");
		// }
		//
		// @Override
		// public void onNetworkUnavailableException(
		// NetworkUnavailableException arg0,
		// Object arg1) {
		// lis.onLoginError("您的网络环境不稳定，登录失败，请稍候..");
		// }
		//
		// @Override
		// public void onMalformedURLException(
		// MalformedURLException arg0, Object arg1) {
		// lis.onLoginError("登录失败，请稍候..");
		// }
		//
		// @Override
		// public void onJSONException(JSONException arg0,
		// Object arg1) {
		// lis.onLoginError("登录失败，请稍候..");
		// }
		//
		// @Override
		// public void onIOException(IOException arg0,
		// Object arg1) {
		// lis.onLoginError("登录失败，请稍候..");
		// }
		//
		// @Override
		// public void onHttpStatusException(
		// HttpStatusException arg0, Object arg1) {
		// lis.onLoginError("您的网络环境不稳定，登录失败，请稍候..");
		// }
		//
		// @Override
		// public void onConnectTimeoutException(
		// ConnectTimeoutException arg0, Object arg1) {
		// lis.onLoginError("您的网络环境不稳定，登录失败，请稍候..");
		// }
		//
		// @Override
		// public void onComplete(JSONObject response,
		// Object state) {
		// Log.e("onComplete:" + response);
		// if (lis != null) {
		// String name;
		// try {
		// name = response.getString("nickname");
		// String gender = response
		// .getString("gender");
		// String imgs[] = new String[] {
		// response.getString("figureurl_qq_1"),
		// response.getString("figureurl_qq_2") };
		// lis.onLoginSuc(name, gender,
		// mTencent.getOpenId(), imgs,
		// mTencent.getAccessToken(),
		// response);
		// } catch (JSONException e) {
		// lis.onLoginError("登录失败，请稍候..");
		// e.printStackTrace();
		// }
		// }
		// }
		// }, null);
		// // Constants.HTTP_GET, new
		// // BaseApiListener("get_simple_userinfo", false), null);
		// }
		//
		// @Override
		// public void onError(UiError arg0) {
		// if (lis != null) {
		// lis.onLoginError(arg0.toString());
		// }
		// Log.e("mTencent:error:" + arg0);
		// }
		// };
		// mTencent.login(context, "get_simple_userinfo", listener);
	}

	/**
	 * 初始化微信分享绑定帐号时不需要调用此方法
	 * 
	 * @param context
	 * @param wxAppID
	 *            微信官网申请的应用AppID
	 */
	public void initWx(Context context, String wxAppID) {
		if (isInitWx) {
			return;
		}
		isInitWx = true;
		api = WXAPIFactory.createWXAPI(context, wxAppID, true);
		mTencent = Tencent.createInstance(BaseConstants.QQ_APP_ID, context);
		api.registerApp(wxAppID);
		CustomPlatform mWXPlatform = new CustomPlatform("微信", R.drawable.umeng_share_weixin_icon);
		CustomPlatform mWXCircle = new CustomPlatform("朋友圈", R.drawable.umeng_share_wxcircel);
		CustomPlatform qqPlatform = new CustomPlatform("QQ", R.drawable.base_share_qq);
		CustomPlatform smsPlatform = new CustomPlatform("短信", R.drawable.umeng_socialize_sms);

		mWXPlatform.mClickListener = new OnCustomPlatformClickListener() {
			@Override
			public void onClick(CustomPlatform arg0, SocializeEntity arg1, SnsPostListener arg2) {
				Log.d("微信");
				if (!api.isWXAppInstalled()) {
					Toast.makeText(mContext, "你还没有安装微信", Toast.LENGTH_SHORT).show();
					return;
				} else if (!api.isWXAppSupportAPI()) {
					Toast.makeText(mContext, "你安装的微信版本不支持当前API", Toast.LENGTH_SHORT).show();
					return;
				}

				sendByWX(api, arg1.getShareContent(), arg1.getMedia(), false);
			}
		};
		mWXCircle.mClickListener = new OnCustomPlatformClickListener() {
			@Override
			public void onClick(CustomPlatform arg0, SocializeEntity arg1, SnsPostListener arg2) {
				Log.d("朋友圈");
				if (!api.isWXAppInstalled()) {
					Toast.makeText(mContext, "你还没有安装微信", Toast.LENGTH_SHORT).show();
					return;
				} else if (!api.isWXAppSupportAPI()) {
					Toast.makeText(mContext, "你安装的微信版本不支持当前API", Toast.LENGTH_SHORT).show();
					return;
				}

				sendByWX(api, arg1.getShareContent(), arg1.getMedia(), true);
			}
		};
		qqPlatform.mClickListener = new OnCustomPlatformClickListener() {

			@Override
			public void onClick(CustomPlatform arg0, SocializeEntity arg1, SnsPostListener arg2) {
				if (mTencent == null) {
					ToastUtil.showMessage("您的手机未发现QQ应用，请安装后重试..");
					return;
				}
				Log.e("mActivity:" + mActivity + "  " + bundle);
				mTencent.shareToQQ(mActivity, bundle, new IUiListener() {
					@Override
					public void onError(UiError arg0) {
						Log.e("onError:" + arg0.errorCode + "  " + arg0.errorDetail + "  " + arg0.errorMessage);
					}

					@Override
					public void onCancel() {
						Log.e("onCancel:");
					}

					@Override
					public void onComplete(Object arg0) {
						Log.e("onComplete:" + arg0);
					}
				});
				Log.d("argo:" + arg0 + ";arg1" + arg1);
			}
		};
		smsPlatform.mClickListener = new OnCustomPlatformClickListener() {

			@Override
			public void onClick(CustomPlatform arg0, SocializeEntity arg1, SnsPostListener arg2) {
				Log.d("argo:" + arg0 + ";arg1" + arg1);
				Uri uri = Uri.parse("smsto:");
				Intent it = new Intent(Intent.ACTION_SENDTO, uri);
				it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				it.putExtra("sms_body", msg.description);
				mContext.startActivity(it);
			}
		};

		socializeConfig.addCustomPlatform(mWXPlatform);
		socializeConfig.addCustomPlatform(mWXCircle);
		socializeConfig.addCustomPlatform(qqPlatform);
		socializeConfig.addCustomPlatform(smsPlatform);
		controller.getConfig().setPlatformOrder(SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT, SHARE_MEDIA.WEIXIN,
				SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.SMS);
	}

	private boolean sendByWX(final IWXAPI api, String shareContent, UMediaObject shareImage, boolean toCircle) {

		if (shareImage != null && shareImage.getMediaType() == MediaType.IMAGE) {
			byte[] b = shareImage.toByte();
			if (b != null) {
				Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
				// Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
				// THUMB_SIZE, true);
				// bmp.recycle();
				// msg.thumbData = Util.bmpToByteArray(thumbBmp, true); // 设置缩略图
				b = WxShareUtil.processBitmap(bmp, THUMB_SIZE, THUMB_SIZE);
				msg.thumbData = b; // 设置缩略图
			}
		}
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		if (toCircle && wxObj instanceof WXVideoObject && StringUtil.isNotBlank(((WXVideoObject) wxObj).videoUrl)) {
			WXWebpageObject wxObj2 = new WXWebpageObject();
			wxObj2.webpageUrl = ((WXVideoObject) wxObj).videoUrl;
			msg.mediaObject = wxObj2;
		}
		Log.e("msg.des:" + msg.description);
		req.message = msg;
		req.scene = toCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		boolean sendReq = api.sendReq(req);
		// if (sendReq) {
		// ToastUtil.showMessage("分享成功！");
		// } else {
		// ToastUtil.showMessage("分享失败！");
		// }
		return sendReq;
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	public static ShareUtil getInstance() {
		if (instance == null) {
			synchronized (ShareUtil.class) {
				if (instance == null) {
					instance = new ShareUtil();
				}
			}
		}
		return instance;
	}

	/**
	 * 设置分享内容，当不支持微信时，推荐使用此方法。
	 * 
	 * @param des
	 *            分享描述信息
	 * @param img
	 *            要分享的图片， img 只能是Bitmap, 和String UIL类型不需要分享图片时可以传入null
	 */
	public void setShareContent(Activity context, String des, Object img) {

		Log.e("des:" + des + "\nimg:" + img);
		msg = null;
		wxObj = null;
		// if (!TextUtils.isEmpty(des)) {
		controller.setShareContent(des);
		// }
		UMImage uImage = null;
		if (img != null) {
			if (img instanceof Bitmap) {
				uImage = new UMImage(context, (Bitmap) img);
			} else if (img instanceof String) {
				uImage = new UMImage(context, (String) img);
			} else {
				throw new ClassCastException("参数类型错误!img 只能是Bitmp 或 String");
			}
		}
		setShareQQContent(context, null, des, null, null);
		controller.setShareImage(uImage);
	}

	/**
	 * 设置分享内容, 当支持微信分享时必须使用此方法
	 * 
	 * @param des
	 *            分享描述信息
	 * @param img
	 *            要分享的图片， img 只能是Bitmap, 和String UIL类型不需要分享图片时可以传入null
	 */
	public void setShareContent(Activity context, String title, String des, String url, Object img) {

		Log.e("title:" + title + "\ndes:" + des + "\nurl:" + url + "\nimg:" + img);
		wxObj = new WXWebpageObject();
		msg = new WXMediaMessage(wxObj);
		if (!TextUtils.isEmpty(title)) {
			msg.title = title;
		}
		// if (!TextUtils.isEmpty(des)) {
		msg.description = des;
		// }
		if (!TextUtils.isEmpty(url)) {
			((WXWebpageObject) wxObj).webpageUrl = url;
		}
		setShareQQContent(context, title, des, url, (String) img);

		// if (!TextUtils.isEmpty(des)) {
		if (!TextUtils.isEmpty(url)) {
			// des = des + "\n" + url;
		}
		controller.setShareContent(des);
		// }
		UMImage uImage = null;
		if (img != null) {
			if (img instanceof Bitmap) {
				uImage = new UMImage(context, (Bitmap) img);
			} else if (img instanceof String) {
				uImage = new UMImage(context, (String) img);
			} else {
				throw new ClassCastException("参数类型错误!img 只能是Bitmp 或 String");
			}
		}
		controller.setShareImage(uImage);
	}

	/**
	 * 设置分享内容, 当支持QQ分享时必须使用此方法
	 * 
	 * @param des
	 *            分享描述信息
	 * @param img
	 *            要分享的图片， img 只能是Bitmap, 和String UIL类型不需要分享图片时可以传入null
	 */
	public void setShareContent(Activity context, String title, String des, String url, Object img, String imgurl) {

		Log.e("title:" + title + "\ndes:" + des + "\nurl:" + url + "\nimg:" + img);
		wxObj = new WXWebpageObject();
		msg = new WXMediaMessage(wxObj);
		if (!TextUtils.isEmpty(title)) {
			msg.title = title;
		}
		msg.description = des;
		if (!TextUtils.isEmpty(url)) {
			((WXWebpageObject) wxObj).webpageUrl = url;
		}
		setShareQQContent(context, title, des, url, imgurl);

		if (!TextUtils.isEmpty(url)) {
			// des = des + "\n" + url;
		}
		controller.setShareContent(des);
		UMImage uImage = null;
		if (img != null) {
			if (img instanceof Bitmap) {
				uImage = new UMImage(context, (Bitmap) img);
			} else if (img instanceof String) {
				uImage = new UMImage(context, (String) img);
			} else {
				throw new ClassCastException("参数类型错误!img 只能是Bitmp 或 String");
			}
		}
		controller.setShareImage(uImage);
	}

	/**
	 * 设置分享内容, 当支持微信分享时必须使用此方法
	 * 
	 * @param des
	 *            分享描述信息
	 * @param img
	 *            要分享的图片， img 只能是Bitmap, 和String UIL类型不需要分享图片时可以传入null
	 */
	public void setShareContent(Bitmap img) {
		wxObj = new WXImageObject(img);
		msg = new WXMediaMessage(wxObj);
	}

	/**
	 * 设置分享内容, 当支持微信音频，视频分享时必须使用此方法。当为视频时，在微信分享中sharurl将不被使用。当为其他分享时，url将不被使用。
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
	 * @param type
	 *            分享类型：3 音频， 4 视频
	 */
	public void setShareContent(Activity context, String title, String des, String url, String sharUrl, Object img,
			int type) {
		Log.e("title:" + title + "\ndes:" + des + "\nurl:" + url + "\nsharUrl:" + sharUrl + "\nimg:" + img + "\ntype:"
				+ type);
		msg = new WXMediaMessage();
		if (!TextUtils.isEmpty(title)) {
			msg.title = title;
		}
		if (!TextUtils.isEmpty(url)) {
			if (type == SHARE_TYPE_AUDIO) {
				wxObj = new WXMusicObject();
				((WXMusicObject) wxObj).musicDataUrl = url;
				((WXMusicObject) wxObj).musicLowBandDataUrl = url;
				((WXMusicObject) wxObj).musicUrl = sharUrl;
				((WXMusicObject) wxObj).musicLowBandUrl = sharUrl;
			} else if (type == SHARE_TYPE_VIDIO) {
				wxObj = new WXVideoObject();
				((WXVideoObject) wxObj).videoLowBandUrl = url;
				((WXVideoObject) wxObj).videoUrl = sharUrl;
			}
			msg.mediaObject = wxObj;
		}
		msg.description = des;
		setShareQQContent(context, title, des, sharUrl, (String) img);

		// if (!TextUtils.isEmpty(des)) {
		if (!TextUtils.isEmpty(sharUrl)) {
			// des = des + "\n" + sharUrl;
		}
		controller.setShareContent(des);
		// }
		UMImage uImage = null;
		if (img != null) {
			if (img instanceof Bitmap) {
				uImage = new UMImage(context, (Bitmap) img);
			} else if (img instanceof String) {
				uImage = new UMImage(context, (String) img);
			} else {
				throw new ClassCastException("参数类型错误!img 只能是Bitmp 或 String");
			}
		}
		controller.setShareImage(uImage);
	}

	/**
	 * @param act
	 * @param title
	 *            标题
	 * @param des
	 *            描述
	 * @param shareUrl
	 *            点击跳转URL
	 * @param imgUrl
	 *            分享的图片的URL
	 */
	public void setShareQQContent(Activity act, String title, String des, String shareUrl, String imgUrl) {
		Log.d("-->title:" + title + ", des:" + des + ", shareUrl:" + shareUrl);
		this.mActivity = act;
		bundle = new Bundle();
		String appName = act.getResources().getString(R.string.app_name);
		bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);
		bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
		bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
		bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
	}

	/**
	 * 不使用时及时调用 removeListener
	 * 
	 * @param snsPostListener
	 */
	public void setListener(SnsPostListener snsPostListener) {
		controller.registerListener(snsPostListener);
	}

	/**
	 * 
	 * @param snsPostListener
	 */
	public void removeListener(SnsPostListener snsPostListener) {
		if (snsPostListener != null) {
			controller.unregisterListener(snsPostListener);
		}
	}

	/**
	 * 打开分享页面
	 * 
	 * @param context
	 *            上下文
	 * @param forceLogin
	 *            是否进行登录验证
	 */
	/**
	 * 打开分享
	 * 
	 * @param context
	 * @param forceLogin
	 *            是否进行登录验证 默认为false
	 * @param wxAppID
	 *            微信官网申请的应用AppID http://open.weixin.qq.com/
	 */
	public void openShare(Activity context, boolean forceLogin, String wxAppID) {
		if (!TextUtils.isEmpty(wxAppID)) {
			initWx(context, wxAppID);
		}

		controller.openShare(context, forceLogin);
	}

	public void openShare(Context context, String wxAppID) {
		if (!TextUtils.isEmpty(wxAppID)) {
			initWx(context, wxAppID);
		}

		Bitmap bmp = BitmapFactory.decodeByteArray(((WXImageObject) msg.mediaObject).imageData, 0,
				((WXImageObject) msg.mediaObject).imageData.length);
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
		bmp.recycle();
		msg.thumbData = Utils.bmpToByteArray(thumbBmp, true); // 设置缩略图
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
	}

	/**
	 * 打开个人中授权页面
	 * 
	 * @param context
	 * @param flag
	 *            登录参数
	 *            <p>
	 *            另请参见：
	 *            <p>
	 *            SocializeConstants#FLAG_USER_CENTER_HIDE_LOGININFO, SocializeConstants#FLAG_USER_CENTER_HIDE_SNSINFO,
	 *            SocializeConstants#FLAG_USER_CENTER_LOGIN_VERIFY
	 */
	public void openUserCenter(Context context, int flag) {
		controller.openUserCenter(context, flag);
	}

}
