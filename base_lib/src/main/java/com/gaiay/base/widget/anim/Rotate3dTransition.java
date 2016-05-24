package com.gaiay.base.widget.anim;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import com.gaiay.base.util.Log;

public class Rotate3dTransition {
	ViewGroup mContainer;
	View fromView, toView;
	boolean isAnimationEnd=false;
	boolean isZheng;
	AnimListener al;
	public Rotate3dTransition(ViewGroup container, View from, View to, boolean isZheng, AnimListener al) {
		mContainer = container;
		fromView = from;
		toView = to;
		this.isZheng = isZheng;
		this.al = al;
		mContainer.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
	}

	public void start() {
		applyRotation(0, 90);
		//applyRotation(180, 90);
	}

	/**
	 * Setup a new 3D rotation on the container view.
	 * 
	 * @param start
	 *          the start angle at which the rotation must begin
	 * @param end
	 *          the end angle of the rotation
	 */
	private void applyRotation(float start, float end) {
		// Find the center of the container
		final float centerX = mContainer.getWidth() / 2.0f;
		final float centerY = mContainer.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		Rotate3dAnimation rotation;
		if (isZheng) {
			rotation = new Rotate3dAnimation(0, 90, centerX, centerY, 310.0f, true);
		} else {
			rotation = new Rotate3dAnimation(360, 270, centerX, centerY, 310.0f, true);
		}
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView());

		mContainer.startAnimation(rotation);
	}

	/**
	 * This class listens for the end of the first half of the animation. It then
	 * posts a new action that effectively swaps the views when the container is
	 * rotated 90 degrees and thus invisible.
	 */
	private final class DisplayNextView implements Animation.AnimationListener {
		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			mContainer.post(new SwapViews());
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	public interface AnimListener {
		public void onEnd();
		public void onPre();
	}
	
	/**
	 * This class is responsible for swapping the views and start the second half
	 * of the animation.
	 */
	private final class SwapViews implements Animation.AnimationListener,Runnable {
		public void run() {
			Log.e("SwapViews", "SwapViews");
			final float centerX = mContainer.getWidth() / 2.0f;
			final float centerY = mContainer.getHeight() / 2.0f;
			Rotate3dAnimation rotation;

			if(fromView!=toView){
				fromView.setVisibility(View.GONE);
			}
			toView.setVisibility(View.VISIBLE);
			toView.requestFocus();
			if (isZheng) {
				rotation = new Rotate3dAnimation(270, 360, centerX, centerY, 310.0f, false);
			} else {
				rotation = new Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
			}
			rotation.setDuration(500);
			//rotation.setFillAfter(true);
			rotation.setFillBefore(true);
			rotation.setInterpolator(new DecelerateInterpolator());
			rotation.setAnimationListener(this);
			mContainer.startAnimation(rotation);
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			isAnimationEnd=true;
			if (al != null) {
				al.onEnd();
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			if (al != null) {
				al.onPre();
			}
		}
	}
	public boolean isAnimationEnd(){
		return isAnimationEnd;
	}
}
