package com.xiaoshan.mymobilesafe.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class KillAllTaskReceiver extends BroadcastReceiver {

	private ActivityManager am;

	@Override
	public void onReceive(Context context, Intent intent) {
		am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			am.killBackgroundProcesses(runningAppProcessInfo.processName);
		}
		Toast.makeText(context, "恭喜,一键清理完毕,手机达到最好状态!", Toast.LENGTH_SHORT)
				.show();
	}

}
