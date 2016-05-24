package com.gaiay.base.widget.gallery;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

import com.gaiay.base.util.Log;

public class SpeciallyGallery extends Gallery {

	public SpeciallyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        this.setStaticTransformationsEnabled(true);
	}

	public SpeciallyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.setStaticTransformationsEnabled(true);
	}

	public SpeciallyGallery(Context context) {
		super(context);
        this.setStaticTransformationsEnabled(true);
	}
	
    /**
     * Graphics Camera used for transforming the matrix of ImageViews
     */
    private Camera mCamera = new Camera();

    /**
     * The maximum angle the Child ImageView will be rotated by
     */
    private int mMaxRotationAngle = 60;

    /**
     * The maximum zoom on the centre Child
     */
    private int mMaxZoom = -120;

    /**
     * The Centre of the Coverflow
     */
    private int mCoveflowCenter;


    /**
     * Get the max rotational angle of the image
     * 
     * @return the mMaxRotationAngle
     */
    public int getMaxRotationAngle() {
            return mMaxRotationAngle;
    }

    /**
     * Set the max rotational angle of each image
     * 
     * @param maxRotationAngle
     *            the mMaxRotationAngle to set
     */
    public void setMaxRotationAngle(int maxRotationAngle) {
            mMaxRotationAngle = maxRotationAngle;
    }

    /**
     * Get the Max zoom of the centre image
     * 
     * @return the mMaxZoom
     */
    public int getMaxZoom() {
            return mMaxZoom;
    }

    /**
     * Set the max zoom of the centre image
     * 
     * @param maxZoom
     *            the mMaxZoom to set
     */
    public void setMaxZoom(int maxZoom) {
            mMaxZoom = maxZoom;
    }

    /**
     * Get the Centre of the Coverflow
     * 
     * @return The centre of this Coverflow.
     */
    private int getCenterOfCoverflow() {
            return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
                            + getPaddingLeft();
    }

    /**
     * Get the Centre of the View
     * 
     * @return The centre of the given view.
     */
    private static int getCenterOfView(View view) {
            return view.getLeft() + view.getWidth() / 2;
    }

    /**
     * {@inheritDoc}
     * 
     * @see #setStaticTransformationsEnabled(boolean)
     */
    protected boolean getChildStaticTransformation(View child, Transformation t) {

            final int childCenter = getCenterOfView(child);
            final int childWidth = child.getWidth();
            float rotationAngle = 0;

            t.clear();
            t.setTransformationType(Transformation.TYPE_MATRIX);

            if (childCenter == mCoveflowCenter) {
            	transformChild(child, t, 0);
            } else {
                rotationAngle = (float) (((float) (mCoveflowCenter - childCenter) / (float)childWidth));
//                Log.e("mCoveflowCenter:" + mCoveflowCenter + " childCenter:" + childCenter + " childWidth:" + childWidth + " rotationAngle:" + rotationAngle);
                if (Math.abs(rotationAngle) > 1) {
                        rotationAngle = (rotationAngle < 0) ? -1
                                        : 1;
                }
                transformChild(child, t, rotationAngle);
                
            }
//            final int childCenter = getCenterOfView(child);
//            final int childWidth = child.getWidth();
//            int rotationAngle = 0;
//            
//            t.clear();
//            t.setTransformationType(Transformation.TYPE_MATRIX);
//            
//            if (childCenter == mCoveflowCenter) {
//            	transformImageBitmap(child, t, 0);
//            } else {
//            	rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
//            	if (Math.abs(rotationAngle) > mMaxRotationAngle) {
//            		rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle
//            				: mMaxRotationAngle;
//            	}
//            	transformImageBitmap(child, t, rotationAngle);
//            	
//            }

            return true;
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     * 
     * @param w
     *            Current width of this view.
     * @param h
     *            Current height of this view.
     * @param oldw
     *            Old width of this view.
     * @param oldh
     *            Old height of this view.
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mCoveflowCenter = getCenterOfCoverflow();
            super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Transform the Image Bitmap by the Angle passed
     * 
     * @param imageView
     *            ImageView the ImageView whose bitmap we want to rotate
     * @param t
     *            transformation
     * @param rotationAngle
     *            the Angle by which to rotate the Bitmap
     */
    private void transformImageBitmap(View child, Transformation t,
                    int rotationAngle) {
    	Log.e("transformImageBitmap:" + rotationAngle);
            mCamera.save();
            final Matrix imageMatrix = t.getMatrix();
            final int imageHeight = child.getLayoutParams().height;
            final int imageWidth = child.getLayoutParams().width;
            final int rotation = Math.abs(rotationAngle);

            // 在Z轴上正向移动camera的视角，实际效果为放大图片。
            // 如果在Y轴上移动，则图片上下移动；X轴上对应图片左右移动。
            mCamera.translate(0.0f, 0.0f, 100.0f);

            // As the angle of the view gets less, zoom in
            if (rotation < mMaxRotationAngle) {
                    float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
                    mCamera.translate(0.0f, 0.0f, zoomAmount);
            }

            // 在Y轴上旋转，对应图片竖向向里翻转。
            // 如果在X轴上旋转，则对应图片横向向里翻转。
//            mCamera.rotateY(rotationAngle);
            mCamera.getMatrix(imageMatrix);
//            imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
//            imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
            mCamera.restore();
    }
    private void transformChild(View child, Transformation t,
    		float rotationAngle) {
    	mCamera.save();
    	final Matrix imageMatrix = t.getMatrix();
    	final int imageHeight = child.getLayoutParams().height;
    	final int imageWidth = child.getLayoutParams().width;
    	float rotation = Math.abs(rotationAngle);
    	int h = (getHeight() - imageHeight) / 12;
    	int w = (getWidth() - imageWidth) / 7;
    	((Gallery.LayoutParams)child.getLayoutParams()).width = Gallery.LayoutParams.WRAP_CONTENT;
    	if (rotationAngle < 0) {
    		Log.e("transformImageBitmap:" + rotationAngle);
    	}
//    	if (rotation < 0.1) {
//    		rotation = 0.1f;
//		}
    	// 在Z轴上正向移动camera的视角，实际效果为放大图片。
    	// 如果在Y轴上移动，则图片上下移动；X轴上对应图片左右移动。
    	if (rotationAngle <= 0) {
    		mCamera.translate(0.0f, -(h * rotation), (100.0f * rotation));
		} else if(rotationAngle >= 0.9f){
			mCamera.translate(w, -(h * rotation), (100.0f * rotation));
		} else {
			mCamera.translate(0.0f, -(h * rotation), (100.0f * rotation));
		}
//    	mCamera.translate(0.0f, -(50.0f * rotation), -(300.0f * rotation));
    	
    	// As the angle of the view gets less, zoom in
//    	if (rotation < mMaxRotationAngle) {
//    		float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
//    		mCamera.translate(0.0f, 0.0f, zoomAmount);
//    	}
    	
    	// 在Y轴上旋转，对应图片竖向向里翻转。
    	// 如果在X轴上旋转，则对应图片横向向里翻转。
//            mCamera.rotateY(rotationAngle);
    	mCamera.getMatrix(imageMatrix);
//            imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
//            imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
    	mCamera.restore();
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
