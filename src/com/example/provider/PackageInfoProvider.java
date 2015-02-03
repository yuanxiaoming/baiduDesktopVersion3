package com.example.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.entity.AppInfo;
import com.example.utils.TimeComparator;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class PackageInfoProvider {
	private static final String tag = "GetappinfoActivity";
	private Context context;
	private List<AppInfo> recentAppInfos;// 最近应用
	private List<AppInfo> recentInstallAppInfos;// 最近安装应用
	private List<AppInfo> oftenUsedAppInfos;// 经常使用应用
	private AppInfo recentAppinfo;//
	private List<AppInfo> AllAppInfos;// 所有应用
	public static int sizeOfAppLists = 0;
	private int getNumber = 19;
	public PackageInfoProvider(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 获取最近使用的应用
	 * 
	 * @return
	 */
	public List<AppInfo> getRecentTasks() {
		PackageManager packageManager = context.getPackageManager();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		
		List<ActivityManager.RecentTaskInfo> appLists = mActivityManager
				.getRecentTasks(getNumber, ActivityManager.RECENT_WITH_EXCLUDED);
		System.out.println("共获取这么多："+appLists.size());
		sizeOfAppLists = appLists.size();
		
		recentAppInfos = new ArrayList<AppInfo>();
		for (ActivityManager.RecentTaskInfo running : appLists) {
			recentAppinfo = new AppInfo();
			Intent intent = running.baseIntent;
			ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);
			if (resolveInfo != null) {
				recentAppinfo.setAppName(resolveInfo.loadLabel(packageManager)
						.toString());
				recentAppinfo.setDrawable(resolveInfo.loadIcon(packageManager));
				recentAppinfo
						.setPackageName(resolveInfo.activityInfo.packageName);
				recentAppInfos.add(recentAppinfo);
			}
		}
		return recentAppInfos;
	}

	/**
	 * 获取经常使用的应用
	 * 
	 * @return
	 */
	public List<AppInfo> getOftenTasks() {

		return recentAppInfos;
	}

	/**
	 * 获取最近安装的应用
	 */
	@SuppressLint("NewApi")
	public List<AppInfo> getRecentlyinstalledTasks() {
		recentInstallAppInfos = new ArrayList<AppInfo>();
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pakageinfos = packageManager.getInstalledPackages(0);

		AllAppInfos = new ArrayList<AppInfo>();

		AppInfo allAppInfo = null;

		for (PackageInfo packageInfo : pakageinfos) {
			allAppInfo = new AppInfo();

			allAppInfo.setAppName(packageInfo.applicationInfo.loadLabel(
					packageManager).toString());
			allAppInfo.setDrawable(packageInfo.applicationInfo
					.loadIcon(packageManager));
			allAppInfo.setPackageName(packageInfo.packageName);
			allAppInfo.setFirstInstallTime(packageInfo.firstInstallTime);
			AllAppInfos.add(allAppInfo);
			allAppInfo = null;
		}
		Collections.sort(AllAppInfos, new TimeComparator());
	/*	for (AppInfo appInfo : AllAppInfos) {
			System.out.println("" + appInfo.getFirstInstallTime() + "");
		}*/
		for (int i = 0; i < 6; i++) {
			recentInstallAppInfos.add(AllAppInfos.get(i));
		}
		return recentInstallAppInfos;
	}
}