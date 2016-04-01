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
	 * ���ݶ���ʱ�Ļص��ӿ�
	 * @author Administrator
	 *
	 */
	public interface BackupCallBack {
		/**
		 * ��ʼ����ʱ,Ϊ�����������ֵ.
		 * @param max ���ֵ
		 */
		public void beforeBackup(int max);
		
		/**
		 * ���ݹ�����,���ñ��ݵĽ���.
		 * @param process ���ݽ���
		 */
		public void onBackup(int process);
	}
	
	/**
	 * ���ݶ���
	 * @param context ������
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
			serializer.text(body);//�����Ŷ�ʱ���ݵ��˴�����͹ҵ�,��617������
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
