package com.jwen.recyclerrefreshlayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * author: Jwen
 * date:2016-10-15.
 */
public class RefreshLayout extends LinearLayout{


    private int mDefaultWidth;
    private static int DEFAULT_DURATION = 1000;
    private int mRefreshHeight;
    private int mLoadingHeight;
    private boolean mIsRefreshing = false;
    private boolean mIsLoading = false;
    private OverScroller mScroller;

    private OnRefreshListener mOnRefreshListener;
    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
        this.mOnRefreshListener = onRefreshListener;
    }

    public RefreshLayout(Context context) {
        this(context,null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScroller = new OverScroller(context);
    }

    RefreshView refreshView;
    LoadingView loadingView;
    View contentView;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        refreshView = new RefreshView(getContext());
        this.addView(refreshView);
        loadingView = new LoadingView(getContext());
        this.addView(loadingView);
    }


    private int startY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isTouch = super.onTouchEvent(event);

        int disY = 0;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                return !mIsRefreshing;
            case MotionEvent.ACTION_MOVE:
                disY = (int) (startY - event.getY());
                if(disY < 0){
                    if(Math.abs(disY) > 200){
                        refreshView.setCircleRadius(Math.abs(disY)-200);
                    }
                }
                scrollTo(0,disY);
                break;
            case MotionEvent.ACTION_UP:
                disY = (int) (startY - event.getY());
                if(disY < 0){
                    if(Math.abs(disY) < mRefreshHeight){
                        smoothCloseRefresh();
                    }else{
                        startRefresh();
                    }
                }else{
                    startLoading();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                break;
        }
        return isTouch;
    }

    private void smoothCloseRefresh() {
        mIsRefreshing = false;
        int scrollY = getScrollY();
        mScroller.startScroll(0, scrollY,0,  -scrollY, DEFAULT_DURATION);
        invalidate();
    }

    /**
     * 刷新方法
     */
    private void startRefresh() {
        if(mOnRefreshListener != null){
            mOnRefreshListener.onRefresh();
        }
        mIsRefreshing = true;
        refreshView.startRefresh();
        int scrollY =  Math.abs( getScrollY());
        if(scrollY > mRefreshHeight){
            mScroller.startScroll(0, scrollY,0,  -(scrollY - mRefreshHeight), DEFAULT_DURATION);
        }
        invalidate();
    }

    /**
     * 停止刷新
     */
    public void stopRefresh(){
        smoothCloseRefresh();
        refreshView.stopRefresh();
    }


    /**
     * 加载更多
     */
    public void startLoading(){
        mIsLoading = true;
        loadingView.startLoading();
        int scrollY =  Math.abs( getScrollY());
        if(scrollY > mLoadingHeight){
            mScroller.startScroll(0, -scrollY,0,  scrollY - mLoadingHeight, DEFAULT_DURATION);
        }
        invalidate();
    }

    /**
     * 停止加载
     */
    public void stopLoading(){
        smoothCloseLoading();
        loadingView.stopLoading();
    }

    private void smoothCloseLoading() {
        mIsLoading = false;
        int scrollY = getScrollY();
        mScroller.startScroll(0, scrollY,0,  -scrollY, DEFAULT_DURATION);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currY = mScroller.getCurrY();
            if(currY == 0){
                refreshView.setCircleRadius(0);
            }
            /**
             * BUG
             */
            scrollTo(0,-Math.abs(currY));
            invalidate();
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mDefaultWidth = contentView.getMeasuredWidthAndState();
        int contentViewHeight = contentView.getMeasuredHeightAndState();
        contentView.layout(0,0,mDefaultWidth,contentViewHeight);

        int refreshViewWidth = refreshView.getMeasuredWidthAndState();
        mRefreshHeight = refreshView.getMeasuredHeightAndState();
        refreshView.layout(0,-mRefreshHeight,refreshViewWidth,0);

        int loadingViewWidth = loadingView.getMeasuredWidthAndState();
        mLoadingHeight = loadingView.getMeasuredHeightAndState();
        loadingView.layout(0,contentViewHeight,loadingViewWidth,mLoadingHeight + contentViewHeight);
    }
}
