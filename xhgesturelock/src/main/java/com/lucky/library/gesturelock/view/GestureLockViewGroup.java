package com.lucky.library.gesturelock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.lucky.library.gesturelock.listener.GestureLockVerifyListener;
import com.lucky.library.gesturelock.util.ResultUtil;
import com.lucky.library.gesturelock.R;
import com.lucky.library.gesturelock.listener.GestureLockListener;
import com.lucky.library.gesturelock.listener.GestureLockPathListener;

import java.util.ArrayList;
import java.util.List;


/**
 * 整体包含n*n个GestureLockView,每个GestureLockView间间隔mMarginBetweenLockView，
 * 最外层的GestureLockView与容器存在mMarginBetweenLockView的外边距
 *
 * 假设屏幕宽 mWidth ,每一个GestureLockView 的直径为a， 相邻GestureLockView 之间间距=GestureLockView距离容器的外边距=0.5*a
 *
 * 观察第一行的GestureLockView，
 *
 * a=(2*mWidth)/(3*n+1)
 *
 *
 * @author xhao
 */
public class GestureLockViewGroup extends RelativeLayout {

	private static final String TAG = "GestureLock";
	/**
	 * 保存所有的GestureLockView
	 */
	private GestureLockView[] mGestureLockViews;
	/**
	 * 每个边上的GestureLockView的个数
	 */
	private int mCount = 3;
	/**
	 * 保存用户选中的GestureLockView的id
	 */
	private List<Integer> mResultPath = new ArrayList<Integer>();
	private List<Integer> mChoose = new ArrayList<Integer>();


	private Paint mPaint;

	private int mMarginBetweenLockView = 10;

	private int mGestureLockViewWidth;

	private int mColorCustom = 0xFF69b6fe;//普通状态下颜色
	private int mColorMove = 0xff108ee9;//手指移动上去颜色
	private int mColorError = 0xfff4333c;//错误状态下颜色
	private float mLineCustomSize = 1.0f;//普通状态下线条的size
	private float mLineMoveSize = 2.0f;//手触摸后线条的size


	/**
	 * 宽度
	 */
	private int mWidth;
	/**
	 * 高度
	 */
	private int mHeight;

	private Path mPath;
	/**
	 * 指引线的开始位置x
	 */
	private int mLastPathX;
	/**
	 * 指引线的开始位置y
	 */
	private int mLastPathY;
	/**
	 * 指引下的结束位置
	 */
	private Point mTmpTarget = new Point();


	/**
	 * 回调接口
	 */
	private GestureLockListener mGestureLockListener;

