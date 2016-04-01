package com.xiaoshan.mymobilesafe.receiver;

import com.xiaoshan.mymobilesafe.service.UpdateWidgetService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyWidget extends AppWidgetProvider {

	private static final String TAG = "MyWidget";

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.d(TAG, "onReceive");
//		Intent intent2 = new Intent(context, UpdateWidgetService.class);
//		context.startService(intent2);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled");
		super.onEnabled(context);
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}

	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled");
		super.onDisabled(context);
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
	}
	
}
