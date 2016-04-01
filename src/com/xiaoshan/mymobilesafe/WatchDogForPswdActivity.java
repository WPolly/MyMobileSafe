package com.xiaoshan.mymobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WatchDogForPswdActivity extends Activity {

	private EditText etWatchDogPsd;
	private Button btWatchDogOk;
	private TextView tvLockedAppName;
	private ImageView ivLockedAppIcon;
	private Vibrator vibrator;
	private SharedPreferences sp;
	private PackageManager pm;
	private Intent intent;
	private String value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_dog_for_pswd);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		pm = getPackageManager();
		intent = getIntent();
		value = intent.getStringExtra("package_name");
		tvLockedAppName = (TextView) findViewById(R.id.tv_locked_app_name);
		ivLockedAppIcon = (ImageView) findViewById(R.id.iv_locked_app_icon);
		etWatchDogPsd = (EditText) findViewById(R.id.et_watch_dog_psd);
		btWatchDogOk = (Button) findViewById(R.id.bt_watch_dog_ok);
		try {
			ApplicationInfo info = pm.getApplicationInfo(value, 0);
			tvLockedAppName.setText(info.loadLabel(pm));
			ivLockedAppIcon.setImageDrawable(info.loadIcon(pm));
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		btWatchDogOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String psd = etWatchDogPsd.getText().toString().trim();
				if (TextUtils.isEmpty(psd)) {
					vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					vibrator.vibrate(500);
					Toast.makeText(WatchDogForPswdActivity.this, " ‰»Î√‹¬Î≤ªƒ‹Œ™ø’",
							Toast.LENGTH_SHORT).show();
					return;
				}

				String watchDogPsd = sp.getString("watch_dog_psd", "");

				if (psd.equals(watchDogPsd)) {
					Intent intent = new Intent();
					intent.setAction("com.xiaoshan.mymobilesafe.tempstopprotect");
					intent.putExtra("package_name", value);
					sendBroadcast(intent);
					finish();
				} else {
					vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					vibrator.vibrate(200);
					Toast.makeText(WatchDogForPswdActivity.this, " ‰»Î√‹¬Î¥ÌŒÛ",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		finish();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
	}
	
	@Override
	protected void onStop() {
		finish();
		super.onStop();
	}

}
