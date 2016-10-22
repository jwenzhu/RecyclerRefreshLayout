package com.jwen.recyclerrefreshlayout.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


    private OnLoadingListener mOnLoadingListener;
    public void setOnLoadingListener(OnLoadingListener onLoadingListener){
        this.mOnLoadingListener = onLoadingListener;
    }

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
    RecyclerView contentView;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = (RecyclerView) getChildAt(0);
        refreshView = new RefreshView(getContext());
        this.addView(refreshView);
        loadingView = new LoadingView(getContext());
        this.addView(loadingView);
    }

    int firstY = 0;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = super.onInterceptTouchEvent(ev);
        float disY;

        int firstItemPosition = 0;
        int lastItemPosition = 0;
        RecyclerView.LayoutManager layoutManager = contentView.getLayoutManager();
        int count = contentView.getAdapter().getItemCount();
        if(layoutManager instanceof LinearLayoutManager){
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            //获取第一个可见view的位置
            firstItemPosition = linearManager.findFirstVisibleItemPosition();
            //获取最后一个可见view的位置
            lastItemPosition = linearManager.findLastVisibleItemPosition();
        }
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                firstY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                disY = ev.getY() - firstY;
                if(firstItemPosition == 0 && disY > 0){
                    return true;
                } else if((lastItemPosition == count-1) && disY < 0){
                    return true;
                }else{
                    return false;
                }
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;

        }
        return isIntercept;
    }

    private int startY = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isTouch = super.onTouchEvent(event);

        int disY = 0;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                if(mIsRefreshing || mIsLoading){
                    return false;
                }else {
                    return true;
                }
            case MotionEvent.ACTION_MOVE:
                startY = firstY;
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
                    if(Math.abs(disY) < mLoadingHeight){
                        smoothCloseLoading();
                    }else{
                        startLoading();
                    }
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
        int scrollY =  getScrollY();
        if(Math.abs(scrollY) > mRefreshHeight){
            mScroller.startScroll(0, scrollY,0,  Math.abs(scrollY) - mRefreshHeight, DEFAULT_DURATION);
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


    private void smoothCloseLoading() {
        mIsLoading = false;
        int scrollY =  getScrollY();
        mScroller.startScroll(0, scrollY,0, -scrollY, DEFAULT_DURATION);
        invalidate();
    }

    /**
     * 加载更多
     */
    public void startLoading(){
        if(mOnLoadingListener != null){
            mOnLoadingListener.onLoading();
        }
        mIsLoading = true;
        loadingView.startLoading();
        int scrollY =  getScrollY();
        if( Math.abs(scrollY) > mLoadingHeight){
            mScroller.startScroll(0, scrollY,0, mLoadingHeight - Math.abs(scrollY), DEFAULT_DURATION);
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

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int currY = mScroller.getCurrY();
            if(currY == 0){
                refreshView.setCircleRadius(0);
                loadingView.initView();
            }
            scrollTo(0,currY);
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
