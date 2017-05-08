package com.carecloud.carepaylibray.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.carecloud.carepaylibray.base.BaseActivity;

/**
 * Created by lmenendez on 5/8/17.
 */

public class WebViewKeyboardAdjuster implements KeyboardWatcher.KeyboardStateListener {

    private View targetView;
    private int openOffset;
    private int maxHeight;


    /**
     * Constructor for KB adjuster
     * @param targetView view to adjust when keyboard state changes
     * @param openOffset additional offset for view. The amount of this offset will be hidden under the keyboard when open
     */
    public WebViewKeyboardAdjuster(View targetView, int openOffset ){
        this.targetView = targetView;
        this.openOffset = openOffset;
        this.targetView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    private ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int currentHeight = computeViewHeight();
            if(currentHeight > maxHeight){
                maxHeight = currentHeight;
            }
        }
    };

    private int computeViewHeight() {
        Rect rect = new Rect();
        targetView.getGlobalVisibleRect(rect);
        return (rect.bottom - rect.top);
    }


    @Override
    public void onKeyboardOpened(int kbHeight) {
        ViewGroup.LayoutParams layoutParams = targetView.getLayoutParams();
        layoutParams.height = maxHeight - kbHeight + openOffset;
        targetView.requestLayout();
    }

    @Override
    public void onKeyboardClosed() {
        ViewGroup.LayoutParams layoutParams = targetView.getLayoutParams();
        layoutParams.height = maxHeight;
        targetView.requestLayout();

        Context context = targetView.getContext();
        if(context instanceof BaseActivity){
            ((BaseActivity) context).setNavigationBarVisibility();
        }
    }
}
