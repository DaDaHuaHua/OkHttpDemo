package com.gaiay.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class KeyboardMonitorLayout extends RelativeLayout {
	public static final byte KEYBOARD_STATE_SHOW = -3;
	public static final byte KEYBOARD_STATE_HIDE = -2;
	public static final byte KEYBOARD_STATE_INIT = -1;
	
	private boolean mHasInit;
	private boolean mHasKeybord;
	private int mHeight;
	private onKybdsChangeListener mListener;
	private int mLayoutHeight;

	public KeyboardMonitorLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public KeyboardMonitorLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public KeyboardMonitorLayout(Context context) {
		this(context, null);
	}
	
	/**
	 * set keyboard state listener
	 */
	public void setOnkbdStateListener(onKybdsChangeListener listener){
		mListener = listener;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!mHasInit) {
			mHasInit = true;
			mHeight = b;
			mLayoutHeight = b;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_INIT);
			}
		} else {
			if (mHeight > mLayoutHeight) {
				mHeight = mLayoutHeight;
			}
			if (b > mLayoutHeight) {
				b = mLayoutHeight;
			}
			mHeight = mHeight < b ? b : mHeight;// 取最大
		}
		if (mHasInit && mHeight > b && !mHasKeybord) {
			mHasKeybord = true;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_SHOW);
			}
		}
		if (mHasInit && mHasKeybord && mHeight == b) {
			mHasKeybord = false;
			if (mListener != null) {
				mListener.onKeyBoardStateChange(KEYBOARD_STATE_HIDE);
			}
		}
	}
	
	public interface onKybdsChangeListener{
		/**
		 * -1:初始化
		 * -2：键盘隐藏
		 * -3：键盘显示
		 * @param state
		 */
		public void onKeyBoardStateChange(int state);
	}
}
