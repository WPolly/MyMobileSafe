package com.xiaoshan.mymobilesafe;

import com.xiaoshan.mymobilesafe.ui.SettingItemView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView bindSIM;
	private SharedPreferences sp;
	private TelephonyManager tm;
	private TextView tvNextStep;
	private TextView tvPreStep;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		bindSIM = (SettingItemView) findViewById(R.id.siv_lock_sim);
		tvNextStep = (TextView) findViewById(R.id.tv_next_step2);
		tvPreStep = (TextView) findViewById(R.id.tv_pre_step);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		boolean checkUpdate = sp.getBoolean("lock_sim", false);
		bindSIM.setChecked(checkUpdate);
		bindSIM.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (bindSIM.isChecked()) {
					bindSIM.setChecked(false);
					editor.putBoolean("lock_sim", false);
				} else {
					bindSIM.setChecked(true);
					editor.putBoolean("lock_sim", true);
					String simSerial = tm.getSimSerialNumber();
					editor.putString("sim_serial", simSerial);
				}
				editor.commit();
			}
		});

		tvNextStep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showNextPage();
			}
		});

		tvPreStep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPrePage();
			}
		});
	}

	@Override
	public void showPrePage() {
		Intent intent = new Intent(Setup2Activity.this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
	}

	@Override
	public void showNextPage() {
		if (bindSIM.isChecked()) {
			Intent intent = new Intent(Setup2Activity.this,
					Setup3Activity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.next_in, R.anim.next_out);
		} else {
			Toast.makeText(Setup2Activity.this, "您还没有绑定SIM卡", Toast.LENGTH_LONG)
					.show();
		}
	}
}
