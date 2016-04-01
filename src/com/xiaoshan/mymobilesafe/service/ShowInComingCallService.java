package com.xiaoshan.mymobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.xiaoshan.mymobilesafe.R;
import com.xiaoshan.mymobilesafe.db.dao.PhoneAddressQueryDao;

public class ShowInComingCallService extends Service {

	private TelephonyManager tm;
	private PhoneStateListener listener;
	private OutCallReceiver receiver;
	private SharedPreferences sp;
	private WindowManager.LayoutParams params;
	private WindowManager wm;
	private View view;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		receiver = new OutCallReceiver();
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		IntentFilter filter = new IntentFilter(
				"android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		System.out.println("我被开启了..");
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(receiver);
		System.out.println("我被关闭了..");
		listener = null;
		receiver = null;
	}

	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String address = PhoneAddressQueryDao
						.queryAddress(incomingNumber);
				myToast(address);
				break;

			case TelephonyManager.CALL_STATE_IDLE:
				if(view != null)
				wm.removeView(view);
				break;

			default:
				break;
			}

		}

	}

	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			System.out.println(number);
			String address = PhoneAddressQueryDao.queryAddress(number);
			myToast(address);

		}

	}

	private void myToast(String toast) {
		int[] ids = { R.drawable.call_locate_blue, R.drawable.call_locate_gray,
				R.drawable.call_locate_green, R.drawable.call_locate_orange,
				R.drawable.call_locate_white };

		view = View.inflate(this, R.layout.mytoast_address_show, null);
		TextView tvToastAddress = (TextView) view
				.findViewById(R.id.tv_toast_address);

		int i = sp.getInt("toast_which", 0);
		view.setBackgroundResource(ids[i]);
		tvToastAddress.setText(toast);
		view.setOnTouchListener(new OnTouchListener() {
			
			int startX, startY;
			@SuppressWarnings("deprecation")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
					
				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					
					params.x += dx;
					params.y += dy;
					if (params.x < 0) {
						params.x = 0;
					}
					
					if (params.y < 0) {
						params.y = 0;
					}
					
					if (params.x > wm.getDefaultDisplay().getWidth() - v.getWidth()) {
						params.x = (wm.getDefaultDisplay().getWidth() - v.getWidth());
					}
					
					if (params.y > wm.getDefaultDisplay().getHeight() - v.getHeight()) {
						params.y = (wm.getDefaultDisplay().getHeight() - v.getHeight());
					}
					
					wm.updateViewLayout(v, params);
					
					startX = newX;
					startY = newY;
					break;
					
				case MotionEvent.ACTION_UP:
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();
					break;

				default:
					break;
				}
				return false;
			}
		});

		params = new LayoutParams();
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;

		params.gravity = Gravity.LEFT + Gravity.TOP;
		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);
		params.flags = LayoutParams.FLAG_KEEP_SCREEN_ON
				| LayoutParams.FLAG_NOT_FOCUSABLE;

		params.format = PixelFormat.TRANSLUCENT;
		params.type = LayoutParams.TYPE_PHONE;

		wm.addView(view, params);

	}

}
