package com.gaiay.support.update;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gaiay.base.R;

public class UpdateDialog extends Activity {
    
    TextView mTxt;
    public static boolean hasShow = false;
    static UpdateDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (dialog != null) {
			dialog.finish();
		}
		setContentView(R.layout.dialog_update);
		mTxt = (TextView) findViewById(R.id.txt);
		findViewById(R.id.btnQD).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UpdateService.instance.getNewVersion();
				finish();
			}
		});
		findViewById(R.id.btnQX).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Intent intent = getIntent();
		if (intent != null) {
			mTxt.setText(UpdateService.instance.strNotifyContent);
		}
		UpdateService.instance.onShowUpdateDialog();
	}
	
	@Override
	protected void onStart() {
		hasShow = true;
		dialog = this;
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		hasShow = false;
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		dialog = null;
		super.onDestroy();
	}
	
}
