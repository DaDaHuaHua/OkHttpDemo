package com.gaiay.base.widget.dialog;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.gaiay.base.R;
import com.gaiay.base.util.Log;

/**
 * 重写popupWindow
 * @author Administrator
 *
 */
public class XQuickActionBar extends PopupWindow {

	private View root;
	private Context context;

	private View anchor;

	private PopupWindow window;
	private Drawable background = null;
	private WindowManager windowManager;
	private XBarAdapter adapter;
	private ListView listView;
//	private String[] arrays;
	private int[] arrays;

	public XQuickActionBar(View anchor) {

		super(anchor);

		this.anchor = anchor;

		this.window = new PopupWindow(anchor.getContext());

		/**
		 * 在popwindow外点击即关闭该window
		 */
		window.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					// 让其消失
					XQuickActionBar.this.window.dismiss();
					return true;
				}
				return false;
			}
		});

		context = anchor.getContext();
		windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		root = (ViewGroup) inflater.inflate(R.layout.x_circle_pop_right, null);

		adapter = new XBarAdapter(arrays);

		listView = (ListView)root.findViewById(R.id.listview_x);
		listView.setOnItemClickListener(itemClickListener);
		listView.setAdapter(adapter);
		setContentView(root);

	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			if(onPopClickListener != null){
				onPopClickListener.onPopClick(arrays[position]);
			}
		}

	};

	/**
	 * 返回ListView 事件监听处理
	 * @return
	 */
	public ListView getListView() {
		return listView;
	}

	public void show(int[] aStrings){
		if(aStrings != null){
			arrays = aStrings;
		}
		show();
	}

	/**
	 * 弹出窗体
	 */
	public void show() {
		if(adapter != null){
			adapter.setArrays(arrays);
			adapter.notifyDataSetChanged();
		}else {
			adapter = new XBarAdapter(arrays);
		}


		preShow();

		int[] location = new int[2];

		// 得到anchor的位置
		anchor.getLocationOnScreen(location);
		// 以anchor的位置构造一个矩形
		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());

		root.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
       
		int rootWidth = root.getMeasuredWidth();
		
		// 得到屏幕的宽
		int screenWidth = windowManager.getDefaultDisplay().getWidth();

		// 设置弹窗弹出的位置的X y
		int xPos = (screenWidth - rootWidth);
		int yPos = 0;
		if (isTop) {
			int totalHeight = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                    View listItem = adapter.getView(i, null, listView);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
            }
			yPos = anchorRect.top - 35 - totalHeight;
			root.setBackgroundResource(R.drawable.x_circle_pop_window_back_top);
			window.setAnimationStyle(R.style.Animations_PopDownMenu_Bottom);
		} else {
			yPos = anchorRect.bottom;
			window.setAnimationStyle(R.style.Animations_PopDownMenu_Top);
		}
		

//		xPos = (location[0] + (anchor.getWidth()-rootWidth)/2);
//		xPos = ((anchorRect.left + (anchorRect.width() / 2)) - (rootWidth / 2));
		xPos = anchorRect.left;
		Log.e("anchorRect.width():" + anchorRect.width() + " rootWidth:" + rootWidth);
