package com.xiaoshan.mymobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoshan.mymobilesafe.service.AutoCleanService;
import com.xiaoshan.mymobilesafe.service.CallSmsSafeService;
import com.xiaoshan.mymobilesafe.service.ShowInComingCallService;
import com.xiaoshan.mymobilesafe.service.WatchDogService;
import com.xiaoshan.mymobilesafe.ui.SettingItemView;
import com.xiaoshan.mymobilesafe.ui.SettingSelectView;
import com.xiaoshan.mymobilesafe.utils.ServiceStatusUtils;

public class SettingActivity extends Activity {

	private SettingItemView sivCheckUpdate, sivShowIncomingCall,
			sivCallSmsSafe, sivAutoCleanTask, sivWatchDog;
	private SettingSelectView ssvChooseShowAddressStyle, ssvSetWatchDogPsd;
	private SharedPreferences sp;
	private String[] items;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sivCheckUpdate = (SettingItemView) findViewById(R.id.siv_check_update);
		sivShowIncomingCall = (SettingItemView) findViewById(R.id.siv_set_show_incoming_call);
		sivCallSmsSafe = (SettingItemView) findViewById(R.id.siv_set_callsms_safe);
		sivWatchDog = (SettingItemView) findViewById(R.id.siv_watch_dog);
		sivAutoCleanTask = (SettingItemView) findViewById(R.id.siv_auto_clean_task);
		ssvChooseShowAddressStyle = (SettingSelectView) findViewById(R.id.ssv_incoming_call_style);
		ssvSetWatchDogPsd = (SettingSelectView) findViewById(R.id.ssv_set_watch_dog_psd);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		items = new String[] { "卫士蓝", "金属灰", "苹果绿", "活力橙", "半透明" };
		boolean checkUpdate = sp.getBoolean("check_update", false);
		sivCheckUpdate.setChecked(checkUpdate);
		boolean showIncomingCall = sp.getBoolean("show_incoming_call", false);
		sivShowIncomingCall.setChecked(showIncomingCall);
		boolean callSmsSafe = sp.getBoolean("call_sms_safe_status", false);
		sivCallSmsSafe.setChecked(callSmsSafe);
		boolean autoClean = sp.getBoolean("auto_clean", false);
		sivAutoCleanTask.setChecked(autoClean);
		boolean watchDog = sp.getBoolean("watch_dog", false);
		sivWatchDog.setChecked(watchDog);
		int which = sp.getInt("toast_which", 0);
		ssvChooseShowAddressStyle.setDesc(items[which]);
		String watchDogPsd = sp.getString("watch_dog_psd", "");
		ssvSetWatchDogPsd.setDesc(watchDogPsd);

		sivCheckUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (sivCheckUpdate.isChecked()) {
					sivCheckUpdate.setChecked(false);
					editor.putBoolean("check_update", false);
				} else {
					sivCheckUpdate.setChecked(true);
					editor.putBoolean("check_update", true);
				}
				editor.commit();
			}
		});

		sivCallSmsSafe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				Intent intent = new Intent(getApplicationContext(),
						CallSmsSafeService.class);
				String clsName = CallSmsSafeService.class.getName();
				System.out.println(clsName);
				boolean isRunning = ServiceStatusUtils.isServiceRunning(
						getApplicationContext(), clsName);
				if (sivCallSmsSafe.isChecked()) {
					sivCallSmsSafe.setChecked(false);
					if (isRunning) {
						stopService(intent);
					}
					editor.putBoolean("call_sms_safe_status", false);
				} else {
					sivCallSmsSafe.setChecked(true);
					if (!isRunning) {
						startService(intent);
					}
					editor.putBoolean("call_sms_safe_status", true);
				}
				editor.commit();
			}
		});

		sivWatchDog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String SettedPsd = sp.getString("watch_dog_psd", "");
				if (TextUtils.isEmpty(SettedPsd)) {
					Toast.makeText(SettingActivity.this, "请先设置程序锁密码",
							Toast.LENGTH_SHORT).show();
				} else {
					Editor editor = sp.edit();
					Intent intent = new Intent(getApplicationContext(),
							WatchDogService.class);
					String clsName = WatchDogService.class.getName();
					boolean isRunning = ServiceStatusUtils.isServiceRunning(
							getApplicationContext(), clsName);
					if (sivWatchDog.isChecked()) {
						sivWatchDog.setChecked(false);
						if (isRunning) {
							stopService(intent);
						}
						editor.putBoolean("watch_dog", false);
					} else {
						sivWatchDog.setChecked(true);
						if (!isRunning) {
							startService(intent);
						}
						editor.putBoolean("watch_dog", true);
					}
					editor.commit();
				}
			}
		});

		sivAutoCleanTask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				Intent intent = new Intent(getApplicationContext(),
						AutoCleanService.class);
				String clsName = AutoCleanService.class.getName();
				boolean isRunning = ServiceStatusUtils.isServiceRunning(
						getApplicationContext(), clsName);
				if (sivAutoCleanTask.isChecked()) {
					sivAutoCleanTask.setChecked(false);
					if (isRunning) {
						stopService(intent);
					}
					editor.putBoolean("auto_clean", false);
				} else {
					sivAutoCleanTask.setChecked(true);
					if (!isRunning) {
						startService(intent);
					}
					editor.putBoolean("auto_clean", true);
				}
				editor.commit();
			}
		});

		sivShowIncomingCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				Intent intent = new Intent(getApplicationContext(),
						ShowInComingCallService.class);
				String clsName = ShowInComingCallService.class.getName();
				System.out.println(clsName);
				boolean isRunning = ServiceStatusUtils.isServiceRunning(
						getApplicationContext(), clsName);
				if (sivShowIncomingCall.isChecked()) {
					sivShowIncomingCall.setChecked(false);
					if (isRunning) {
						stopService(intent);
					}
					editor.putBoolean("show_incoming_call", false);
				} else {
					sivShowIncomingCall.setChecked(true);
					if (!isRunning) {
						startService(intent);
					}
					editor.putBoolean("show_incoming_call", true);
				}
				editor.commit();
			}
		});

		ssvChooseShowAddressStyle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("号码归属地显示风格");

				int checkedItem = sp.getInt("toast_which", 0);

				builder.setSingleChoiceItems(items, checkedItem,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Editor editor = sp.edit();
								editor.putInt("toast_which", which);
								editor.commit();
								dialog.dismiss();
								ssvChooseShowAddressStyle.setDesc(items[which]);
							}
						});

				builder.setNegativeButton("取消", null);

				builder.show();
			}
		});

		ssvSetWatchDogPsd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View view = View.inflate(SettingActivity.this,
						R.layout.dialog_set_watch_dog_psd, null);
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				final AlertDialog dialog = builder.create();
				final EditText etWatchPsd = (EditText) view
						.findViewById(R.id.et_input_watch_dog_psd);
				TextView tvOk = (TextView) view.findViewById(R.id.tv_ok);
				TextView tvCancel = (TextView) view
						.findViewById(R.id.tv_cancel);
				tvOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String psd = etWatchPsd.getText().toString().trim();
						if (TextUtils.isEmpty(psd)) {
							Toast.makeText(SettingActivity.this, "您没有输入密码",
									Toast.LENGTH_SHORT).show();
							return;
						} else {
							System.out.println(psd);
							Editor editor = sp.edit();
							editor.putString("watch_dog_psd", psd);
							editor.commit();
						}
						ssvSetWatchDogPsd.setDesc(psd);

						dialog.dismiss();
					}
				});

				tvCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.setView(view, 0, 0, 0, 0);
				dialog.show();
			}
		});
	}
}
