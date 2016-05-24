package com.gaiay.base.widget.horizontalscrollview;

import java.util.ArrayList;
import java.util.List;

import com.gaiay.base.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class HorizontalScrollView extends LinearLayout implements OnClickListener {
	private LinearLayout fatherLayout = this;
	private LinearLayout layout;
	private int leftCellSpacing;
	private int rightCellSpacing;
	private int topCellSpacing;
	private int bottomCellSpacing;
	private LayoutInflater inflater;
	private LinearLayout contentView;
	private android.widget.HorizontalScrollView scrollView;

	private int position;
	/** 屏幕中心(硬件) */
	private int centerScreen;
	private WindowManager manager;
	private HorizontalScrollViewAdapter adapter;
	private List<Boolean> flags;
	private int currentSelection = -1;
	private boolean isFirst;
	private boolean isInit = false;

	public HorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		isInit = true;
	}

	/**
	 * 初始化父类
	 * 
	 * @param context
	 */
	private void init(Context context) {
		manager = (WindowManager) context.getSystemService("window");
		centerScreen = manager.getDefaultDisplay().getWidth() / 2;
		inflater = LayoutInflater.from(context);
		layout = (LinearLayout) inflater.inflate(R.layout.horizontalscrollview, null);
		flags = new ArrayList<Boolean>();
		addView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		contentView = (LinearLayout) layout.findViewById(R.id.content);
		scrollView = (android.widget.HorizontalScrollView) layout.findViewById(R.id.hsView);
	}

	boolean toSelect = false;
	boolean isUseSelect = true;
	boolean isUseLeftAndRight = true;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (toSelect) {
			scrollToSelect();
			toSelect = false;
		}
		initLeftAndRight();
	}

	private final int BZ = 25;

	private void initLeftAndRight() {
		if (isUseLeftAndRight) {
			if (contentView == null || scrollView == null || contentView.getChildAt(0) == null) {
				return;
			}
			int[] loc = new int[2];
			int[] loc2 = new int[2];
			int[] loc3 = new int[2];

			contentView.getChildAt(0).getLocationOnScreen(loc);
			contentView.getChildAt(contentView.getChildCount() - 1).getLocationOnScreen(loc3);
			scrollView.getLocationOnScreen(loc2);

			if (Math.abs(loc[0] - loc2[0]) < BZ) {
				if (Math.abs((loc3[0] + contentView.getChildAt(contentView.getChildCount() - 1).getWidth())
						- (loc2[0] + scrollView.getWidth())) < BZ) {
					initBG(0);
				} else if (Math.abs((loc[0] + contentView.getChildAt(contentView.getChildCount() - 1).getWidth())
						- (loc2[0] + scrollView.getWidth())) >= BZ) {
					initBG(2);
				}
			} else if (Math.abs(loc[0] - loc2[0]) >= BZ) {
				if (Math.abs((loc3[0] + contentView.getChildAt(contentView.getChildCount() - 1).getWidth())
						- (loc2[0] + scrollView.getWidth())) < BZ) {
					initBG(1);
				} else if (Math.abs((loc[0] + contentView.getChildAt(contentView.getChildCount() - 1).getWidth())
						- (loc2[0] + scrollView.getWidth())) >= BZ) {
					initBG(3);
				}
			}
		} else {
			initBG(0);
		}
	}

	public void setBackgroud(int id) {
		if (id > 0) {
			fatherLayout.setBackgroundResource(id);
		}
	}

	int pos = -1;

	private void initBG(int p) {
		if (pos == p) {
			return;
		}
		pos = p;
		switch (p) {
		case 0:
			fatherLayout.setBackgroundResource(R.drawable.base_widget_horview_normal_bg);
			break;
		case 1:
			fatherLayout.setBackgroundResource(R.drawable.base_widget_horview_normal_bg_left);
			break;
		case 2:
			// fatherLayout.setBackgroundResource(R.drawable.base_widget_horview_normal_bg_right);
			break;
		case 3:
			fatherLayout.setBackgroundResource(R.drawable.base_widget_horview_bg);
			break;
		default:
			fatherLayout.setBackgroundResource(R.drawable.base_widget_horview_bg);
			break;
		}
	}

	private void setUpView() {
		if (adapter != null) {
			for (int i = 0; i < adapter.getCount(); i++) {
				if (isFirst) {
					addCell(adapter.getView(contentView, i, true));
					isFirst = !isFirst;
					continue;
				}
				if (i == currentSelection) {
					addCell(adapter.getView(contentView, i, true));
					continue;
				}
				addCell(adapter.getView(contentView, i, false));
			}
		}
	}

	public void addCell(View view) {
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		p.setMargins(leftCellSpacing, topCellSpacing, rightCellSpacing, bottomCellSpacing);
		view.setLayoutParams(p);
		view.setTag((Object) position);
		view.setOnClickListener(this);
		contentView.addView(view);
		flags.add(false);
		position++;
	}

	public void setAdapter(HorizontalScrollViewAdapter adapter) {
		this.adapter = adapter;
		if (adapter != null) {
			reDraw();
		}
	}

	@Override
	public void invalidate() {
		if (isInit) {
			reDraw();
		}
		super.invalidate();
	}

	public void setUseLeftAndRight(boolean isUse) {
		isUseLeftAndRight = isUse;
	}

	public boolean isUseLeftAndRight() {
		return isUseLeftAndRight;
	}

	public void selectPosition(int position) {
		if (currentSelection == position) {
			scrollToSelect();
			return;
		}
		if (adapter != null && adapter.getCount() > 0) {
			if (position >= adapter.getCount()) {
				position = 0;
			}
			if (currentSelection != -1) {
				if (currentSelection >= flags.size()) {
					currentSelection = 0;
					for (int i = 0; i < flags.size(); i++) {
						flags.set(i, false);
					}
				} else {
					flags.set(currentSelection, false);
				}
			}
			currentSelection = position;
			reDraw();
			flags.set(currentSelection, true);
			scrollToSelect();
			toSelect = true;
		}
	}

	private void reDraw() {
		position = 0;
		contentView.removeAllViews();
		flags.clear();
		setUpView();
		isInit = false;
	}

	public void setCellSpacing(int spacing) {
		leftCellSpacing = rightCellSpacing = topCellSpacing = bottomCellSpacing = spacing;
		reDraw();
	}

	public void setViewOnItemClickListener(HorizontalScrollViewAdapter myViewAdapter) {
		this.adapter = myViewAdapter;
	}

	public <T> void setData(List<T> data) {
		if (data != null) {
			reDraw();
		}
	}

	/**
	 * 设置间隙距离..
	 * 
	 * @param leftSpacing
	 * @param topSpacing
	 * @param rightSpacing
	 * @param bottomSpacing
	 */
	public void setCellSpacing(int leftSpacing, int topSpacing, int rightSpacing, int bottomSpacing) {
		if (leftCellSpacing != leftSpacing || topCellSpacing != topSpacing || rightCellSpacing != rightSpacing
				|| bottomCellSpacing != bottomSpacing) {
			this.leftCellSpacing = leftSpacing;
			this.topCellSpacing = topSpacing;
			this.rightCellSpacing = rightSpacing;
			this.bottomCellSpacing = bottomSpacing;
			reDraw();
		}
	}

	private void scrollToSelect() {
		postDelayed(new Runnable() {
			@Override
			public void run() {
				View v = null;
				int p = 0;
				for (int i = 0; i < flags.size(); i++) {
					if (flags.get(i)) {
						v = contentView.getChildAt(i);
						p = i;
						break;
					}
				}

				if (v == null) {
					scrollView.scrollTo(0, 0);
				} else {
					int left_x = 0;
					int right_x = 0;
					if (v.getWidth() == 0) {
						left_x = v.getLeft();
						right_x = v.getRight();
					} else {
						for (int i = 0; i < contentView.getChildCount(); i++) {
							if (p > i) {
								left_x += contentView.getChildAt(i).getMeasuredWidth();
							}
						}
						right_x = left_x + v.getMeasuredWidth();
					}
					int centerX = ((left_x - scrollView.getScrollX()) + (right_x - scrollView.getScrollX())) / 2;
					int leng = centerX - centerScreen;
					if (Math.abs(leng) > 10) {
						scrollView.smoothScrollBy(leng, 0);
					}
				}
			}
		}, 100);

	}

	@Override
	public void onClick(View v) {
		if (adapter != null) {
			adapter.onMyItemClick(v, (Integer) v.getTag());
			selectPosition((Integer) v.getTag());
		}
	}

	public interface HorizontalScrollViewAdapter {
		int getCount();

		View getView(View parentView, int position, boolean isSelected);

		void onMyItemClick(View v, int position);

		void onMyItemSelected(View selectView, View oldSelectView);
	}

	int firstScroll = 0;
	int lastScroll = 0;
	int temp;

}