	public GestureLockViewGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}


	public GestureLockViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setWillNotDraw(false);
		/**
		 * 获得所有自定义的参数的值
		 */
		TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GestureLockViewGroup, defStyle, 0);

		int n = array.getIndexCount();

		for (int i = 0; i < n; i++) {
			int attr = array.getIndex(i);
			if (attr == R.styleable.GestureLockViewGroup_color_custom) {
				mColorCustom = array.getColor(attr, mColorCustom);

			} else if (attr == R.styleable.GestureLockViewGroup_color_move) {
				mColorMove = array.getColor(attr, mColorMove);

			} else if (attr == R.styleable.GestureLockViewGroup_color_error) {
				mColorError = array.getColor(attr, mColorError);

			} else if (attr == R.styleable.GestureLockViewGroup_line_custom_size) {
				mLineCustomSize = array.getFloat(attr, mLineCustomSize);

			} else if (attr == R.styleable.GestureLockViewGroup_line_move_size) {
				mLineMoveSize = array.getFloat(attr, mLineMoveSize);

			} else if (attr == R.styleable.GestureLockViewGroup_count) {
				mCount = array.getInt(attr, 3);

			}
		}
		array.recycle();

		// 初始化画笔
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeWidth(mLineMoveSize);
		mPaint.setColor(mColorMove);
		mPath = new Path();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
		mHeight = mWidth = mWidth < mHeight ? mWidth : mHeight;

	}

	private void addView(){
		// 初始化mGestureLockViews
		if (mGestureLockViews == null) {
			mGestureLockViews = new GestureLockView[mCount * mCount];
			// 计算每个GestureLockView的宽度
			mGestureLockViewWidth = (int) (2 * mWidth * 1.0f / (3* mCount + 1));
			//计算每个GestureLockView的间距
			mMarginBetweenLockView = (int) (mGestureLockViewWidth * 0.5);

			for (int i = 0; i < mGestureLockViews.length; i++) {
				//初始化每个GestureLockView
				mGestureLockViews[i] = new GestureLockView(getContext(), mColorCustom, mColorMove, mColorError,mLineCustomSize,mLineMoveSize);
				mGestureLockViews[i].setId(i + 1);
				//设置参数，主要是定位GestureLockView间的位置
				LayoutParams lockerParams = new LayoutParams(mGestureLockViewWidth, mGestureLockViewWidth);
				// 不是每行的第一个，则设置位置为前一个的右边
				if (i % mCount != 0) {
					lockerParams.addRule(RelativeLayout.RIGHT_OF, mGestureLockViews[i - 1].getId());
				}
				// 从第二行开始，设置为上一行同一位置View的下面
				if (i > mCount - 1) {
					lockerParams.addRule(RelativeLayout.BELOW, mGestureLockViews[i - mCount].getId());
				}
				//设置右下左上的边距
				int rightMargin = mMarginBetweenLockView;
				int bottomMargin = mMarginBetweenLockView;
				int leftMagin = 0;
				int topMargin = 0;
				/**
				 * 每个View都有右外边距和底外边距 第一行的有上外边距 第一列的有左外边距
				 */
				if (i >= 0 && i < mCount){// 第一行
					topMargin = 0;
				}
				if (i % mCount == 0){// 第一列
					leftMagin = mMarginBetweenLockView;
				}
				lockerParams.setMargins(leftMagin, topMargin, rightMargin, bottomMargin);
				mGestureLockViews[i].setMode(GestureLockView.Mode.STATUS_NO_FINGER);
				addView(mGestureLockViews[i], lockerParams);
			}
			Log.e(TAG, "mWidth = " + mWidth + " ,  mGestureViewWidth = " + mGestureLockViewWidth + " , mMarginBetweenLockView = " + mMarginBetweenLockView);

		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		addView();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				// 重置
				//reset();
				break;
			case MotionEvent.ACTION_MOVE:
				GestureLockView child = getChildIdByPos(x, y);
				if (child != null) {
					int cId = child.getId();
					if (!mChoose.contains(cId)) {
						mChoose.add(cId);
						child.setMode(GestureLockView.Mode.STATUS_FINGER_ON);
						// 设置指引线的起点
						Log.e(">>>>>>>>>>", "left:" + child.getLeft() + ",right:" + child.getRight() + ",top:" + child.getTop() + ",bottom:" + child.getBottom());
						mLastPathX = child.getLeft() / 2 + child.getRight() / 2;
						mLastPathY = child.getTop() / 2 + child.getBottom() / 2;
						if (mChoose.size() == 1) {// 当前添加为第一个
							mPath.moveTo(mLastPathX, mLastPathY);
						} else {// 非第一个，将两者使用线连上
							mPath.lineTo(mLastPathX, mLastPathY);

						}
					}
				}
				// 指引线的终点
				mTmpTarget.x = x;
				mTmpTarget.y = y;

				break;
			case MotionEvent.ACTION_UP:
				if (mGestureLockListener != null) {
					if (mGestureLockListener instanceof GestureLockVerifyListener) {

						if (compare(mChoose,mResultPath)) {
							((GestureLockVerifyListener) mGestureLockListener).onGestureResult(true);
						}else {
							((GestureLockVerifyListener) mGestureLockListener).onGestureResult(false);
						}

					}else if (mGestureLockListener instanceof GestureLockPathListener){

						((GestureLockPathListener) mGestureLockListener).onGestureResult(ResultUtil.list2string(mChoose));
					}
				}
				// 将终点 设为上一个起点
				mTmpTarget.x = mLastPathX;
				mTmpTarget.y = mLastPathY;

				// 改变子元素的状态为UP
//				changeItemMode();

				// 计算每个元素中箭头需要旋转的角度
				for (int i = 0; i + 1 < mChoose.size(); i++) {
					int childId = mChoose.get(i);
					int nextChildId = mChoose.get(i + 1);
					GestureLockView startChild = (GestureLockView) findViewById(childId);
					GestureLockView nextChild = (GestureLockView) findViewById(nextChildId);
					int dx = nextChild.getLeft() - startChild.getLeft();
					int dy = nextChild.getTop() - startChild.getTop();
					// 计算角度
					int angle = (int) Math.toDegrees(Math.atan2(dy, dx)) + 90;
					startChild.setArrowDegree(angle);
				}
				//手指抬起后 延迟一段时间 重置view
				onDelayReset();
				break;
			default:{
				break;
			}

		}
		invalidate();
		return true;
	}

	/**
	 * 延迟清除内容
	 */
	public  void onDelayReset() {
		this.postDelayed(new Runnable() {
			@Override
			public void run() {
				reset();
				invalidate();
			}
		},1000);
	}

	private void changeItemMode() {
		for (GestureLockView gestureLockView : mGestureLockViews) {
			if (mChoose.contains(gestureLockView.getId())) {
				if(mResultPath.size() != 0 && !compare(mChoose,mResultPath)){
					mPaint.setColor(mColorError);
					gestureLockView.setMode(GestureLockView.Mode.STATUS_FINGER_UP_ERROR);
				}else{
					if(!compare(mChoose,mResultPath)) {
						gestureLockView.setMode(GestureLockView.Mode.STATUS_FINGER_UP_ERROR);
					}else {
						gestureLockView.setMode(GestureLockView.Mode.STATUS_FINGER_UP_SUCCESS);
					}
				}
			}
		}
	}

	public void showSuccess(){
		for (GestureLockView gestureLockView : mGestureLockViews) {
			if (mChoose.contains(gestureLockView.getId())) {
				gestureLockView.setMode(GestureLockView.Mode.STATUS_FINGER_UP_SUCCESS);
			}
		}
	}

	public void showFailure(){
		for (GestureLockView gestureLockView : mGestureLockViews) {
			if (mChoose.contains(gestureLockView.getId())) {
				mPaint.setColor(mColorError);
				gestureLockView.setMode(GestureLockView.Mode.STATUS_FINGER_UP_ERROR);
			}
		}
	}
	/**
	 *
	 * 做一些必要的重置
	 */
	private void reset() {
		mChoose.clear();
		mPaint.setColor(mColorMove);
		mPath.reset();
		for (GestureLockView gestureLockView : mGestureLockViews) {
			gestureLockView.setMode(GestureLockView.Mode.STATUS_NO_FINGER);
			gestureLockView.setArrowDegree(-1);
		}
	}
	/**
	 * 检查用户绘制的手势是否正确
	 * @return
	 */
	public static  boolean compare(List<Integer> a, List<Integer> b) {
		if(a.size() != b.size()){
			return false;
		}

		for(int i=0;i<a.size();i++){
			if(!a.get(i).equals(b.get(i))){
				return false;
			}

		}
		return true;
	}
	/**
	 * 检查当前坐标是否在child中
	 * @param child
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean checkPositionInChild(View child, int x, int y) {
		if (x >= child.getLeft() && x <= child.getRight()&& y >= child.getTop()  && y <= child.getBottom()) {
			return true;
		}
		return false;
	}

	/**
	 * 检查当前触摸点 是否在圆的画线之上
	 * @param child
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean checkPositionInRound(View child, int x, int y){
		if (x == child.getLeft() && x == child.getRight() && y == child.getTop() && y == child.getBottom()) {
			return true;
		}
		return false;
	}

	/**
	 * 通过x,y获得落入的GestureLockView
	 * @param x
	 * @param y
	 * @return
	 */
	private GestureLockView getChildIdByPos(int x, int y) {
		for (GestureLockView gestureLockView : mGestureLockViews) {
			if (checkPositionInChild(gestureLockView, x, y)) {
				return gestureLockView;
			}
		}
		return null;
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		//绘制GestureLockView间的连线
		if (mPath != null) {
			canvas.drawPath(mPath, mPaint);
		}
		//绘制指引线
		if (mChoose.size() > 0) {
			if (mLastPathX != 0 && mLastPathY != 0) {
				canvas.drawLine(mLastPathX, mLastPathY, mTmpTarget.x, mTmpTarget.y, mPaint);
			}
		}

	}
	/**
	 * 设置回调接口
	 * @param listener
	 */
	public void setOnGestureLockListener(GestureLockListener listener) {
		this.mGestureLockListener = listener;
	}

    /**
     * 设置校验密码
	 * @param mPath
	 */
	public void setPassword(String mPath) {
		this.mResultPath = ResultUtil.string2list(mPath);
	}
}
