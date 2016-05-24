package com.gaiay.base.widget.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.gaiay.base.R;
import com.gaiay.base.util.Log;
import com.gaiay.base.util.StringUtil;

public class Dialog implements OnClickListener, OnItemClickListener, AnimationListener {

	public interface MClickListener {
		public boolean onClick(Dialog dialog, View v);
	}

	public interface MItemClickListener {
		public boolean onClick(Dialog dialog, View v, int position, String title);
	}

	public interface MShowAndDisEndListener {
		public void onShowEnd();

		public void onDisEnd(boolean isQD);
	}

	public interface MShowAndDisEndForThreeListener extends MShowAndDisEndListener {
		public void onDisEnd(int pos);
	}

	private static final String TAG = "widget.Dialog";

	/**
	 * 图标
	 */
	private ImageView icon;
	/**
	 * 标题
	 */
	private TextView title;
	/**
	 * 确定按钮
	 */
	private Button queding;
	/**
	 * 取消按钮
	 */
	private Button qvxiao;
	/**
	 * 确定按钮（单）
	 */
	private Button queding_1;
	private Button btn1, btn2, btn3;
	/**
	 * list
	 */
	private ListView list;

	private RelativeLayout center;
	private RelativeLayout title_layout;
	private RelativeLayout bottom_layout;
	private View rootView;

	private Activity cxt;

	private MClickListener qd_lis;
	private MClickListener qx_lis;
	private MClickListener qd_1_lis;
	private MClickListener btn1_lis;
	private MClickListener btn2_lis;
	private MClickListener btn3_lis;
	private MItemClickListener item_lis;
	private MShowAndDisEndListener sd_lis;

	private List<ModelCategory> list_data;
	private DialogAdapter list_adapter;

	private String msg;

	Animation anim_scale_in;
	Animation anim_scale_out;
	Animation last_anim;

	ControlChildTouchRelativeLayout layout;

	private PopupWindow dialog;
	View parentView;
	LayoutInflater inflater;

	boolean isTitle = false;
	boolean isBottom = false;
	boolean isList = false;

	private static boolean isShow = false;
	private static boolean isAnim = false;

	private boolean hasAnim = true;

	private int margin_top;

