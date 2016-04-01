package com.xiaoshan.mymobilesafe.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.xiaoshan.mymobilesafe.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallSmsSafeService extends Service {

	private static final String TAG = "CallSmsSafeService";
	private BroadcastReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private PhoneStateListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		receiver = new SmsReceiver();
		dao = new BlackNumberDao(this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		listener = new MyListener();
		filter.setPriority(1000);
		registerReceiver(receiver, filter);
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		Log.i(TAG, "我被开启了..");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
		Log.i(TAG, "我被关闭了..");
	}

	private class SmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
				String address = message.getOriginatingAddress();
				String mode = dao.findMode(address);
				if (mode.equals("2") || mode.equals("3")) {
					Log.d(TAG, "开始屏蔽短信..");
					abortBroadcast();
					Log.d(TAG, "已经屏蔽短信..");
				}
			}
		}

	}

	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				Log.d(TAG, incomingNumber);
				String mode = dao.findMode(incomingNumber);
				if (mode.equals("1") || mode.equals("3")) {
					ContentResolver resolver = getContentResolver();
					Uri uri = Uri.parse("content://call_log/calls");
					resolver.registerContentObserver(uri, true,
							new CallLogObserver(incomingNumber, new Handler()));
					endCall();
				}
				break;

			default:
				break;
			}
		}

	}

	private class CallLogObserver extends ContentObserver {

		private String incomingNumber;

		public CallLogObserver(String incomingNumber, Handler handler) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			deleteCallLog(incomingNumber);
			getContentResolver().unregisterContentObserver(this);
		}

	}

	public void endCall() {
		try {
			Log.d(TAG, "开始挂电话..");
			Class<?> clazz = CallSmsSafeService.class.getClassLoader()
					.loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(iBinder).endCall();
			Log.d(TAG, "已挂电话..");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://call_log/calls");
		resolver.delete(uri, "number = ?", new String[] {incomingNumber});
	}

}
