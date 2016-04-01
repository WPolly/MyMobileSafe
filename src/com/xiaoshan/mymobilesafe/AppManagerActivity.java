package com.xiaoshan.mymobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.xiaoshan.mymobilesafe.db.dao.AppLockDao;
import com.xiaoshan.mymobilesafe.domain.AppInfo;
import com.xiaoshan.mymobilesafe.engine.AppInfoProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.Vibrator;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity implements OnClickListener {

	private TextView tvRomMem, tvSDMem, tvUpdateAppAttr;
	private ListView lvAppInfo;
	private List<AppInfo> appInfos, userAppInfos, sysAppInfos;
	
	private AppInfoAdapter adapter;
	private PopupWindow pWindow;
	private LinearLayout llAppInfoLoading, llUninstallApp, llOpenApp,
			llShareApp;
	
	private ViewHolder holder;
	private AppInfo appInfo;
	private AppLockDao dao;
	
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		dao = new AppLockDao(this);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		tvRomMem = (TextView) findViewById(R.id.tv_rom_mem);
		tvSDMem = (TextView) findViewById(R.id.tv_sd_mem);
		tvUpdateAppAttr = (TextView) findViewById(R.id.tv_update_app_desc);
		lvAppInfo = (ListView) findViewById(R.id.lv_software_info);
		llAppInfoLoading = (LinearLayout) findViewById(R.id.ll_software_loading);
		llAppInfoLoading.setVisibility(View.VISIBLE);
		fillData();

		String romSpace = getAvailSpace(Environment.getDataDirectory()
				.getAbsolutePath());
		String sdSpace = getAvailSpace(Environment
				.getExternalStorageDirectory().getAbsolutePath());

		tvRomMem.setText("手机可用内存: " + romSpace);
		tvSDMem.setText("SD卡可用内存: " + sdSpace);

		lvAppInfo.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				dismissPopup();

				if (userAppInfos != null && sysAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tvUpdateAppAttr.setText("系统应用: " + sysAppInfos.size()
								+ "个");
					} else {
						tvUpdateAppAttr.setText("用户应用: " + userAppInfos.size()
								+ "个");
					}
				}
			}
		});

		lvAppInfo.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == 0) {
					return;
				}

				if (position == userAppInfos.size() + 1) {
					return;
				}

				if (position <= userAppInfos.size()) {
					int newPosition = position - 1;
					appInfo = userAppInfos.get(newPosition);
				} else {
					int newPosition = position - userAppInfos.size() - 2;
					appInfo = sysAppInfos.get(newPosition);
				}

				View contentView = View.inflate(AppManagerActivity.this,
						R.layout.popup_app_item, null);
				AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
				alphaAnimation.setDuration(200);
				ScaleAnimation scaleAnimation = new ScaleAnimation(0.2f, 1.0f,
						0.2f, 1.0f, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation.setDuration(200);
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(scaleAnimation);
				set.addAnimation(alphaAnimation);
				contentView.startAnimation(set);

				llUninstallApp = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall_app);
				llOpenApp = (LinearLayout) contentView
						.findViewById(R.id.ll_open_app);
				llShareApp = (LinearLayout) contentView
						.findViewById(R.id.ll_share_app);
				llUninstallApp.setOnClickListener(AppManagerActivity.this);
				llOpenApp.setOnClickListener(AppManagerActivity.this);
				llShareApp.setOnClickListener(AppManagerActivity.this);
				dismissPopup();

				pWindow = new PopupWindow(contentView, -2, -2);
				pWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				int[] location = new int[2];
				view.getLocationInWindow(location);
				pWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
						location[0] + holder.ivAppIcon.getWidth(), location[1]);
			}
		});

		lvAppInfo.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					return true;
				}

				if (position == userAppInfos.size() + 1) {
					return true;
				}

				if (position <= userAppInfos.size()) {
					int newPosition = position - 1;
					appInfo = userAppInfos.get(newPosition);
				} else {
					int newPosition = position - userAppInfos.size() - 2;
					appInfo = sysAppInfos.get(newPosition);
				}

				String packageName = appInfo.getPackageName();
				ViewHolder holder = (ViewHolder) view.getTag();

				if (packageName.equals(getPackageName())) {
					return true;
				}

				if (dao.find(packageName)) {
					dao.delete(packageName);
					holder.ivAppLock.setImageResource(R.drawable.toggle_off);
					vibrator.vibrate(100);
					Toast.makeText(AppManagerActivity.this, "已解锁该应用!",
							Toast.LENGTH_SHORT).show();
				} else {
					dao.add(packageName);
					holder.ivAppLock.setImageResource(R.drawable.toggle_on);
					vibrator.vibrate(100);
					Toast.makeText(AppManagerActivity.this, "已锁定该应用!",
							Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		});
	}

	private void fillData() {
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider.getAppInfo(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				sysAppInfos = new ArrayList<AppInfo>();

				for (AppInfo appInfo : appInfos) {
					if (appInfo.isUser()) {
						userAppInfos.add(appInfo);
					} else {
						sysAppInfos.add(appInfo);
					}
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (adapter == null) {
							adapter = new AppInfoAdapter();
							lvAppInfo.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						llAppInfoLoading.setVisibility(View.INVISIBLE);
						tvUpdateAppAttr.setText("用户应用: " + userAppInfos.size()
								+ "个");
					}
				});
			}
		}.start();

	}

	private class AppInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return appInfos.size() + 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (position == 0) {
				TextView tvUserApp = new TextView(AppManagerActivity.this);
				tvUserApp.setText("");
				tvUserApp.setBackgroundColor(Color.TRANSPARENT);
				tvUserApp.setTextColor(Color.WHITE);
				return tvUserApp;
			}

			if (position == userAppInfos.size() + 1) {
				TextView tvSysApp = new TextView(AppManagerActivity.this);
				tvSysApp.setText("系统应用: " + sysAppInfos.size() + "个");
				tvSysApp.setBackgroundColor(Color.argb(136, 0, 0, 0));
				tvSysApp.setTextColor(Color.WHITE);
				return tvSysApp;
			}

			View view = null;

			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(AppManagerActivity.this,
						R.layout.list_item_software_info, null);
				holder = new ViewHolder();
				holder.ivAppIcon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.tvAppLocation = (TextView) view
						.findViewById(R.id.tv_app_location);
				holder.tvAppName = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.ivAppLock = (ImageView) view
						.findViewById(R.id.iv_applock);
				view.setTag(holder);
			}

			int newPosition = 0;

			if (position <= userAppInfos.size()) {
				newPosition = position - 1;
				holder.ivAppIcon.setImageDrawable(userAppInfos.get(newPosition)
						.getAppIcon());
				holder.tvAppName.setText(userAppInfos.get(newPosition)
						.getAppName());
				if (userAppInfos.get(newPosition).getPackageName()
						.equals(getPackageName())) {
					holder.ivAppLock.setVisibility(View.INVISIBLE);
				} else {
					holder.ivAppLock.setVisibility(View.VISIBLE);
				}

				if (dao.find(userAppInfos.get(newPosition).getPackageName())) {
					holder.ivAppLock.setImageResource(R.drawable.toggle_on);
				} else {
					holder.ivAppLock.setImageResource(R.drawable.toggle_off);
				}
				if (userAppInfos.get(newPosition).isRom()) {
					holder.tvAppLocation.setText("手机内存");
				} else {
					holder.tvAppLocation.setText("外部存储");
				}

			} else {
				newPosition = position - userAppInfos.size() - 2;
				holder.ivAppIcon.setImageDrawable(sysAppInfos.get(newPosition)
						.getAppIcon());
				holder.tvAppName.setText(sysAppInfos.get(newPosition)
						.getAppName());
				holder.ivAppLock.setVisibility(View.VISIBLE);

				if (dao.find(sysAppInfos.get(newPosition).getPackageName())) {
					holder.ivAppLock.setImageResource(R.drawable.toggle_on);
				} else {
					holder.ivAppLock.setImageResource(R.drawable.toggle_off);
				}

				if (sysAppInfos.get(newPosition).isRom()) {
					holder.tvAppLocation.setText("手机内存");
				} else {
					holder.tvAppLocation.setText("外部存储");
				}
			}

			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	class ViewHolder {
		ImageView ivAppIcon, ivAppLock;
		TextView tvAppName, tvAppLocation;
	}

	private String getAvailSpace(String absolutePath) {

		StatFs statFs = new StatFs(absolutePath);
		long blocks = statFs.getAvailableBlocksLong();
		long size = statFs.getBlockSizeLong();
		return Formatter.formatFileSize(this, blocks * size);
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillData();
	}

	@Override
	protected void onDestroy() {
		dismissPopup();
		super.onDestroy();
	}

	private void dismissPopup() {
		if (pWindow != null) {
			pWindow.dismiss();
			pWindow = null;
		}
	}

	@Override
	public void onClick(View v) {

		dismissPopup();
		switch (v.getId()) {
		case R.id.ll_uninstall_app:
			if (appInfo.isUser()) {
				uninstallApp();
			} else {
				Toast.makeText(this, "系统应用不可卸载,需获取root权限", Toast.LENGTH_SHORT)
						.show();
			}

			break;

		case R.id.ll_open_app:
			startApp();

			break;

		case R.id.ll_share_app:
			shareApp();

			break;

		default:
			break;
		}
	}

	private void shareApp() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "推荐你使用这款名叫 " + appInfo.getAppName()
				+ " 的软件!");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		startActivity(intent);
	}

	private void uninstallApp() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appInfo.getPackageName()));
		startActivityForResult(intent, 0);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		fillData();
		super.startActivityForResult(intent, requestCode);
	}

	private void startApp() {
		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(this, "该应用已禁止启动", Toast.LENGTH_SHORT).show();
		}
	}
	
}
