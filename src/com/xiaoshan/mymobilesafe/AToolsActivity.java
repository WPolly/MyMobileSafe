package com.xiaoshan.mymobilesafe;

import com.xiaoshan.mymobilesafe.utils.SmsUtils;
import com.xiaoshan.mymobilesafe.utils.SmsUtils.BackupCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class AToolsActivity extends Activity implements OnClickListener {

	private TextView tvPhoneAddressQuery, tvBackupSms;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		tvPhoneAddressQuery = (TextView) findViewById(R.id.tv_phone_address_query);
		tvBackupSms = (TextView) findViewById(R.id.tv_backup_sms);
		progressDialog = new ProgressDialog(this);
		tvPhoneAddressQuery.setOnClickListener(this);
		tvBackupSms.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.tv_phone_address_query:
			Intent intent = new Intent(AToolsActivity.this,
					PhoneAddressQueryActivity.class);
			startActivity(intent);
			break;
			
		case R.id.tv_backup_sms:
			progressDialog.setTitle("正在备份..");
			progressDialog
					.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setCancelable(false);
			progressDialog.show();
			new Thread() {
				public void run() {
					try {
						SmsUtils.backupSms(getApplicationContext(),
								new BackupCallBack() {

									@Override
									public void onBackup(int process) {
										
										progressDialog.setProgress(process);
										
									}

									@Override
									public void beforeBackup(int max) {
										progressDialog.setMax(max);
									}
								});
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(AToolsActivity.this,
										"备份成功!", Toast.LENGTH_SHORT).show();
							}
						});

					} catch (Exception e) {
						e.printStackTrace();
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(AToolsActivity.this,
										"备份失败!", Toast.LENGTH_SHORT).show();
							}
						});
					} finally {
						progressDialog.dismiss();
					}
				}
			}.start();
			break;

		default:
			break;
		}
	}
}
