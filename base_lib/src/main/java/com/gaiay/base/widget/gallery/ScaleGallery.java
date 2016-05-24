package com.gaiay.base.widget.gallery;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Gallery;

import com.gaiay.base.util.Log;

public class ScaleGallery extends Gallery {
	private GestureDetector gestureScanner;
	private ScaleImageView imageView;

	// 锟斤拷幕锟斤拷锟?
	public static int screenWidth;
	// 锟斤拷幕锟竭讹拷
	public static int screenHeight;

	public ScaleGallery(Context context) {
		super(context);

	}

	public ScaleGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScaleGallery(Context context, AttributeSet attrs) {
		super(context, attrs);

		screenWidth = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();
		screenHeight = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getHeight();

		gestureScanner = new GestureDetector(new MySimpleGesture());
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = ScaleGallery.this.getSelectedView();
				if (view instanceof ScaleImageView) {
					imageView = (ScaleImageView) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						baseValue = 0;
						originalScale = imageView.getScale();
					}
					
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if (event.getPointerCount() == 2) {
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float value = (float) Math.sqrt(x * x + y * y);// 璁＄畻涓ょ偣鐨勮窛锟?
							// System.out.println("value:" + value);
							if (baseValue == 0) {
								baseValue = value;
							} else {
								float scale = value / baseValue;// 褰撳墠涓ょ偣闂寸殑璺濈闄や互鎵嬫寚钀戒笅鏃朵袱鐐归棿鐨勮窛绂诲氨鏄渶瑕佺缉鏀剧殑姣斾緥锟?
								// scale the image
								imageView.zoomTo(jiansuScale(originalScale * scale), x
										+ event.getX(1), y + event.getY(1));

							}
						}
					} else if(event.getAction() == MotionEvent.ACTION_UP) {
						if (event.getPointerCount() == 2) {
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float scale = imageView.getScale();
							Log.e("MyGallery:scale_Up:" + scale);
							if (scale > 1.8f) {
								imageView.zoomTo(1.8f, (x + event.getX(1)), (y + event.getY(1)), 200f);
							}
						}
					}
				}
				return false;
			}

		});
	}

	public void notifyGallery() {
		View view = ScaleGallery.this.getSelectedView();
		if (view instanceof ScaleImageView) {
			imageView = (ScaleImageView) view;
			imageView.zoomTo(imageView.getScale(), (getLeft() + getWidth()) / 2, (getTop() + getHeight()) / 2);
		}
	}
	
	public float jiansuScale(float scale) {
		if (scale >= 2.0f) {
			scale = 2.0f;
		} else if (scale > 1.8f) {
			scale = (scale - 1.8f) * (2.0f - scale) + 1.8f;
		}

		Log.e("MyGallery:scale_jiansuScale:" + scale);
		return scale;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		View view = ScaleGallery.this.getSelectedView();
		if (view instanceof ScaleImageView) {
			imageView = (ScaleImageView) view;

			float v[] = new float[9];
			Matrix m = imageView.getImageMatrix();
			m.getValues(v);
			float left, right;
			float width, height;
			width = imageView.getScale() * imageView.getImageWidth();
			height = imageView.getScale() * imageView.getImageHeight();
			if ((int) width <= screenWidth && (int) height <= screenHeight)// 濡傛灉鍥剧墖褰撳墠澶у皬<灞忓箷澶у皬锛岀洿鎺ュ鐞嗘粦灞忎簨锟?
			{
				super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				left = v[Matrix.MTRANS_X];
				right = left + width;
				Rect r = new Rect();
				imageView.getGlobalVisibleRect(r);

				if (distanceX > 0)// 鍚戝乏婊戝姩
				{
					if (r.left > 0) {// 鍒ゆ柇褰撳墠ImageView鏄惁鏄剧ず瀹屽叏
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (right < screenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						imageView.postTranslate(-distanceX, -distanceY);
					}
				} else if (distanceX < 0)// 鍚戝彸婊戝姩
				{
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
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureScanner.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// 鍒ゆ柇涓婁笅杈圭晫鏄惁瓒婄晫
			View view = ScaleGallery.this.getSelectedView();
			if (view instanceof ScaleImageView) {
				imageView = (ScaleImageView) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale()
						* imageView.getImageHeight();
				if ((int) width <= screenWidth && (int) height <= screenHeight)// 濡傛灉鍥剧墖褰撳墠澶у皬<灞忓箷澶у皬锛屽垽鏂竟锟?
				{
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float parentBottom = this.getHeight() + this.getTop();
				float parentTop = this.getTop();
				float top = v[Matrix.MTRANS_Y] - parentTop;
				float bottom = top + height;

//				Log.e("MyGallery:parentTop_" + parentTop + "  parentBottom_"
//						+ parentBottom + "  top_" + top + "  bottom_" + bottom);

				if (height <= getHeight()) {
					if (top < 0) {
						imageView.postTranslateDur(-top, 200f);
						break;
					}
					if (bottom > parentBottom) {
						imageView.postTranslateDur(
								parentBottom - bottom,
								200f);
						break;
					}
				} else {
					if (top > 0) {
						imageView.postTranslateDur(-top, 200f);
						break;
					} 
					if (bottom < parentBottom) {
						imageView.postTranslateDur(parentBottom - bottom, 200f);
					}
				}
				

				// if (top > 0) {
				// imageView.postTranslateDur(-top, 200f);
				// }
				// Log.i("manga", "bottom:" + bottom);
				// if (bottom < screenHeight) {
				// imageView.postTranslateDur(screenHeight - bottom, 200f);
				// }

			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private class MySimpleGesture extends SimpleOnGestureListener {
		// 鎸変袱涓嬬殑绗簩涓婽ouch down鏃惰Е锟?
		public boolean onDoubleTap(MotionEvent e) {
			View view = ScaleGallery.this.getSelectedView();
			if (view instanceof ScaleImageView) {
				imageView = (ScaleImageView) view;
				if (imageView.getScale() > imageView.getScaleRate()) {
					imageView.zoomTo(imageView.getScaleRate(), screenWidth / 2,
							screenHeight / 2, 200f);
					// imageView.layoutToCenter();
				} else {
					imageView.zoomTo(1.0f, screenWidth / 2, screenHeight / 2,
							200f);
				}

			} else {

			}
			// return super.onDoubleTap(e);
			return true;
		}
	}
}
