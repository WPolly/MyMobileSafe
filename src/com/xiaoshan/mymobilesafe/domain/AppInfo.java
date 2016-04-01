package com.xiaoshan.mymobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {

	String appName;
	String packageName;
	Drawable appIcon;
	boolean isRom;
	boolean isUser;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public boolean isRom() {
		return isRom;
	}

	public void setRom(boolean isRom) {
		this.isRom = isRom;
	}

	public boolean isUser() {
		return isUser;
	}

	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}

	@Override
	public String toString() {
		return "AppInfo [appName=" + appName + ", packageName=" + packageName
				+ ", appIcon=" + appIcon + ", isRom=" + isRom + ", isUser="
				+ isUser + "]";
	}
}
