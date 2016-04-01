package com.xiaoshan.mymobilesafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.xiaoshan.mymobilesafe.R;
import com.xiaoshan.mymobilesafe.receiver.MyWidget;

public class UpdateWidgetService extends Service {

	public static final String TAG = "UpdateWidgetService";

	private ScreenOnReceiver onReceiver;
	private ScreenOffReceiver offReceiver;

	private Timer timer;
	private TimerTask timerTask;

	private ActivityManager am;
	private AppWidgetManager awm;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "我也被开启了..");
		super.onCreate();
		awm = AppWidgetManager.getInstance(this);
		onReceiver = new ScreenOnReceiver();
		offReceiver = new ScreenOffReceiver();
		registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(offReceiver,
				new IntentFilter(Intent.ACTION_SCREEN_OFF));
		startTimer();

	}

	private class ScreenOnReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "屏幕解锁了...");
			startTimer();
		}

	}

	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "屏幕锁屏了...");
			stopTimer();
		}

	}

	public void startTimer() {
		Log.d(TAG, "开始计时更新");
		if (timer == null && timerTask == null) {
			timer = new Timer();
			timerTask = new TimerTask() {

				@Override
				public void run() {
					Log.d(TAG, "更新widget.");
					am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					List<RunningAppProcessInfo> runningAppProcesses = am
							.getRunningAppProcesses();
					MemoryInfo outInfo = new MemoryInfo();
					am.getMemoryInfo(outInfo);
					long availMemy = outInfo.availMem;

					ComponentName provider = new ComponentName(
							UpdateWidgetService.this, MyWidget.class);
					RemoteViews views = new RemoteViews(getPackageName(),
							R.layout.process_widget);
					views.setTextViewText(R.id.tv_widget_running_app_count,
							"正在运行的软件: " + runningAppProcesses.size());
					views.setTextViewText(
							R.id.tv_widget_mem_available,
							"可用内存: "
									+ Formatter.formatFileSize(
											getApplicationContext(), availMemy));

					Intent intent = new Intent();
					intent.setAction("com.xiaoshan.mymobilesafe.killalltask");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.bt_widget_kill_all_task,
							pendingIntent);
					awm.updateAppWidget(provider, views);
				}
			};

			timer.schedule(timerTask, 0, 3000);
		}
	}

	public void stopTimer() {
		
		if (timer != null && timerTask != null) {
			timer.cancel();
			timerTask.cancel();
			timer = null;
			timerTask = null;
			Log.d(TAG, "停止计时更新");
		}
	}

	@Override
	public void onDestroy() {
		stopTimer();
		unregisterReceiver(onReceiver);
		unregisterReceiver(offReceiver);
		onReceiver = null;
		offReceiver = null;
		Log.d(TAG, "我被消灭了..");
		super.onDestroy();
	}

}
