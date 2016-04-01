package com.xiaoshan.mymobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.IBinder;

public class AutoCleanService extends Service {

	private ScreenOffReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		receiver = new ScreenOffReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
	}

	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(final Context context, Intent intent) {
			
			new CountDownTimer(5000, 1000) {
				
				@Override
				public void onTick(long millisUntilFinished) {
					System.out.println("hello world!");
				}
				
				@Override
				public void onFinish() {
					System.out.println("…±À¿ƒ„√«!");
					ActivityManager am = (ActivityManager) context
							.getSystemService(Context.ACTIVITY_SERVICE);
					List<RunningAppProcessInfo> runningAppProcesses = am
							.getRunningAppProcesses();
					for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
						am.killBackgroundProcesses(runningAppProcessInfo.processName);
					}
					
				}
			}.start();
		}

	}

}
