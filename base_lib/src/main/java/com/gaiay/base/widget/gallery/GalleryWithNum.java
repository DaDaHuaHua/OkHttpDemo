package com.gaiay.base.widget.gallery;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;

import com.gaiay.base.R;
import com.gaiay.base.common.CommonCode;
import com.gaiay.base.net.bitmap.BitmapAsynTask;
import com.gaiay.base.net.bitmap.BitmapManager;
import com.gaiay.base.util.Log;

public class GalleryWithNum extends FrameLayout implements OnItemSelectedListener, OnClickListener, AnimationListener{

	public interface OnViewColseListener {
		public void onViewColse();
	}
	public interface OnItemClickListener {
		public void OnItemClick(int position);
	}
	
	Context cxt;
	
	Gallery gallery;
	NumBar nb;
	
	Bitmap[] bmps;
	Bitmap bmpDef;
	List<BitmapAsynTask> tasks;
	MyAdapter adapter;
	OnViewColseListener listener;
	OnItemClickListener itemListener;
	
	AlphaAnimation aain;
	AlphaAnimation aaout;
	
	public GalleryWithNum(Context context) {
		super(context);
		cxt = context;
	}
	
	public GalleryWithNum(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		cxt = context;
	}


	public GalleryWithNum(Context context, AttributeSet attrs) {
		super(context, attrs);
		cxt = context;
	}

	boolean hasInit = false;

