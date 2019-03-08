package com.example.qd.douyinwu.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.qd.douyinwu.interfaces.OnViewPagerListener;

public class PagerLayoutManager extends LinearLayoutManager {
    private PagerSnapHelper mPagerSnapHelper;
    private OnViewPagerListener mOnViewPagerListener;
    private RecyclerView mRecyclerView;
    private int mDrift;//位移，用来判断移动方向

    public PagerLayoutManager(Context context, int orientation) {
        super(context, orientation, false);
        init();
    }

    public PagerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    private void init() {
        mPagerSnapHelper = new PagerSnapHelper();
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mPagerSnapHelper.attachToRecyclerView(view);
        this.mRecyclerView = view;
        mRecyclerView.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }

    /**
     * 滑动状态的改变
     * 缓慢拖拽-> SCROLL_STATE_DRAGGING
     * 快速滚动-> SCROLL_STATE_SETTLING
     * 空闲状态-> SCROLL_STATE_IDLE
     *
     * @param state
     */
    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
                Log.e("=====", "滑动监听");
                View viewIdle = mPagerSnapHelper.findSnapView(this);
                if (viewIdle != null) {
                    int positionIdle = getPosition(viewIdle);
                    if (mOnViewPagerListener != null && getChildCount() == 1) {
                        mOnViewPagerListener.onPageSelected(positionIdle, positionIdle == getItemCount() - 1, viewIdle);
                    }
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                Log.e("=====", "滑动监听111");
                View viewDrag = mPagerSnapHelper.findSnapView(this);
                if (viewDrag != null) {
                    int positionDrag = getPosition(viewDrag);
                }
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                Log.e("=====", "滑动监听222");
                View viewSettling = mPagerSnapHelper.findSnapView(this);
                if (viewSettling != null) {
                    int positionSettling = getPosition(viewSettling);
                }
                break;
        }
    }

    public void setPosition() {
        View viewIdle = mPagerSnapHelper.findSnapView(this);
        if (viewIdle != null) {
            int positionIdle = getPosition(viewIdle);
            if (mOnViewPagerListener != null && getChildCount() == 1) {
                mOnViewPagerListener.onPageSelected(positionIdle, positionIdle == getItemCount() - 1, viewIdle);
            }
        }
    }

    /**
     * 监听竖直方向的相对偏移量
     *
     * @param dy
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    /**
     * 监听水平方向的相对偏移量
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dx;
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnViewPagerListener(OnViewPagerListener listener) {
        this.mOnViewPagerListener = listener;
    }

    private RecyclerView.OnChildAttachStateChangeListener mChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
        /**
         * itemView依赖Window
         */
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (mOnViewPagerListener != null && getChildCount() == 1) {
                mOnViewPagerListener.onInitComplete(view);
            }
        }

        /**
         *itemView脱离Window
         */
        @Override
        public void onChildViewDetachedFromWindow(View view) {
            if (mDrift >= 0) {
                if (mOnViewPagerListener != null)
                    mOnViewPagerListener.onPageRelease(true, getPosition(view), view);
            } else {
                if (mOnViewPagerListener != null)
                    mOnViewPagerListener.onPageRelease(false, getPosition(view), view);
            }

        }
    };
}
