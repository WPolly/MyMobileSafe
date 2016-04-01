package com.xiaoshan.mymobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDao {

	private static final String path = "data/data/com.xiaoshan.mymobilesafe/files/antivirus.db";

	public static boolean isVirus(String md5) {
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		Cursor cursor = database.query("datable", null, "md5 = ?",
				new String[] { md5 }, null, null, null);

		boolean flag = false;

		if (cursor.moveToNext()) {
			flag = true;
		}
		
		cursor.close();
		database.close();

		return flag;
	}
}
