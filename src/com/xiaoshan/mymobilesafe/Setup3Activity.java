package com.xiaoshan.mymobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {

	private TextView tvNextStep3;
	private TextView tvPreStep2;
	private EditText etSafeNum;
	private Button btnSelectCont;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		String safeNum = sp.getString("safenumber", "");
		tvNextStep3 = (TextView) findViewById(R.id.tv_next_step3);
		tvPreStep2 = (TextView) findViewById(R.id.tv_pre_step2);
		etSafeNum = (EditText) findViewById(R.id.et_safe_number);
		btnSelectCont = (Button) findViewById(R.id.bt_select_contacts);

		etSafeNum.setText(safeNum);

		btnSelectCont.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Setup3Activity.this,
						SelectContactActivity.class);
				startActivityForResult(intent, 0);
			}
		});

		tvNextStep3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showNextPage();
			}
		});

		tvPreStep2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPrePage();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			String tel = data.getStringExtra("tel").replace("-", "")
					.replace(" ", "");
			etSafeNum.setText(tel);
			etSafeNum.setSelection(tel.length());
		}
	}

	@Override
	public void showPrePage() {
		Intent intent = new Intent(Setup3Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
	}

	@Override
	public void showNextPage() {
		String safeNum = etSafeNum.getText().toString().trim();
		if (TextUtils.isEmpty(safeNum)) {
			Toast.makeText(this, "您还没有设置安全号码", Toast.LENGTH_SHORT).show();
		} else {
			Editor editor = sp.edit();
			editor.putString("safenumber", safeNum);
			editor.commit();
			Intent intent = new Intent(Setup3Activity.this,
					Setup4Activity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.next_in, R.anim.next_out);
		}
	}
}
