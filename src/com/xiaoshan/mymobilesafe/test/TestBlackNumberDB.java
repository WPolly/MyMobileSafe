package com.xiaoshan.mymobilesafe.test;

import java.util.Random;

import com.xiaoshan.mymobilesafe.db.BlackNumberDBOpenHelper;
import com.xiaoshan.mymobilesafe.db.dao.BlackNumberDao;

import android.test.AndroidTestCase;

public class TestBlackNumberDB extends AndroidTestCase {
	public void testCreateDB() throws Exception {
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(
				getContext());
		helper.getWritableDatabase();
	}

	public void testAdd() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		long baseNumber = 13800000000l;
		Random random = new Random();

		for (int i = 0; i < 100; i++) {

			dao.add(String.valueOf(baseNumber + i),
					String.valueOf(random.nextInt(3) + 1));
		}
	}
	
	public void testDelete() {
		
	}
	
	public void testFindMode() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		String mode = dao.findMode("18516532658");
		assertEquals("3", mode);
	}
}
