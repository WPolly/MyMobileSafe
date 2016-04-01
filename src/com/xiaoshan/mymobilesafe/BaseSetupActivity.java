package com.xiaoshan.mymobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public abstract class BaseSetupActivity extends Activity {
	
	private GestureDetector detector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		detector = new GestureDetector(this, new OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
				
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) {
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				if (velocityX < -500) {
					showNextPage();
					return true;
				}
				
				if (velocityX > 500) {
					showPrePage();
					return true;
				}
				
				if ((e2.getRawX() - e1.getRawX()) > 200) {
					showPrePage();
					return true;
				}
				
				if ((e2.getRawX() - e1.getRawX()) < -200) {
					showNextPage();
					return true;
				}
				return false;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
	}
	
	public abstract void showPrePage();
	public abstract void showNextPage();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
