package com.carecloud.carepaylibray.utils;

import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * @author pjohnson on 21/04/17.
 */
public class KeyboardScrollWithWebViewFix {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistFragment() on an Fragment that already has its content view set.

    /**
     *
     * @param fragment the fragment to manage its height
     * @param offset the bottom offset
     */
    public static void assistFragment(Fragment fragment, int offset) {
        new KeyboardScrollWithWebViewFix(fragment, offset);
    }

    private View childOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private KeyboardScrollWithWebViewFix(Fragment fragment, final int offset) {
        if (fragment != null) {
            FrameLayout content = (FrameLayout) fragment.getView().getParent();
            childOfContent = content.getChildAt(0);
            childOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    possiblyResizeChildOfContent(offset);
                }
            });
            frameLayoutParams = (FrameLayout.LayoutParams) childOfContent.getLayoutParams();
        }
    }

    private void possiblyResizeChildOfContent(int offset) {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = childOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference + offset;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard - offset;
            }
            childOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect rect = new Rect();
        childOfContent.getWindowVisibleDisplayFrame(rect);
        return (rect.bottom - rect.top);
    }

}
