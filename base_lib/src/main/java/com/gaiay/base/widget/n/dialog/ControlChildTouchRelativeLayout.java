package com.gaiay.base.widget.n.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class ControlChildTouchRelativeLayout extends RelativeLayout {

	private boolean touchModel = false;
	
	public ControlChildTouchRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlChildTouchRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ControlChildTouchRelativeLayout(Context context) {
		super(context);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return touchModel;
	}
	
	public void setChildTouch(boolean isCan) {
		touchModel = !isCan;
	}
	
}