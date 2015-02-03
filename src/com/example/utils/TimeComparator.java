package com.example.utils;

import java.util.Comparator;

import com.example.entity.AppInfo;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
/**
 * 根据时间排序所有应用
 * @author Administrator
 *
 */
public class TimeComparator implements Comparator<AppInfo>{

	@SuppressLint("NewApi")
	@Override
	public int compare(AppInfo lhs, AppInfo rhs) {
		// TODO Auto-generated method stub
		if((lhs.getFirstInstallTime() - rhs.getFirstInstallTime()) < 0){
			return 1;
		}else if(lhs.getFirstInstallTime() - rhs.getFirstInstallTime() == 0){
			return 0;
		}
		return -1;
	}
}
