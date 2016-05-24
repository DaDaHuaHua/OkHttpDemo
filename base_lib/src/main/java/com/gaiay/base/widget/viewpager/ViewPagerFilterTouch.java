package com.gaiay.base.widget.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerFilterTouch extends ViewPager  {

	float topY;
	float bottomY;
	boolean isFileY = false;
	
	public ViewPagerFilterTouch(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewPagerFilterTouch(Context context) {
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (isFileY) {
			if (me.getY() > topY && me.getY() < bottomY) {
				return false;
			}
		}
		return super.onTouchEvent(me);
	}
	
	public void setYToYNoTouch(float topY, float bottomY) {
		if (topY >= bottomY) {
			isFileY = false;
			return;
		}
		isFileY = true;
		this.topY = topY;
		this.bottomY = bottomY;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent me) {
		if (isFileY) {
			if (me.getY() > topY && me.getY() < bottomY) {
				return false;
			}
		}
		return super.onInterceptTouchEvent(me);
	}
}