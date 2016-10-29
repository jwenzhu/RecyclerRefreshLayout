package com.jwen.recyclerrefreshlayout.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwen.recyclerrefreshlayout.R;

/**
 * author: Jwen
 * date:2016-10-27.
 */
public class EmptyLayout extends LinearLayout {
    public EmptyLayout(Context context) {
        this(context,null);
    }

    public EmptyLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EmptyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(Gravity.CENTER);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.mipmap.icon_refreshing);
        LayoutParams imageLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        imageLayoutParams.gravity = Gravity.CENTER;
        this.addView(imageView,imageLayoutParams);

        TextView textView = new TextView(getContext());
        textView.setText("暂无列表");
        LayoutParams textLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        textLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        this.addView(textView,textLayoutParams);

        Button button = new Button(getContext());
        button.setText("点击重试");
        LayoutParams buttonLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        this.addView(button,buttonLayoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = 0;
        int width = 0;
        int count = getChildCount();
        for (int i=0;i<count;i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int childHeight = child.getMeasuredHeight();
            int childWidth = child.getMeasuredWidth();
            height += childHeight;
            width = Math.max(childWidth, width);
        }
        setMeasuredDimension((measureWidthMode == MeasureSpec.EXACTLY) ? measureWidth: width, (measureHeightMode == MeasureSpec.EXACTLY) ? measureHeight: height);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
