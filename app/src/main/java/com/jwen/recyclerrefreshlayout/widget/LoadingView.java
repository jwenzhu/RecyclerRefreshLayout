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

import com.jwen.recyclerrefreshlayout.R;

/**
 * author: Jwen
 * date:2016-10-17.
 */
public class LoadingView extends View {

    private float mStartRadian = Constants.START_RADIAN;
    private float mEndRadian = Constants.END_RADIAN;
    private Paint mPaint;
    private String mLoadingText = getResources().getString(R.string.loading_start);
    private boolean mIsStartLoading = false;//是否开始加载


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                if(mIsStartLoading){
                    invalidate();
                    mHandler.sendEmptyMessageDelayed(0, 5);
                    mStartRadian = mStartRadian%360f + Constants.RATE_ROTATE;
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

        setMeasuredDimension(widthMeasureSpec,Constants.LOADING_DEFAULT_HEIGHT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF rect = new RectF(Constants.RECT_LEFT,Constants.RECT_TOP,
                Constants.RECT_LEFT + Constants.RECT_DEGREE, Constants.RECT_TOP + Constants.RECT_DEGREE);
        canvas.drawArc(rect, mStartRadian, mEndRadian, false, mPaint);

        mPaint.setTextSize(Constants.TEXT_SIZE);
        canvas.drawText(mLoadingText,Constants.TEXT_LEFT,Constants.TEXT_TOP,mPaint);
    }



    public void initView(){
        mLoadingText = getResources().getString(R.string.loading_start);
        mStartRadian = Constants.START_RADIAN;
        mEndRadian = Constants.END_RADIAN;
        invalidate();
    }


    public void startLoading(){
        mIsStartLoading = true;
        mLoadingText =getResources().getString(R.string.loading_loading);
        mHandler.sendEmptyMessage(0);
    }

    public void stopLoading(){
        mIsStartLoading = false;
        mLoadingText =getResources().getString(R.string.loading_finish);
        invalidate();
        mHandler.removeMessages(0);
    }






}
