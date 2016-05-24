package com.gaiay.base.widget.progress;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gaiay.base.R;

public class MProgressBar extends RelativeLayout {

	Context cxt;
	
	View progress;
	
	int count;
	int position;
	
	public MProgressBar(Context context) {
		super(context);
		initUI(context);
	}

	public MProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initUI(context);
	}

	public MProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUI(context);
	}

	private void initUI(Context context) {
		cxt = context;
		inflate(cxt, R.layout.progress_bar, this);
		progress = findViewById(R.id.progress);
	}
	
	public void setBackground(int resId) {
		if (resId < 0) {
			return;
		}
		((ImageView)findViewById(R.id.progress_back)).setImageResource(resId);
	}
	
	public void setProgressBackground(int resId) {
		if (resId < 0) {
			return;
		}
		((ImageView)findViewById(R.id.progress)).setImageResource(resId);
	}
	
	public void init(int count, int progress) {
		if (count < 0) {
			return;
		}
		this.position = progress;
		this.count = count;
	}
	
	public void setMax(int count) {
		if (count < 0) {
			count = 0;
		}
		this.count = count;
	}
	
	public int getProgress() {
		return position;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setProgress(int progress) {
		if (count < 0) {
			return;
		}
		if (progress > count) {
			progress = count;
		}
		if (progress < 0) {
			progress = 0;
		}
		this.position = progress;
//		handler.post(new Runnable() {
//			@Override
//			public void run() {
//				setProg(position);
//			}
//		});
		setProg(position);
	}
	
	Handler handler = new Handler ();
	
	private void setProg(int pos) {
		int w = getWidth();
		pos = (int)(((double)pos / (double)count) * w);
		RelativeLayout.LayoutParams lp = (LayoutParams) progress.getLayoutParams();
		lp.width = pos;
		lp.height = RelativeLayout.LayoutParams.FILL_PARENT;
		progress.setLayoutParams(lp);
	}
	
}
