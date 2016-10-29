package com.jwen.recyclerrefreshlayout.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.RelativeLayout;

/**
 * author: Jwen
 * date:2016-10-15.
 */
public class RefreshLayout extends RelativeLayout{


    private int mDefaultWidth;//默认宽度
    private int DEFAULT_DURATION_SPEECH = 100;//默认时间
    private int DEFAULT_DURATION = 1000;//默认关闭时间
    private int mRefreshHeight;//刷新控件高度
    private int mLoadingHeight;//加载控件高度
    private boolean mIsRefreshing = false;//是否刷新
    private boolean mIsLoading = false;//是否加载
    private OverScroller mScroller;
    private boolean isInControl = false;//是否菜单滑动
    private VelocityTracker mVelocityTracker;
    private int mScaledTouchSlop;
    private int mScaledMinimumFlingVelocity;
    private int mScaledMaximumFlingVelocity;


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

        ViewConfiguration viewConfig = ViewConfiguration.get(context);
        mScaledTouchSlop = viewConfig.getScaledTouchSlop();
        mScaledMinimumFlingVelocity = viewConfig.getScaledMinimumFlingVelocity();
        mScaledMaximumFlingVelocity = viewConfig.getScaledMaximumFlingVelocity();

    }

    private RefreshView refreshView;
    private LoadingView loadingView;
    private View contentView;
    private View emptyView;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        if(contentView instanceof ListView){
            ((ListView) contentView).addHeaderView(new View(getContext()));
            ((ListView) contentView).addFooterView(new View(getContext()));
        } else if(contentView instanceof RecyclerView){
            layoutManager= ((RecyclerView)contentView).getLayoutManager();
        }
        refreshView = new RefreshView(getContext());
        this.addView(refreshView);
        loadingView = new LoadingView(getContext());
        this.addView(loadingView);

        emptyView = new EmptyLayout(getContext());
        this.addView(emptyView);
    }



    /**
     * 获取第一条目的位置
     * @return firstItemPosition
     */
    private int getCurrentFirstItemPosition(){
        if(contentView instanceof ListView){
            return ((ListView)contentView).getFirstVisiblePosition();
        }else if(contentView instanceof RecyclerView){
            if(layoutManager == null){
                layoutManager= ((RecyclerView)contentView).getLayoutManager();
            }
            if(layoutManager instanceof LinearLayoutManager){
                return  ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            }else if(layoutManager instanceof GridLayoutManager){
                return  ((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            }
        }
        return 0;
    }

    /**
     * 获取最后条目的位置
     * @return lastItemPosition
     */
    private int getCurrentLastItemPosition(){
       if(contentView instanceof ListView){
            return ((ListView)contentView).getLastVisiblePosition();
       }else if(contentView instanceof RecyclerView){
            if(layoutManager == null){
                layoutManager= ((RecyclerView)contentView).getLayoutManager();
            }
            if(layoutManager instanceof LinearLayoutManager){
                return  ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            }else if(layoutManager instanceof GridLayoutManager){
                return ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            }
       }
        return 0;
    }

    /**
     * 获取条目数目
     * @return count
     */
    private int getCurrentItemCount(){
        if(contentView instanceof ListView){
            return ((ListView)contentView).getCount();
        }else if(contentView instanceof RecyclerView){
            return ((RecyclerView)contentView).getAdapter().getItemCount();
        }
        return 0;
    }


    /**
     * 获取第一条目的位置
     * @return firstItemPosition
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean dispatch = super.dispatchTouchEvent(ev);
        int firstItemPosition = getCurrentFirstItemPosition();
        int lastItemPosition = getCurrentLastItemPosition();
        int count = getCurrentItemCount();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(mIsRefreshing) smoothCloseRefresh(DEFAULT_DURATION_SPEECH);
                if(mIsLoading) smoothCloseLoading(DEFAULT_DURATION_SPEECH);
                break;
            case MotionEvent.ACTION_MOVE:
                if((!isInControl) && (firstItemPosition == 0 || (lastItemPosition == count-1)) ){
                    isInControl = true;
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    MotionEvent ev2 = MotionEvent.obtain(ev);
                    dispatchTouchEvent(ev);
                    ev2.setAction(MotionEvent.ACTION_DOWN);
                    return dispatchTouchEvent(ev2);
                }
                break;
            case MotionEvent.ACTION_UP:
                isInControl = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;

        }
        return dispatch;
    }

    int firstY = 0;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = super.onInterceptTouchEvent(ev);
        float disY;
        int firstItemPosition = getCurrentFirstItemPosition();
        int lastItemPosition = getCurrentLastItemPosition();
        int count = getCurrentItemCount();

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                firstY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                disY = ev.getY() - firstY;
                if((firstItemPosition == 0 && disY > 0) || ((lastItemPosition == count-1) && disY < 0))return true;
                return false;
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
        if(mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.addMovement(event);

        int disY = 0;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                return !(mIsRefreshing || mIsLoading);
            case MotionEvent.ACTION_MOVE:
                startY = firstY;
                disY = (int) (startY - event.getY());
                if((disY < 0 )&& (Math.abs(disY) > 200))
                    refreshView.setCircleRadius(Math.abs(disY)-200);
                scrollTo(0,disY);
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
                int velocityX = (int) mVelocityTracker.getYVelocity();
                int velocity = Math.abs(velocityX);
                int duration = DEFAULT_DURATION;
                if (velocity > mScaledMinimumFlingVelocity)
                    duration = disY > 0 ? getSwipeDuration(event, velocity,mLoadingHeight):getSwipeDuration(event, velocity,mRefreshHeight);

                disY = (int) (startY - event.getY());
                if(disY < 0){
                    if(Math.abs(disY) < mRefreshHeight){
                        smoothCloseRefresh(duration);
                    }else{
                        startRefresh(duration);
                    }
                }else{
                    if(Math.abs(disY) < mLoadingHeight){
                        smoothCloseLoading(duration);
                    }else{
                        startLoading(duration);
                    }
                }
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                break;
        }
        return isTouch;
    }

    /**
     * 获取动画时间
     * @param event
     * @param velocity
     * @return
     */
    private int getSwipeDuration(MotionEvent event, int velocity,int height) {
        int sx = getScrollX();
        int dx = (int) (event.getX() - sx);
        int halfWidth = height / 2;
        float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / height);
        float distance = halfWidth + halfWidth * distanceInfluenceForSnapDuration(distanceRatio);
        int duration;
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float pageDelta = (float) Math.abs(dx) / height;
            duration = (int) ((pageDelta + 1) * 100);
        }
        duration = Math.min(duration, DEFAULT_DURATION);
        return duration;
    }

    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    /**
     * 关闭刷新
     */
    private void smoothCloseRefresh(int duration) {
        mIsRefreshing = false;
        int scrollY = getScrollY();
        mScroller.startScroll(0, scrollY,0,  -scrollY, DEFAULT_DURATION);
        invalidate();
    }

    /**
     * 刷新方法
     */
    private void startRefresh(int duration) {
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
        smoothCloseRefresh(DEFAULT_DURATION);
        refreshView.stopRefresh();
    }

    /**
     * 关闭加载
     */
    private void smoothCloseLoading(int duration) {
        mIsLoading = false;
        int scrollY =  getScrollY();
        mScroller.startScroll(0, scrollY,0, -scrollY, DEFAULT_DURATION);
        invalidate();
    }

    /**
     * 加载更多
     */
    public void startLoading(int duration){
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
        smoothCloseLoading(DEFAULT_DURATION);
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
        if(getCurrentItemCount() == 0){
            int emptyHeight = emptyView.getMeasuredHeightAndState();
            int emptyWidth = emptyView.getMeasuredWidthAndState();
            emptyView.layout(0,0,emptyWidth,emptyHeight);
        }else{
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
}
