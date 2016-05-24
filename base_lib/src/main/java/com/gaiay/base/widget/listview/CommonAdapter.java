package com.gaiay.base.widget.listview;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.tsz.afinal.FinalBitmap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaiay.base.BaseApplication;
import com.gaiay.base.util.StringUtil;

@SuppressLint("UseSparseArrays")
public class CommonAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<?> items;
	private List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
	private int resource;
	private String[] from;
	private int[] to;
	private int count;
	private BitmapConfig mConfig;
	private FinalBitmap fb;
	
	/**
	 * 构造一个CommonAdapter，它会把data中得数据，自动填充到resource中
	 * 
	 * @param context
	 * @param data List<?>
	 * @param resource item资源文件id
	 * @param from data中实体的属性名
	 * @param to resource中用于显示from的view的id，与from相对应
	 */
	public CommonAdapter(Context context, List<?> data, int resource, String[] from, int[] to) {
		this.context = context;
		this.resource = resource;
		this.from = from;
		this.to = to;
		count = to.length;
		inflater = LayoutInflater.from(context);
		this.items = data;
		buildData();
		if (StringUtil.isBlank(BaseApplication.app.getCacheFolder())) {
			fb = FinalBitmap.create(context);
		} else {
			fb = FinalBitmap.create(context, BaseApplication.app.getCacheFolder());
		}
	}
	
	public void configFinalBitmap(int loadfailImage, int loadingImage, boolean isUserAnim) {
		fb.configLoadfailImage(loadfailImage);
		fb.configLoadingImage(loadingImage);
		fb.configIsUseAnim(isUserAnim);
	}
	
	public void setBitmapConfig(BitmapConfig bitmapConfig) {
		this.mConfig = bitmapConfig;
		if (mConfig.defaultImageRes > 0 && mConfig.defaultImageBitmap == null) {
			mConfig.defaultImageBitmap = BitmapFactory.decodeResource(context.getResources(), mConfig.defaultImageRes);
		}
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Map<Integer, View> holder = new HashMap<Integer, View>();
		if (convertView == null) {
			convertView = inflater.inflate(resource, null);
			for (int i = 0; i < count; i++) {
				holder.put(to[i], convertView.findViewById(to[i]));
			}
			for (Integer resId : listeners.keySet()) {
				if (holder.get(resId) == null) {
					holder.put(resId, convertView.findViewById(resId));
				}
			}
			for (Integer resId : callbacks.keySet()) {
				if (holder.get(resId) == null) {
					holder.put(resId, convertView.findViewById(resId));
				}
			}
			convertView.setTag(holder);
		} else {
			holder.putAll((Map<Integer, View>) convertView.getTag());
		}
		Map<String, Object> entity = data.get(position);
		for (int i = 0; i < count; i++) {
			View v = holder.get(to[i]);
			Object value = entity.get(from[i]);
			if (value instanceof Integer) {
				v.setVisibility((Integer)value);
			} else if (v instanceof Checkable) {
	            if (value instanceof Boolean) {
	                ((Checkable) v).setChecked((Boolean) value);
	            }
//	            else if (value instanceof String) {
//	                ((Checkable) v).setText(String.valueOf(value));
//	            } else {
//	                throw new IllegalStateException(v.getClass().getName() +
//	                        " should be bound to a Boolean, not a " +
//	                        (value == null ? "<unknown type>" : value.getClass()));
//	            }
	        } else if (v instanceof TextView) {
        		((TextView) v).setText(Html.fromHtml(String.valueOf(value)));
	        } else if (v instanceof ImageView) {
	        	if (value instanceof Integer) {
	        		((ImageView) v).setImageResource((Integer) value);                            
	        	} else if (value instanceof Bitmap) {
	        		((ImageView) v).setImageBitmap((Bitmap) value);
	        	} else if (value instanceof String) {
	        		// 从网络获取图片
	        		fb.display((ImageView) v, (String) value);
	        	} else {
	        		if (mConfig != null && mConfig.defaultImageBitmap != null && !mConfig.defaultImageBitmap.isRecycled()) {
	        			((ImageView) v).setImageBitmap(mConfig.defaultImageBitmap);
	        		}
	        	}
	        }
		}
		for (final Entry<Integer, Callback> entry : callbacks.entrySet()) {
			if (entry.getValue() != null) {
				entry.getValue().onViewInit(holder.get(entry.getKey()), position);
			}
		}
		final View itemView = convertView;
		for (final Entry<Integer, OnClickListener> entry : listeners.entrySet()) {
			holder.get(entry.getKey()).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					entry.getValue().onClick(v, itemView, holder, position);
				}
				
			});
		}
		
		if (this.onListItemListener != null) {
			this.onListItemListener.onItem(position, convertView, parent);
		}
		return convertView;
	}

	/**
	 * 将data转换为所需要的Map集合，key为实体的属性名，value为对应的值
	 */
	private void buildData() {
		data.clear();
		if (items != null && !items.isEmpty()) {
			Map<String, Object> entity = null;
			for (Object obj : items) {
				Class<?> clazz = obj.getClass();
				entity = new HashMap<String, Object>();
				for (int i = 0; i < from.length; i++) {
					try {
						Field f = clazz.getDeclaredField(from[i]);
						entity.put(from[i], f.get(obj));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				data.add(entity);
			}
		}
	}
	
	@Override
	public void notifyDataSetChanged() {
		buildData();
		super.notifyDataSetChanged();
	}
	
	public void notifyDataSetChanged(List<?> data) {
		this.items = data;
		buildData();
		super.notifyDataSetChanged();
	}
	
	public interface OnClickListener {
		public abstract void onClick(View v, View convertView, Map<Integer, View> resMap, int position);
	}
	
	private Map<Integer, OnClickListener> listeners = new HashMap<Integer, OnClickListener>();
	
	/**
	 * 为item中的按钮或者view增加点击事件，需要先调用initData方法
	 * 
	 * @param resId
	 * @param onClickListener
	 */
	public void setOnClickListener(int resId, OnClickListener onClickListener) {
		listeners.put(resId, onClickListener);
	}
	
	public interface Callback {
		public abstract void onViewInit(View v, int position);
	}
	
	private Map<Integer, Callback> callbacks = new HashMap<Integer, Callback>();
	
	/**
	 * 单独为item进行赋值等操作。比如可以判断收藏状态，然后为ImageView设置收藏或者未收藏的背景图片
	 * 
	 * @param resId
	 * @param callback
	 */
	public void setCallback(int resId, Callback callback) {
		callbacks.put(resId, callback);
	}
		
	public interface OnListItemListener {
		public abstract void onItem(int position, View convertView, ViewGroup parent);
	}
	
	private OnListItemListener onListItemListener;

	public void setOnListItemListener(OnListItemListener onListItemListener) {
		this.onListItemListener = onListItemListener;
	}
	
	public void setDefImgRes(int resId) {
		fb.configLoadingImage(resId);
	}
	
}
