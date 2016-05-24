package com.gaiay.base.widget.gallery;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaiay.base.R;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

public class NumBar extends RelativeLayout {

//	Gallery list;
	Context cxt;
	int count = 0;
	int flag = 0;
	MyAdapter adapter;
	boolean isHorizontal = false;
	int resFocus = R.drawable.icon_yuandian_b;
	int resDisFocus = R.drawable.icon_yuandian_s;
	
	public NumBar(Context context, AttributeSet attrs) {
		this(context, attrs, false);
	}
	public NumBar(Context context, AttributeSet attrs, boolean isHorizontal) {
		super(context, attrs);
		cxt = context;
		if (isHorizontal) {
			inflate(context, R.layout.num_bar_horizontal, this);
		} else {
			inflate(context, R.layout.num_bar, this);
		}
	}
	
	int textColor = 3332;
	
	public void setTextColor(int color) {
		if (adapter != null) {
			adapter.setTextColor(color);
		} else {
			textColor = color;
		}
	}
	
	public void setNumResources(int focus, int disFocus) {
		if (focus > 0) {
			resFocus = focus;
		}
		if (disFocus > 0) {
			resDisFocus = disFocus;
		}
		invalidate();
	}
	
	public void setHorizontal(boolean isHorizontal) {
		if (isHorizontal != this.isHorizontal) {
			this.isHorizontal = isHorizontal;
			removeAllViews();
			if (isHorizontal) {
				inflate(cxt, R.layout.num_bar_horizontal, this);
			} else {
				inflate(cxt, R.layout.num_bar, this);
			}
		}
	}

	public void init(int count, int which) {
		if (count == 1) {
			return;
		}
		if (count <= 0) {
			Log.e("NumBar_init:(count参数只能大于0，当前count=" + count + ")");
			return;
		}
		if (which < 0 || which >= count) {
			Log.e("NumBar_init:(which参数只能大于等于0并且小于count参数，当前which=" + which + ")");
			which = 0;
		}
		this.count = count;
		this.flag = which;
		adapter = new MyAdapter();
	}
	
	public void setSelection(int which) {
		if (count == 0) {
			Log.e("NumBar_setSelection:(请先调用init()方法初始化此类)");
			return;
		}
		if (which < 0 || which >= count) {
			which = 0;
		}
		if (adapter != null) {
			adapter.notifyUI(which);
		}
	}
	public void setSelection(int which, String msg) {
		if (count == 0) {
			Log.e("NumBar_setSelection:(请先调用init()方法初始化此类)");
			return;
		}
		if (which < 0 || which >= count) {
			which = 0;
		}
		if (StringUtil.isBlank(msg)) {
			msg = "";
		}
		if (adapter != null) {
			adapter.notifyUI(which, msg);
		}
	}
	
	private class MyAdapter {
		LinearLayout ll;
		TextView txt;
		
		MyAdapter() {
			ll = (LinearLayout) findViewById(R.id.numbar_layout);
			ll.removeAllViews();
			ll.setOrientation(LinearLayout.HORIZONTAL);
			for (int i = 0; i < count; i++) {
				ll.addView(getView(i));
			}
			txt = (TextView) findViewById(R.id.numbar_txt);
			if (textColor != 3332) {
				txt.setTextColor(textColor);
			}
		}
		
		public void setTextColor(int color) {
			txt.setTextColor(color);
		}
		
		public void notifyUI(int position) {
			for (int i = 0; i < count; i++) {
				ll.findViewById(i).setBackgroundResource(resDisFocus);
			}
			ll.findViewById(position).setBackgroundResource(resFocus);
			txt.setText("");
			invalidate();
		}
		public void notifyUI(int position, String msg) {
			for (int i = 0; i < count; i++) {
				ll.findViewById(i).setBackgroundResource(resDisFocus);
			}
			ll.findViewById(position).setBackgroundResource(resFocus);
			txt.setText(msg);
			invalidate();
		}
		
		public View getView(int position) {
			ImageView iv = new ImageView(cxt);
			if (position != flag) {
				iv.setBackgroundResource(resDisFocus);
			} else {
				iv.setBackgroundResource(resFocus);
			}
			iv.setPadding(10, 0, 10, 0);
			iv.setId(position);
			return iv;
		}
	}
}
