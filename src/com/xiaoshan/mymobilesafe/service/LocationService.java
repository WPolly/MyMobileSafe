package com.xiaoshan.mymobilesafe.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.xiaoshan.mymobilesafe.domain.PointDouble;
import com.xiaoshan.mymobilesafe.utils.ModifyOffset;

public class LocationService extends Service {

	private SharedPreferences sp;
	private LocationManager lm;
	private LocationListener listener;
	private List<String> allProviders;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		listener = new MyLocationListener();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		allProviders = lm.getAllProviders();
		System.out.println(allProviders);
		Editor editor = sp.edit();
		editor.putString("provider", allProviders.toString());
		editor.commit();
		//String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates("passive", 6000, 10, listener);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(listener);
		listener = null;
	}

	private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {

			InputStream is = null;
			try {
				File file = new File(getFilesDir(), "axisoffset.dat");
				is = new FileInputStream(file);
				ModifyOffset offset = ModifyOffset.getInstance(is);
				PointDouble pointDouble = new PointDouble(
						location.getLongitude(), location.getLatitude());
				PointDouble s2c = offset.s2c(pointDouble);
				String longitude = "经度: " + s2c.getX() + "\n";
				String latitude = "纬度: " + s2c.getY() + "\n";
				String accuracy = "精确度: "+ location.getAccuracy() + "\n";
				Editor editor = sp.edit();
				editor.putString("location", longitude + latitude + accuracy);
				editor.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

	}

}
