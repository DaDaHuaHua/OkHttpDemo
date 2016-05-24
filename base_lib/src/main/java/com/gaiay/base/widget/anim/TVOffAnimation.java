package com.gaiay.base.widget.anim;


import android.graphics.Matrix;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;


public class TVOffAnimation extends Animation {

	private int halfWidth;

	private int halfHeight;

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {

		super.initialize(width, height, parentWidth, parentHeight);
		setDuration(400);
		setFillAfter(true);
		//保存View的中心点
		halfWidth = width / 2;
		halfHeight = height / 2;
		setInterpolator(new AccelerateDecelerateInterpolator());
		
	}
	int num = 0;
	float in;
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {

		final Matrix matrix = t.getMatrix();
//		if (interpolatedTime < 0.8) {
//			matrix.preScale(1 + 0.625f * interpolatedTime, 1 - interpolatedTime / 0.8f + 0.01f, halfWidth, halfHeight);
//		} else if(interpolatedTime > 0.8) {
//			matrix.preScale(7.5f * (1 - interpolatedTime), 0.002f, halfWidth, halfHeight);
//			num ++;
//			Log.e("num:" + num);
//			if (num == 2) {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
			
//		}
		if (interpolatedTime < 0.7) {
			matrix.preScale(1 + 0.625f * interpolatedTime, 1 - interpolatedTime / 0.6805451f, halfWidth, halfHeight);
			in = interpolatedTime;
		} else if(interpolatedTime > 0.8) {
			float f1 = 1 - interpolatedTime;
			f1 = f1 / 62.4595867f;
			if (f1 < 0.0015) {
				f1 = 0.0015f;
			}
			matrix.preScale(7.5f * (1 - interpolatedTime), f1, halfWidth, halfHeight);
		} else {
			matrix.preScale(1 + 0.625f * in, 0.003f, halfWidth, halfHeight);
		}
	}
}