package com.xiaoshan.mymobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String serialNumber = tm.getSimSerialNumber();
		String originSIM = sp.getString("sim_serial", "");
		if (serialNumber.equals(originSIM)) {
			return;
		} else {
			SmsManager.getDefault().sendTextMessage(
					sp.getString("safenumber", "10086"), null,
					"sim has been changed", null, null);
		}
	}

}
