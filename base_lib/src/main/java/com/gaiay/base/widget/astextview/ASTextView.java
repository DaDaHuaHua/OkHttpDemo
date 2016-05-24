package com.gaiay.base.widget.astextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

/**
* 
* TODO 单行文本跑马灯控件
*/
public class ASTextView extends TextView {
	
    private float textLength = 0f;//文本长度
    private float viewWidth = 0f;
    private float step = 0f;//文字的横坐标
    private float y = 0f;//文字的纵坐标
    private float temp_view_plus_text_length = 0.0f;//用于计算的临时变量
    private float temp_view_plus_two_text_length = 0.0f;//用于计算的临时变量
    public boolean isStarting = false;//是否开始滚动
    private Paint paint = null;//绘图样式
    private String text = "";//文本内容

    
    public ASTextView(Context context) {
        super(context);
    }

    public ASTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ASTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	return super.onTouchEvent(event);
    }
    
    /**
     * 文本初始化，每次更改文本内容或者文本效果等之后都需要重新初始化一下
     */
    public void init(WindowManager windowManager, int color, int viewWidth) {
        paint = getPaint();
        paint.setColor(color);
        text = getText().toString();
        textLength = viewWidth;
//        viewWidth = getWidth();
//        if(viewWidth == 0) {
//            if(windowManager != null) {
//                Display display = windowManager.getDefaultDisplay();
//                viewWidth = display.getWidth();
//            }
//        }
        step = textLength;
        temp_view_plus_text_length = textLength;
        temp_view_plus_two_text_length = textLength * 2;
        y = getTextSize() + getPaddingTop();
    }
    
    int color;
    
    /**
     * 文本初始化，每次更改文本内容或者文本效果等之后都需要重新初始化一下
     */
    public void init(int color) {
    	this.color = color;
    	paint = getPaint();
		paint.setColor(color);
    	
    	text = getText().toString();
    	//文本宽度
    	textLength = paint.measureText(text);
    	//控件宽度
    	viewWidth = getWidth();
    	//step文本
    	step = textLength;
    	temp_view_plus_text_length = textLength;
    	temp_view_plus_two_text_length = textLength * 2;
    	y = getTextSize() + ((getHeight() - getTextSize()) / 5) * 2;
    }
    /**
     * 文本初始化，每次更改文本内容或者文本效果等之后都需要重新初始化一下
     */
    public void init() {
    	text = getText().toString();
    	//文本宽度
    	paint = getPaint();
    	textLength = paint.measureText(text);
    	//控件宽度
    	viewWidth = getWidth();
    	//step文本
    	step = textLength;
    	temp_view_plus_text_length = textLength;
    	temp_view_plus_two_text_length = textLength * 2;
    	y = getTextSize() + ((getHeight() - getTextSize()) / 5) * 2;
    }
    boolean foces_scroll;
    /**
     * 开始滚动
     */
    public void startScroll(boolean foces_scroll) {
    	isStarting = true;
    	this.foces_scroll = foces_scroll;
        invalidate();
    }
    
    /**
     * 停止滚动
     */
    public void stopScroll() {
        isStarting = false;
        invalidate();
    }
    
    @Override
    public void onDraw(Canvas canvas) {
    	if(canvas == null) {
    		invalidate();
    		return;
    	}
    	if (!isStarting) {
    		super.onDraw(canvas);
        	return;
		}
    	if (!foces_scroll && textLength <= viewWidth) {
    		isStarting = false;
    		super.onDraw(canvas);
    		return;
    	}
    	if(paint == null) {
    		init(color);
    	}
        canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
        if(viewWidth == 0) {
        	init(paint.getColor());
        }
        step += 2;
        if(step >= temp_view_plus_two_text_length)
            step = temp_view_plus_text_length - viewWidth;
        invalidate();
    }
}
