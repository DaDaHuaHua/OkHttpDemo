package com.gaiay.support.update;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.SystemClock;

import com.gaiay.base.BaseApplication;
import com.gaiay.base.common.CommonCode;
import com.gaiay.base.net.NetAsynTask;
import com.gaiay.base.net.NetCallback;
import com.gaiay.base.request.BaseRequest;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.Mobile;
import com.gaiay.base.util.StringUtil;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UpdateHelper {
    public final static String DOWNLOAD_URL = "http://a.app.qq.com/o/simple.jsp?pkgname=com.gaiay.mobilecard";

    private Context mContext;
	String url;
	String method;
	String apkName;
	ReqUpdate mReqUpdate;
	OnCheckEndListener lis;

	FinalHttp fh;
	HttpHandler<File> hh;

	public interface OnCheckEndListener {
		public void hasNewVersion(ModelUpdate model);

		public void noNewVersion();

		public void checkError();
	}

	public UpdateHelper(Context context, String url, String method, String apkName) {
        this.mContext = context;
		this.url = url;
		this.method = method;
		this.apkName = apkName;
	}

	public void checkVersionByGet(String type, OnCheckEndListener lis) {
		final String versionName = Mobile.getAppVersionName(BaseApplication.app);
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", "");
		map.put("version", versionName);
		map.put("type", type);
		map.put("appVersion", versionName);
		map.put("appOs", "android");

		this.lis = lis;

		final BaseRequest<Boolean> mReq = new BaseRequest<Boolean>() {
			@Override
			public int parseJson(String paramString) throws JSONException {
				if (StringUtil.isNotBlank(paramString)) {
					try {
						JSONObject jo = new JSONObject(paramString);
                        int code = jo.optInt("code", 1);
						if (code != 0 && code != 9999) {
							return CommonCode.ERROR_OTHER;
						} else {
							model = new ModelUpdate();
							model.name = jo.optString("name", "");
							model.code = jo.optString("version", "");
							model.desc = jo.optString("message", "");
							model.date = jo.optString("date", "");
							model.url = jo.optString("url", "");
							try {
								model.hasNewVersion = model.code != null
										&& (model.code.replace(".", "").compareTo(versionName) > 0);
							} catch (Exception e) {
								e.printStackTrace();
							}
							model.isForce = jo.getInt("code") == 9999;
							if (model.isForce) {
								model.hasNewVersion = true;
							}

							return CommonCode.SUCCESS;
						}
					} catch (JSONException e) {
						e.printStackTrace();
						model = null;
					}
				}
				return CommonCode.ERROR_OTHER;
			}
		};
		NetAsynTask.connectByGet(this.url, map, new NetCallback() {
			@Override
			public void onGetSucc() {
				if (UpdateHelper.this.lis != null) {
					if (model == null && !model.hasNewVersion) {
						UpdateHelper.this.lis.noNewVersion();
					} else {
						UpdateHelper.this.lis.hasNewVersion(model);
					}
				}
			}

			@Override
			public void onGetFaild() {
				if (UpdateHelper.this.lis != null) {
					UpdateHelper.this.lis.noNewVersion();
				}
			}

			@Override
			public void onGetError() {
				if (UpdateHelper.this.lis != null) {
					UpdateHelper.this.lis.noNewVersion();
				}
			}

			@Override
			public void onComplete() {

			}
		}, mReq);
	}

	public void checkVersion(String cid, OnCheckEndListener lis) {
		if (cid == null || cid.equals("")) {
			Log.e("checkVersion", ">>>>checkVersion::cid为NULL!!!");
			return;
		}
		this.lis = lis;
		Map<String, String> map = new HashMap<String, String>();
		map.put("cid", cid);
		map.put("version", getVersion() + "");
		map.put("method", method);
		map.put("project", UpdateService.project);
		mReqUpdate = new ReqUpdate();
		Log.e("checkVersion", ">>>>cid::" + cid);
		Log.e("checkVersion", ">>>>method::" + method);
		Log.e("checkVersion", ">>>>url::" + url);

		AjaxParams ap = new AjaxParams();
		ap.put("json", getJSONRequest(map));
		if (fh == null) {
			fh = new FinalHttp();
		}
		fh.post(url, ap, new AjaxCallBack<String>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                Log.e("checkVersion", ">>>>onFailure" + strMsg);
                if (UpdateHelper.this.lis != null) {
                    UpdateHelper.this.lis.noNewVersion();
                }
            }

            @Override
            public void onSuccess(String t) {
                Log.e("checkVersion", ">>>>onSuccess：" + t);
                if (UpdateHelper.this.lis != null) {
                    try {
                        mReqUpdate.parseJSON(t);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!mReqUpdate.hasData()) {
                        UpdateHelper.this.lis.noNewVersion();
                    } else {
                        model = mReqUpdate.model;
                        UpdateHelper.this.lis.hasNewVersion(model);
                    }
                }
            }

        });

	}

    public void startDownloadPage() {
        // 打开浏览器进行下载
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(UpdateHelper.DOWNLOAD_URL))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

	public void downloadNewVersion(String path, AjaxCallBack<File> callback) {
		if (hasCheckVersion() && (path != null && !path.equals(""))) {
			if (hh == null || !hh.isRunning()) {
				// File f = new File(path);
				// f.mkdirs();
				// if (f.exists()) {
				// f.delete();
				// }
				// if (fh == null) {
				// fh = new FinalHttp();
				// }
				Log.e("downloadNewVersion", model.url);
				// hh = fh.download(model.url, path, false, callback);

                // 通过APP下载应用
				download(model.url, path, callback);
			} else {
				Log.e("downloadNewVersion", "下载新版本apk的线程已经启动！");
			}
		}
	}

	ModelUpdate model = null;

	private boolean hasCheckVersion() {
		return model.url != null && !"".equals(model.url);
	}

	public static int getVersion() {
		try {
			return UpdateService.instance.getPackageManager().getPackageInfo(UpdateService.instance.getPackageName(),
					0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	protected String getJSONRequest(Map<String, String> req) {
		JSONObject json = new JSONObject();
		try {
			for (Map.Entry<String, String> entry : req.entrySet()) {
				json.put(entry.getKey(), entry.getValue());
			}
			return json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	boolean isRunning = false;

	public void download(final String ur, final String path, final AjaxCallBack<File> callback) {
		if (isRunning) {
			return;
		}
		new Thread() {
			public void run() {
				isRunning = true;
				callback.onStart();
				try {
					URL url = new URL(ur);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setConnectTimeout(5 * 1000);
					connection.setReadTimeout(60 * 1000);
					if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
						throw new Exception();
					// 获取输入流
					InputStream is = connection.getInputStream();
					// 创建文件输出流
					FileOutputStream fos = new FileOutputStream(path);
					byte buffer[] = new byte[1024 * 4];
					// 获取文件总长
					float filesize = connection.getContentLength();
					// 记录已下载长度
					float temp = 0;
					int len = 0;
					long pre = 0;
					while ((len = is.read(buffer)) != -1) {
						// 将字节写入文件输出流
						fos.write(buffer, 0, len);
						temp += len;
						Log.d("", temp / filesize * 100 + "%");
						if (SystemClock.uptimeMillis() - pre >= 1000) {
							pre = SystemClock.uptimeMillis();
							callback.onLoading((long) filesize, (long) temp);
						}
					}
					System.err.println("下载完成");
					callback.onSuccess(new File(path));
					fos.flush();
					fos.close();
					is.close();
					connection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
					callback.onFailure(e, 1, "");
				}
				isRunning = false;
			};
		}.start();
	}

}
