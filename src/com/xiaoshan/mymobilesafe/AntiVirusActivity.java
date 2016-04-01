package com.xiaoshan.mymobilesafe;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import com.xiaoshan.mymobilesafe.db.dao.AntiVirusDao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {

	protected static final int SCANNING = 0;
	protected static final int FINISHING = 1;
	private ImageView ivScanning;
	private ProgressBar pbScanProgress;
	private TextView tvScanningStats;
	private LinearLayout llContainer;
	private PackageManager pm;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		
		String addition = "未发现病毒,手机安全.";

		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case SCANNING:
				AntiVirusInfo info = (AntiVirusInfo) msg.obj;
				tvScanningStats.setTextColor(Color.BLACK);
				tvScanningStats.setText("正在扫描: " + info.appName);
				TextView tv = new TextView(getApplicationContext());
				if (info.isVirus) {
					tv.setText("发现病毒: " + info.appName);
					tv.setTextColor(Color.RED);
					addition = "发现病毒: " + info.appName;
				} else {
					tv.setText("扫描安全: " + info.appName);
					tv.setTextColor(Color.BLACK);
				}
				llContainer.addView(tv, 0);

				break;

			case FINISHING:

				tvScanningStats.setText("扫描完成," + addition);
				tvScanningStats.setTextColor(Color.BLACK);
				ivScanning.clearAnimation();

				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		ivScanning = (ImageView) findViewById(R.id.iv_scanning_virus);
		pbScanProgress = (ProgressBar) findViewById(R.id.pb_scan_progress);
		tvScanningStats = (TextView) findViewById(R.id.tv_scan_status);
		llContainer = (LinearLayout) findViewById(R.id.ll_container);
		RotateAnimation ram = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ram.setDuration(2000);
		ram.setRepeatCount(Animation.INFINITE);
		ivScanning.startAnimation(ram);
		pm = getPackageManager();

		scanVirus();
	}

	private void scanVirus() {
		tvScanningStats.setText("正在初始化杀毒引擎..");
		tvScanningStats.setTextColor(Color.MAGENTA);
		new Thread() {
			public void run() {
				List<ApplicationInfo> applications = pm
						.getInstalledApplications(0);
				pbScanProgress.setMax(applications.size());
				int progress = 0;
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				for (ApplicationInfo applicationInfo : applications) {
					AntiVirusInfo info = new AntiVirusInfo();
					String appPath = applicationInfo.sourceDir;
					String md5 = getFileMd5(appPath);
					info.isVirus = AntiVirusDao.isVirus(md5);
					info.appName = applicationInfo.loadLabel(pm).toString();
					progress++;
					pbScanProgress.setProgress(progress);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					Message msg = Message.obtain();
					msg.what = SCANNING;
					msg.obj = info;
					handler.sendMessage(msg);
				}

				Message msg = Message.obtain();
				msg.what = FINISHING;
				handler.sendMessage(msg);
			};
		}.start();
	}

	class AntiVirusInfo {
		String appName;
		boolean isVirus;
	}

	private String getFileMd5(String path) {
		try {
			// 获取一个文件的特征信息，签名信息。
			File file = new File(path);
			// md5
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			fis.close();
			byte[] result = digest.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : result) {
				// 与运算
				int number = b & 0xff;// 加盐
				String str = Integer.toHexString(number);
				// System.out.println(str);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
