package com.gaiay.support.umeng;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gaiay.base.BaseApplication;
import com.gaiay.base.BaseConstants;
import com.gaiay.base.R;
import com.gaiay.base.net.NetCallback;
import com.gaiay.base.util.BitmapUtil;
import com.gaiay.base.util.FileUtil;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.ShortDomainUtil;
import com.gaiay.base.util.StringUtil;
import com.gaiay.base.util.ToastUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXVideoObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 后台单个分享工具类
 */
public class ShareNewUIUtil {
	/** 分享url的占位符 */
	public final static String SHARE_URL = "#share_url#";

	public enum shareTo {
		tenc, sina, weixin, weixincircle, qq, qzone, sms,
	}

	ModelShare model;

	UMSocialService controller;

	PopupWindow pw;

	View mRoot;

	Activity mAct;

	public Tencent mTencent;
	public IWXAPI api;

	private static final int THUMB_SIZE = 150;
	private boolean isShowing = false;

	private boolean mBossVisible = true;

	public void setBossVisible(boolean visible) {
		mBossVisible = visible;
	}

	public void show() {
		// defImgId = 0;
		if (mRoot == null) {
			mRoot = View.inflate(mAct, R.layout.base_share_dialog, null);
			GridView gridView = (GridView) mRoot.findViewById(R.id.grid_view);
			List<Object[]> data = new ArrayList<Object[]>();
			if (mAct instanceof OnShareToZhangMenListener) {
				mShareToZhangMenListener = (OnShareToZhangMenListener) mAct;
				if (mBossVisible) {
					data.add(new Object[] { R.drawable.base_share_dialog_icon_boss, "BOSS圈" });
				}
				data.add(new Object[] { R.drawable.base_share_dialog_icon_zm, "掌门好友" });
			}
			data.add(new Object[] { R.drawable.base_share_dialog_icon_wxcircle, "朋友圈" });
			data.add(new Object[] { R.drawable.base_share_dialog_icon_wx, "微信好友" });
			data.add(new Object[] { R.drawable.base_share_dialog_icon_sina, "新浪微博" });
			data.add(new Object[] { R.drawable.base_share_dialog_icon_qzone, "QQ空间" });
			data.add(new Object[] { R.drawable.base_share_dialog_icon_qq, "QQ好友" });
			data.add(new Object[] { R.drawable.base_share_dialog_icon_sms, "手机短信" });
			// data.add(new Object[] { R.drawable.base_share_dialog_icon_tenc, "腾讯微博" });
			gridView.setAdapter(new ShareItemAdapter(data));

			mRoot.findViewById(R.id.mBtnCancel).setOnClickListener(click);
		}

		if (pw == null) {
			pw = new PopupWindow(mRoot, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
			pw.setBackgroundDrawable(new BitmapDrawable());
			pw.setOutsideTouchable(true);
			pw.setAnimationStyle(R.style.base_share_window);
		}

		if (!isShowing) {
			isShowing = true;
			pw.showAtLocation(mAct.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
			pw.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					isShowing = false;
					// removeListener();
				}
			});

		}
	}

	public static ShareNewUIUtil getInstance(Activity act) {
		return new ShareNewUIUtil(act);
	}

	private ShareNewUIUtil(Activity act) {
		mAct = act;
		controller = UMServiceFactory.getUMSocialService("http://www.gaiay.cn", RequestType.SOCIAL);
		controller.setShareContent(null);
		controller.setShareImage(null);

		api = WXAPIFactory.createWXAPI(act, BaseConstants.WEIXIN_APP_KEY, false);
		mTencent = Tencent.createInstance(BaseConstants.QQ_APP_ID, act);
		api.registerApp(BaseConstants.WEIXIN_APP_KEY);

		model = new ModelShare();
	}

	/**
	 * 掌门内部分享
	 */
	OnShareToZhangMenListener mShareToZhangMenListener;

	public void setOnShareToZhangMenListener(OnShareToZhangMenListener shareToZhangMen) {
		this.mShareToZhangMenListener = shareToZhangMen;
	}

	public void removeShareToZhangMenListener() {
		if (mShareToZhangMenListener != null) {
			mShareToZhangMenListener = null;
		}
	}

	public interface OnShareToZhangMenListener {
		public void getTag(int tag, ModelShare model, Activity ac);
	}

	View.OnClickListener click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			// 这里的resId也充当了控件id，方便使用，不需要再重写一套id
			if (id == R.drawable.base_share_dialog_icon_sina) {
				shareBg(shareTo.sina);
			} else if (id == R.drawable.base_share_dialog_icon_tenc) {
				shareBg(shareTo.tenc);
			} else if (id == R.drawable.base_share_dialog_icon_sms) {
				shareBg(shareTo.sms);
			} else if (id == R.drawable.base_share_dialog_icon_wxcircle) {
				shareBg(shareTo.weixincircle);
			} else if (id == R.drawable.base_share_dialog_icon_wx) {
				shareBg(shareTo.weixin);
			} else if (id == R.drawable.base_share_dialog_icon_qq) {
				shareBg(shareTo.qq);
			} else if (id == R.drawable.base_share_dialog_icon_qzone) {
				shareBg(shareTo.qzone);
			} // type 99 掌信
			else if (id == R.drawable.base_share_dialog_icon_zm) {
				if (mShareToZhangMenListener != null) {
					mShareToZhangMenListener.getTag(99, model, mAct);
				}
			} // type 24 BOSS圈
			else if (id == R.drawable.base_share_dialog_icon_boss) {
				if (mShareToZhangMenListener != null) {
					mShareToZhangMenListener.getTag(24, model, mAct);
				}
			}
			pw.dismiss();
		}
	};

	public void shareToSina() {
		shareBg(shareTo.sina);
	}

	public void shareToBoss() {
		if (mShareToZhangMenListener != null) {
			mShareToZhangMenListener.getTag(24, model, mAct);
		}
	}

	public void shareToZM() {
		if (mShareToZhangMenListener != null) {
			mShareToZhangMenListener.getTag(99, null, mAct);
		}
	}

	public void shareToWeixin() {
		shareBg(shareTo.weixin);
	}

	public void shareToWeixinCircle() {
		shareBg(shareTo.weixincircle);
	}

	public void shareToQQ() {
		shareBg(shareTo.qq);
	}

	public void shareBg(shareTo plm) {
		// if (model.type == 3) {
		// controller.setShareMedia(new UMusic(model.mediaUrl));
		// } else if(model.type == 4) {
		// controller.setShareMedia(new UMVideo(model.mediaUrl));
		// }
		SHARE_MEDIA p = null;
		switch (plm) {
		case tenc:
			p = SHARE_MEDIA.TENCENT;
			break;
		case sina:
			p = SHARE_MEDIA.SINA;
			break;
		case weixin:
			sendByWX(false);
			break;
		case weixincircle:
			sendByWX(true);
			break;
		case qq:
			sendByQQ(false);
			break;
		case qzone:
			if (StringUtil.isBlank(model.shortUrl)) {
				showWaitDialog("正在请求数据，请稍等..");
				final ShortDomainUtil sdu = new ShortDomainUtil();
				sdu.convertDomainToShort(model.webUrl, new NetCallback() {
					@Override
					public void onGetSucc() {
						model.shortUrl = sdu.getShortUrl();
						sendByQQ(true);
					}

					@Override
					public void onGetFaild() {
						ToastUtil.showMessage("获取失败");
					}

					@Override
					public void onGetError() {
						ToastUtil.showMessage("获取失败");
					}

					@Override
					public void onComplete() {
						dismisWaitDialog();
					}
				});
			} else {
				sendByQQ(true);
			}
			return;
		case sms:
			p = SHARE_MEDIA.SMS;
			break;
		default:
			break;
		}
		if (StringUtil.isBlank(model.shortUrl) && p != null && isUseShortURLCreate) {
			showWaitDialog("正在请求数据，请稍等..");
			final SHARE_MEDIA plat = p;
			final ShortDomainUtil sdu = new ShortDomainUtil();
			sdu.convertDomainToShort(model.webUrl, new NetCallback() {
				@Override
				public void onGetSucc() {
					model.shortUrl = sdu.getShortUrl();
					sendOther(plat);
				}

				@Override
				public void onGetFaild() {
					ToastUtil.showMessage("获取失败");
				}

				@Override
				public void onGetError() {
					ToastUtil.showMessage("获取失败");
				}

				@Override
				public void onComplete() {
					dismisWaitDialog();
				}
			});
		} else {
			sendOther(p);
		}
	}

	private void sendOther(SHARE_MEDIA p) {
		if (p == null) {
			return;
		}
		if (!isUseShortURLCreate) {
			model.shortUrl = model.webUrl;
		}
		if (p == SHARE_MEDIA.SMS) {
			Uri uri = Uri.parse("smsto:");
			Intent it = new Intent(Intent.ACTION_SENDTO, uri);
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (speSmsNoShortUrl != null) {
				if (!speSmsNoShortUrl.contains(model.shortUrl)) {
					if (speSmsNoShortUrl.contains(SHARE_URL)) {
						it.putExtra("sms_body", speSmsNoShortUrl.replace(SHARE_URL, model.shortUrl));
					} else {
						it.putExtra("sms_body", speSmsNoShortUrl + "  " + model.shortUrl);
					}
				} else {
					it.putExtra("sms_body", speSmsNoShortUrl);
				}
			} else if (speSms != null) {
				it.putExtra("sms_body", speSms);
			} else {
				it.putExtra("sms_body", model.description);
			}
			mAct.startActivity(it);
			if (ShareNewUIUtil.this.snsPostListener != null) {
				ShareNewUIUtil.this.snsPostListener.onComplete(SHARE_MEDIA.SMS, 200, null);
			}
			return;
		}
		if (StringUtil.isNotBlank(model.description) && StringUtil.isNotBlank(model.shortUrl)
				&& !model.description.contains(model.shortUrl) && !model.description.contains(model.webUrl)) {
			if (model.description.contains(SHARE_URL)) {
				controller.setShareContent(model.description.replace(SHARE_URL, model.shortUrl));
			} else {
				controller.setShareContent(model.description + "  " + model.shortUrl);
			}
		} else {
			controller.setShareContent(model.description);
		}
		//迭代21中，summary
		if (StringUtil.isNotBlank(model.summary) && StringUtil.isNotBlank(model.shortUrl)
				&& !model.summary.contains(model.shortUrl) && !model.summary.contains(model.webUrl)) {
			if (model.summary.contains(SHARE_URL)) {
				model.summary = model.summary.replace(SHARE_URL, model.shortUrl);
			}
		}
		if (StringUtil.isNotBlank(model.imgUrl)) {
			controller.setShareImage(new UMImage(mAct, model.imgUrl));
		} else if (model.imgUM != null) {
			controller.setShareImage(model.imgUM);
		}
		controller.getConfig().closeToast();
		controller.postShare(mAct, p, snsPostListener == null ? new SnsPostListener() {

			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(SHARE_MEDIA arg0, int arg1, SocializeEntity arg2) {
				Log.e("arg1:" + arg1 + "  arg2:" + arg2);
			}

		} : snsPostListener);
	}

	private void sendByQQ(final boolean isQZong) {
		if (mTencent == null) {
			ToastUtil.showMessage("您的手机未发现QQ应用，请安装后重试..");
			return;
		}
		if (isQZong) {
			setShareQZoneContent();
		} else {
			setShareQQContent();
			// bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
		}
		IUiListener l = new IUiListener() {
			@Override
			public void onError(UiError arg0) {
				Log.e("onError:" + arg0.errorCode + "  " + arg0.errorDetail + "  " + arg0.errorMessage);
			}

			@Override
			public void onComplete(Object arg0) {
				Log.e("onComplete:" + arg0);
				if (ShareNewUIUtil.this.snsPostListener != null) {
					ShareNewUIUtil.this.snsPostListener.onComplete(isQZong ? SHARE_MEDIA.QZONE : SHARE_MEDIA.QQ, 200,
							null);
				}
			}

			@Override
			public void onCancel() {
			}

		};
		if (isQZong) {
			mTencent.shareToQzone(mAct, bundle, l);
		} else {
			mTencent.shareToQQ(mAct, bundle, l);
		}
	}

	int defImgId = 0;

	public void setUseDefImg(int id) {
		defImgId = id;
		try {
			if (model == null) {
				model = new ModelShare();
			}
			model.imgUM = new UMImage(mAct, BitmapFactory.decodeResource(mAct.getResources(), defImgId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 概要，在微信或者QQ手机 ,QQ空间分享时，需要同时显示标题和概要时使用
	 */
	public void setSummary(String summary) {
		model.summary = summary;
	}

	/**
	 * 分享到朋友圈的内容
	 */
	public void setWeixinCircle(String content) {
		model.weixinCircle = content;
	}

	public void setExtras(Map extras) {
		model.extras = extras;
	}

	// bizId 社群直播中的直播id
	public void setChat(String title, String content, int contentType, String id, String bizId) {
		model.chatTitle = title;
		model.chatContent = content;
		model.chatContentType = contentType;
		model.id = id;
		model.bizId = bizId;
	}
	boolean isUseShortURLCreate = true;

	/**
	 * 配置是否使用URL默认短链接转换，默认为true
	 * 
	 * @param isCreate
	 */
	public void cfgUseShortURLCreate(boolean isCreate) {
		isUseShortURLCreate = isCreate;
	}

	boolean isImgSuc = true;

	private boolean sendByWX(final boolean toCircle) {

		if (!api.isWXAppInstalled()) {
			Toast.makeText(mAct, "你还没有安装微信", Toast.LENGTH_SHORT).show();
			return false;
		} else if (!api.isWXAppSupportAPI()) {
			Toast.makeText(mAct, "你安装的微信版本不支持当前API", Toast.LENGTH_SHORT).show();
			return false;
		}

		WXMediaMessage msg = new WXMediaMessage();
		if (model != null) {
			if (model.img != null && !model.img.isRecycled()) {
				msg.thumbData = WxShareUtil.processBitmap(model.img.copy(Config.ARGB_8888, false), THUMB_SIZE,
						THUMB_SIZE);
			} else if (StringUtil.isNotBlank(model.imgUrl) && isImgSuc) {
				mAct.runOnUiThread(new Runnable() {
					ProgressDialog pd;

					private void showDialog() {
						if (pd == null) {
							pd = new ProgressDialog(mAct);
							pd.setOnCancelListener(null);
							pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							pd.setIndeterminate(false);
							pd.setCanceledOnTouchOutside(false);
							pd.setMessage("请稍候..");
							pd.setMax(1000);
							pd.setProgress(1);
						}
						pd.show();
					}

					private void disDialog() {
						if (pd != null) {
							pd.dismiss();
						}
					}

					public void run() {
						final boolean toCir = toCircle;
						FinalHttp fh = new FinalHttp();
						fh.download(model.imgUrl.trim(),
								Environment.getExternalStorageDirectory().getAbsolutePath() + "/shareImg",
								new AjaxCallBack<File>() {
							@Override
							public void onStart() {
								showDialog();
								super.onStart();
							}

							public void onFailure(Throwable t, int errorNo, String strMsg) {
								Log.e("onFailure:" + t.getMessage() + " \nstrMsg" + strMsg);
								disDialog();
								isImgSuc = false;
								sendByWX(toCir);
							};

							@Override
							public void onSuccess(File t) {
								Log.e("onSuccess:" + t.getAbsolutePath() + "  " + t.length());
								disDialog();
								isImgSuc = false;
								model.img = BitmapFactory.decodeFile(t.getAbsolutePath());
								sendByWX(toCir);
								super.onSuccess(t);
							}

						});
					};

				});
				return false;
			} else if (defImgId > 0) {
				model.img = BitmapFactory.decodeResource(mAct.getResources(), defImgId);
				msg.thumbData = WxShareUtil.processBitmap(model.img.copy(Config.ARGB_8888, false), THUMB_SIZE,
						THUMB_SIZE);
				defImgId = 0;
			}
			isImgSuc = true;
			if (toCircle) {
				if (StringUtil.isNotBlank(model.weixinCircle)) {
					msg.title = model.weixinCircle;
				} else {
					msg.title = model.title;
				}
			} else {
				msg.title = model.title;
			}
			if (StringUtil.isNotBlank(model.summary)) {
				msg.description = model.summary;
			} else if (model.description != null) {
				msg.description = model.description;
			}
			if (model.type == 3) {
				WXMusicObject wo = new WXMusicObject();
				wo.musicDataUrl = model.mediaUrl;
				wo.musicLowBandDataUrl = model.mediaUrl;
				wo.musicUrl = model.webUrl;
				wo.musicLowBandUrl = model.webUrl;
				msg.mediaObject = wo;
			} else if (model.type == 4) {
				if (toCircle) {
					msg.mediaObject = new WXWebpageObject(model.webUrl);
				} else {
					WXVideoObject wo = new WXVideoObject();
					wo.videoLowBandUrl = model.mediaUrl;
					wo.videoUrl = model.webUrl;
					msg.mediaObject = wo;
				}
			} else if (model.webUrl != null) {
				msg.mediaObject = new WXWebpageObject(model.webUrl);
			}
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		if (msg.title != null && msg.title.length() > 200) {
			msg.title = msg.title.substring(0, 200);
		}
		if (msg.description != null && msg.description.length() > 200) {
			msg.description = msg.description.substring(0, 200);
		}
		req.message = msg;
		req.scene = toCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		boolean sendReq = api.sendReq(req);
		if (snsPostListener != null) {
			snsPostListener.onComplete(toCircle ? SHARE_MEDIA.WEIXIN_CIRCLE : SHARE_MEDIA.WEIXIN, 200, null);
		}
		return sendReq;
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	/**
	 * 设置分享内容，当不支持微信时，推荐使用此方法。
	 * 
	 * @param des
	 *            分享描述信息
	 * @param img
	 *            要分享的图片， img 只能是Bitmap, 和String UIL类型不需要分享图片时可以传入null
	 */
	public void setShareContent(String des, Object img) {

		Log.e("des:" + des + "\nimg:" + img);
		if (model == null) {
			model = new ModelShare();
		}
		model.description = des;

		if (img != null) {
			if (img instanceof Bitmap) {
				model.img = Bitmap.createBitmap((Bitmap) img);
				model.imgUM = new UMImage(mAct, model.img);
			} else if (img instanceof String) {
				model.imgUrl = (String) img;
				model.imgUM = new UMImage(mAct, model.imgUrl);
			} else {
				throw new ClassCastException("参数类型错误!img 只能是Bitmp 或 String");
			}
		}
	}

	/**
	 * 设置分享内容, 当支持微信分享时必须使用此方法
	 * 
	 * @param des
	 *            分享描述信息
	 * @param img
	 *            要分享的图片， img 只能是Bitmap, 和String UIL类型不需要分享图片时可以传入null
	 */
	public void setShareContent(String title, String des, String url, Object img) {

		Log.e("title:" + title + "\ndes:" + des + "\nurl:" + url + "\nimg:" + img);
		if (model == null) {
			model = new ModelShare();
		}
		model.title = title;
		model.webUrl = url;
		model.description = des;

		if (img != null) {
			if (img instanceof Bitmap) {
				model.img = Bitmap.createBitmap((Bitmap) img);
				model.imgUM = new UMImage(mAct, model.img);
			} else if (img instanceof String) {
				model.imgUrl = (String) img;
				model.imgUM = new UMImage(mAct, model.imgUrl);
			} else {
				throw new ClassCastException("参数类型错误!img 只能是Bitmp 或 String");
			}
		}
	}

	/**
	 * 设置分享内容, 当支持QQ分享时必须使用此方法
	 * 
	 * @param des
	 *            分享描述信息
	 * @param img
	 *            要分享的图片， img 只能是Bitmap, 和String UIL类型不需要分享图片时可以传入null
	 */
	public void setShareContent(String title, final String des, String url, Object img, String imgurl) {
		Log.e("title:" + title + "\ndes:" + des + "\nurl:" + url + "\nimg:" + img);
		if (model == null) {
			model = new ModelShare();
		}
		model.title = title;
		model.webUrl = url;
		model.shareUrl = url;
		if (StringUtil.isBlank(model.shortUrl) && des.contains(SHARE_URL)) {
			final ShortDomainUtil sdu = new ShortDomainUtil();
			sdu.convertDomainToShort(model.webUrl, new NetCallback() {
				@Override
				public void onGetSucc() {
					model.shortUrl = sdu.getShortUrl();
					if (des.contains(SHARE_URL)) {
						model.description = des.replace(SHARE_URL, model.shortUrl);
					} else {
						model.description = des;
					}
					//迭代21
					if (StringUtil.isNotBlank(model.summary) && model.summary.contains(SHARE_URL)) {
						model.summary = model.summary.replace(SHARE_URL, model.shortUrl);
					}
				}

				@Override
				public void onGetFaild() {
				}

				@Override
				public void onGetError() {
				}

				@Override
				public void onComplete() {
				}
			});
		}
		model.description = des;
		model.imgUrl = imgurl == null ? "" : imgurl.trim();

		if (img != null) {
			if (img instanceof Bitmap) {
				model.img = Bitmap.createBitmap((Bitmap) img);
				model.imgUM = new UMImage(mAct, model.img);
			} else if (img instanceof String) {
				model.imgUrl = (String) img;
				model.imgUM = new UMImage(mAct, model.imgUrl);
			} else {
				throw new ClassCastException("参数类型错误!img 只能是Bitmp 或 String");
			}
		}
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
		if (model == null) {
			model = new ModelShare();
		}
		model.img = Bitmap.createBitmap((Bitmap) img);
	}

	public void setInnerUrl(String url) {
		model.innerUrl = url;
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
	public void setShareContent(String title, String des, String url, String sharUrl, Object img, int type) {
		Log.e("title:" + title + "\ndes:" + des + "\nurl:" + url + "\nsharUrl:" + sharUrl + "\nimg:" + img + "\ntype:"
				+ type);
		if (model == null) {
			model = new ModelShare();
		}
		model.title = title;
		model.type = type;
		model.mediaUrl = url;
		model.webUrl = sharUrl;
		model.description = des;

		if (img != null) {
			if (img instanceof Bitmap) {
				model.img = Bitmap.createBitmap((Bitmap) img);
				model.imgUM = new UMImage(mAct, model.img);
			} else if (img instanceof String) {
				model.imgUrl = (String) img;
				model.imgUM = new UMImage(mAct, model.imgUrl);
			} else {
				throw new ClassCastException("参数类型错误!img 只能是Bitmp 或 String");
			}
		}
	}

	Bundle bundle;

	private void setShareQQContent() {
		bundle = new Bundle();
		String appName = mAct.getResources().getString(R.string.app_name);

		bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		bundle.putString(QQShare.SHARE_TO_QQ_TITLE, model.title);
		if (StringUtil.isNotBlank(model.summary)) {
			bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, model.summary);
		} else {
			bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, model.description);
		}
		bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, model.webUrl);
		bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
		if (StringUtil.isNotBlank(model.imgUrl)) {
			bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, model.imgUrl);
		} else {
			String path = initShareQQDefImg();
			if (path != null) {
				bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, path);
			}
		}
	}

	private void setShareQZoneContent() {
		bundle = new Bundle();
		String appName = mAct.getResources().getString(R.string.app_name);

		bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, model.title);
		String str = "";
		if (StringUtil.isNotBlank(model.summary)) {
			str = model.summary;
		} else {
			str = model.description;
		}
		if (StringUtil.isNotBlank(str)) {
			bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, str.replace(SHARE_URL, model.shortUrl));
		}
		bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, model.webUrl);
		bundle.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, appName);
		if (StringUtil.isNotBlank(model.imgUrl)) {
			ArrayList<String> imgs = new ArrayList<String>();
			imgs.add(model.imgUrl);
			bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgs);
		} else {
			String path = initShareQQDefImg();
			if (StringUtil.isNotBlank(path)) {
				ArrayList<String> imgs = new ArrayList<String>();
				imgs.add(path);
				bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgs);
			}
		}
	}

	private String initShareQQDefImg() {
		if (defImgId <= 0) {
			return null;
		}

		String path = BaseApplication.app.getCacheFolder() + "def_Img";

		if (FileUtil.saveLocalFile(path,
				BitmapUtil.convertBitmapToBytes(BitmapFactory.decodeResource(mAct.getResources(), defImgId)))) {
			return path;
		}
		return null;

	}

	public SnsPostListener snsPostListener;

	/**
	 * 不使用时及时调用 removeListener
	 * 
	 * @param snsPostListener
	 */
	public void setListener(SnsPostListener snsPostListener) {
		this.snsPostListener = snsPostListener;
		// controller.registerListener(snsPostListener);
	}

	/**
	 * 
	 * @param snsPostListener
	 */
	public void removeListener(SnsPostListener snsPostListener) {
		if (snsPostListener != null) {
			this.snsPostListener = null;
			// controller.unregisterListener(snsPostListener);
		}
	}

	/**
	 * 
	 * @param snsPostListener
	 */
	public void removeListener() {
		if (snsPostListener != null) {
			// controller.unregisterListener(snsPostListener);
			this.snsPostListener = null;
		}
	}
	String speSms = null;
	String speSmsNoShortUrl = null;

	public void setSpecialSMSContent(String msg) {
		speSms = msg;
	}

	public void setSpecialSMSContentNoShortUrl(String msg) {
		speSmsNoShortUrl = msg;
	}

	ProgressDialog progress;

	public void showWaitDialog(String msg) {
		if (mAct == null) {
			return;
		}
		if (progress == null) {
			progress = new ProgressDialog(mAct);
		}
		progress.setOnCancelListener(null);
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(false);
		progress.setCanceledOnTouchOutside(false);
		progress.setMessage(msg);
		progress.setMax(1000);
		progress.setProgress(1);
		progress.show();
	}

	public void dismisWaitDialog() {
		if (progress != null && progress.isShowing()) {
			progress.cancel();
		}
	}

	private class ShareItemAdapter extends BaseAdapter {
		// 0 resId；1 title
		private List<Object[]> mData;

		public ShareItemAdapter(List<Object[]> data) {
			this.mData = data;
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = (TextView) LayoutInflater.from(mAct).inflate(R.layout.base_share_dialog_item, null);
			Object[] objs = mData.get(position);
			// 这里的resId也充当了控件id，方便使用，不需要再重写一套id
			tv.setId((Integer) objs[0]);
			Drawable d = mAct.getResources().getDrawable((Integer) objs[0]);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicWidth());
			tv.setCompoundDrawables(null, d, null, null);
			tv.setText(objs[1] + "");
			tv.setOnClickListener(click);
			return tv;
		}

	}
}
