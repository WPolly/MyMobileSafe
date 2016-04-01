package com.xiaoshan.mymobilesafe.domain;

import android.graphics.drawable.Drawable;

public class CacheInfo {
	private Drawable appIcon;
	private String appName;
	private String cacheSize;
	private String packageName;

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(String cacheSize) {
		this.cacheSize = cacheSize;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return "CacheInfo [appName=" + appName + ", cacheSize=" + cacheSize
				+ "]";
	}

}
