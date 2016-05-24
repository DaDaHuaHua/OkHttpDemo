package com.gaiay.support.update;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.widget.RemoteViews;

import com.gaiay.base.R;
import com.gaiay.base.util.Log;
import com.gaiay.support.update.UpdateHelper.OnCheckEndListener;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("HandlerLeak")
public class UpdateService extends Service {

	public static String appId;
	public static String appName;
	public static String project;
	public static String url;
	public static boolean isLoop = false;
	private final String METHOD = "update";

	private Timer mTimer = null;
	private UpdateHelper helper;
	private static final int INTERVAL = 1 * 60 * 60 * 1000;
	private static final int INTERVAL_SMALL = 30 * 60 * 1000;
	public ModelUpdate model = null;
	public NotificationManager notificationManager;
	private String path = null;
	private boolean isDoing = false;
	private boolean isDowning = false;
	long dur = 0;

	public static UpdateService instance;

	public static final int notifyNewVersion = 19871201;
	public static final int notifyDowning = 19871202;
	public static final String NOTIFI = "UPDATE_NOTIFI";
	public static final String NOTIFI_TYPE = "NOTIFI_TYPE";
	public static final String CHECK_URL = "url";
	public static final String CHECK_APPNAME = "appname";
	public static final String CHECK_APPID = "appid";
	public static final String CHECK_IS_LOOP = "CHECK_IS_LOOP";
	public static boolean isStarted = false;

	public boolean isUseType2 = false;
	public String appType = "";

	@Override
	public void onCreate() {
		Log.d("--->onCreate");
		instance = this;
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		registerReceiver(new MyReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!isStarted) {
			isStarted = true;
			Log.d("---> URL:" + url);
			Log.d("---> APPNAME:" + appName);
			Log.d("---> APPID:" + appId);
			Log.d("---> CHECK_IS_LOOP:" + isLoop);
			initHelper();
			try {
				if (isLoop) {
					mTimer = new Timer();
					mTimer.schedule(mTimerTask, 0, INTERVAL);
				} else {
					if (isDoing) {
						return START_NOT_STICKY;
					}
					// checkUpdate();
				}
			} catch (Exception e) {
			}
		}
		Log.d("--->onStartCommand");

		return START_NOT_STICKY;
	};

	@Override
	public void onDestroy() {
		isStarted = false;
		instance = null;
		super.onDestroy();
	}

	OnUpdateListener lis;

