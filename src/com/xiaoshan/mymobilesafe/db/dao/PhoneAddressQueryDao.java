package com.xiaoshan.mymobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class PhoneAddressQueryDao {

	private static final String path = "data/data/com.xiaoshan.mymobilesafe/files/address.db";

	public static String queryAddress(String input) {
		String address = null;
		SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		
		if (TextUtils.isEmpty(input)) {
			return ("ºÅÂë²»Ïê!");
		}

		if (input.length() > 6 && input.startsWith("1")) {
			Cursor cursor = sqLiteDatabase
					.rawQuery(
							"select location from data2 where id = (select outkey from data1 where id = ?)",
							new String[] { input.substring(0, 7) });

			while (cursor.moveToNext()) {
				String location = cursor.getString(0);
				address = location;
			}
			cursor.close();
		} else if (input.length() > 2 && input.startsWith("0")) {
			Cursor cursor = sqLiteDatabase.rawQuery(
					"select location from data2 where area = ?",
					new String[] { input.substring(1, input.length()) });
			while (cursor.moveToNext()) {
				String location = cursor.getString(0);
				address = location.substring(0, location.length()-2);
			}
			cursor.close();
		}
		return address;
	}
}
