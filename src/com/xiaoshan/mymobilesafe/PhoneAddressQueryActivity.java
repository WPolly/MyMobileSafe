package com.xiaoshan.mymobilesafe;

import com.xiaoshan.mymobilesafe.db.dao.PhoneAddressQueryDao;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneAddressQueryActivity extends Activity {

	private EditText etAddressToQuery, etAreaToQuery;
	private Button btQueryAddress, btAreaToQuery;
	private TextView tvQueryResult;
	private Animation animation;
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_address_query);
		etAddressToQuery = (EditText) findViewById(R.id.et_address_to_query);
		btQueryAddress = (Button) findViewById(R.id.bt_query_address);
		tvQueryResult = (TextView) findViewById(R.id.tv_address_query_result);
		etAreaToQuery = (EditText) findViewById(R.id.et_area_to_query);
		btAreaToQuery = (Button) findViewById(R.id.bt_query_area);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		animation = AnimationUtils.loadAnimation(this, R.anim.shake);

		btQueryAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchForResult(etAddressToQuery);
			}
		});

		btAreaToQuery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchForResult(etAreaToQuery);
			}
		});
	}

	private void searchForResult(EditText et) {
		String input = et.getText().toString();
		if (TextUtils.isEmpty(input)) {

			et.startAnimation(animation);
			Toast.makeText(PhoneAddressQueryActivity.this, "您没有输入号码",
					Toast.LENGTH_SHORT).show();
		} else {
			long[] pattern = { 100, 100, 200, 200, 300, 300, 500, 500, 1000,
					1000 };
			switch (et.getId()) {
			case R.id.et_address_to_query:
				if (input.length() < 7) {
					Toast.makeText(PhoneAddressQueryActivity.this,
							"您输入的手机号码不足七位", Toast.LENGTH_SHORT).show();
					vibrator.vibrate(pattern, -1);
				} else {
					showResult(input);
				}
				break;

			case R.id.et_area_to_query:
				if (input.length() < 3) {
					Toast.makeText(PhoneAddressQueryActivity.this,
							"您输入的长途区号不足三位", Toast.LENGTH_SHORT).show();
					vibrator.vibrate(pattern, -1);
				} else {
					showResult(input);
				}
				break;
			}
		}
	}

	private void showResult(String input) {
		String result = PhoneAddressQueryDao.queryAddress(input);
		if (TextUtils.isEmpty(result)) {
			tvQueryResult.setText("404 NOT FOUND");
			tvQueryResult.startAnimation(animation);
		} else {
			tvQueryResult.setText(result);
			tvQueryResult.startAnimation(animation);
		}
	}
}
