package com.gaiay.base.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.view.View;

public class BitmapUtil extends FileUtil {

	public static final String IMG_TYPE = ".jpg";

	/**
	 * 计算给定Bitmap占用的空间大小,如果bmp参数为null,则返回-1
	 * 
	 * @param bmp
	 *            给定的Bitmap
	 * @return 计算所得的结果
	 */
	public static int getBitmapSize(Bitmap bmp) {
		if (bmp == null) {
			return -1;
		}
		// int w = bmp.getWidth();
		// int h = bmp.getHeight();
		// if (bmp.getConfig() == Config.ALPHA_8) {
		// return w * h * 1;
		// } else if(bmp.getConfig() == Config.ARGB_4444) {
		// return w * h * 2;
		// } else if(bmp.getConfig() == Config.ARGB_8888) {
		// return w * h * 4;
		// } else if(bmp.getConfig() == Config.RGB_565) {
		// return w * h * 2;
		// }

		return bmp.getRowBytes() * bmp.getHeight();
		// return -1;
	}

	/**
	 * 获取指定大小的Bitmap
	 * 
	 * @param bmp
	 * @param max
	 *            指定的大小，单位是KB
	 * @return
	 */
	public static Bitmap getSpecilSizeBitmap(Bitmap bmp, int max) {
		if (max <= 0 || bmp == null || bmp.isRecycled()) {
			return bmp;
		}
		double size = getBitmapSize(bmp) / 1024;
		if (size <= max) {
			return bmp;
		}
		double f = (double) max / (double) size;
		return ThumbnailUtils.extractThumbnail(bmp, (int) (bmp.getWidth() * Math.sqrt(f)),
				(int) (bmp.getHeight() * Math.sqrt(f)), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	}

	public static byte[] getBitmapByteSize(Bitmap bmp) {
		if (bmp == null) {
			return null;
		}
		return Utils.bmpToByteArray(bmp, true);
	}

	/**
	 * 将Bitmap转换为字节数组
	 * 
	 * @param bmp
	 * @return
	 */
	public static byte[] convertBitmapToBytes(Bitmap bmp) {
		if (bmp == null || bmp.isRecycled()) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static String getImgLocName(String url) {
		return MD5.md5Upper(url.toString()) + IMG_TYPE;
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * 得到 图片旋转 的角度
	 * 
	 * @param filepath
	 * @return
	 */
	public static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		if (StringUtil.isBlank(filepath)) {
			return 0;
		}
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
			Log.e("cannot read exif:" + ex);
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			}
		}
		return degree;
	}

	public static Bitmap autoScaleBitmap(String picUri) {
		if (StringUtil.isBlank(picUri)) {
			return null;
		}
		int angle = getExifOrientation(picUri);
		try {
			Bitmap photoViewBitmap;
			if (angle != 0) { // 如果照片出现了 旋转 那么 就更改旋转度数
				Matrix matrix = new Matrix();
				matrix.postRotate(angle);
				Bitmap bmp = BitmapFactory.decodeFile(picUri);
				photoViewBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			} else {
				photoViewBitmap = BitmapFactory.decodeFile(picUri);
			}
			return photoViewBitmap;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return BitmapFactory.decodeFile(picUri);
		}
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 获取指定view上的图像
	 * 
	 * @param v
	 * @return
	 */
	public static Bitmap createViewBitmap(View v) {
		if (v == null) {
			Log.e("传入的view为null");
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		v.draw(canvas);
		return bitmap;
	}
}
