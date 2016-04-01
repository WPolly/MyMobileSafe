package com.xiaoshan.mymobilesafe.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUtils {
	/**
	 * 备份短信时的回调接口
	 * @author Administrator
	 *
	 */
	public interface BackupCallBack {
		/**
		 * 开始备份时,为进度设置最大值.
		 * @param max 最大值
		 */
		public void beforeBackup(int max);
		
		/**
		 * 备份过程中,设置备份的进度.
		 * @param process 备份进度
		 */
		public void onBackup(int process);
	}
	
	/**
	 * 备份短信
	 * @param context 上下文
	 * @throws Exception
	 */
	public static void backupSms(Context context, BackupCallBack callBack) throws Exception {
		File file = new File(Environment.getExternalStorageDirectory(),
				"smsbackup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[] { "address", "body",
				"type", "date" }, null, null, null);
		int count = cursor.getCount();
		callBack.beforeBackup(count);
		int process = 0;
		
		while (cursor.moveToNext()) {
			Thread.sleep(50);
			serializer.startTag(null, "sms");
			String address = cursor.getString(0);
			String body = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			
			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");
			
			serializer.startTag(null, "body");
			serializer.text(body);//当短信多时备份到此处程序就挂掉,第617条短信
			serializer.endTag(null, "body");
			
			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");
			
			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			
			serializer.endTag(null, "sms");
			callBack.onBackup(process);
			process++;
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
	}
}
