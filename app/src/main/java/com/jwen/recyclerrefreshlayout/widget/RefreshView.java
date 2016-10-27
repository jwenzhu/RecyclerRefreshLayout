package com.jwen.recyclerrefreshlayout.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import com.jwen.recyclerrefreshlayout.R;


/**
 * author: Jwen
 * date:2016-10-17.
 */
public class RefreshView extends View{

    private static int TOTAL_PART = 100;//总份数
    private static float SCALE_RADIAN = 360f/TOTAL_PART;//每份的角度
    private int mScreenWidth;//屏幕的宽度
    private int mViewHeight;//高度
    private float mRadian = 0f;//弧度
    private String mRefreshText = getResources().getString(R.string.refresh_start);
    private boolean mIsOpen =false;
    private boolean mIsStartRefresh = false;

    private float mStartRadian =Constants.START_RADIAN;
    private float mEndRadian =Constants.END_RADIAN;

    private float mBottomDistance = 40f;//文字到底部的距离
    private float mTextBottomDistance = mBottomDistance + Constants.RECT_DEGREE/4;//文字到底部的距离

    private Bitmap mBitmap;
    private Paint mPaint;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                if(mIsStartRefresh){
                    invalidate();
                    mHandler.sendEmptyMessageDelayed(0, 5);
                    mStartRadian = mStartRadian%360f + Constants.RATE_ROTATE;
                }
            }
        }
    };


    public RefreshView(Context context) {
        this(context,null);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScreenWidth = Utils.getScreenWidth(context);
        if(mBitmap == null){
            mBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_refreshing);
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);  //设置画笔颜色
        mPaint.setStyle(Paint.Style.STROKE);//填充样式改为描边
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mBitmap == null){
            mViewHeight = mBitmap.getHeight();
        }else{
            mViewHeight = 300;
        }
        setMeasuredDimension(widthMeasureSpec,mViewHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(mBitmap, null, new Rect(0, 0,mScreenWidth,mScreenWidth*mBitmap.getHeight()/mBitmap.getWidth()), mPaint);
        RectF rect = new RectF(Constants.RECT_LEFT, mViewHeight - mBottomDistance - Constants.RECT_DEGREE,
                Constants.RECT_LEFT + Constants.RECT_DEGREE, mViewHeight - mBottomDistance);


        if(mIsStartRefresh){
            canvas.drawArc(rect, mStartRadian, mEndRadian, false, mPaint);
        }else{
            canvas.drawArc(rect, mStartRadian, mRadian, false, mPaint);
        }

        if(!mIsOpen){
            canvas.drawLine(Constants.RECT_LEFT + Constants.RECT_DEGREE/2,mViewHeight - mBottomDistance - Constants.RECT_DEGREE*3/4,
                    Constants.RECT_LEFT + Constants.RECT_DEGREE/2,mViewHeight - mBottomDistance - Constants.RECT_DEGREE/4,mPaint);
            Path path = new Path();
            path.moveTo(Constants.RECT_LEFT + Constants.RECT_DEGREE/4,mViewHeight - mBottomDistance - Constants.RECT_DEGREE/2);
            path.lineTo(Constants.RECT_LEFT + Constants.RECT_DEGREE/2,mViewHeight - mBottomDistance - Constants.RECT_DEGREE/4);
            path.lineTo(Constants.RECT_LEFT + Constants.RECT_DEGREE*3/4,mViewHeight - mBottomDistance - Constants.RECT_DEGREE/2);
            canvas.drawPath(path,mPaint);
        }

        mPaint.setTextSize(Constants.TEXT_SIZE);
        canvas.drawText(mRefreshText,Constants.TEXT_LEFT,mViewHeight - mTextBottomDistance,mPaint);
    }

    /**
     * 设置弧度
     * @param radius
     */

    public void setCircleRadius(int radius){
        if(radius >= TOTAL_PART){
            radius = TOTAL_PART;
            mRefreshText = getResources().getString(R.string.refresh_stop);
            mIsOpen = true;
        }else {
            mRefreshText = getResources().getString(R.string.refresh_start);
            mIsOpen = false;
        }
        mRadian = radius*SCALE_RADIAN;
        invalidate();
    }

    /**
     * 开始刷新
     */
    public void startRefresh(){
        mIsStartRefresh = true;
        mRefreshText = getResources().getString(R.string.refresh_loading);
        mHandler.sendEmptyMessage(0);
    }

    /**
     * 停止刷新
     */
    public void stopRefresh(){
        mIsStartRefresh = false;
        mRefreshText = getResources().getString(R.string.refresh_finish);
        mStartRadian = Constants.START_RADIAN;
        mEndRadian = Constants.END_RADIAN;
        invalidate();
        mHandler.removeMessages(0);
    }


    /**
     * 设置颜色
     * @param color
     */
    public  void setTextColor(int color){
        mPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置背景
     * @param bitmap
     */
    public void setBitmapResource(Bitmap bitmap){
        mBitmap = bitmap;
        invalidate();
    }
}
