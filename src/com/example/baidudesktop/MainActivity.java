package com.example.baidudesktop;

/**
 * 主Activity
 */
import com.example.myView.MyView;
import com.example.myView.MyView.OnTurnplateListener;
import com.example.myView.MyView.Point;
import com.example.utils.WindowData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTurnplateListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取屏幕尺寸
        WindowData.screen_height = getWindowManager().getDefaultDisplay()
                .getHeight();
        WindowData.screen_width = getWindowManager().getDefaultDisplay()
                .getWidth();
        // 设置屏幕背景
        getWindow().setBackgroundDrawableResource(
                R.drawable.trashcan_background);
        // 创建View
        MyView myView = new MyView(this, WindowData.screen_width / 2,
                WindowData.screen_height / 2, WindowData.screen_width / 3);
        // 设置监听
        myView.setOnTurnplateListener(this);
        // 填充View
        setContentView(myView);
    }

    /**
    * 点击事件 根据Point的flag或者packageName处理事件
    */

    @Override
    public void onPointTouch(Point point) {

        Toast.makeText(this, String.valueOf(point.flag), Toast.LENGTH_SHORT)
                .show();
        // 点击进入应用
        startApp(point.packageName);
        this.finish();

    }

    /**
    * 根据包名打开应用
    *
    * @param packageName
    */
    public void startApp(String packageName) {
        Intent intent = MainActivity.this.getPackageManager()
                .getLaunchIntentForPackage(packageName);
        // 已安装包 直接启动
        if (intent != null) {
            startActivity(intent);
        }
    }

}
