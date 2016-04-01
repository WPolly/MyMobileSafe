package com.xiaoshan.mymobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoshan.mymobilesafe.service.AutoCleanService;
import com.xiaoshan.mymobilesafe.service.CallSmsSafeService;
import com.xiaoshan.mymobilesafe.service.ShowInComingCallService;
import com.xiaoshan.mymobilesafe.service.WatchDogService;
import com.xiaoshan.mymobilesafe.utils.ServiceStatusUtils;
import com.xiaoshan.mymobilesafe.utils.StreamTools;

public class SplashActivity extends Activity {
	protected static final int ENTER_HOME = 0;
	protected static final int UPDATE_DIALOG = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NET_ERROR = 3;
	protected static final int JSON_ERROR = 4;

	private TextView versionName;
	private TextView updateProcess;
	private SharedPreferences sp;

	private String description;
	private String urlDes;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ENTER_HOME:
				enterHome();
				break;

			case UPDATE_DIALOG:
				System.out.println("显示升级对话框.");
				showUpdateDialog();
				break;

			case URL_ERROR:
				Toast.makeText(SplashActivity.this, "URL错误!",
						Toast.LENGTH_SHORT).show();
				enterHome();
				break;

			case NET_ERROR:
				Toast.makeText(SplashActivity.this, "网络异常!", Toast.LENGTH_SHORT)
						.show();
				enterHome();
				break;

			case JSON_ERROR:
				Toast.makeText(SplashActivity.this, "JSON解析异常!",
						Toast.LENGTH_SHORT).show();
				enterHome();

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		versionName = (TextView) findViewById(R.id.tv_version);
		updateProcess = (TextView) findViewById(R.id.tv_update_process);
		versionName.setText("版本: " + getVersionName());
		Animation animation = new AlphaAnimation(0.2f, 1.0f);
		animation.setDuration(2000);
		findViewById(R.id.rl_splash).startAnimation(animation);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		copyDB("address.db");
		copyDB("axisoffset.dat");
		copyDB("antivirus.db");

		boolean checkUpdate = sp.getBoolean("check_update", false);
		boolean showIncomingCall = sp.getBoolean("show_incoming_call", false);
		boolean callSmsSafe = sp.getBoolean("call_sms_safe_status", false);
		boolean autoClean = sp.getBoolean("auto_clean", false);
		boolean watchDog = sp.getBoolean("watch_dog", false);
		resetServiceStatus(showIncomingCall, ShowInComingCallService.class);
		resetServiceStatus(callSmsSafe, CallSmsSafeService.class);
		resetServiceStatus(autoClean, AutoCleanService.class);
		resetServiceStatus(watchDog, WatchDogService.class);
		if (checkUpdate) {
			checkUpdate();
		} else {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					enterHome();
				}
			}, 2000);
		}

	}

	private void resetServiceStatus(boolean b, Class<?> cls) {
		boolean isRunning = ServiceStatusUtils.isServiceRunning(
				getApplicationContext(), cls.getName());

		if (b) {
			Intent intent = new Intent(getApplicationContext(), cls);
			if (!isRunning) {
				startService(intent);
			}
		}
	}

	private void copyDB(String fileName) {
		File file = new File(getFilesDir(), fileName);
		if (!file.exists() || file.length() == 0) {
			try {
				InputStream is = getAssets().open(fileName);
				OutputStream os = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					os.write(buffer, 0, len);
				}

				is.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(this, "载入数据库异常", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 弹出升级对话框
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("升级提示:");
		builder.setMessage(description);
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				enterHome();
			}
		});

		builder.setPositiveButton("立刻升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					FinalHttp finalHttp = new FinalHttp();
					finalHttp.download(urlDes, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/uc2.apk", new AjaxCallBack<File>() {

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							Toast.makeText(getApplicationContext(), "下载更新失败!",
									Toast.LENGTH_LONG).show();
							updateProcess.setText("更新进度: " + errorNo + strMsg);
							super.onFailure(t, errorNo, strMsg);
						}

						@Override
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							int process = (int) ((current * 100) / count);
							updateProcess.setText("更新进度: " + process + "%");
						}

						@Override
						public void onSuccess(File t) {
							super.onSuccess(t);

						}

					});
				} else {
					Toast.makeText(getApplicationContext(), "请正确安装sdcard.",
							Toast.LENGTH_LONG).show();
					return;
				}

			}
		});

		builder.setNegativeButton("暂不升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();
			}
		});

		builder.show();
	}

	/**
	 * 进入主页面
	 */
	protected void enterHome() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 检查更新
	 */
	private void checkUpdate() {
		new Thread() {
			@Override
			public void run() {
				long startTime = System.currentTimeMillis();
				Message msg = Message.obtain();
				try {
					URL url = new URL(getString(R.string.serverurl));
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(4000);
					int code = connection.getResponseCode();
					if (code == 200) {
						InputStream is = connection.getInputStream();
						String result = StreamTools.readFromStream(is);
						System.out.println(result);
						JSONObject object = new JSONObject(result);
						description = object.getString("description");
						urlDes = object.getString("url");
						String verson = object.getString("version");
						if (verson.equals(getVersionName())) {
							msg.what = ENTER_HOME;
						} else {
							msg.what = UPDATE_DIALOG;
						}

					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = NET_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = JSON_ERROR;
				} finally {
					long endTime = System.currentTimeMillis();
					long dTime = endTime - startTime;

					if (dTime < 2000) {
						try {
							Thread.sleep(2000 - dTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
				}
			}
		}.start();
	}

	/**
	 * 获取版本信息
	 */
	private String getVersionName() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "版本不详!";
		}
	}
}
