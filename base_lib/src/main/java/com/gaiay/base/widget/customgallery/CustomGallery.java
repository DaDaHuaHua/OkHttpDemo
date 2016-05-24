package com.gaiay.base.widget.customgallery;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.gaiay.base.util.Log;
import com.gaiay.base.widget.gallery.NoFlingGallery;

public class CustomGallery extends NoFlingGallery {
	private GestureDetector gestureScanner;
	private CustomImageView imageView;
	private boolean isMagnify = true;
	private boolean flag = true;
	private int screenWidth;
	private int screenHeight;
	private WindowManager mWindowManager;

	public CustomGallery(Context context) {
		super(context);
		getWindowsSize(context);
	}

	public CustomGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getWindowsSize(context);
	}

	public CustomGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		getWindowsSize(context);
		gestureScanner = new GestureDetector(new MySimpleGesture());
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = CustomGallery.this.getSelectedView();
				if (view instanceof CustomImageView) {
					imageView = (CustomImageView) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						flag = false;
						baseValue = 0;
						originalScale = imageView.getScale();
						Log.d("onTouch", "MotionEvent.ACTION_DOWN");
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if (event.getPointerCount() == 2) {
							Log.d("onTouch", "MotionEvent.ACTION_MOVE:2");
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
							if (baseValue == 0) {
								baseValue = value;
							} else {
								float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
								imageView.zoomTo(originalScale * scale, x + event.getX(1), y + event.getY(1));
							}
							flag = true;
						}
					}
				}
				return false;
			}
		});
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

		if (flag) {
			return false;
		}
		View view = CustomGallery.this.getSelectedView();
		if (view instanceof CustomImageView) {
			imageView = (CustomImageView) view;
			float v[] = new float[9];
			Matrix m = imageView.getImageMatrix();
			m.getValues(v);
			float left, right;
			float width, height;
			width = imageView.getScale() * imageView.getImageWidth();
			height = imageView.getScale() * imageView.getImageHeight();
			if ((int) width <= screenWidth && (int) height <= screenHeight)// 如果图片当前大小<屏幕大小，直接处理滑屏事件
			{
				super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				left = v[Matrix.MTRANS_X];
				right = left + width;
				Rect r = new Rect();
				imageView.getGlobalVisibleRect(r);

				if (distanceX > 0) {
					if (r.left > 0) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (right < screenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						imageView.postTranslate(-distanceX, -distanceY);
					}
				} else if (distanceX < 0) {
					if (r.right < screenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (left > 0) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						imageView.postTranslate(-distanceX, -distanceY);
					}
				}

			}

		} else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (this.listener != null) {
			this.listener.onSlide(e1, e2, getSelectedItemPosition());
			return true;
		}
		return super.onFling(e1, e2, velocityX, velocityY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureScanner.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// 判断上下边界是否越界
			View view = CustomGallery.this.getSelectedView();
			if (view instanceof CustomImageView) {
				imageView = (CustomImageView) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale() * imageView.getImageHeight();
				if ((int) width <= screenWidth && (int) height <= screenHeight) {
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;
				Log.i("manga", "bottom:" + bottom);
				Log.i("top", "top:" + top);
			}
			break;
		}
		if (lisTouchEventHandleListener != null) {
			if (lisTouchEventHandleListener.onTouchEventHandle(event)) {
				return true;
			}
		}
		return super.onTouchEvent(event);
	}
	
	OnTouchEventHandleListener lisTouchEventHandleListener;
	
	public interface OnTouchEventHandleListener {
		public boolean onTouchEventHandle(MotionEvent event);
	}

	public void setOnTouchEventHandleListener(OnTouchEventHandleListener listener) {
		lisTouchEventHandleListener = listener;
	}
	
	private class MySimpleGesture extends SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (onItemSingleClickListener != null) {
				onItemSingleClickListener.onClick();
				return true;
			}
			return super.onSingleTapConfirmed(e);
		}
		
		// 按两下的第二下Touch down时触发
		public boolean onDoubleTap(MotionEvent e) {
			View view = CustomGallery.this.getSelectedView();
			if (view instanceof CustomImageView) {
				imageView = (CustomImageView) view;
				if (isMagnify) {
					imageView.zoomTo(2.0f, screenWidth / 2, screenHeight / 2, 200f);
					isMagnify = false;
				} else {
					imageView.zoomTo(imageView.getScaleRate(), screenWidth / 2, screenHeight / 2, 200f);
					isMagnify = true;
				}
			}
			return true;
		}
	}

	private void getWindowsSize(Context context) {
		mWindowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
		screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		screenHeight = mWindowManager.getDefaultDisplay().getHeight();
	}
	
	public interface OnSlideListener{
		public void onSlide(MotionEvent e1, MotionEvent e2, int position);
	}
	public void  setOnSideListener(OnSlideListener listener) {
		this.listener = listener;
	}
	private OnSlideListener listener;
	
	private OnItemSingleClickListener onItemSingleClickListener;
	
	public interface OnItemSingleClickListener {
		public abstract void onClick();
	}
	
	public void setOnItemSingleClickListener(OnItemSingleClickListener listener) {
		this.onItemSingleClickListener = listener;
	}
}
