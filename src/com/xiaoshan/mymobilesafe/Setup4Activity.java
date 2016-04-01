package com.xiaoshan.mymobilesafe;

import com.xiaoshan.mymobilesafe.receiver.MyAdminReceiver;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Setup4Activity extends BaseSetupActivity {

	private TextView tvSetSucced;
	private CheckBox cbActivateLostFind;
	private TextView tvPreStep3;
	private ImageView imActivateEntirety;
	private SharedPreferences sp;
	private DevicePolicyManager dpm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		tvSetSucced = (TextView) findViewById(R.id.tv_set_succed);
		tvPreStep3 = (TextView) findViewById(R.id.tv_pre_step3);
		cbActivateLostFind = (CheckBox) findViewById(R.id.cb_start_lost_find);
		imActivateEntirety = (ImageView) findViewById(R.id.iv_arrow);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		boolean lostFindStatus = sp.getBoolean("lost_find_activated", false);
		cbActivateLostFind.setChecked(lostFindStatus);
		if (lostFindStatus) {
			cbActivateLostFind.setText("你已经开启防盗保护");
		} else {
			cbActivateLostFind.setText("你没有开启防盗保护");
		}

		imActivateEntirety.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ComponentName cn = new ComponentName(Setup4Activity.this,
						MyAdminReceiver.class);
				if (!dpm.isAdminActive(cn)) {
					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
					// 劝说用户开启管理员
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"开启我就可以锁屏了.");
					startActivity(intent);
				} else {
					Toast.makeText(Setup4Activity.this, "您已经开启全面保护了.",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		cbActivateLostFind
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Editor editor = sp.edit();

						if (isChecked) {
							buttonView.setText("你已经开启防盗保护");
							editor.putBoolean("lost_find_activated", true);
						} else {
							buttonView.setText("你没有开启防盗保护");
							editor.putBoolean("lost_find_activated", false);
						}

						editor.commit();
					}
				});
		tvSetSucced.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				editor.putBoolean("set_guided", true);
				editor.commit();
				Intent intent = new Intent(Setup4Activity.this,
						PhoneFindBackActivity.class);
				startActivity(intent);
				finish();
			}
		});

		tvPreStep3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPrePage();
			}
		});
	}

	@Override
	public void showPrePage() {
		Intent intent = new Intent(Setup4Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
	}

	@Override
	public void showNextPage() {
		// TODO Auto-generated method stub

	}
}