	public void setOnUpdateListener(OnUpdateListener l) {
		lis = l;
	}

	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (isDoing) {
				return;
			}
			Log.e("mTimerTask:");
			checkUpdate();
			// handler.sendEmptyMessage(0);
		}
	};

	public void checkUpdate() {
		Log.e("checkUpdate");
		handler.sendEmptyMessage(1);
	}

	public boolean hasNewVersion() {
		if (model == null) {
			return false;
		}
		return model.hasNewVersion;
	}

	public ModelUpdate getNewVersionModel() {
		return model;
	}

	public String strNotifyTitle = "掌门新版本更新升级";
	public String strNotifyContent = "新版本使用更稳定，更流畅，赶快下载体验吧！";

	OnCheckEndListener lisCheck = new OnCheckEndListener() {
		@Override
		public void noNewVersion() {
			Log.e("noNewVersion");
			isDoing = false;
			if (lis != null) {
				Log.e("noNewVersion2");
				lis.onCheckComplete(false, null);
			}
		}

		@Override
		public void checkError() {
			isDoing = false;
			dur = 0;
			if (lis != null) {
				lis.onCheckError();
			}
		}

		@Override
		public void hasNewVersion(ModelUpdate model) {
			UpdateService.this.model = model;
			Log.e("hasNewVersion");
			if (lis != null) {

				Log.e("hasNewVersion2");
				if (!lis.onCheckComplete(true, model)) {
					handler.sendEmptyMessage(0);
				}
			} else {
				handler.sendEmptyMessage(0);
			}
			isDoing = false;
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				showNewVersionNotification(strNotifyTitle, strNotifyContent);
				break;
			case 1:
				if (helper == null || isDoing) {
					return;
				}
				isDoing = true;
				if (NetworkUtil.isNetworkValidate(instance)) {
					dur = SystemClock.uptimeMillis();
				} else {
					return;
				}
				if (isUseType2) {
					helper.checkVersionByGet(appType, lisCheck);
				} else {
					helper.checkVersion(appId, lisCheck);
				}
				break;
			case 2:
                // 通过默认浏览器下载
                if (helper != null && model != null && model.hasNewVersion) {
                    helper.startDownloadPage();
                }
                // 通过app进行下载
//				if (helper != null && model != null && model.hasNewVersion && !isDoing) {
//					isDoing = true;
//					if (getPath() != null) {
//						path = getPath() + "/" + (model.name + "_" + UpdateHelper.getVersion()) + ".apk";
//					}
//					helper.downloadNewVersion(path, new AjaxCallBack<File>() {
//
//						@Override
//						public void onFailure(Throwable t, int num, String strMsg) {
//							isDoing = false;
//							isDowning = false;
//							if (strMsg != null && strMsg.trim().contains("maybe you have download complete")) {
//								File f = new File(path);
//								if (f.exists() && f.length() > 0) {
//									exec(path);
//									if (lis != null) {
//										if (!lis.onDownComplete(path)) {
//											showNewVersionSetupNotification();
//										}
//									} else {
//										showNewVersionSetupNotification();
//									}
//									return;
//								}
//							}
//							Log.e("", ">>>onFailure:" + strMsg);
//							if (lis != null) {
//								if (!lis.onDownError("您的网络环境不稳定，请检查您的网络，稍候重试。")) {
//									showNewVersionNotification("下载失败，请重新尝试。", "您的网络环境不稳定，请检查您的网络，稍候重试。");
//								}
//							} else {
//								showNewVersionNotification("下载失败，请重新尝试。", "您的网络环境不稳定，请检查您的网络，稍候重试。");
//							}
//						}
//
//						@Override
//						public void onLoading(long count, long current) {
//							isDoing = true;
//							Log.e("", ">>>current:" + current + "  count:" + count);
//							if (lis != null) {
//								if (!lis.onDown(current, count)) {
//									int pos = (int) (((double) ((double) current / (double) count)) * 100);
//									showNewVersionDowningNotification(pos);
//
//								}
//							} else {
//								int pos = (int) (((double) ((double) current / (double) count)) * 100);
//								showNewVersionDowningNotification(pos);
//							}
//						}
//
//						@Override
//						public void onStart() {
//							Log.e("", ">>>onStart:");
//							isDoing = true;
//							isDowning = true;
//							if (lis != null) {
//								if (!lis.onStartDown()) {
//									showNewVersionDowningNotification(-1);
//								}
//							} else {
//								showNewVersionDowningNotification(-1);
//							}
//						}
//
//						@Override
//						public void onSuccess(File t) {
//							isDowning = false;
//							isDoing = false;
//							exec(path);
//							if (lis != null) {
//								if (!lis.onDownComplete(path)) {
//									showNewVersionSetupNotification();
//									setupNewVersion();
//								}
//							} else {
//								showNewVersionSetupNotification();
//								setupNewVersion();
//							}
//						}
//
//					});
//				}
				break;
			default:
				break;
			}
		};
	};

	private void exec(String filePath) {
		String[] command = { "chmod", "777", filePath };
		ProcessBuilder builder = new ProcessBuilder(command);
		try {
			builder.start();
		} catch (IOException e) {
			Log.e("", e.getMessage());
		}
	}

	void initHelper() {
		helper = new UpdateHelper(this.getApplicationContext(), url, METHOD, appName);
	}

	public void getNewVersion() {
		handler.sendEmptyMessage(2);
	}

	public void onShowUpdateDialog() {
		if (lis != null) {
			lis.onShowUpdateDialog();
		}
	}

	void showNewVersionNotification(String top, String msg) {

		Notification notification = new Notification();
		notification.icon = R.drawable.notification_update_icon;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.tickerText = top;
		notification.when = System.currentTimeMillis();
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_succeed);
		notification.contentView = remoteViews;
		remoteViews.setTextViewText(R.id.title, appName);
		remoteViews.setTextViewText(R.id.text, msg);
		Intent intent = new Intent(this, UpdateDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(NOTIFI, true);
		intent.putExtra(NOTIFI_TYPE, notifyNewVersion);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 200, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = pendingIntent;
		notificationManager.notify(notifyNewVersion, notification);
	}

	void showNewVersionSetupNotification() {
		Notification notification = new Notification();
		notification.icon = R.drawable.notification_update_icon;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.tickerText = "下载成功！";
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_succeed);
		notification.contentView = remoteViews;
		remoteViews.setTextViewText(R.id.title, appName);
		remoteViews.setTextViewText(R.id.text, "下载成功！点击立即安装！");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri u = Uri.parse("file://" + path);
		Log.e("", "onSuccess4:" + u);
		intent.setDataAndType(u, "application/vnd.android.package-archive");
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 200, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = pendingIntent;
		notificationManager.notify(notifyNewVersion, notification);
	}

	void setupNewVersion() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri u = Uri.parse("file://" + path);
		intent.setDataAndType(u, "application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	void showNewVersionDowningNotification(int progress) {

		Notification notification = new Notification();
		notification.icon = R.drawable.notification_update_icon;
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.tickerText = "正在努力为您更新！";
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_execute);
		notification.contentView = remoteViews;
		remoteViews.setTextViewText(R.id.title, appName);
		if (progress > 100 || progress < 0) {
			remoteViews.setViewVisibility(R.id.text, View.GONE);
			remoteViews.setViewVisibility(R.id.tv_pro, View.GONE);
			remoteViews.setViewVisibility(R.id.tv_pro2, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.layout1, View.GONE);
			remoteViews.setViewVisibility(R.id.layout2, View.VISIBLE);
		} else {
			remoteViews.setTextViewText(R.id.text, "正在更新");
			remoteViews.setViewVisibility(R.id.text, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.tv_pro, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.tv_pro2, View.GONE);
			remoteViews.setViewVisibility(R.id.layout1, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.layout2, View.GONE);
			remoteViews.setProgressBar(R.id.pro, 100, progress, false);
			remoteViews.setTextViewText(R.id.tv_pro, progress + "%");
		}
		Intent intent = new Intent(this, UpdateDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(NOTIFI, true);
		intent.putExtra(NOTIFI_TYPE, notifyDowning);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 200, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = pendingIntent;
		notificationManager.notify(notifyNewVersion, notification);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (NetworkUtil.isNetworkValidate(context)) {
				if ((SystemClock.uptimeMillis() - dur) > INTERVAL_SMALL && !isDoing) {
					startService(new Intent(context, UpdateService.class));
					handler.sendEmptyMessage(1);
				} else if (isDowning && NetworkUtil.isWifi(context)) {
					handler.sendEmptyMessage(2);
				}
			}
		}
	}

	private String getPath() {
		File dir = getDir("apk", Context.MODE_PRIVATE | Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		if (dir != null) {
			return dir.getAbsolutePath();
		}
		return null;
	}

	public interface OnUpdateListener {
		public boolean onStartCheck();

		public void onShowUpdateDialog();

		public boolean onCheckComplete(boolean hasNew, ModelUpdate model);

		public boolean onCheckError();

		public boolean onStartDown();

		public boolean onDown(long curr, long count);

		public boolean onDownComplete(String path);

		public boolean onDownError(String str);
	}

}
