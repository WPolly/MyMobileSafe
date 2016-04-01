package com.xiaoshan.mymobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.xiaoshan.mymobilesafe.R;
import com.xiaoshan.mymobilesafe.service.LocationService;

public class SMSReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private DevicePolicyManager dpm;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean b = sp.getBoolean("lost_find_activated", false);
		if (b) {
			Object[] object = (Object[]) intent.getExtras().get("pdus");

			for (Object obj : object) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String address = smsMessage.getOriginatingAddress();
				String safeNum = sp.getString("safenumber", "");
				Toast.makeText(context, address, Toast.LENGTH_SHORT).show();

				if (address.contains(safeNum)) {
					String body = smsMessage.getMessageBody();
					MediaPlayer player = MediaPlayer.create(context,
							R.raw.hulijiao);

					if (body.equals("#*location*#")) {
						Intent intent2 = new Intent(context,
								LocationService.class);
						context.startService(intent2);
						String location = sp.getString("location", "ÕýÔÚËÑË÷..");
						SmsManager.getDefault().sendTextMessage(safeNum, null,
								location, null, null);

						abortBroadcast();

					} else if (body.equals("#*alarm*#")) {
						player.start();
						player.setLooping(true);
						player.setVolume(1.0f, 1.0f);
						abortBroadcast();

					} else if (body.equals("#*wipedata*#")) {
						ComponentName name = new ComponentName(context, MyAdminReceiver.class);
						if (dpm.isAdminActive(name)) {
							//dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
							//dpm.wipeData(0));
						} else {
							return;
						}

					} else if (body.equals("#*lockscreen*#")) {
						dpm = (DevicePolicyManager) context
								.getSystemService(Context.DEVICE_POLICY_SERVICE);
						ComponentName name = new ComponentName(context, MyAdminReceiver.class);
						if (dpm.isAdminActive(name)) {
							dpm.lockNow();
							dpm.resetPassword("1253", 0);
						} else {
							return;
						}

					} else if (body.equals("stop")) {
						Toast.makeText(context, "stop???", Toast.LENGTH_SHORT)
								.show();
						player.stop();
						player = null;
					}
				}
			}
		}
	}
}
