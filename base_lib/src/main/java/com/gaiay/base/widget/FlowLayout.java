package com.gaiay.base.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 流式布局，当View排满整行时，会自动换行。<br>
 * 可以使用{@link #setMaxLines(int)}来设置最大的行数。<br>
 * <br>
 * <b>注意：</b>当使用addView()添加View时<br>
 * 1.使用inflate(int resource, ViewGroup root, boolean attachToRoot)生成View，root指定为FlowLayout<br>
 * 2.或者用new TextView(Context context)时，需要指定LayoutParameter为MarginLayoutParams.LayoutParameter
 */
public class FlowLayout extends ViewGroup {
    private List<List<View>> mChildren = new ArrayList<List<View>>();
    private List<Integer> mHeights = new ArrayList<Integer>();

    public FlowLayout(Context context) {
        this(context, null, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (parentWidthMode == MeasureSpec.EXACTLY && parentHeightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(parentWidthSize, parentHeightSize);
            return;
        }

        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        mLines = 0;
        mChildren.clear();
        mHeights.clear();

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (lineWidth + childWidth + lp.leftMargin + lp.rightMargin > parentWidthSize - getPaddingLeft() - getPaddingRight()) {
                width = Math.max(width, lineWidth);
                height += lineHeight;

                mChildren.add(views);
                mHeights.add(lineHeight);

                mLines++;
                if (mMaxLines > 0 && mMaxLines == mLines) {
                    break;
                }

                lineWidth = childWidth + lp.leftMargin + lp.rightMargin;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                views = new ArrayList<View>();
                views.add(child);
            } else {
                lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
                lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
                views.add(child);
            }
            if (i == getChildCount() - 1) {
                mChildren.add(views);
                mHeights.add(lineHeight);
                mLines++;
                width = Math.max(width, lineWidth);
                height += lineHeight;
                views = null;
            }
        }
        width += getPaddingLeft() + getPaddingRight();
        height += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(parentWidthMode == MeasureSpec.EXACTLY ? parentWidthSize : width,
                parentHeightSize == MeasureSpec.EXACTLY ? parentHeightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int top = getPaddingTop();
        int left = getPaddingLeft();
        List<View> views;
        for (int i = 0; i < mChildren.size(); i++) {
            views = mChildren.get(i);
            for (int j = 0; j < views.size(); j++) {
                View child = views.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                left += lp.leftMargin;
                int right = left + child.getMeasuredWidth();
                int bottom = top + lp.topMargin + child.getMeasuredHeight();
                child.layout(left, top + lp.topMargin, right, bottom);
                left += child.getMeasuredWidth() + lp.rightMargin;
            }
            top += mHeights.get(i);
            left = getPaddingLeft();
        }
        views = null;
    }

    private int mLines;
    private int mMaxLines = -1;

    /**
     * 设置最大行数，只能设置大于0的数<br/>
     * <b>注意：</b>在FlowLayout设置了LayoutTransition并且设置了最大行数时，会有BUG，暂时没有修复
     */
    public void setMaxLines(int maxLines) {
        this.mMaxLines = maxLines;
    }

    /**
     * 获取总行数<br/>
     * 注意：异步请求，刚刚更新完view时候调用，不能及时获取到最新的行数。可以在FlowLayout.post(Runnable runnable)获取。
     */
    public int getTotalLines() {
        return mLines;
    }
    
    /**
     * 获取某一行View的数量，lineNum从0开始
     */
    public int getViewCountInLine(int lineNum) {
    	if (lineNum >= 0 && lineNum < mChildren.size()) {
    		return mChildren.get(lineNum).size();
    	}
    	return 0;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
