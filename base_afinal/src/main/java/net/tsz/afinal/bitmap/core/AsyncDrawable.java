package net.tsz.afinal.bitmap.core;

import java.lang.ref.WeakReference;

import net.tsz.afinal.FinalBitmap.BitmapLoadAndDisplayTask;
import net.tsz.afinal.utils.Utils;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class AsyncDrawable extends BitmapDrawable implements
		AsyncBitmapDrawable {
	private final WeakReference<BitmapLoadAndDisplayTask> bitmapWorkerTaskReference;

	public AsyncDrawable(Resources res, Bitmap bitmap,
			BitmapLoadAndDisplayTask bitmapWorkerTask) {
		super(res, bitmap);
		bitmapWorkerTaskReference = new WeakReference<BitmapLoadAndDisplayTask>(
				bitmapWorkerTask);
	}

	public AsyncDrawable(Resources res, Drawable drawable,
			BitmapLoadAndDisplayTask bitmapWorkerTask) {
		super(res, drawable == null ? null
				: Utils.convertDrawable2BitmapByCanvas(drawable));
		bitmapWorkerTaskReference = new WeakReference<BitmapLoadAndDisplayTask>(
				bitmapWorkerTask);
	}

	public BitmapLoadAndDisplayTask getBitmapWorkerTask() {
		return bitmapWorkerTaskReference.get();
	}
}
