package com.gaiay.base.net.bitmap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.gaiay.base.BaseApplication;
import com.gaiay.base.net.NetworkUtil;
import com.gaiay.base.util.FileUtil;
import com.gaiay.base.util.MD5;
import com.gaiay.base.util.StringUtil;

public class BitmapLoader {
	private String INCOMPLETE_SUFFIX = ".gaiay";
	private int BUFFER_SIZE = 1024;
	private String mCacheFolder;
	
	private Context cxt;
	private Map<Integer, SoftReference<Bitmap>> mBitmaps = new HashMap<Integer, SoftReference<Bitmap>>();
	
	private int mHeight;
	private int mWidth;
	private Handler mHandler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {
			try {
				Object[] objs = null;
				if (msg.obj != null && (objs = (Object[]) msg.obj) != null) {
					((ImageView) objs[0]).setImageBitmap(((Bitmap) objs[1]));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
	};
	
	public BitmapLoader(Context cxt) {
		this.cxt = cxt;
	}
	
	public void setCacheFolder(String cacheFolder) {
		this.mCacheFolder = cacheFolder;
	}
	
	public void setHeight(int height) {
		mHeight = height;
	}
	
	public void setWidth(int width) {
		mWidth = width;
	}
	
	public Bitmap getBitmap() {
		return getBitmap(0);
	}
	
	public Bitmap getBitmap(int position) {
		return mBitmaps.get(position).get();
	}
	
	public void recycle() {
		recycle(0);
	}
	
	public void recycle(int position) {
		Bitmap b = mBitmaps.get(position).get();
		if (b != null && !b.isRecycled()) {
			b.recycle();
			System.gc();
			b = null;
		}
	}
	
	/**
	 * 加载图片<br>
	 * step1.检查是否有本地图片，有的话，直接返回<br>
	 * step2.判断网络是否可用，不可以用的话调用{@link OnNetworkErrorListener}<br>
	 * step3.连接网络，加载失败的话，会进行自动重连，尝试3次<br>
	 * step4.图片下载成功，则判断SD卡空间是否足够，够的话，调用{@link OnSuccessListener}；否则调用{@link OnNoMoreSpaceListener}<br>
	 * step5.下载失败则调用{@link OnFailedListener}
	 */
	public void load(final ImageView imageView, final String path) {
		load(imageView, 0, path);
	}
	
	/**
	 * 加载图片<br>
	 * step1.检查是否有本地图片，有的话，直接返回<br>
	 * step2.判断网络是否可用，不可以用的话调用{@link OnNetworkErrorListener}<br>
	 * step3.连接网络，加载失败的话，会进行自动重连，尝试3次<br>
	 * step4.图片下载成功，则判断SD卡空间是否足够，够的话，调用{@link OnSuccessListener}；否则调用{@link OnNoMoreSpaceListener}<br>
	 * step5.下载失败则调用{@link OnFailedListener}
	 */
	public void load(final ImageView imageView, final int position, final String path) {
		if (imageView == null) {
			return;
		}
		if (StringUtil.isBlank(mCacheFolder)) {
			if (StringUtil.isBlank(BaseApplication.app.getCacheFolder())) {
				throw new IllegalStateException("请调用setCacheFolder()方法初始化缓存目录");
			} else {
				mCacheFolder = BaseApplication.app.getCacheFolder();
			}
		}
		Bitmap bitmap = mBitmaps.get(position).get();
		if (bitmap != null && !bitmap.isRecycled()) {
			mHandler.sendMessage(mHandler.obtainMessage(0, new Object[] { imageView, bitmap }));
		}
		final String filename = MD5.md5Upper(path) + path.substring(path.lastIndexOf("."));
		File cache = new File(mCacheFolder);
		if (!cache.exists()) {
			cache.mkdirs();
		}
		// 判断本地是否有该图片
		final File imageFile = new File(mCacheFolder, filename);
		if (imageFile.length() > 0) {
			try {
				Bitmap b = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), getBitmapFactoryOptions(imageFile));
				mBitmaps.put(position, new SoftReference<Bitmap>(b));
				mHandler.sendMessage(mHandler.obtainMessage(0, new Object[] { imageView, b }));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		if (NetworkUtil.isNetworkValidate(cxt)) {
			ThreadPool.getInstance().getPool().execute(new Runnable() {
				
				@Override
				public void run() {
					if (StringUtil.isBlank(path) || cxt == null) {
						return;
					}
					InputStream is = null;
					OutputStream os = null;
					HttpURLConnection conn = null;
					try {
						URL url = new URL(path);
						int i = 0;
						while (i < 3) {
							i++;
							conn = null;
							conn = NetworkUtil.toConn(url);
							if (conn == null) {
								continue;
							}
							conn.connect();
							if ((is = conn.getInputStream()) != null) {
								break;
							}
							conn.disconnect();
							conn = null;
						}
						if (conn != null && is != null) {
							Bitmap b = null;
							// 判断SD卡空间是否足够
							if (FileUtil.isSDCardFree()) { // 是
								File newFile = new File(mCacheFolder, filename + INCOMPLETE_SUFFIX);
								os = new FileOutputStream(newFile);
								byte[] buffer = new byte[1024];
								int len = 0;
								while ((len = is.read(buffer)) != -1) {
									os.write(buffer, 0, len);
								}
								newFile.renameTo(imageFile);
								b = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), getBitmapFactoryOptions(imageFile));
							} else { // 否
								b = BitmapFactory.decodeStream(is, null, getBitmapFactoryOptions(is));
								if (onNoMoreSpaceListener != null) {
									onNoMoreSpaceListener.onNoMoreSpace();
								}
							}
							if (b != null) {
								mBitmaps.put(position, new SoftReference<Bitmap>(b));
								mHandler.sendMessage(mHandler.obtainMessage(0, new Object[] { imageView, b }));
								if (onSuccessListener != null) {
									onSuccessListener.onSuccess();
								}
							}
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (is != null) {
							try {
								is.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							is = null;
						}
						if (os != null) {
							try {
								os.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							os = null;
						}
					}
					if (onFailedListener != null) {
						onFailedListener.onFailed();
					}
				}
				
			});
		} else {
			if (onNetworkErrorListener != null) {
				onNetworkErrorListener.onNetworkError();
			}
		}
	}
	
	private BitmapFactory.Options getBitmapFactoryOptions(InputStream is) throws Exception {
		if (mHeight == 0 || mWidth == 0) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new BufferedInputStream(is, BUFFER_SIZE), null, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, mHeight * mWidth);
			opts.inJustDecodeBounds = false;
			return opts;
		}
		return null;
	}
	
	private BitmapFactory.Options getBitmapFactoryOptions(File file) throws Exception {
		if (mHeight == 0 || mWidth == 0) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE), null, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, mHeight * mWidth);
			opts.inJustDecodeBounds = false;
			return opts;
		}
		return null;
	}

	/**
	 * 计算BitmapFactory.Options的inSampleSize
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	private int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
				Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	
	/**
	 * 加载成功
	 */
	private OnSuccessListener onSuccessListener;
	
	public interface OnSuccessListener {
		public abstract void onSuccess();
	}

	public void setOnSuccessListener(OnSuccessListener onSuccessListener) {
		this.onSuccessListener = onSuccessListener;
	}

	/**
	 * 加载失败
	 */
	private OnFailedListener onFailedListener;
	
	public interface OnFailedListener {
		public abstract void onFailed();
	}
	
	public void setOnFailedListener(OnFailedListener onFailedListener) {
		this.onFailedListener = onFailedListener;
	}
	
	/**
	 * 网络异常
	 */
	private OnNetworkErrorListener onNetworkErrorListener;
	
	public interface OnNetworkErrorListener {
		public abstract void onNetworkError();
	}
	
	public void setOnNetworkErrorListener(OnNetworkErrorListener onNetworkErrorListener) {
		this.onNetworkErrorListener = onNetworkErrorListener;
	}
	
	/**
	 * 内存卡不足
	 */
	private OnNoMoreSpaceListener onNoMoreSpaceListener;
	
	public interface OnNoMoreSpaceListener {
		public abstract void onNoMoreSpace();
	}
	
	public void setOnNoMoreSpaceListener(OnNoMoreSpaceListener onNoMoreSpaceListener) {
		this.onNoMoreSpaceListener = onNoMoreSpaceListener;
	}
}
