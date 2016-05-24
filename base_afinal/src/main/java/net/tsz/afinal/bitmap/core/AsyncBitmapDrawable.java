package net.tsz.afinal.bitmap.core;

import net.tsz.afinal.FinalBitmap.BitmapLoadAndDisplayTask;


public interface AsyncBitmapDrawable {
	public BitmapLoadAndDisplayTask getBitmapWorkerTask();
}
