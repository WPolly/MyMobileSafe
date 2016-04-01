package com.xiaoshan.mymobilesafe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoshan.mymobilesafe.domain.CacheInfo;

public class CleanCacheActivity extends Activity {

	private PackageManager pm;
	private ProgressBar pbCleanCache;
	private TextView tvCleanCacheStats;
	private ListView lvCacheInfo;
	private Button btClearCache;
	private CacheInfo cacheInfo;
	private List<CacheInfo> cacheInfos;
	private MyAdapter adapter;
	private int progress;
	private long totalCache;
	private boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		pm = getPackageManager();
		cacheInfos = new ArrayList<CacheInfo>();
		pbCleanCache = (ProgressBar) findViewById(R.id.pb_clean_cache);
		tvCleanCacheStats = (TextView) findViewById(R.id.tv_clean_cache_stats);
		lvCacheInfo = (ListView) findViewById(R.id.lv_cache_info);
		btClearCache = (Button) findViewById(R.id.bt_clear_cache);
		btClearCache.setVisibility(View.INVISIBLE);
		flag = true;
		scanCache();
	}

	private void scanCache() {
		new Thread() {
			public void run() {
				Method getPackageSizeInfoMethod = null;
				Method[] methods = PackageManager.class.getMethods();
				for (Method method : methods) {
					if ("getPackageSizeInfo".equals(method.getName())) {
						getPackageSizeInfoMethod = method;
						break;
					}
				}

				List<PackageInfo> packages = pm.getInstalledPackages(0);
				pbCleanCache.setMax(packages.size());

				for (PackageInfo packageInfo : packages) {
					
					if (flag) {
						try {
							getPackageSizeInfoMethod.invoke(pm,
									packageInfo.packageName, new MyStatsObserver());
							//System.out.println("hello-----");
							Thread.sleep(800);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						break;
					}
					
				}

				runOnUiThread(new Runnable() {

					public void run() {

						pbCleanCache.setVisibility(View.GONE);
						tvCleanCacheStats.setText("扫描完毕! 共找到"
								+ cacheInfos.size()
								+ "项缓存数据, 总大小 "
								+ Formatter.formatFileSize(
										getApplicationContext(), totalCache));
						tvCleanCacheStats.setBackgroundColor(Color.argb(150, 0,
								0, 0));
						tvCleanCacheStats.setTextColor(Color.WHITE);
						adapter = new MyAdapter();
						lvCacheInfo.setAdapter(adapter);
						lvCacheInfo
								.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										CacheInfo info = cacheInfos
												.get(position);
										String packageName = info
												.getPackageName();
										Intent intent = new Intent();
										intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
										intent.addCategory("android.intent.category.DEFAULT");
										Uri data = Uri.parse("package:"
												+ packageName);
										intent.setData(data);
										startActivity(intent);
									}
								});
						btClearCache.setVisibility(View.VISIBLE);
						btClearCache.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Method[] mds = PackageManager.class
										.getMethods();
								for (Method md : mds) {
									if (md.getName().equals(
											"freeStorageAndNotify")) {
										try {
											md.invoke(
													pm,
													Integer.MAX_VALUE,
													new IPackageDataObserver.Stub() {

														@Override
														public void onRemoveCompleted(
																String packageName,
																boolean succeeded)
																throws RemoteException {
															System.out
																	.println(packageName
																			+ succeeded);
															System.out
																	.println("Hello, World!");
															runOnUiThread(new Runnable() {
																public void run() {
																	Toast.makeText(
																			CleanCacheActivity.this,
																			"缓存清理完毕",
																			Toast.LENGTH_SHORT)
																			.show();
																	cacheInfos
																			.clear();
																	adapter.notifyDataSetChanged();
																	tvCleanCacheStats
																			.setText("恭喜,缓存清理完毕!");
																}
															});

														}
													});
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
						});
					}
				});
			};
		}.start();
	}

	private class MyStatsObserver extends IPackageStatsObserver.Stub {
		ApplicationInfo applicationInfo;

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {

			try {
				applicationInfo = pm.getApplicationInfo(pStats.packageName, 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			long cache = pStats.cacheSize;

			if (cache > 12 * 1024) {
				cacheInfo = new CacheInfo();
				String cacheSize = Formatter.formatFileSize(
						getApplicationContext(), cache);
				cacheInfo.setCacheSize(cacheSize);
				cacheInfo.setPackageName(pStats.packageName);
				cacheInfo.setAppIcon(applicationInfo.loadIcon(pm));
				cacheInfo.setAppName((String) applicationInfo.loadLabel(pm));
				cacheInfos.add(cacheInfo);
				totalCache += cache;
			}

			progress++;
			pbCleanCache.setProgress(progress);

			runOnUiThread(new Runnable() {
				public void run() {
					tvCleanCacheStats.setText("正在扫描: "
							+ applicationInfo.loadLabel(pm));
				}
			});
		}

	}

	// private class MypackDataObserver extends IPackageDataObserver.Stub {
	// @Override
	// public void onRemoveCompleted(String packageName, boolean succeeded)
	// throws RemoteException {
	// System.out.println(packageName + succeeded);
	// System.out.println("Hello, World!");
	// }
	// }

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return cacheInfos.size();

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = null;
			ViewHolder holder = new ViewHolder();

			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(CleanCacheActivity.this,
						R.layout.list_item_cache_info, null);
				holder.tvAppName = (TextView) view
						.findViewById(R.id.tv_app_name);
				holder.tvCacheSize = (TextView) view
						.findViewById(R.id.tv_cache_size);
				holder.ivAppIcon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				view.setTag(holder);
			}
			CacheInfo info = cacheInfos.get(position);
			holder.tvAppName.setText(info.getAppName());
			holder.tvCacheSize.setText(info.getCacheSize());
			holder.ivAppIcon.setImageDrawable(info.getAppIcon());
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
		TextView tvAppName, tvCacheSize;
		ImageView ivAppIcon;
	}

	@Override
	protected void onDestroy() {
		flag = false;
		super.onDestroy();
	}
}
