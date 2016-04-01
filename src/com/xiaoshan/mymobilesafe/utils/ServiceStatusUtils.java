package com.xiaoshan.mymobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtils {

	public static boolean isServiceRunning(Context context, String serviceName) {

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am.getRunningServices(30);
		
		if (runningServices.size() > 0) {
			for (RunningServiceInfo runningServiceInfo : runningServices) {
				String className = runningServiceInfo.service.getClassName();
				//System.out.println(className);
				if (serviceName.equals(className)) {
					return true;
				}
			}
		}
		return false;
	}
}
