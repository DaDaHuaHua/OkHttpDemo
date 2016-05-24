package com.gaiay.base.widget;

import com.gaiay.base.util.Log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class DragGridView extends GridView {

	// 定义基本的成员变量
	private ImageView dragImageView;
	//拖拽的条目以前的位置
	private int dragSrcPosition;
	//拖拽的条目新的位置
	private int dragPosition;
	// x,y坐标的计算
	private int dragPointX;
	private int dragPointY;
	private int dragOffsetX;
	private int dragOffsetY;
	private WindowManager windowManager;
	private WindowManager.LayoutParams windowParams;
	private int scaledTouchSlop;
	private int upScrollBounce;
	private int downScrollBounce;
	private boolean isUseLongClick = false;
//	Vibrator mVib;

	public DragGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void configUseLongClick(boolean isUse) {
		isUseLongClick = isUse;
//		if (isUseLongClick) {
//			mVib = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
//		} else {
//			if (mVib != null) {
//				mVib.cancel();
//				mVib = null;
//			}
//		}
	}
	private int moveCount = 0;
	
	long lastTime = 0;
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.e(ev);
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			if (System.currentTimeMillis() - lastTime < 100 && getOnItemClickListener() != null) {
				getOnItemClickListener().onItemClick(null, getChildAt(pointToPosition(x, y)
							- getFirstVisiblePosition()), pointToPosition(x, y), 0);
			}
			
			if (dragImageView != null && dragPosition != INVALID_POSITION) {
				int upX = (int) ev.getX();
				int upY = (int) ev.getY();
				stopDrag();
				onDrop(upX, upY);
				if (mLoveTouchLis != null) {
					mLoveTouchLis.isNeedTouchEvent(false);
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (moveCount != -1) {
				moveCount ++;
			}
			if (dragPosition != INVALID_POSITION) {
				if (moveCount > 5 && !checkDragPos()) {
					moveCount = -1;
					ViewGroup itemView = (ViewGroup) getChildAt(dragPosition
							- getFirstVisiblePosition());
					dragPointX = x - itemView.getLeft();
					dragPointY = y - itemView.getTop();
					dragOffsetX = (int) (ev.getRawX() - x);
					dragOffsetY = (int) (ev.getRawY() - y);
					View dragger = itemView.findViewById(dragResId);
					// 如果选中拖动图标
					if (dragger != null && dragPointX > dragger.getLeft()
							&& dragPointX < dragger.getRight()
							&& dragPointY > dragger.getTop()
							&& dragPointY < dragger.getBottom() + 20) {
						upScrollBounce = Math.min(y - scaledTouchSlop, getHeight() / 4);
						downScrollBounce = Math.max(y + scaledTouchSlop,
								getHeight() * 3 / 4);
						itemView.setDrawingCacheEnabled(true);
						Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
						startDrag(bm, x, y);
					}
				}
				if (dragImageView != null) {
					int moveX = (int) ev.getX();
					int moveY = (int) ev.getY();
					onDrag(moveX, moveY);
					if (mLoveTouchLis != null) {
						mLoveTouchLis.isNeedTouchEvent(true);
					}
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			lastTime = System.currentTimeMillis();
			initDown(x, y);
			break;
		default:
			break;
		}
		return false;
	}
	
	private boolean inDragModel = false;
	
	private void initDown(int x, int y) {
		final int lx = x;
		final int ly = y;
		moveCount = 0;
		dragPosition = INVALID_POSITION;
		if (isUseLongClick) {
			inDragModel = false;
			new Thread() {
				@Override
				public void run() {
					int llx = lx;
					int lly = ly;
					try {
						sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (moveCount < 20) {
						dragSrcPosition = dragPosition = pointToPosition(llx, lly);
						inDragModel = true;
					} else {
						inDragModel = false;
					}
					super.run();
				}
			}.start();
		} else {
			inDragModel = true;
			dragSrcPosition = dragPosition = pointToPosition(x, y);
		}
	}
	
	
	
	int dragResId = 0;
	
	public void configDragResId(int id) {
		dragResId = id;
	}

	MotionEvent lastEv = null;
	MotionEvent lastEvent = null;
	
	int[] noDragPos;
	
	public void cinfigNoDragPos(int[] noDragPos) {
		this.noDragPos = noDragPos;
	}
	
	private boolean checkDragPos() {
		boolean isDrag = false;
		if (noDragPos != null) {
			for (int i = 0; i < noDragPos.length; i++) {
				if (noDragPos[i] == dragPosition) {
					isDrag = true;
					break;
				}
			}
		}
		return dragPosition == AdapterView.INVALID_POSITION || isDrag;
	}
	
	public void startDrag(Bitmap bm, int x, int y) {
		stopDrag();
		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
		windowParams.x = x - dragPointX + dragOffsetX;
		windowParams.y = y - dragPointY + dragOffsetY;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;
		ImageView imageView = new ImageView(getContext());
		imageView.setImageBitmap(bm);
		windowManager = (WindowManager) getContext().getSystemService("window");
		windowManager.addView(imageView, windowParams);
		dragImageView = imageView;
	}

	public void onDrag(int x, int y) {
		if (dragImageView != null) {
			windowParams.alpha = 0.8f;
			windowParams.x = x - dragPointX + dragOffsetX;
			windowParams.y = y - dragPointY + dragOffsetY;
			windowManager.updateViewLayout(dragImageView, windowParams);
		}
		int tempPosition = pointToPosition(x, y);
		if (tempPosition != INVALID_POSITION) {
			dragPosition = tempPosition;
		}
		// 滚动
		if (y < upScrollBounce || y > downScrollBounce) {
			// 使用setSelection来实现滚动
//			setSelection(dragPosition);
		}
	}

	public void onDrop(int x, int y) {
		// 为了避免滑动到分割线的时候，返回-1的问题
		int tempPosition = pointToPosition(x, y);
		if (tempPosition != INVALID_POSITION) {
			dragPosition = tempPosition;
		}
		// 超出边界处理
		if (y < getChildAt(0).getTop()) {
			// 超出上边界
			dragPosition = 0;
		} else if (y > getChildAt(getChildCount() - 1).getBottom()
				|| (y > getChildAt(getChildCount() - 1).getTop() && x > getChildAt(
						getChildCount() - 1).getRight())) {
			// 超出下边界
			dragPosition = getAdapter().getCount() - 1;
		}
		// 数据交换
		if (dragPosition != dragSrcPosition && dragPosition > -1
				&& dragPosition < getAdapter().getCount()) {
			if (lis != null) {
				lis.OnDragEnd(dragSrcPosition, dragPosition);
			}
		}
	}

	public void stopDrag() {
		if (dragImageView != null) {
			windowManager.removeView(dragImageView);
			dragImageView = null;
			dragPosition = INVALID_POSITION;
		}
	}
	
	OnDragListener lis;
	LoveNoDragTouchEventListener mLoveTouchLis;
	
	public void configOnDragListener(OnDragListener l) {
		lis = l;
	}
	public void configLoveNoDragTouchEventListener(LoveNoDragTouchEventListener l) {
		mLoveTouchLis = l;
	}
	
	public interface OnDragListener {
		void OnDragEnd(int oldPos, int newPos);
	}
	public interface LoveNoDragTouchEventListener {
		public void isNeedTouchEvent(boolean isNeed);
	}
	
}
