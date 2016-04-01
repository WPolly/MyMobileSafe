package com.xiaoshan.mymobilesafe;

import com.xiaoshan.mymobilesafe.utils.MD5Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private GridView homeGridView;
	private Intent intent;
	private SharedPreferences sp;
	private String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒",
			"缓存清理", "高级工具", "设置中心" };
	private int[] ids = { R.drawable.safe, R.drawable.callmsgsafe,
			R.drawable.app, R.drawable.taskmanager, R.drawable.netmanager,
			R.drawable.trojan, R.drawable.sysoptimize, R.drawable.atools,
			R.drawable.settings

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		homeGridView = (GridView) findViewById(R.id.gv_home_list);
		homeGridView.setAdapter(new MyAdapter());
		homeGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				
				case 0:
					String password = sp.getString("findphone_password", "");
					if (TextUtils.isEmpty(password)) {
						showSetPasswordDialog();
					} else {
						showLoginDialog();

					}

					break;
				
					
				case 1:
					intent = new Intent(HomeActivity.this,
							CallSmsSafeActivity.class);
					startActivity(intent);
					break;
					
				case 2:
					intent = new Intent(HomeActivity.this,
							AppManagerActivity.class);
					startActivity(intent);
					break;
					
				case 3:
					intent = new Intent(HomeActivity.this,
							TaskManagerActivity.class);
					startActivity(intent);
					break;
					
				case 4:
					intent = new Intent(HomeActivity.this,
							TrafficManagerActivity.class);
					startActivity(intent);
					break;
					
				case 5:
					intent = new Intent(HomeActivity.this,
							AntiVirusActivity.class);
					startActivity(intent);
					break;
					
				case 6:
					intent = new Intent(HomeActivity.this,
							CleanCacheActivity.class);
					startActivity(intent);
					break;
					
				case 7:
					intent = new Intent(HomeActivity.this,
							AToolsActivity.class);
					startActivity(intent);
					break;
					
				case 8:
					intent = new Intent(HomeActivity.this,
							SettingActivity.class);
					startActivity(intent);
					break;

				

				default:
					break;
				}
			}
		});
	}

	private EditText etInputPsw;
	private AlertDialog dialog;

	protected void showSetPasswordDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(HomeActivity.this,
				R.layout.set_password_dialog, null);
		builder.setView(view);
		builder.setCancelable(false);
		dialog = builder.show();
		etInputPsw = (EditText) view.findViewById(R.id.et_input_password);
		final EditText etConfirmPsw = (EditText) view
				.findViewById(R.id.et_confirm_password);
		Button btConfirmPsw = (Button) view
				.findViewById(R.id.bt_set_password_confirm);
		Button btConcel = (Button) view
				.findViewById(R.id.bt_set_password_cancel);

		btConfirmPsw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String inputPsw = etInputPsw.getText().toString().trim();
				String confirmPsw = etConfirmPsw.getText().toString().trim();
				if (TextUtils.isEmpty(inputPsw)
						|| TextUtils.isEmpty(confirmPsw)) {
					Toast.makeText(HomeActivity.this, "密码输入不能为空!",
							Toast.LENGTH_SHORT).show();
				} else {
					if (inputPsw.equals(confirmPsw)) {
						Editor editor = sp.edit();
						editor.putString("findphone_password",
								MD5Util.md5Encode(inputPsw));
						editor.commit();
						Toast.makeText(HomeActivity.this, "密码设置成功!",
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						intent = new Intent(HomeActivity.this,
								PhoneFindBackActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(HomeActivity.this, "两次输入的密码不一致,请重新输入.",
								Toast.LENGTH_SHORT).show();
						etInputPsw.setText("");
						etConfirmPsw.setText("");
					}
				}
			}
		});

		btConcel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

	}

	protected void showLoginDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View
				.inflate(HomeActivity.this, R.layout.login_dialog, null);
		
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.setCancelable(false);
		dialog.show();
		etInputPsw = (EditText) view.findViewById(R.id.et_input_password);
		Button btConfirmPsw = (Button) view
				.findViewById(R.id.bt_set_password_confirm);
		Button btConcel = (Button) view
				.findViewById(R.id.bt_set_password_cancel);

		btConfirmPsw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = sp.getString("findphone_password", "");
				String origin = etInputPsw.getText().toString().trim();
				String string = MD5Util.md5Encode(origin);
				if (TextUtils.isEmpty(origin)) {
					Toast.makeText(HomeActivity.this, "输入密码不能为空!",
							Toast.LENGTH_SHORT).show();
				} else {
					if (string.equals(password)) {
						dialog.dismiss();
						intent = new Intent(HomeActivity.this,
								PhoneFindBackActivity.class);
						startActivity(intent);
					} else {
						Toast.makeText(HomeActivity.this, "密码输入错误 ,请重新输入!",
								Toast.LENGTH_SHORT).show();
						etInputPsw.setText("");
					}
				}
			}
		});

		btConcel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this,
					R.layout.list_home_item, null);
			TextView nameItem = (TextView) view.findViewById(R.id.tv_home_item);
			ImageView imageItem = (ImageView) view
					.findViewById(R.id.iv_home_item);
			nameItem.setText(names[position]);
			imageItem.setImageResource(ids[position]);
			return view;
		}

	}
}
