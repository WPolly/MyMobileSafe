package com.xiaoshan.mymobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.xiaoshan.mymobilesafe.db.dao.BlackNumberDao;
import com.xiaoshan.mymobilesafe.domain.BlackNumberInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsSafeActivity extends Activity {
	private ListView lvBlackNum;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;
	private CallSmsSafeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		lvBlackNum = (ListView) findViewById(R.id.lv_blacknumber);
		infos = new ArrayList<BlackNumberInfo>();
		dao = new BlackNumberDao(getApplicationContext());
		infos = dao.findAll();
		
		adapter = new CallSmsSafeAdapter();
		lvBlackNum.setAdapter(adapter);
	}

	private class CallSmsSafeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public View getView(final int position, View contentView,
				ViewGroup parent) {
			View view;
			ViewHolder viewHolder = new ViewHolder();
			if (contentView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_blacknumber, null);
				viewHolder.tvBlackNum = (TextView) view
						.findViewById(R.id.tv_black_number);
				viewHolder.tvBlockMode = (TextView) view
						.findViewById(R.id.tv_block_mode);
				viewHolder.ivDeleteBlackNum = (ImageView) view
						.findViewById(R.id.iv_delete_black_number);
				view.setTag(viewHolder);
			} else {
				view = contentView;
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.tvBlackNum.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			
			if (TextUtils.isEmpty(mode)) {
				viewHolder.tvBlockMode.setText("СBug!");
			} else {
				if (mode.equals("1")) {
					viewHolder.tvBlockMode.setText("�绰����");
				} else if (mode.equals("2")) {
					viewHolder.tvBlockMode.setText("��������");
				} else {
					viewHolder.tvBlockMode.setText("ȫ������");
				}
			}
			
			viewHolder.ivDeleteBlackNum
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							AlertDialog.Builder builder = new Builder(
									CallSmsSafeActivity.this);
							builder.setTitle("��ʾ");
							builder.setMessage("ȷ��Ҫ�Ƴ��ú�����?");
							builder.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dao.delete(infos.get(position)
													.getNumber());
											infos.remove(position);
											adapter.notifyDataSetChanged();
										}
									});
							builder.setNegativeButton("ȡ��", null);
							builder.show();
						}
					});

			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	class ViewHolder {
		TextView tvBlackNum, tvBlockMode;
		ImageView ivDeleteBlackNum;
	}

	public void addBlackNum(View v) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_add_black_num, null);
		final EditText et = (EditText) view
				.findViewById(R.id.et_input_blacknum);
		final CheckBox cbPhone = (CheckBox) view.findViewById(R.id.cb_phone);
		final CheckBox cbSms = (CheckBox) view.findViewById(R.id.cb_sms);
		Button btOk = (Button) view.findViewById(R.id.bt_ok);
		Button btCancel = (Button) view.findViewById(R.id.bt_cancel);
		btCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		btOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String blackNum = et.getText().toString().trim();
				if (TextUtils.isEmpty(blackNum)) {
					Toast.makeText(CallSmsSafeActivity.this, "������벻��Ϊ��",
							Toast.LENGTH_SHORT).show();
					;
					return;
				}
				String mode = null;
				if (cbPhone.isChecked() && cbSms.isChecked()) {
					mode = "3";
				} else if (cbPhone.isChecked()) {
					mode = "1";
				} else if (cbSms.isChecked()) {
					mode = "2";
				} else {
					Toast.makeText(CallSmsSafeActivity.this, "��ѡ������ģʽ",
							Toast.LENGTH_SHORT).show();
					return;
				}

				dao.add(blackNum, mode);
				BlackNumberInfo info = new BlackNumberInfo();
				info.setNumber(blackNum);
				info.setMode(mode);
				infos.add(0, info);
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
}
