package com.gaiay.base.framework.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.gaiay.base.net.bitmap.BitmapManager;
import com.gaiay.base.util.Log;
import com.gaiay.base.widget.dialog.Dialog;
import com.gaiay.base.widget.dialog.Dialog.MShowAndDisEndListener;

public abstract class RootContainer extends FragmentActivity {

	FrameLayout viewLayer;

	FragmentManager fm;

	public int containerId;
	public static RootContainer root;

	List<Page> stack;
	
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		fm = getSupportFragmentManager();
		stack = new ArrayList<Page>();
		root = this;
	}

	public void startPage(Page page) {
		if (page == null) {
			return;
		}
		if (stack.size() > 0) {
			stack.get(stack.size() - 1).setIsBack(false);
			page.setIsBack(false);
		}
		stack.add(page);
		FragmentTransaction tra = fm.beginTransaction();
		tra.add(containerId, page);
//		if (stack.size() > 1) {
//			tra.detach(stack.get(stack.size() - 2));
//		}
//		tra.attach(page);
		tra.addToBackStack(null);
		tra.commitAllowingStateLoss();
	}
	
	public void clearBacks(int clears) {
		if (stack.size() < clears || clears < 0
				|| clears > fm.getBackStackEntryCount()) {
			Log.e("clears参数传输错误：clears（" + clears + ") stack.size("
					+ stack.size() + ")" + " fm.getBackStackEntryCount("
					+ fm.getBackStackEntryCount() + ")");
			return;
		}
		int cou = clears;
		while (cou > 0) {
			stack.remove(stack.size() - 1);
			cou--;
		}
		for (int i = 0; i < clears; i++) {
			fm.popBackStack();
		}
	}
	public void startPageClearBacks(Page page, int clears) {
		if (page == null) {
			return;
		}
		if (stack.size() < clears || clears < 0
				|| clears > fm.getBackStackEntryCount()) {
			Log.e("clears参数传输错误：clears（" + clears + ") stack.size("
					+ stack.size() + ")" + " fm.getBackStackEntryCount("
					+ fm.getBackStackEntryCount() + ")");
			return;
		}
		int cou = clears;
		while (cou > 0) {
			stack.remove(stack.size() - 1);
			cou--;
		}
		stack.add(page);
		for (int i = 0; i < clears; i++) {
			fm.popBackStack();
		}
		FragmentTransaction tra = fm.beginTransaction();
		tra.replace(containerId, page);
		tra.addToBackStack(null);
		tra.commitAllowingStateLoss();
	}

	public void startPageNoBack(Page page) {
		if (page == null) {
			return;
		}
		stack.clear();
		stack.add(page);
		Log.e("fm.getBackStackEntryCount():" + fm.getBackStackEntryCount());
		FragmentTransaction tra = fm.beginTransaction();
		for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
			fm.popBackStack();
		}
		tra.replace(containerId, page);
		tra.commitAllowingStateLoss();
	}

	public final String key_backstack = "key_backstack";

	@Override
	public void onBackPressed() {
		if (stack.size() > 0 && stack.get(stack.size() - 1).onBackPressed()) {
			return;
		}
		doFinish();
	}
	
	private void doFinish() {
		if (fm.getBackStackEntryCount() > 0) {
			if (stack.size() > 1) {
				Page page = stack.remove(stack.size() - 1);
				Page p2 = stack.get(stack.size() - 1);
				page.setIsBack(true);
				p2.setIsBack(true);
				if (p2 != null && p2.isForResult) {
					p2.onPageResult(page.isResult(), page.requestCode,
							page.getResult());
				}
			}
			fm.popBackStack();
		} else {
			Log.e("退出了~");
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (stack != null && stack.size() > 0) {
			if (stack.get(stack.size() - 1).onKeyDown(keyCode, event)) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void setViewLayer(FrameLayout view) {
		viewLayer = view;
	}

	public void showViewLayer(View view, Animation anim) {
		if (view == null || viewLayer == null) {
			return;
		}

		viewLayer.removeAllViews();
		viewLayer.addView(view);
		viewLayer.setVisibility(View.VISIBLE);
		viewLayer.bringToFront();
		if (anim != null) {
			view.startAnimation(anim);
		}
	}

	public void showViewLayer() {
		if (viewLayer == null || viewLayer.getChildCount() <= 0) {
			return;
		}

		viewLayer.setVisibility(View.VISIBLE);
		if (viewLayer.getChildAt(0) != null
				&& viewLayer.getChildAt(0).getAnimation() != null) {
			viewLayer.getChildAt(0).startAnimation(
					viewLayer.getChildAt(0).getAnimation());
		}

	}

	public void closeViewLayer(Animation anim) {
		if (viewLayer == null || viewLayer.getChildCount() <= 0) {
			return;
		}
		if (anim != null) {

		}
	}

	public boolean isCurrClass(Class<?> c) {
		if (stack.size() <= 0 || c == null) {
			return false;
		}
		return c.getName().equals(stack.get(stack.size() - 1).getClass().getName());
	}

	public void back() {
		super.onBackPressed();
	}

	public boolean isFull = true;

	/**
	 * 让fragment的位置全屏
	 */
	public void fullScreen() {
		isFull = true;
	}

	public void fullScreenClose() {
		isFull = false;
	}
	
	public void fullScreenNoAnim() {
		isFull = true;
	}

	public void fullScreenCloseNoAnim() {
		isFull = false;
	}

	public void autoChangeFullScreen() {
		if (isFull) {
			fullScreenClose();
		} else {
			fullScreen();
		}
	}
	
	boolean isForceScreen = false;
	
	public void forceFullScreen(boolean isFull) {
		if (isFull) {
			fullScreenNoAnim();
		} else {
			fullScreenCloseNoAnim();
		}
		isForceScreen = true;
	}
	public void forceFullScreenUnlock(final boolean isFull) {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				isForceScreen = false;
				if (isFull) {
					fullScreenNoAnim();
				} else {
					fullScreenCloseNoAnim();
				}
			}
		}, 300);
	}
	public boolean isLockScreen() {
		return isForceScreen;
	}

	@Override
	public void finish() {
		if (showDefaultFinishDialog) {
			Dialog d = new Dialog(this);
			d.setMsg("确定退出么？");
			d.setTitle("提示");
			d.setQD("", null);
			d.setOnShowAndDisEndListener(new MShowAndDisEndListener() {
				@Override
				public void onShowEnd() {
				}
				@Override
				public void onDisEnd(boolean isQD) {
					if (isQD) {
						BitmapManager.recycleSelf();
						System.exit(0);
					}
				}
			});
			d.show();
		} else {
			super.finish();
		}
	}

	public boolean showDefaultFinishDialog = true;
	
	public void gotoMain() {
		
	}
	
}
