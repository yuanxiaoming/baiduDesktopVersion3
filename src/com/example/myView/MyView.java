package com.example.myView;

import java.util.List;

import com.example.baidudesktop.R;
import com.example.entity.AppInfo;
import com.example.provider.PackageInfoProvider;

import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class MyView extends View implements OnTouchListener {

	private Paint paintForLine = new Paint();// 点和线
	private Paint paintForCircle = new Paint();// 圆
	private Paint paintForText = new Paint();// 字体
	private Paint paintForShan;//扇形
	private Paint paintForBitmap ;//图形
	private Bitmap[] icons = new Bitmap[18];// 图标列表
	private Point[] AllPoints;// 所有点列表
	private Point[] RecentPoints; // 最近使用的应用
	private Point[] RecentInstalledPoints; // 最近安装的应用
	private Point[] OftenUsedPoints; // 经常使用的应用
	private static final int PONIT_NUM = 18;// 点的数目
	private int mPointX = 0, mPointY = 0;// 圆心坐标
	private int mRadius = 0;// 半径
	private int mDegreeDelta;// 每两个点相隔的角度
	private int tempDegree = 0;// 每次转动的角度差
	private int chooseBtn = 999;// 选中的图标标识 999：未选中任何图标
	private Matrix mMatrix = new Matrix();
	private int startAngle = 210;//开始角度
	private int degree;// 偏转角度
	private int selectFlag = 0;//扇形选择区标志
	private PackageInfoProvider pakageInfoProvider = null;
	private static List<AppInfo> recentAppInfos = null;
	private static List<AppInfo> recentInstallAppInfos = null;
	private static List<AppInfo> oftenUseAppInfos = null;
	private Bitmap bitmapBg;//每个应用的北京图片
	private static int mAlpha = 255;
	// 动作监听
	private OnTurnplateListener onTurnplateListener;

	public void setOnTurnplateListener(OnTurnplateListener onTurnplateListener) {
		this.onTurnplateListener = onTurnplateListener;
	}

	public MyView(Context context, int px, int py, int radius) {
		super(context);
		// 获取包管理信息
		pakageInfoProvider = new PackageInfoProvider(context);
		//获取最近应用
		recentAppInfos = pakageInfoProvider.getRecentTasks();
		//获取最近安装应用
		recentInstallAppInfos = pakageInfoProvider.getRecentlyinstalledTasks();
		bitmapBg = convertToBitmapFromDrawable(getResources().getDrawable(
				R.drawable.bg_green));
		initPaints();// 初始化画笔
		mPointX = px;
		mPointY = py;
		mRadius = radius;
		initPoints();
		computeCoordinates();// 计算每个点的坐标
	}

	/**
	 * 初始化画笔
	 */
	private void initPaints() {
		paintForLine = new Paint();// 点和线
		paintForCircle = new Paint();// 圆
		paintForText = new Paint();// 字体
		paintForShan = new Paint();//扇形
		paintForBitmap = new Paint();//图形
		
		paintForLine.setColor(Color.RED);
		paintForLine.setStrokeWidth(2);
		paintForCircle.setAntiAlias(true);
		paintForCircle.setColor(Color.WHITE);

		paintForText.setColor(Color.WHITE);
		paintForText.setTextSize(15.0f);
		paintForText.setTextAlign(Align.CENTER);
		paintForText.setAlpha(mAlpha);
		
		paintForShan.setColor(Color.BLACK);
		paintForShan.setAlpha(35);
		paintForBitmap.setAlpha(mAlpha);
	}

	/**
	 * 将drawable转换为bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	private Bitmap convertToBitmapFromDrawable(Drawable drawable) {
		//修改此处可以更改图标的大小
		Bitmap bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, 80, 80);
		drawable.draw(canvas);
		
		return bitmap;
	}

	/**
	 * 初始化所有的点
	 */
	private void initPoints() {
		AllPoints = new Point[PONIT_NUM];
		initRecentPoints();
		initRecentInstalledPoints();
		initOftenUsedPoints();//可以根据自己需求更改，此处用最近使用的应用代替
		for (int i = 0; i < 6; i++) {
			AllPoints[i] = RecentPoints[i];
			AllPoints[i + 6] = RecentInstalledPoints[i];
			AllPoints[i + 12] = OftenUsedPoints[i];
		}
	}

	/**
	 *功能：初始化最近使用的点 参数 点的角度，偏移角度，位图，标志
	 */

	private void initRecentPoints() {
		RecentPoints = new Point[6];
		Point point;
		int angle = 270;// 起始角度
		int sizeOfPoint =0;
		//从堆栈里面提取的数据前两个为安卓系统，和当前应用，所以此处的编号均加2，即为8
		if (PackageInfoProvider.sizeOfAppLists>=8) {
			mDegreeDelta = 360 / 6;
			sizeOfPoint = 6;
			// 防止数组越界问题
			for (int index = 0; index < sizeOfPoint; index++) {
				point = new Point();
				point.angle = angle;
				angle += mDegreeDelta;
				if (angle > 360 || angle < -360) {
					angle = angle % 360;
				}// 保证angle在0~360范围内
				point.bitmap = convertToBitmapFromDrawable(recentAppInfos.get(
						index + 2).getDrawable());
				point.flag = index;
				point.appName = recentAppInfos.get(index +2).getAppName();
				point.packageName = recentAppInfos.get(index + 2).getPackageName();
				point.startDegree = angle;
				RecentPoints[index] = point;
			}
		}else {
			sizeOfPoint = pakageInfoProvider.sizeOfAppLists;
			// 防止数组越界问题
			for (int index = 0; index < sizeOfPoint; index++) {
				point = new Point();
				point.angle = angle;
				angle += mDegreeDelta;
				if (angle > 360 || angle < -360) {
					angle = angle % 360;
				}// 保证angle在0~360范围内
				point.bitmap = convertToBitmapFromDrawable(recentAppInfos.get(
						index).getDrawable());
				point.flag = index;
				point.appName = recentAppInfos.get(index ).getAppName();
				point.packageName = recentAppInfos.get(index).getPackageName();
				point.startDegree = angle;
				RecentPoints[index] = point;
			}
		}
	}

	/**
	 * 
	 * 
	 */

	private void initRecentInstalledPoints() {
		RecentInstalledPoints = new Point[6];
		Point point;
		int angle = 270;// 起始角度
		mDegreeDelta = 360 / 6;

		for (int index = 0; index < 6; index++) {
			point = new Point();
			point.angle = angle;
			angle += mDegreeDelta;
			if (angle > 360 || angle < -360) {
				angle = angle % 360;
			}// 保证angle在0~360范围内
			point.bitmap = convertToBitmapFromDrawable(recentInstallAppInfos
					.get(index).getDrawable());
			point.flag = index + 6;
			point.appName = recentInstallAppInfos.get(index).getAppName();
			point.packageName = recentInstallAppInfos.get(index)
					.getPackageName();
			point.startDegree = angle;
			RecentInstalledPoints[index] = point;
		}
	}

	/**
	 * 
	 * 方法名：initPoints 功能：初始化每个点 参数 点的角度，偏移角度，位图，标志
	 */

	private void initOftenUsedPoints() {
		OftenUsedPoints = new Point[6];
		Point point;
		int angle = 270;// 起始角度
		mDegreeDelta = 360 / 6;

		for (int index = 0; index < 6; index++) {
			point = new Point();
			point.angle = angle;
			angle += mDegreeDelta;
			if (angle > 360 || angle < -360) {
				angle = angle % 360;
			}// 保证angle在0~360范围内
			point.bitmap = convertToBitmapFromDrawable(recentAppInfos.get(
					index+2).getDrawable());
			point.flag = index + 12;
			point.appName = recentAppInfos.get(index+2).getAppName();
			point.packageName = recentAppInfos.get(index+2).getPackageName();
			point.startDegree = angle;
			OftenUsedPoints[index] = point;
		}
	}

	/**
	 * 
	 * 方法名：resetPointAngle 功能：重新计算每个点的角度 参数：
	 * 
	 */
	private void resetPointAngle(float x, float y) {
		// 每次转动的角度
		degree = computeMigrationAngle(x, y);
		for (int index = 0 + selectFlag; index < 6 + selectFlag; index++) {
			AllPoints[index].angle += degree;
			if (AllPoints[index].angle > 360) {
				AllPoints[index].angle -= 360;
			} else if (AllPoints[index].angle < 0) {
				AllPoints[index].angle += 360;
			}
		}
	}

	/**
	 * 
	 * 方法名：computeCoordinates 功能：计算每个点的坐标 参数： 点的坐标,点和圆心之间中心点的坐标
	 */
	private void computeCoordinates() {
		Point point;
		for (int index = 0 + selectFlag; index < 6 + selectFlag; index++) {
			point = AllPoints[index];
			point.x = mPointX
					+ (float) (mRadius * Math.cos(point.angle * Math.PI / 180));
			point.y = mPointY
					+ (float) (mRadius * Math.sin(point.angle * Math.PI / 180));
			point.x_c = mPointX + (point.x - mPointX) / 2;
			point.y_c = mPointY + (point.y - mPointY) / 2;
		}
	}

	/**
	 * 
	 * 方法名：computeMigrationAngle 功能：计算偏移角度 参数： 每次转动的角度差
	 */
	private int computeMigrationAngle(float x, float y) {
		int a = 0;
		float distance = (float) Math
				.sqrt(((x - mPointX) * (x - mPointX) + (y - mPointY)
						* (y - mPointY)));
		int degree = (int) (Math.acos((x - mPointX) / distance) * 180 / Math.PI);
		if (y < mPointY) {
			degree = -degree;
		}
		if (tempDegree != 0) {
			a = degree - tempDegree;
		}
		tempDegree = degree;
		return a;
	}

	/**
	 * 
	 * 方法名：computeCurrentDistance 功能：计算触摸的位置与各个元点的距离 参数： 是否选择，标志
	 */
	private void computeCurrentDistance(float x, float y) {
		for (Point point : AllPoints) {
			float distance = (float) Math
					.sqrt(((x - point.x) * (x - point.x) + (y - point.y)
							* (y - point.y)));
			if (distance < 31) {
				chooseBtn = 999;
				point.isCheck = true;
				break;
			} else {
				point.isCheck = false;
				chooseBtn = point.flag;
			}
		}
	}

	private void switchScreen(MotionEvent event) {
		computeCurrentDistance(event.getX(), event.getY());
		for (Point point : AllPoints) {
			if (point.isCheck) {
				onTurnplateListener.onPointTouch(point);
				break;
			}
		}
	}

	/**
	 * 调度触摸事件
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN :
			
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			resetPointAngle(event.getX(), event.getY());
			MoveDegreeShan();
			computeCoordinates();
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			switchScreen(event);
			upDegreeShan();
			tempDegree = 0;
			initPoints();
			computeCoordinates();
			invalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		
	
		return true;
	}

	private void MoveDegreeShan() {
		startAngle += degree;
		if (startAngle > 360 || startAngle < -360) {
			startAngle = startAngle % 360;
		}

		if (startAngle >= 150 && startAngle < 270) {
			selectFlag = 0;
			// 显示第一组
		} else if (startAngle >= 30 && startAngle < 150) {
			// 显示第三组
			selectFlag = 12;
		} else {
			// 显示第二组
			selectFlag = 6;
		}
		//setAnimation(startAngle % 120);
		setAlphaForPaint((startAngle-30)%120);
	}

	private void setAlphaForPaint(int number){
		int numberForAlpha = number*255/120;
		paintForBitmap.setAlpha(255-numberForAlpha);
		paintForText.setAlpha(255-numberForAlpha);
	}
	private void setAnimation(int number) {
		for (int i = 0; i < AllPoints.length; i++) {
			AllPoints[i].bitmap = setAlpha(AllPoints[i].bitmap, number);
		}
	}

	private void upDegreeShan() {
		if (startAngle >= 150 && startAngle < 270) {
			selectFlag = 0;
			startAngle = 210;
			setAlphaForPaint(255);
			// 显示第一组
		} else if (startAngle >= 30 && startAngle < 150) {
			// 显示第三组
			selectFlag = 12;
			startAngle = 90;
			setAlphaForPaint(255);
		} else {
			// 显示第二组
			selectFlag = 6;
			startAngle = 330;
			setAlphaForPaint(255);
		}
		
	}

	@Override
	public void onDraw(Canvas canvas) {

		Bitmap bitmap = ((BitmapDrawable) (getResources()
				.getDrawable(R.drawable.quick_launcher_bg))).getBitmap();
		Bitmap girlBitmap = ((BitmapDrawable) (getResources()
				.getDrawable(R.drawable.quick_launcher_tab_bg))).getBitmap();
		canvas.drawBitmap(bitmap, mPointX - bitmap.getWidth() / 2, mPointY
				- bitmap.getHeight() / 2, null);
		canvas.drawBitmap(girlBitmap, mPointX - girlBitmap.getWidth() / 2,
				mPointY - girlBitmap.getHeight() / 2, null);

		RectF rect = new RectF(mPointX - girlBitmap.getWidth() / 2, mPointY
				- girlBitmap.getHeight() / 2, mPointX + girlBitmap.getWidth()
				/ 2, mPointY + girlBitmap.getHeight() / 2);
		canvas.drawArc(rect, // 弧线所使用的矩形区域大小
				startAngle, // 开始角度
				120, // 扫过的角度
				true, // 是否使用中心
				paintForShan);

		for (int index = 0 + selectFlag; index < 6 + selectFlag; index++) {

			drawInCenter(canvas, AllPoints[index].bitmap, AllPoints[index].x,
					AllPoints[index].y, AllPoints[index].flag,
					AllPoints[index].appName, AllPoints[index].packageName);
		}
	}

	/**
	 * 
	 * 方法名：drawInCenter 功能：把点放到图片中心处 参数：
	 */
	void drawInCenter(Canvas canvas, Bitmap bitmap, float left, float top,
			int flag, String appName, String packageName) {
		//canvas.drawPoint(left, top, paintForLine);

		/*if (chooseBtn == flag) {
			
			 mMatrix.setScale(70f / bitmap.getWidth(), 70f /
			 bitmap.getHeight());// 绘制 
			 mMatrix.postTranslate(left - 35, top -35);// 移动
			 canvas.drawBitmap(bitmap, mMatrix, null);
		} else {*/
			canvas.drawBitmap(bitmapBg, left - bitmap.getWidth() / 2, top
					- bitmap.getHeight() / 2, paintForBitmap);
			canvas.drawBitmap(bitmap, left - bitmap.getWidth() / 2, top
					- bitmap.getHeight() / 2, paintForBitmap);
			// 绘制appName
			canvas.drawText(appName, left, top + bitmap.getHeight() / 2 + 20,
					paintForText);
	}

	/**
	 * 设置透明度
	 * @param sourceImg
	 * @param number
	 * @return
	 */
	public static Bitmap setAlpha(Bitmap sourceImg, int number) {
		
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
				sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
		number = number * 255 / 100;
		for (int i = 0; i < argb.length; i++) {
			argb[i] = (number <<24) | (argb[i] & 0x00FFFFFF);// 修改最高2位的值
		}
		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
				sourceImg.getHeight(), Config.ARGB_8888);
		return sourceImg;
	}

	public class Point {
		public int flag;// 位置标识
		Bitmap bitmap;// 图片
		String appName;
		public String packageName;
		int angle;// 角度
		float x;// x坐标
		float y;// y坐标
		float x_c;// 点与圆心的中心x坐标
		float y_c;// 点与圆心的中心y坐标
		int acrossDegree = 60;// 每个图标活动的范围为60度
		int startDegree;
		boolean isCheck = false;
	}

	private class Shan {
		int startDegree;// 起始角度
		int acrossDegree = 120;// 跨幅
		int flag;// 位置标识
		boolean isCheck;// 是否选中
	}

	public static interface OnTurnplateListener {
		public void onPointTouch(Point point);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if (arg1.getAction() == MotionEvent.ACTION_MOVE) {
			return false;
		}
		return true;
	}

	
}
