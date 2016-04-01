package com.xiaoshan.mymobilesafe.service;

import java.util.List;

import com.xiaoshan.mymobilesafe.WatchDogForPswdActivity;
import com.xiaoshan.mymobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class WatchDogService extends Service {

	private static final String TAG = "WatchDogService";
	private List<String> protectedPkgNames;
	private AppLockDao dao;
	private String tempProtectedPkgName = "";
	private boolean flag;

	private ActivityManager am;

	private TempStopProtectReceiver tspReceiver;
	private UpdateListReceiver updateListReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "看门狗开始工作了..");
		flag = true;
		dao = new AppLockDao(this);
		protectedPkgNames = dao.findAll();
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		tspReceiver = new TempStopProtectReceiver();
		updateListReceiver = new UpdateListReceiver();

		registerReceiver(tspReceiver, new IntentFilter(
				"com.xiaoshan.mymobilesafe.tempstopprotect"));
		registerReceiver(updateListReceiver, new IntentFilter(
				"com.xiaoshan.mymobilesafe.updatelist"));
		new Thread() {
			public void run() {
				while (flag) {
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					String packageName = runningTasks.get(0).topActivity
							.getPackageName();
					//Log.d(TAG, "当前栈顶包名: " + packageName);
					if (protectedPkgNames.contains(packageName)) {
						if (tempProtectedPkgName.equals(packageName)) {

						} else {
							Intent intent = new Intent(WatchDogService.this,
									WatchDogForPswdActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("package_name", packageName);
							startActivity(intent);
						}
					} else {
						if (!packageName.equals(getPackageName())) {
							tempProtectedPkgName = "";
						}
					}

					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	private class TempStopProtectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String string = intent.getStringExtra("package_name");
			tempProtectedPkgName = string;
		}

	}

	private class UpdateListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			protectedPkgNames = dao.findAll();
		}

	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "看门狗要休息了..");
		flag = false;
		unregisterReceiver(tspReceiver);
		unregisterReceiver(updateListReceiver);
		tspReceiver = null;
		updateListReceiver = null;
		super.onDestroy();
	}

}
