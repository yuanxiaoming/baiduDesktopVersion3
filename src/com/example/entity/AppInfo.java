package com.example.entity;

import android.graphics.drawable.Drawable;
/**
 * 应用的类
 * @author Administrator
 *
 */
public class AppInfo {
	private String appName;//应用名称
	private Drawable drawable;//图标
	private String packageName;//包名
	private long firstInstallTime;//首次安装时间
	private long lastUpdateTime;//最后更新时间
	
	public long getFirstInstallTime() {
		return firstInstallTime;
	}

	public void setFirstInstallTime(long firstInstallTime) {
		this.firstInstallTime = firstInstallTime;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	
	
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public String toString() {
		return "" + appName + drawable;
	}
}