//		Log.e(xPos);
//		if(xPos < 5) {
//			xPos = 5;
//		}
//
//		if((xPos + rootWidth) > (screenWidth - 5))
//			xPos = screenWidth - rootWidth - 5;
		Log.e(xPos);
		boolean okTop = true;
		boolean okBottom = true;
		if (viewTop != null) {
			viewTop.getLocationOnScreen(location);
			if (yPos < location[1]) {
				okTop = false;
				yPos = anchorRect.bottom;
				root.setBackgroundResource(R.drawable.x_circle_pop_window_back);
				window.setAnimationStyle(R.style.Animations_PopDownMenu_Top);
			}
		}
		if (viewBottom != null) {
			viewBottom.getLocationOnScreen(location);
			if (yPos > location[1]) {
				okBottom = false;
				int totalHeight = 0;
	            for (int i = 0; i < adapter.getCount(); i++) {
	                    View listItem = adapter.getView(i, null, listView);
	                    listItem.measure(0, 0);
	                    totalHeight += listItem.getMeasuredHeight();
	            }
				yPos = anchorRect.top - 35 - totalHeight;
				root.setBackgroundResource(R.drawable.x_circle_pop_window_back_top);
				window.setAnimationStyle(R.style.Animations_PopDownMenu_Bottom);
			}
		}
		if (viewTopPos >= 0) {
			if (yPos < viewTopPos) {
				okTop = false;
				yPos = anchorRect.bottom;
				root.setBackgroundResource(R.drawable.x_circle_pop_window_back);
				window.setAnimationStyle(R.style.Animations_PopDownMenu_Top);
			}
		}
		if (viewBottomPos >= 0) {
			if (yPos > viewBottomPos) {
				okBottom = false;
				int totalHeight = 0;
	            for (int i = 0; i < adapter.getCount(); i++) {
	                    View listItem = adapter.getView(i, null, listView);
	                    listItem.measure(0, 0);
	                    totalHeight += listItem.getMeasuredHeight();
	            }
				yPos = anchorRect.top - 35 - totalHeight;
				root.setBackgroundResource(R.drawable.x_circle_pop_window_back_top);
				window.setAnimationStyle(R.style.Animations_PopDownMenu_Bottom);
			}
		}
		if (!okTop && !okBottom) {
			if (isTop) {
				yPos = anchorRect.top - root.getMeasuredHeight();
				window.setAnimationStyle(R.style.Animations_PopDownMenu_Bottom);
			} else {
				yPos = anchorRect.bottom;
				window.setAnimationStyle(R.style.Animations_PopDownMenu_Top);
			}
		}
		
		// 在指定位置弹出弹窗
		window.showAtLocation(this.anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}
	
	View viewTop;
	View viewBottom;
	boolean isTop;
	int viewTopPos = -1;
	int viewBottomPos = -1;
	public void setTopAndBottom(View viewTop, View viewBottom) {
		this.viewTop = viewTop;
		this.viewBottom = viewBottom;
		if (viewTop != null) {
			viewTopPos = -1;
		}
		if (viewBottom != null) {
			viewBottomPos = -1;
		}
	}
	public void setTopPos(int y) {
		viewTopPos = y;
		viewTop = null;
	}
	public void setBottomPos(int y) {
		viewBottomPos = y;
		viewBottom = null;
	}
	
	/**
	 * 设置弹出框是否默认显示在基础view的上面，默认是在下面
	 */
	public void setDefIsTop(boolean isTop) {
		this.isTop = isTop;
	}
	
	int resTextColor;
	int resTextSize;
	
	public void setTextColor(int resId) {
		resTextColor = resId;
	}
	
	public void setTextSize(int resId) {
		resTextSize = resId;
	}
	
	/**
	 * 消失
	 */
	public void dismissBar() {
		// 让其消失
		if(XQuickActionBar.this.window != null && XQuickActionBar.this.window.isShowing())
			XQuickActionBar.this.window.dismiss();
	}

	/**
	 * 预处理窗口
	 */
	protected void preShow() {

		if (root == null) {
			throw new IllegalStateException("需要为弹窗设置布局");
		}

		if (background == null) {
			window.setBackgroundDrawable(new BitmapDrawable());
		} else {
			window.setBackgroundDrawable(background);
		}

		// 设置宽度
		window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		// 设置高度
		window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

		window.setTouchable(true);
		window.setFocusable(true);
		window.setOutsideTouchable(true);
		// 指定布局
		window.setContentView(root);
	}

	class XBarAdapter extends BaseAdapter {

		private int[] arrays = null;

		public XBarAdapter (int[] aStrings) {
			this.arrays = aStrings;
		}

		public void setArrays(int[] strings) {
			if(arrays != null)
				arrays = null;
			this.arrays = strings;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(arrays == null || arrays.length == 0)
				return 0;
			return arrays.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if(arrays == null || arrays.length == 0)
				return null;
			return arrays[position%arrays.length];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			if(arrays == null || arrays.length == 0)
				return 0;
			return position%arrays.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null || convertView.getTag() == null) {
				convertView = ((LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.x_circle_pop_list_item, null);
				holder = new ViewHolder();
				holder.nameTv = (TextView)convertView.findViewById(R.id.x_circle_pop_right);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			holder.nameTv.setText(context.getText(arrays[position]));
			if (resTextColor > 0) {
				holder.nameTv.setTextColor(context.getResources().getColor(resTextColor));
			}
			if (resTextSize > 0) {
				holder.nameTv.setTextSize(context.getResources().getDimension(resTextSize));
			}
			return convertView;
		}
		private class ViewHolder {
			TextView nameTv;
		}
	}

	/**
	 * @return the arrays
	 */
	public int[] getArrays() {
		return arrays;
	}

	/**
	 * @param arrays the arrays to set
	 */
	public void setArrays(int[] arrays) {
		this.arrays = arrays;
	}

	public interface OnPopClickListener {
		public void onPopClick(int index);
	}

	private OnPopClickListener onPopClickListener;

	/**
	 * @return the onPopClickListener
	 */
	public OnPopClickListener getOnPopClickListener() {
		return onPopClickListener;
	}

	/**
	 * @param onPopClickListener the onPopClickListener to set
	 */
	public void setOnPopClickListener(OnPopClickListener onPopClickListener) {
		this.onPopClickListener = onPopClickListener;
	}

}