	private void doInit(int defaultImgRes) {
		if (hasInit) {
			return;
		}
		hasInit = true;
		aain = new AlphaAnimation(0.0f, 1.0f);
		aaout = new AlphaAnimation(1.0f, 0.2f);
		aain.setDuration(1000);
		aaout.setDuration(1000);
		aain.setAnimationListener(this);
		aaout.setAnimationListener(this);
		bmpDef = BitmapFactory.decodeResource(cxt.getResources(), defaultImgRes);
		
		inflate(cxt, R.layout.gallery_with_num, this);
		
		gallery = (Gallery) this.findViewById(R.id.gwn_gallery);
		nb = (NumBar) this.findViewById(R.id.gwn_nb);
		if (nbHight > 0) {
			initNumHight(nbHight);
		}
		if (nbColor != 3336) {
			setNumTextColor(nbColor);
		}
		if (resDisFocus != 0 || resFocus != 0) {
			setNumResources(resFocus, resDisFocus);
		}
		gallery.setOnItemSelectedListener(this);
		gallery.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				handler.sendMessage(handler.obtainMessage(0, adapter.getPosition(arg2), 0));
			}
		});
	}
	
	int nbHight = 0;
	int nbColor = 3336;
	int resFocus = 0;
	int resDisFocus = 0;
	
	public void initNumHight(int px) {
		if (nb != null) {
			android.view.ViewGroup.LayoutParams lp = nb.getLayoutParams();
			lp.height = px;
			nb.setLayoutParams(lp);
		} else {
			nbHight = px;
		}
	}
	
	public void setNumTextColor(int color) {
		if (nb != null) {
			nb.setTextColor(color);
		} else {
			nbColor = color;
		}
	}
	
	public void setNumResources(int focus, int disFocus) {
		if (nb != null) {
			nb.setNumResources(focus, disFocus);
		} else {
			if (focus > 0) {
				resFocus = focus;
			}
			if (disFocus > 0) {
				resDisFocus = disFocus;
			}
		}
	}
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		itemListener = listener;
	}
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case CommonCode.BITMAP_HOLDE_MSG:
				if (msg.arg1 == CommonCode.SUCCESS_BITMAP) {
					bmps[msg.arg2] = (Bitmap) msg.obj;
					adapter.notifyDataSetChanged();
				}
				break;
			case 0:
				if (itemListener != null) {
					itemListener.OnItemClick(msg.arg1);
				}
				break;
			default:
				break;
			}
		};
	};
	
	boolean isOnAnim = false;
	
	float x1;
	float x2;
	int selectPosition;
	int oldPosition;
	
	@Override
	public void onAnimationEnd(Animation animation) {
		isOnAnim = false;
		if (animation == aaout) {
			nb.setVisibility(View.INVISIBLE);
		} else {
			nb.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}
	public void init(int[] ids, int defaultImgRes, OnViewColseListener lis) {
		doInit(defaultImgRes);
		if (ids == null) {
			Log.e("GalleryWithNum_init:(ids参数不能为null)");
			return;
		}
		listener = lis;
		recycle();
		bmps = new Bitmap[ids.length];
		for (int i = 0; i < ids.length; i++) {
			bmps[i] = BitmapFactory.decodeResource(cxt.getResources(), ids[i]);
		}
		adapter = new MyAdapter();
		gallery.setAdapter(adapter);
		nb.init(ids.length, 0);
	}
	public void init(int[] ids, String[] names, int defaultImgRes, OnViewColseListener lis, boolean isHor) {
		doInit(defaultImgRes);
		if (isHor) {
			nb.setHorizontal(true);
		}
		if (ids == null) {
			Log.e("GalleryWithNum_init:(ids参数不能为null)");
			return;
		}
		listener = lis;
		this.names = names;
		recycle();
		bmps = new Bitmap[ids.length];
		for (int i = 0; i < ids.length; i++) {
			bmps[i] = BitmapFactory.decodeResource(cxt.getResources(), ids[i]);
		}
		adapter = new MyAdapter();
		gallery.setAdapter(adapter);
		nb.init(ids.length, 0);
	}
	
	public void init(String[] urls, String setId, String cacheFolder, int defaultImgRes, OnViewColseListener lis) {
		doInit(defaultImgRes);
		if (urls == null) {
			Log.e("GalleryWithNum_init:(ids参数不能为null)");
			return;
		}
		listener = lis;
		recycle();
		bmps = new Bitmap[urls.length];
		if (tasks != null) {
			for (int i = 0; i < tasks.size(); i++) {
				tasks.get(i).cancel(true);
			}
			tasks.clear();
		}
		tasks = new ArrayList<BitmapAsynTask>();
		for (int i = 0; i < urls.length; i++) {
			tasks.add(BitmapManager.getBitmap(urls[i], cxt, handler, setId, i, cacheFolder));
		}
		adapter = new MyAdapter();
		gallery.setAdapter(adapter);
		nb.init(urls.length, 0);
	}
	
	String[] names;
	public void init(String[] urls, String[] names, String setId, String cacheFolder, int defaultImgRes, OnViewColseListener lis) {
		doInit(defaultImgRes);
		if (urls == null) {
			Log.e("GalleryWithNum_init:(ids参数不能为null)");
			return;
		}
		listener = lis;
		this.names = names;
		recycle();
		bmps = new Bitmap[urls.length];
		if (tasks != null) {
			for (int i = 0; i < tasks.size(); i++) {
				tasks.get(i).cancel(true);
			}
			tasks.clear();
		}
		tasks = new ArrayList<BitmapAsynTask>();
		for (int i = 0; i < urls.length; i++) {
			tasks.add(BitmapManager.getBitmap(urls[i], cxt, handler, setId, i, cacheFolder));
		}
		adapter = new MyAdapter();
		gallery.setAdapter(adapter);
		gallery.setSelection(((Integer.MAX_VALUE / bmps.length)/2) * bmps.length);
		nb.init(urls.length, 0);
	}
	public void init(String[] urls, String[] names, String setId, String cacheFolder, int defaultImgRes, OnViewColseListener lis, boolean isHor) {
		doInit(defaultImgRes);
		if (isHor) {
			nb.setHorizontal(true);
		}
		if (urls == null) {
			Log.e("GalleryWithNum_init:(ids参数不能为null)");
			return;
		}
		listener = lis;
		this.names = names;
		recycle();
		bmps = new Bitmap[urls.length];
		if (tasks != null) {
			for (int i = 0; i < tasks.size(); i++) {
				tasks.get(i).cancel(true);
			}
			tasks.clear();
		}
		tasks = new ArrayList<BitmapAsynTask>();
		for (int i = 0; i < urls.length; i++) {
			tasks.add(BitmapManager.getBitmap(urls[i], cxt, handler, setId, i, cacheFolder));
		}
		adapter = new MyAdapter();
		gallery.setAdapter(adapter);
		gallery.setSelection(((Integer.MAX_VALUE / bmps.length)/2) * bmps.length);
		nb.init(urls.length, 0);
	}
	
	public void setSelection(int position) {
		if (bmps == null) {
			Log.e("GalleryWithNum_setSelection:(请先调用init()方法初始化此类)");
			return;
		}
		if (position < 0) {
			position = 0;
		}
		gallery.setSelection((((Integer.MAX_VALUE / bmps.length)/2) * bmps.length) + position);
	}
	
	public void setOnViewColseListener(OnViewColseListener lis) {
		this.listener = lis;
	}
	
	public void notifyData() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
	
	public void recycle() {
		if (tasks != null) {
			for (int i = 0; i < tasks.size() && tasks.get(i) != null; i++) {
				tasks.get(i).cancel(true);
			}
			tasks.clear();
		}
		tasks = null;
		if (gallery != null) {
			gallery.removeAllViewsInLayout();
		}
		if (bmps != null) {
			for (int i = 0; i < bmps.length; i++) {
				if (bmps[i] != null) {
					bmps[i].recycle();
				}
				bmps[i] = null;
			}
		}
		bmps = null;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Log.e("onItemSelected:" + position);
		if (names != null && names.length > adapter.getPosition(position)) {
			nb.setSelection(adapter.getPosition(position), names[adapter.getPosition(position)]);
		} else {
			nb.setSelection(adapter.getPosition(position));
		}
		selectPosition = position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

	@Override
	public void onClick(View v) {
		if (listener != null) {
			listener.onViewColse();
		}
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public int getPosition(int pos) {
			return pos % bmps.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Holder holder;
			View view;
			int flag = getPosition(position);
			if (convertView == null) {
				holder = new Holder();
				view = LayoutInflater.from(cxt).inflate(R.layout.gallery_num_item, null);
				holder.img = (ImageView) view.findViewById(R.id.image_img);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (Holder) view.getTag();
			}
			if (flag < bmps.length) {
				if (bmps[flag] != null && !bmps[flag].isRecycled()) {
					holder.img.setImageBitmap(bmps[flag]);
				} else {
					holder.img.setImageBitmap(bmpDef);
				}
			}
			return view;
		}
	}
	private static class Holder {
		ImageView img;
	}
}
