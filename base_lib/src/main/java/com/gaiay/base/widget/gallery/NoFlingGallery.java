package com.gaiay.base.widget.gallery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class NoFlingGallery extends Gallery {

	public NoFlingGallery(Context context) {
		super(context);
	}
	
	public NoFlingGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public NoFlingGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int key;
		if (e2.getX() > e1.getX()) {
			key = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			key = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(key, null);
		return true;
	}
	
}
