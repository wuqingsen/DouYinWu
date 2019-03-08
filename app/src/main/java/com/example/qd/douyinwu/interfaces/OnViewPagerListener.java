package com.example.qd.douyinwu.interfaces;

import android.view.View;

public interface OnViewPagerListener {

    void onInitComplete(View view);

    void onPageRelease(boolean isNext, int position, View view);

    void onPageSelected(int position, boolean isBottom, View view);
}