	public Dialog(Activity context) {
		cxt = context;
		inflater = LayoutInflater.from(cxt);
		this.parentView = context.getWindow().getDecorView();

		Rect frame = new Rect();
		parentView.getWindowVisibleDisplayFrame(frame);
		margin_top = frame.top;

		rootView = inflater.inflate(R.layout.dialog_view, null);
		rootView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		this.layout = (ControlChildTouchRelativeLayout) rootView.findViewById(R.id.dv_layout2);
		dialog = new PopupWindow(rootView, -1, -1, true) {
			@Override
			public void dismiss() {
				if (hasAnim) {
					disSelf();
				} else {
					super.dismiss();
					hasAnim = false;
					if (sd_lis != null) {
						sd_lis.onDisEnd(isQd);
						if (sd_lis instanceof MShowAndDisEndForThreeListener) {
							((MShowAndDisEndForThreeListener) sd_lis).onDisEnd(pos);
						}
					}
				}
			}
		};

		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if (isShow) {
					isShow = false;
				}
				if (isAnim) {
					isAnim = false;
				}
			}
		});

		title_layout = (RelativeLayout) rootView.findViewById(R.id.dv_layout_title);
		bottom_layout = (RelativeLayout) rootView.findViewById(R.id.dv_layout_bottom);
		center = (RelativeLayout) rootView.findViewById(R.id.dv_layout_center);

		anim_scale_in = AnimationUtils.loadAnimation(cxt, R.anim.dialog_in);
		anim_scale_out = AnimationUtils.loadAnimation(cxt, R.anim.dialog_out);
		anim_scale_out.setAnimationListener(this);
		anim_scale_in.setAnimationListener(this);
	}

	public void setBackgroud2(int id) {
		layout.setBackgroundResource(id);
	}

	/**
	 * 设置上下边距，当为-1时为默认边距
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setViewMargin(int left, int top, int right, int bottom) {
		RelativeLayout.LayoutParams lp = (LayoutParams) layout.getLayoutParams();
		lp.setMargins(left == -1 ? lp.leftMargin : left, top == -1 ? lp.topMargin : top, right == -1 ? lp.rightMargin
				: right, bottom == -1 ? lp.bottomMargin : bottom);
		layout.setLayoutParams(lp);
		layout.requestLayout();
	}

	public void dismiss() {
		Log.e(TAG, "isAnim:" + isAnim + " isShow:" + isShow);
		if (!isShow) {
			if (!isAnim) {
				layout.setChildTouch(false);
				isAnim = true;
				layout.startAnimation(anim_scale_out);
			}
		} else {
			dismissNoAnim();
		}
	}

	private void disSelf() {
		dismiss();
	}

	public void dismissNoAnim() {
		isShow = false;
		hasAnim = false;
		isAnim = false;
		rootView.setVisibility(View.INVISIBLE);
		dialog.dismiss();
	}

	long a;
	int count = 0;

	public void showSigle() {
		if (isShow) {
			return;
		}
		show();
	}

	@SuppressWarnings("deprecation")
	public void show() {
		if (!isAnim) {
			if (dialog != null) {
				isShow = true;
				isAnim = true;
				dialog.setBackgroundDrawable(new BitmapDrawable());
				layout.setAnimation(anim_scale_in);
				if (cxt != null && !cxt.isFinishing()) {
					dialog.showAtLocation(parentView, Gravity.CENTER_HORIZONTAL, 0, 0);
				}
				if (list != null && isList) {
					list.requestFocus();
				}
			}
		} else {
			(new Handler()).postDelayed(new Runnable() {
				@Override
				public void run() {
					count++;
					if (count >= 2) {
						isAnim = false;
						isShow = false;
						count = 0;
					}
					show();
				}
			}, 300);
		}
	}

	public void showNoAnim() {
		if (dialog != null) {
			isShow = true;
			dialog.setBackgroundDrawable(new BitmapDrawable());
			if (cxt != null && !cxt.isFinishing()) {
				dialog.showAtLocation(parentView, Gravity.CENTER_HORIZONTAL, 0, 0);
			}
			if (list != null && isList) {
				list.requestFocus();
			}
		}
	}

	public void setOnShowAndDisEndListener(MShowAndDisEndListener listener) {
		this.sd_lis = listener;
	}

	public void setOnShowAndDisEndListener(MShowAndDisEndForThreeListener listener) {
		this.sd_lis = listener;
	}

	public void setDialogWight(int px) {
		LayoutParams lp = (LayoutParams) layout.getLayoutParams();
		lp.width = px;
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout.setLayoutParams(lp);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		synchronized (this) {
			isAnim = false;
		}
		if (animation == anim_scale_out) {
			synchronized (this) {
				isShow = false;
			}
			(new Handler()).post(new Runnable() {
				@Override
				public void run() {
					dismissNoAnim();
				}
			});
		} else if (animation == anim_scale_in) {
			layout.setChildTouch(true);
			if (sd_lis != null) {
				sd_lis.onShowEnd();
			}
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (animation == anim_scale_in) {
			layout.setChildTouch(false);
		}
		a = System.currentTimeMillis();
	}

	boolean isQd = false;
	int pos = -1;

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.dv_queding) {
			isQd = true;
			if (qd_lis != null) {
				if (!qd_lis.onClick(this, v)) {
					this.dismiss();
				}
			} else {
				this.dismiss();
			}
		} else if (v.getId() == R.id.dv_qvxiao) {
			isQd = false;
			if (qx_lis != null) {
				if (!qx_lis.onClick(this, v)) {
					this.dismiss();
				}
			} else {
				this.dismiss();
			}
		} else if (v.getId() == R.id.dv_queding_2) {
			isQd = true;
			if (qd_1_lis != null) {
				if (!qd_1_lis.onClick(this, v)) {
					this.dismiss();
				}
			} else {
				this.dismiss();
			}
		} else if (v.getId() == R.id.dv_btn1) {
			pos = 1;
			if (btn1_lis != null) {
				if (!btn1_lis.onClick(this, v)) {
					this.dismiss();
				}
			} else {
				this.dismiss();
			}
		} else if (v.getId() == R.id.dv_btn2) {
			pos = 2;
			if (btn2_lis != null) {
				if (!btn2_lis.onClick(this, v)) {
					this.dismiss();
				}
			} else {
				this.dismiss();
			}
		} else if (v.getId() == R.id.dv_btn3) {
			pos = 3;
			if (btn3_lis != null) {
				if (!btn3_lis.onClick(this, v)) {
					this.dismiss();
				}
			} else {
				this.dismiss();
			}
		}
	}

	public void setInOutAnim(Animation in, Animation out) {
		if (in != null) {
			anim_scale_in = in;
			anim_scale_in.setAnimationListener(this);
		}
		if (out != null) {
			anim_scale_out = out;
			anim_scale_out.setAnimationListener(this);
		}
	}

	/**
	 * 设置中部显示内容
	 * 
	 * @param con
	 *            view
	 */
	public void setView(View con) {
		if (con == null) {
			return;
		}
		con.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		center.removeAllViews();
		center.addView(con);
	}

	/**
	 * 设置确定按钮的文字以及监听
	 * 
	 * @param queding
	 *            确定按钮的文字
	 * @param listener
	 *            确定监听
	 */
	public void setQD(String queding, MClickListener listener) {
		isBottom(true);
		changeBottomStatus(1);
		if (StringUtil.isBlank(queding)) {
			queding = "确定";
		}
		this.qd_lis = listener;
		this.queding.setText(queding);
	}

	private void isRadioList(boolean isR) {
		if (isR) {
			if (!isList) {
				center.addView(inflater.inflate(R.layout.dialog_view_center, null));

				list = (ListView) layout.findViewById(R.id.dv_list);

				list.setOnItemClickListener(this);
				isList = true;
			}
		} else {
			if (isList) {
				center.removeAllViews();
				isList = false;
			}
		}
	}

	public void isTitle(boolean isT) {
		if (isT) {
			if (!isTitle) {
				title_layout.addView(inflater.inflate(R.layout.dialog_view_title, null), new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				icon = (ImageView) layout.findViewById(R.id.dv_icon);
				title = (TextView) layout.findViewById(R.id.dv_title);
				isTitle = true;
			}
		} else {
			if (isTitle) {
				title_layout.removeAllViews();
				isTitle = false;
			}
		}
	}

	public void isBottom(boolean isB) {
		if (isB) {
			if (!isBottom) {
				bottom_layout.addView(inflater.inflate(R.layout.dialog_view_bottom, null), new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

				queding = (Button) layout.findViewById(R.id.dv_queding);
				qvxiao = (Button) layout.findViewById(R.id.dv_qvxiao);
				queding_1 = (Button) layout.findViewById(R.id.dv_queding_2);
				btn1 = (Button) layout.findViewById(R.id.dv_btn1);
				btn2 = (Button) layout.findViewById(R.id.dv_btn2);
				btn3 = (Button) layout.findViewById(R.id.dv_btn3);

				queding.setOnClickListener(this);
				qvxiao.setOnClickListener(this);
				queding_1.setOnClickListener(this);
				btn1.setOnClickListener(this);
				btn2.setOnClickListener(this);
				btn3.setOnClickListener(this);
				isBottom = true;
			}
		} else {
			if (isBottom) {
				bottom_layout.removeAllViews();
				isBottom = false;
			}
		}
	}

	/**
	 * 修改标题字体大小，单位为dip
	 * 
	 * @param size
	 */
	public void setTitleTextSize(int size) {
		title.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
	}

	private void changeBottomStatus(int i) {
		switch (i) {
		case 0:
			if (queding.getVisibility() == View.VISIBLE) {
				queding.setVisibility(View.INVISIBLE);
			}
			if (qvxiao.getVisibility() == View.VISIBLE) {
				qvxiao.setVisibility(View.INVISIBLE);
			}
			if (btn1.getVisibility() == View.VISIBLE) {
				btn1.setVisibility(View.INVISIBLE);
			}
			if (btn2.getVisibility() == View.VISIBLE) {
				btn2.setVisibility(View.INVISIBLE);
			}
			if (btn3.getVisibility() == View.VISIBLE) {
				btn3.setVisibility(View.INVISIBLE);
			}
			if (queding_1.getVisibility() != View.VISIBLE) {
				queding_1.setVisibility(View.VISIBLE);
			}
			break;
		case 1:
			if (queding.getVisibility() != View.VISIBLE) {
				queding.setVisibility(View.VISIBLE);
			}
			if (qvxiao.getVisibility() != View.VISIBLE) {
				qvxiao.setVisibility(View.VISIBLE);
			}
			if (queding_1.getVisibility() == View.VISIBLE) {
				queding_1.setVisibility(View.INVISIBLE);
			}
			if (btn1.getVisibility() == View.VISIBLE) {
				btn1.setVisibility(View.INVISIBLE);
			}
			if (btn2.getVisibility() == View.VISIBLE) {
				btn2.setVisibility(View.INVISIBLE);
			}
			if (btn3.getVisibility() == View.VISIBLE) {
				btn3.setVisibility(View.INVISIBLE);
			}
			break;
		case 2:
			if (queding.getVisibility() == View.VISIBLE) {
				queding.setVisibility(View.INVISIBLE);
			}
			if (qvxiao.getVisibility() == View.VISIBLE) {
				qvxiao.setVisibility(View.INVISIBLE);
			}
			if (queding_1.getVisibility() == View.VISIBLE) {
				queding_1.setVisibility(View.INVISIBLE);
			}
			if (btn1.getVisibility() != View.VISIBLE) {
				btn1.setVisibility(View.VISIBLE);
			}
			if (btn2.getVisibility() != View.VISIBLE) {
				btn2.setVisibility(View.VISIBLE);
			}
			if (btn3.getVisibility() != View.VISIBLE) {
				btn3.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 设置取消按钮的文字以及监听
	 * 
	 * @param qvxiao
	 *            取消按钮的文字
	 * @param listener
	 *            取消监听
	 */
	public void setQX(String qvxiao, MClickListener listener) {
		isBottom(true);
		changeBottomStatus(1);
		if (StringUtil.isBlank(qvxiao)) {
			qvxiao = "取消";
		}
		this.qx_lis = listener;
		this.qvxiao.setText(qvxiao);
	}

	/**
	 * 设置三个按钮中的第一个
	 * 
	 * @param qvxiao
	 *            取消按钮的文字
	 * @param listener
	 *            取消监听
	 */
	public void setThreeBtn1(String str1, MClickListener listener) {
		isBottom(true);
		changeBottomStatus(2);
		if (StringUtil.isBlank(str1)) {
			str1 = "取消";
		}
		this.btn1_lis = listener;
		this.btn1.setText(str1);
	}

	/**
	 * 设置三个按钮中的第一个
	 * 
	 * @param qvxiao
	 *            取消按钮的文字
	 * @param listener
	 *            取消监听
	 */
	public void setThreeBtn2(String str1, MClickListener listener) {
		isBottom(true);
		changeBottomStatus(2);
		if (StringUtil.isBlank(str1)) {
			str1 = "取消";
		}
		this.btn2_lis = listener;
		this.btn2.setText(str1);
	}

	/**
	 * 设置三个按钮中的第一个
	 * 
	 * @param qvxiao
	 *            取消按钮的文字
	 * @param listener
	 *            取消监听
	 */
	public void setThreeBtn3(String str1, MClickListener listener) {
		isBottom(true);
		changeBottomStatus(2);
		if (StringUtil.isBlank(str1)) {
			str1 = "取消";
		}
		this.btn3_lis = listener;
		this.btn3.setText(str1);
	}

	/**
	 * 设置单按钮的监听以及文字
	 * 
	 * @param queding
	 *            按钮的文字
	 * @param listener
	 *            按钮监听
	 */
	public void setSingleButton(String queding, MClickListener listener) {
		isBottom(true);
		changeBottomStatus(0);
		if (StringUtil.isBlank(queding)) {
			queding = "确定";
		}
		this.qd_1_lis = listener;
		this.queding_1.setText(queding);
	}

	/**
	 * 设置单选框列表
	 * 
	 * @param data
	 *            数据
	 * @param lis
	 *            条目点击事件监听
	 * @param select_position
	 *            首选的条目
	 */
	public void setRadioList(List<ModelCategory> data, MItemClickListener lis, int select_position) {
		isRadioList(true);
		item_lis = lis;
		if (data != null) {
			if (this.list_data == null) {
				list_data = new ArrayList<ModelCategory>();
			}
			this.list_data.clear();
			this.list_data.addAll(data);
		} else {
			this.list_data = new ArrayList<ModelCategory>();
			Log.e(TAG, "setRadioList():data is null");
		}
		list_adapter = new DialogAdapter(cxt, this.list_data);
		if (data != null) {
			if (select_position >= 0 && select_position < data.size()) {
				list_adapter.setSelected(select_position);
			} else {
				list_adapter.setSelected(0);
				Log.e(TAG, "setRadioList():select_position > data.size()");
			}
		}
		list.setAdapter(list_adapter);
	}

	/**
	 * 设置单选框列表
	 * 
	 * @param data
	 *            String数组的数据
	 * @param lis
	 *            条目点击事件监听
	 * @param select_position
	 *            首选的条目
	 */
	public void setRadioList(String[] data, MItemClickListener lis, int select_position) {
		isRadioList(true);
		item_lis = lis;
		getCategoryData(data);
		list_adapter = new DialogAdapter(cxt, this.list_data);
		if (data != null) {
			if (select_position >= 0 && select_position < list_data.size()) {
				list_adapter.setSelected(select_position);
			} else {
				list_adapter.setSelected(0);
				Log.e(TAG, "setRadioList():select_position > data.size()");
			}
		}
		list.setAdapter(list_adapter);
	}

	/**
	 * 设置单选框列表
	 * 
	 * @param data
	 *            String数组的数据
	 * @param lis
	 *            条目点击事件监听
	 * @param select_position
	 *            首选的条目
	 */
	public void setList(String[] data, MItemClickListener lis) {
		isRadioList(true);
		item_lis = lis;
		getCategoryData(data);
		list_adapter = new DialogAdapter(cxt, this.list_data);
		list_adapter.setRadio(false);
		list.setAdapter(list_adapter);
	}

	/**
	 * 设置title图标
	 * 
	 * @param resid
	 *            资源id
	 */
	public void setIcon(int resid) {
		isTitle(true);
		try {
			if (resid == 0) {
				icon.setVisibility(View.GONE);
				title.setGravity(Gravity.CENTER);
			} else {
				icon.setBackgroundResource(resid);
			}
		} catch (Exception e) {
			icon.setBackgroundResource(0);
			Log.e(TAG, "找不到资源文件：id=" + resid);
		}
	}

	/**
	 * 设置title文字
	 * 
	 * @param title
	 *            文字内容
	 */
	public void setTitle(String title) {
		isTitle(true);
		if (StringUtil.isBlank(title)) {
			title = "";
		}
		this.title.setText(Html.fromHtml(title));
	}

	/**
	 * 设置中部消息
	 * 
	 * @param msg
	 *            消息内容
	 */
	public void setMsg(String msg) {
		if (StringUtil.isBlank(msg)) {
			msg = "";
		}
		TextView txt = new TextView(cxt);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		txt.setTextColor(cxt.getResources().getColor(R.color.dialog_text));
		txt.setPadding(10, 10, 0, 10);
		txt.setLayoutParams(lp);
		txt.setTextSize(18);
		txt.setText(Html.fromHtml(msg));
		this.msg = msg;
		center.removeAllViews();
		center.addView(txt);
	}

	/**
	 * 获取设置的信息
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * 隐藏背景框
	 */
	public void hidBackGround() {
		// this.layout.setBackgroundResource(0);
		this.layout.getBackground().setAlpha(0);
	}

	/**
	 * 全屏显示
	 */
	public void fillScreen() {
		hidBackGround();
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		param.setMargins(0, margin_top, 0, 0);
		this.layout.setLayoutParams(param);
		RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		param2.setMargins(0, 0, 0, 0);
		center.setLayoutParams(param2);
	}

	private void getCategoryData(String[] data) {
		if (data == null) {
			return;
		}
		if (list_data == null) {
			list_data = new ArrayList<ModelCategory>();
		}
		list_data.clear();
		for (int i = 0; i < data.length; i++) {
			ModelCategory model = new ModelCategory();
			model.name = data[i];
			list_data.add(model);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (item_lis != null) {
			for (int i = 0; i < list_data.size(); i++) {
				list_data.get(i).isCheck = false;
			}
			list_data.get(position).isCheck = true;
			list_adapter.notifyDataSetChanged();
			if (!item_lis.onClick(this, view, position, list_data.get(position).name)) {
				this.dismiss();
			}
		} else {
			this.dismiss();
		}
	}

	private class DialogAdapter extends BaseAdapter {
		LayoutInflater inflater;
		boolean isRadio = true;

		public DialogAdapter(Context cxt, List<ModelCategory> data) {
			inflater = LayoutInflater.from(cxt);
		}

		public void setRadio(boolean isRadio) {
			this.isRadio = isRadio;
		}

		public void setSelected(int i) {
			if (list_data != null) {
				for (int j = 0; j < list_data.size(); j++) {
					list_data.get(j).isCheck = false;
				}
				if (list_data.size() > i) {
					list_data.get(i).isCheck = true;
				} else {
					list_data.get(0).isCheck = true;
				}
			}
		}

		@Override
		public int getCount() {
			return list_data.size();
		}

		@Override
		public Object getItem(int position) {
			return list_data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			DHolder holder;
			if (convertView == null) {
				v = inflater.inflate(R.layout.dialog_list_item, null);
				holder = new DHolder();
				holder.name = (TextView) v.findViewById(R.id.rlxqbjflid_txt);
				holder.radio = (RadioButton) v.findViewById(R.id.rlxqbjflid_radio);
				v.setTag(holder);
			} else {
				v = convertView;
				holder = (DHolder) v.getTag();
			}
			ModelCategory model = list_data.get(position);
			holder.name.setText(model.name);
			if (isRadio) {
				holder.radio.setVisibility(View.VISIBLE);
				holder.radio.setClickable(false);
				holder.radio.setChecked(model.isCheck);
			} else {
				holder.radio.setVisibility(View.INVISIBLE);
			}
			return v;
		}

	}

	private static final class DHolder {
		TextView name;
		RadioButton radio;
	}

}
