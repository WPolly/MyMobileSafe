package com.xiaoshan.mymobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.xiaoshan.mymobilesafe.domain.RunningTaskMsg;
import com.xiaoshan.mymobilesafe.engine.RunningTaskInfoProvider;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity implements OnClickListener {

	private TextView tvProcessOnRunning, tvMemInfo, tvUpdateTaskDesc;
	private ActivityManager am;
	private ListView lvTaskInfo;
	private List<RunningTaskMsg> allRunningTaskMsgs, userRunningTaskMsgs,
			sysRunningTaskMsgs;
	private LinearLayout llLoadingTaskInfo;
	private ViewHolder holder;
	private MyAdapter adapter;
	private Button btSelectAll, btSelectOp, btClean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		tvProcessOnRunning = (TextView) findViewById(R.id.tv_process_on_running);
		tvMemInfo = (TextView) findViewById(R.id.tv_meminfo);
		tvUpdateTaskDesc = (TextView) findViewById(R.id.tv_update_task_desc);
		llLoadingTaskInfo = (LinearLayout) findViewById(R.id.ll_taskinfo_loading);
		lvTaskInfo = (ListView) findViewById(R.id.lv_task_info);
		btClean = (Button) findViewById(R.id.bt_clean_task);
		btSelectAll = (Button) findViewById(R.id.bt_select_all);
		btSelectOp = (Button) findViewById(R.id.bt_select_ops);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		setHeadInfo();
		fillData();
		lvTaskInfo.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userRunningTaskMsgs != null && sysRunningTaskMsgs != null) {
					if (firstVisibleItem > userRunningTaskMsgs.size()) {
						tvUpdateTaskDesc.setText("正在运行的系统进程: "
								+ sysRunningTaskMsgs.size() + "个");
					} else {
						tvUpdateTaskDesc.setText("正在运行的用户进程: "
								+ userRunningTaskMsgs.size() + "个");
					}
				}
			}
		});

		lvTaskInfo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				RunningTaskMsg taskMsg = null;
				holder = (ViewHolder) view.getTag();
				if (position == 0) {
					return;
				} else if (position == userRunningTaskMsgs.size() + 1) {
					return;
				} else if (position <= userRunningTaskMsgs.size()) {
					taskMsg = userRunningTaskMsgs.get(position - 1);
				} else {
					taskMsg = sysRunningTaskMsgs.get(position
							- userRunningTaskMsgs.size() - 2);
				}

				if (taskMsg.getPackageName().equals(getPackageName())) {
					return;
				}

				holder.cbTaskSelectStatus.setChecked(!taskMsg.isChecked());
				taskMsg.setChecked(!taskMsg.isChecked());
			}
		});

		btSelectAll.setOnClickListener(this);
		btSelectOp.setOnClickListener(this);
		btClean.setOnClickListener(this);
	}

	private void fillData() {
		llLoadingTaskInfo.setVisibility(View.VISIBLE);
		userRunningTaskMsgs = new ArrayList<RunningTaskMsg>();
		sysRunningTaskMsgs = new ArrayList<RunningTaskMsg>();
		new Thread() {
			@Override
			public void run() {
				allRunningTaskMsgs = RunningTaskInfoProvider
						.getRunningTaskInfo(getApplicationContext());
				for (RunningTaskMsg runningTaskMsg : allRunningTaskMsgs) {
					if (runningTaskMsg.isUserTask()) {
						userRunningTaskMsgs.add(runningTaskMsg);
					} else {
						sysRunningTaskMsgs.add(runningTaskMsg);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tvUpdateTaskDesc.setText("正在运行的用户进程: "
								+ userRunningTaskMsgs.size() + "个");
						llLoadingTaskInfo.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new MyAdapter();
							lvTaskInfo.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
					}
				});
			}
		}.start();

	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return userRunningTaskMsgs.size() + sysRunningTaskMsgs.size() + 2;// 07-14 不可写成allRunningTaskMsgs.size() + 2
																				// 11:26:03.891:
																				// E/AndroidRuntime(6258):
																				// at
																				// com.xiaoshan.mymobilesafe.TaskManagerActivity$MyAdapter.getView(TaskManagerActivity.java:177)

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RunningTaskMsg taskMsg = null;
			if (position == 0) {
				TextView tvUserDesc = new TextView(getApplicationContext());
				tvUserDesc.setBackgroundColor(Color.argb(0, 0, 0, 0));
				return tvUserDesc;
			} else if (position == userRunningTaskMsgs.size() + 1) {
				TextView tvSysDesc = new TextView(getApplicationContext());
				tvSysDesc.setBackgroundColor(Color.argb(136, 0, 0, 0));
				tvSysDesc.setTextColor(Color.WHITE);
				tvSysDesc.setText("正在运行的系统进程: " + sysRunningTaskMsgs.size()
						+ "个");
				return tvSysDesc;
			} else if (position <= userRunningTaskMsgs.size()) {
				taskMsg = userRunningTaskMsgs.get(position - 1);
			} else {
				taskMsg = sysRunningTaskMsgs.get(position
						- userRunningTaskMsgs.size() - 2);
			}
			holder = new ViewHolder();
			View view = null;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(TaskManagerActivity.this,
						R.layout.list_item_runningtask_info, null);
				holder.ivTask = (ImageView) view
						.findViewById(R.id.iv_task_icon);
				holder.tvTaskName = (TextView) view
						.findViewById(R.id.tv_task_name);
				holder.tvTaskMem = (TextView) view
						.findViewById(R.id.tv_task_mem);
				holder.cbTaskSelectStatus = (CheckBox) view
						.findViewById(R.id.cb_task_status);
				view.setTag(holder);
			}

			holder.ivTask.setImageDrawable(taskMsg.getRunningTaskIcon());
			holder.tvTaskName.setText(taskMsg.getRunningTaskName());
			holder.tvTaskMem.setText(Formatter.formatFileSize(
					getApplicationContext(), taskMsg.getMemSize()) + "");
			if (taskMsg.getPackageName().equals(getPackageName())) {
				holder.cbTaskSelectStatus.setVisibility(View.INVISIBLE);
			} else {
				holder.cbTaskSelectStatus.setVisibility(View.VISIBLE);
			}
			holder.cbTaskSelectStatus.setChecked(taskMsg.isChecked());
			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	class ViewHolder {
		ImageView ivTask;
		TextView tvTaskName, tvTaskMem;
		CheckBox cbTaskSelectStatus;
	}

	private void setHeadInfo() {
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		tvProcessOnRunning.setText("正在运行的进程: " + runningAppProcesses.size()
				+ "个");
		tvMemInfo.setText("可用内存/总内存: "
				+ Formatter.formatFileSize(getApplicationContext(),
						outInfo.availMem)
				+ "/"
				+ Formatter.formatFileSize(getApplicationContext(),
						outInfo.totalMem));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_select_all:
			for (RunningTaskMsg runningTaskMsg : userRunningTaskMsgs) {
				if (runningTaskMsg.getPackageName().equals(getPackageName())) {
					continue;
				}
				runningTaskMsg.setChecked(true);
			}

			for (RunningTaskMsg runningTaskMsg : sysRunningTaskMsgs) {
				runningTaskMsg.setChecked(true);
			}
			adapter.notifyDataSetChanged();
			break;

		case R.id.bt_select_ops:
			for (RunningTaskMsg runningTaskMsg : userRunningTaskMsgs) {
				if (runningTaskMsg.getPackageName().equals(getPackageName())) {
					continue;
				}
				runningTaskMsg.setChecked(!runningTaskMsg.isChecked());
			}

			for (RunningTaskMsg runningTaskMsg : sysRunningTaskMsgs) {
				runningTaskMsg.setChecked(!runningTaskMsg.isChecked());
			}
			adapter.notifyDataSetChanged();
			break;

		case R.id.bt_clean_task:

			List<RunningTaskMsg> userKillTasks = new ArrayList<RunningTaskMsg>();
			List<RunningTaskMsg> sysKillTasks = new ArrayList<RunningTaskMsg>();

			for (RunningTaskMsg runningTaskMsg : userRunningTaskMsgs) {
				if (runningTaskMsg.isChecked()) {
					am.killBackgroundProcesses(runningTaskMsg.getPackageName());
					userKillTasks.add(runningTaskMsg);
				}
			}

			for (RunningTaskMsg runningTaskMsg : sysRunningTaskMsgs) {
				if (runningTaskMsg.isChecked()) {
					am.killBackgroundProcesses(runningTaskMsg.getPackageName());
					sysKillTasks.add(runningTaskMsg);
				}
			}

			userRunningTaskMsgs.removeAll(userKillTasks);
			sysRunningTaskMsgs.removeAll(sysKillTasks);
			System.out.println("hello");
			adapter.notifyDataSetChanged();
			System.out.println("world");
			setHeadInfo();
			Toast.makeText(this, "清理完毕", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
}
