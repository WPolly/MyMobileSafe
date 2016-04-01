package com.xiaoshan.mymobilesafe.domain;

import android.graphics.drawable.Drawable;

public class RunningTaskMsg {

	private String packageName;
	private String runningTaskName;
	private long memSize;
	private Drawable runningTaskIcon;
	private boolean isUserTask;
	private boolean isChecked;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getRunningTaskName() {
		return runningTaskName;
	}

	public void setRunningTaskName(String runningTaskName) {
		this.runningTaskName = runningTaskName;
	}

	public long getMemSize() {
		return memSize;
	}

	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}

	public Drawable getRunningTaskIcon() {
		return runningTaskIcon;
	}

	public void setRunningTaskIcon(Drawable runningTaskIcon) {
		this.runningTaskIcon = runningTaskIcon;
	}

	public boolean isUserTask() {
		return isUserTask;
	}

	public void setUserTask(boolean isUserTask) {
		this.isUserTask = isUserTask;
	}
	
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isCheckde) {
		this.isChecked = isCheckde;
	}
	
	@Override
	public String toString() {
		return "RunningTaskInfo [packageName=" + packageName
				+ ", runningTaskName=" + runningTaskName + ", memSize="
				+ memSize + "]";
	}

}
