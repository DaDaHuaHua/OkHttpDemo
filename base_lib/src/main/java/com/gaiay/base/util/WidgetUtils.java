package com.gaiay.base.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class WidgetUtils extends Utils {

	/**
	 * 重新计算ListView高度
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) { 
        setAndGetListViewHeightBasedOnChildren(listView);
    }
	
	/**
	 * 计算ListView高度并且返回高度值
	 * @param listView
	 * @return
	 */
	public static int setAndGetListViewHeightBasedOnChildren(ListView listView) { 
		ListAdapter listAdapter = listView.getAdapter();  
		if (listAdapter == null) { 
			// pre-condition 
			return 0; 
		} 
		
		int totalHeight = 0; 
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView); 
			if (listItem != null) {
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight() + listItem.getPaddingTop() + listItem.getPaddingBottom();
			}
		} 
		
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)); 
		listView.setLayoutParams(params); 
		return params.height;
	}
	
	public static void setGridViewHeightBasedOnChildren(GridView gridView, int numColumns, int verticalSpacing) { 
        ListAdapter listAdapter = gridView.getAdapter();  
        if (listAdapter == null) { 
            return; 
        }
        
        int totalHeight = 0; 
        for (int i = 0; i < listAdapter.getCount(); i += numColumns) {
            View listItem = listAdapter.getView(i, null, gridView); 
            if (listItem != null) {
            	listItem.measure(0, 0);
            	totalHeight += listItem.getMeasuredHeight() + listItem.getPaddingTop() + listItem.getPaddingBottom();
			}
        } 
        
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + (verticalSpacing * (listAdapter.getCount() / numColumns)); 
        gridView.setLayoutParams(params); 
    }
	
	public static void galleryAlignLeftAndRight(Gallery gallery, int gallery_width, int gallery_space, int child_width) {
		
		int offset;
		if (gallery_width <= child_width) {
			offset = gallery_width / 2 - child_width / 2 - gallery_space;
		} else {
			offset = gallery_width - child_width - 2 * gallery_space;
		}
		
		Log.e("gallery.getWidth():" + gallery.getWidth());
		Log.e("gallery.getChildCount():" + gallery.getChildCount());
		Log.e("gallery_width:" + gallery_width);
		Log.e("gallery_space:" + gallery_space);
		Log.e("child_width:" + child_width);
		
		MarginLayoutParams mlp = (MarginLayoutParams) gallery.getLayoutParams();
		mlp.leftMargin = -offset;
		mlp.width = mlp.width - 2 * offset;
		
	}
	
}
