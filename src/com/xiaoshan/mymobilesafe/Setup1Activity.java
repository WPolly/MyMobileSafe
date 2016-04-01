package com.xiaoshan.mymobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Setup1Activity extends BaseSetupActivity {

	private TextView tvNextStep;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		tvNextStep = (TextView) findViewById(R.id.tv_next_step);
		tvNextStep.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showNextPage();
			}
		});
	}

	public void showNextPage() {
		Intent intent = new Intent(Setup1Activity.this,
				Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.next_in, R.anim.next_out);
	}

	@Override
	public void showPrePage() {
		// TODO Auto-generated method stub
		
	}
}
