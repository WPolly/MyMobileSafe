package com.xiaoshan.mymobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.xiaoshan.mymobilesafe.domain.AppInfo;

public class AppInfoProvider {
	
	public static List<AppInfo> getAppInfo(Context context) {
		
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> applications = pm.getInstalledApplications(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		
		for (ApplicationInfo applicationInfo : applications) {
			Drawable icon = applicationInfo.loadIcon(pm);
			String label = (String) applicationInfo.loadLabel(pm);
			String packageName = applicationInfo.packageName;
			boolean isUser = false;
			boolean isRom = false;
			int flags = applicationInfo.flags;
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				isUser = true;
			}
			
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				isRom = true;
			}
			
			AppInfo appInfo = new AppInfo();
			appInfo.setAppIcon(icon);
			appInfo.setAppName(label);
			appInfo.setPackageName(packageName);
			appInfo.setRom(isRom);
			appInfo.setUser(isUser);
			appInfos.add(appInfo);
		}
		
		return appInfos;
		
	}
}
