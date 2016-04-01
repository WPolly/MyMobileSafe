package com.xiaoshan.mymobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoshan.mymobilesafe.db.BlackNumberDBOpenHelper;
import com.xiaoshan.mymobilesafe.domain.BlackNumberInfo;

public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}

	public void add(String number, String mode) {
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("number", number);
		contentValues.put("mode", mode);
		database.insert("blacknumber", null, contentValues);
		database.close();
	}

	public void delete(String number) {
		SQLiteDatabase database = helper.getWritableDatabase();
		database.delete("blacknumber", "number = ?", new String[] { number });
		database.close();
	}

	public void update(String number, String newMode) {
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newMode);
		database.update("blacknumber", values, "number = ?",
				new String[] { number });
		database.close();
	}

	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery(
				"select * from blacknumber where number = ?",
				new String[] { number });
		while (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		database.close();

		return result;
	}

	public String findMode(String number) {
		String result = null;
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery(
				"select mode from blacknumber where number = ?",
				new String[] { number });
		while (cursor.moveToNext()) {
			String string = cursor.getString(0);
			result = string;
		}
		cursor.close();
		database.close();

		return result;
	}

	public List<BlackNumberInfo> findAll() {
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery(
				"select number, mode from blacknumber order by id desc", null);
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setNumber(number);
			info.setMode(mode);
			list.add(info);
		}
		
		cursor.close();
		database.close();
		
		return list;
	}
}
