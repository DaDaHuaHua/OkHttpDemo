package com.gaiay.support.umeng;

import android.app.Activity;
import android.content.Context;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMInfoAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;

public class WeiboShareUtil {
	private static UMSocialService controller = UMServiceFactory.getUMSocialService("", RequestType.SOCIAL);
	
	public WeiboShareUtil() {
		controller.setShareContent("   --- Tom & Jerry");

		//设置分享图片(支持4种方式),一个ActionBar最多只能选择一种
		//注意：预设图片构造会对序列化图片对象，不可以马上使用。
		//
//		controller.setShareMedia(new UMImage(mContext,bytes));

		//配置分享平台，默认全部
		SocializeConfig socializeConfig = SocializeConfig.getSocializeConfig();
		socializeConfig.setPlatforms(SHARE_MEDIA.TENCENT,SHARE_MEDIA.SINA);//设置分享平台
		controller.setConfig(socializeConfig);//该配置只作用于单个ActionBar（相同des参数描述）
	}
	
	/**
	 * 分享选择框
	 * 
	 * @param cxt
	 *            当前所在的Activity，如果在TabActivity的子Activity中应该使用TabActivity。[必须]
	 * @param msg
	 *            选择分享编辑页中的默认文字。[可选]
	 * @param bitmap
	 *            分享的图片
	 */

	public static void shareTo(Context context, SHARE_MEDIA media, String msg, byte[] bitmap) {
		controller.shareTo((Activity) context, media, msg, bitmap);
	}

	/**
	 * 取消指定平台的授权状态
	 * 
	 * @param context
	 * @param platform
	 * @param listener
	 */
	public static void deleteOauth(Context context, SHARE_MEDIA platform, SocializeClientListener listener) {
		controller.deleteOauth(context, platform, listener);
	}

	/**
	 * 授权某个平台
	 * 
	 * @param context
	 * @param platform
	 * @param listener
	 */
	public static void doOauthVerify(Context context, SHARE_MEDIA platform, UMAuthListener listener) {
		controller.doOauthVerify(context, platform, listener);
	}

	/**
	 * 查询 SHARE_MEDIA 是否已经授权
	 * 
	 * @param context
	 * @param platform
	 * @return
	 */
	public static boolean isOauthed(Context context, SHARE_MEDIA platform) {
		return UMInfoAgent.isOauthed(context, platform);
	}

	public static void getPlatformInfo(Context context, SHARE_MEDIA media, UMDataListener listener) {
		controller.getPlatformInfo(context, media, listener);

	}

	/**
	 * 打开分享页面
	 * @param context 
	 * @param forceLogin 是否做分享判断
	 */
	public static void openShare(Activity context, boolean forceLogin, SnsPostListener snsPostListener) {
		controller.registerListener(snsPostListener);
		controller.openShare(context, forceLogin);
	}
}
