package com.carecloud.carepaylibray.utils;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lmenendez on 5/8/17.
 */

public class KeyboardWatcher implements ViewTreeObserver.OnGlobalLayoutListener {

    public interface KeyboardStateListener{
        void onKeyboardOpened(int kbHeight);

        void onKeyboardClosed();
    }

    private static String TAG = KeyboardWatcher.class.getName();

    private View rootView;
    private boolean opened;
    private List<KeyboardStateListener> keyboardStateListeners = new LinkedList<>();

    /**
     * Constructor for basic Keyboard Watcher with no additional Arguments will init with KB displayed false and no listeners
     * @param rootView root view of Activity to measure for height change when KB displayed
     */
    public KeyboardWatcher(View rootView){
        this(rootView, false);
    }

    /**
     * Constructor with option to set current KB state
     * @param rootView root view of Activity to measure for height change when KB displayed
     * @param opened true if KB is currently opened, will affect behavior if this is set incorrectly
     */
    public KeyboardWatcher(View rootView, boolean opened){
        this(rootView, opened, null);
    }

    /**
     * Constuctor with Callback to handle keyboard visibility changes
     * @param rootView root view of Activity to measure for height change when KB displayed
     * @param opened true if KB is currently opened, will affect behavior if this is set incorrectly
     * @param listener callback for changes in KB visibility
     */
    public KeyboardWatcher(View rootView, boolean opened, KeyboardStateListener listener){
        this.rootView = rootView;
        this.opened = opened;
        this.rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        if(listener!=null){
            keyboardStateListeners.add(listener);
        }
    }

    @Override
    public void onGlobalLayout() {
        final Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);

        final int visibleHeight = (rect.bottom - rect.top);
        final int actualHeight = rootView.getRootView().getHeight();
        final int heightDiff = actualHeight - visibleHeight;

        Log.d(TAG, "RootView VisibleHeight: "+visibleHeight);
        Log.d(TAG, "RootView ActualHeight: "+actualHeight);
        Log.d(TAG, "Visible Height Diff: "+heightDiff);

        if(!isOpened() && heightDiff > visibleHeight/4){
            setOpened(true);
            notifyKeyboardStateChanged(heightDiff);

            Log.d(TAG, "Keyboard visible");
        }else if(isOpened() && heightDiff < visibleHeight/4){
            setOpened(false);
            notifyKeyboardStateChanged(0);

            Log.d(TAG, "Keyboard gone");
        }
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    private void notifyKeyboardStateChanged(int height){
        for(KeyboardStateListener listener : keyboardStateListeners){
            if(isOpened()){
                listener.onKeyboardOpened(height);
            }else{
                listener.onKeyboardClosed();
            }
        }
    }

}
