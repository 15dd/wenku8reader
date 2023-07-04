package com.cyh128.wenku8reader.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.widget.NestedScrollView;

public class MyNestedScrollView extends NestedScrollView {

    View.OnTouchListener mGestureListener;
    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    public MyNestedScrollView(Context context) {
        super(context);
    }

    public MyNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                if (xDistance > yDistance) {
                    return false;
                }
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return false;
    }

    @Override //https://blog.csdn.net/u011682673/article/details/109307894
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        child.measure(parentWidthMeasureSpec, parentHeightMeasureSpec);
    }

}
