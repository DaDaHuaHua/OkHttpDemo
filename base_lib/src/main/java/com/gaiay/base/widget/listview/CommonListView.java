package com.gaiay.base.widget.listview;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.gaiay.base.widget.listview.CommonAdapter.Callback;
import com.gaiay.base.widget.listview.CommonAdapter.OnListItemListener;

public class CommonListView extends ListView {
	private Context context;
	private CommonAdapter mAdapter;
	
	public CommonListView(Context context) {
		super(context);
		this.context = context;
	}

	public CommonListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public CommonListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	/**
	 * 初始化CommonListView，设置Adapter
	 * 
	 * @param data List<?>
	 * @param resource item资源文件id
	 * @param from data中实体的属性名
	 * @param to resource中用于显示from的view的id，与from相对应
	 */
	public void initData(List<?> data, int resource, String[] from, int[] to) {
		mAdapter = new CommonAdapter(context, data, resource, from, to);
		setAdapter(mAdapter);
	}
	
	/**
	 * 初始化CommonListView，设置Adapter
	 * 
	 * @param data List<?>
	 * @param resource item资源文件id
	 * @param from data中实体的属性名
	 * @param to resource中用于显示from的view的id，与from相对应
	 * @param config {@link BitmapConfig}
	 */
	public void initData(List<?> data, int resource, String[] from, int[] to, BitmapConfig config) {
		mAdapter = new CommonAdapter(context, data, resource, from, to);
		mAdapter.setBitmapConfig(config);
		setAdapter(mAdapter);
	}
	
	/**
	 * 为item中的按钮或者view增加点击事件，需要先调用initData方法
	 * 
	 * @param resId
	 * @param onClickListener
	 */
	public void setOnClickListener(int resId, CommonAdapter.OnClickListener onClickListener) {
		mAdapter.setOnClickListener(resId, onClickListener);
	}
	
	/**
	 * 单独为item进行赋值等操作。比如可以判断收藏状态，然后为ImageView设置收藏或者未收藏的背景图片
	 * 
	 * @param resId
	 * @param callback
	 */
	public void setCallback(int resId, Callback callback) {
		mAdapter.setCallback(resId, callback);
	}
	
	/**
	 * 用来处理item中View事件，需要先调用initData方法
	 * 
	 * @param onListItemListener  
	 */
	public void setOnListItemListener(OnListItemListener onListItemListener){
		mAdapter.setOnListItemListener(onListItemListener);
	}

	public void notifyDataSetChanged() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public void notifyDataSetChanged(List<?> data) {
		if (data != null && mAdapter != null) {
			mAdapter.notifyDataSetChanged(data);
		}
	}
	
	public void setDefImgRes(int resId) {
		if (mAdapter != null) {
			mAdapter.setDefImgRes(resId);
		}
	}
	
}
