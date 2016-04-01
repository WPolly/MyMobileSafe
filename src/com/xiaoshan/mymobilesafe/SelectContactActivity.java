package com.xiaoshan.mymobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {

	private ListView lvContacts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		lvContacts = (ListView) findViewById(R.id.lv_contacts);
		final List<Map<String, String>> list = getContacts();
		
		lvContacts.setAdapter(new SimpleAdapter(this, list,
				R.layout.list_item_contact, new String[] { "name", "tel" },
				new int[] { R.id.tv_contact_name, R.id.tv_contact_tel }));
		
		lvContacts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String tel = list.get(position).get("tel");
				Intent intent = new Intent();
				intent.putExtra("tel", tel);
				setResult(0, intent);
				finish();
			}
		});
	}

	private List<Map<String, String>> getContacts() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		ContentResolver resolver = getContentResolver();
		Uri uriRawCont = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Uri uriData = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = resolver.query(uriRawCont,
				new String[] { "contact_id" }, null, null, null);

		while (cursor.moveToNext()) {
			String contactId = cursor.getString(0);

			if (contactId != null) {
				Map<String, String> map = new HashMap<String, String>();

				Cursor dataCusor = resolver.query(uriData, new String[] {
						"data1", "mimetype" }, "contact_id=?",
						new String[] { contactId }, null);
				
				while (dataCusor.moveToNext()) {
					String data1 = dataCusor.getString(0);
					String mimetype = dataCusor.getString(1);
					

					if (mimetype.equals("vnd.android.cursor.item/name")) {
						map.put("name", data1);
					} else {
						map.put("tel", data1);
					}
				}

				list.add(map);

				dataCusor.close();
			}

		}

		cursor.close();

		return list;

	}
}
