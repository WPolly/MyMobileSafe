package com.xiaoshan.mymobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.xiaoshan.mymobilesafe.R;
import com.xiaoshan.mymobilesafe.domain.RunningTaskMsg;

public class RunningTaskInfoProvider {

	public static List<RunningTaskMsg> getRunningTaskInfo(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningTaskMsg> taskMsgs = new ArrayList<RunningTaskMsg>();
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			RunningTaskMsg taskMsg = new RunningTaskMsg();
			String packageName = runningAppProcessInfo.processName;
			MemoryInfo[] memoryInfos = am
					.getProcessMemoryInfo(new int[] { runningAppProcessInfo.pid });
			long memSize = memoryInfos[0].getTotalPrivateDirty() * 1024l;
			taskMsg.setMemSize(memSize);
			taskMsg.setPackageName(packageName);
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						packageName, 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				String label = (String) applicationInfo.loadLabel(pm);
				taskMsg.setRunningTaskIcon(icon);
				taskMsg.setRunningTaskName(label);
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					taskMsg.setUserTask(true);
				} else {
					taskMsg.setUserTask(false);
				}

			} catch (NameNotFoundException e) {
				e.printStackTrace();
				taskMsg.setRunningTaskIcon(context.getResources().getDrawable(
						R.drawable.ic_launcher_web));
				taskMsg.setRunningTaskName(packageName);

			} finally {
				taskMsgs.add(taskMsg);
			}
		}
		return taskMsgs;
	}
}
