package net.tsz.afinal.bitmap.core;

import java.lang.ref.WeakReference;

import net.tsz.afinal.FinalBitmap.BitmapLoadAndDisplayTask;
import android.graphics.NinePatch;
import android.graphics.drawable.NinePatchDrawable;

public class AsyncNinePathDrawable extends NinePatchDrawable implements
		AsyncBitmapDrawable {
	private final WeakReference<BitmapLoadAndDisplayTask> bitmapWorkerTaskReference;

	public AsyncNinePathDrawable(NinePatch np,
			BitmapLoadAndDisplayTask bitmapWorkerTask) {
		super(np);
		bitmapWorkerTaskReference = new WeakReference<BitmapLoadAndDisplayTask>(
				bitmapWorkerTask);
	}

	public BitmapLoadAndDisplayTask getBitmapWorkerTask() {
		return bitmapWorkerTaskReference.get();
	}
}
