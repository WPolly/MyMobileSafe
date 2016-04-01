package com.xiaoshan.mymobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PhoneFindBackActivity extends Activity {

	private SharedPreferences sp;
	private Button btRestartSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean guided = sp.getBoolean("set_guided", false);
		if (guided) {
			setContentView(R.layout.activity_phonefindback);
			btRestartSet = (Button) findViewById(R.id.bt_restart_setting);
		} else {
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}

		btRestartSet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PhoneFindBackActivity.this,
						Setup1Activity.class);
				startActivity(intent);
				finish();
			}
		});

	}
}
