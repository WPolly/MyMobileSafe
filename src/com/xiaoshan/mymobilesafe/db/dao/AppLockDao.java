package com.xiaoshan.mymobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoshan.mymobilesafe.db.AppLockDBOpenHelper;

public class AppLockDao {
	private AppLockDBOpenHelper helper;
	private Context context;

	public AppLockDao(Context context) {
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}

	public void add(String packageName) {
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packagename", packageName);
		database.insert("applock", null, values);
		database.close();
		Intent intent = new Intent();
		intent.setAction("com.xiaoshan.mymobilesafe.updatelist");
		context.sendBroadcast(intent);
	}

	public boolean find(String packageName) {
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query("applock", null, "packagename = ?",
				new String[] { packageName }, null, null, null);
		boolean flag = false;
		if (cursor.moveToNext()) {
			flag = true;
		}
		cursor.close();
		database.close();
		return flag;
	}

	public void delete(String packageName) {
		SQLiteDatabase database = helper.getWritableDatabase();
		database.delete("applock", "packagename = ?",
				new String[] { packageName });
		database.close();
		Intent intent = new Intent();
		intent.setAction("com.xiaoshan.mymobilesafe.updatelist");
		context.sendBroadcast(intent);
	}
	
	public List<String> findAll() {
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query("applock", new String[] {"packagename"}, null, null, null, null, null);
		List<String> packageNames = new ArrayList<String>();
		while (cursor.moveToNext()) {
			String name = cursor.getString(0);
			packageNames.add(name);
		}
		cursor.close();
		database.close();
		return packageNames;
	}
}
