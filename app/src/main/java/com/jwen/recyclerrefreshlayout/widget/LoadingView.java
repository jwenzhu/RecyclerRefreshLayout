package com.jwen.recyclerrefreshlayout.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * author: Jwen
 * date:2016-10-17.
 */
public class LoadingView extends View {

    private float mStartRadian = -90f;
    private float mEndRadian = 340f;
    private Paint mPaint;
    private String mLoadingText = "加载中，请稍后...";
    private boolean mIsStartLoading = false;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                if(mIsStartLoading){
                    invalidate();
                    mHandler.sendEmptyMessageDelayed(0, 5);
                    mStartRadian = mStartRadian%360 + 10;
                }
            }
        }
    };


    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);  //设置画笔颜色
        mPaint.setStyle(Paint.Style.STROKE);//填充样式改为描边
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(widthMeasureSpec,120);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF rect = new RectF(200, 20, 280, 100);
        canvas.drawArc(rect, mStartRadian, mEndRadian, false, mPaint);

        mPaint.setTextSize(30);
        canvas.drawText(mLoadingText,300,70,mPaint);
    }


    public void startLoading(){
        mIsStartLoading = true;
        mLoadingText = "加载中，请稍后...";
        mHandler.sendEmptyMessage(0);
    }

    public void stopLoading(){
        mIsStartLoading = false;
        mLoadingText = "加载完成";
        mStartRadian = -90f;
        mEndRadian = 340f;
        invalidate();
        mHandler.removeMessages(0);
    }



}
