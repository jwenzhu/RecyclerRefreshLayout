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

    private static int TOTAL_PART = 100;
    private static float SCALE_RADIAN = 360f/TOTAL_PART;
    private int mScreenWidth;
    private float mRadian = 0f;//弧度
    private String mRefreshText = "下拉刷新...";
    private boolean mIsOpen =false;
    private boolean mIsStartRefresh = false;

    private float mStartRadian = -90f;
    private float mEndRadian = 340f;

    private float mBottomDistance = 100f;//文字到底部的距离

    private Bitmap mBitmap;
    private Paint mPaint;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                if(mIsStartRefresh){
                    invalidate();
                    mHandler.sendEmptyMessageDelayed(0, 5);
                    mStartRadian = mStartRadian%360 + 10;
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
            setMeasuredDimension(widthMeasureSpec,mBitmap.getHeight());
        }else{
            setMeasuredDimension(widthMeasureSpec,300);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(mBitmap, null, new Rect(0, 0,mScreenWidth,mScreenWidth*mBitmap.getHeight()/mBitmap.getWidth()), mPaint);
        RectF rect = new RectF(200, 180, 280, 260);


        if(mIsStartRefresh){
            canvas.drawArc(rect, mStartRadian, mEndRadian, false, mPaint);
        }else{
            canvas.drawArc(rect, mStartRadian, mRadian, false, mPaint);
        }

        if(!mIsOpen){
            canvas.drawLine(240,200,240,240,mPaint);
            Path path = new Path();
            path.moveTo(220,220);
            path.lineTo(240,240);
            path.lineTo(260,220);
            canvas.drawPath(path,mPaint);
        }

        mPaint.setTextSize(30);
        canvas.drawText(mRefreshText,300,230,mPaint);
    }

    public void setCircleRadius(int radius){
        if(radius >= TOTAL_PART){
            radius = TOTAL_PART;
            mRefreshText = "释放刷新...";
            mIsOpen = true;
        }else {
            mRefreshText = "下拉刷新...";
            mIsOpen = false;
        }
        mRadian = radius*SCALE_RADIAN;
        invalidate();
    }


    public void startRefresh(){
        mIsStartRefresh = true;
        mRefreshText = "加载中...";
        mHandler.sendEmptyMessage(0);
    }

    public void stopRefresh(){
        mIsStartRefresh = false;
        mRefreshText = "加载完成";
        mStartRadian = -90f;
        mEndRadian = 340f;
        invalidate();
        mHandler.removeMessages(0);
    }




    public  void setTextColor(int color){
        mPaint.setColor(color);
        invalidate();
    }

    public void setBitmapResource(Bitmap bitmap){
        mBitmap = bitmap;
        invalidate();
    }
}
