package com.carecloud.carepaylibray.keyboard;

/**
 * Created by lsoco_user on 9/4/2016.
 * Tag for the edit texts bound with custom keyboard
 * Helps to navigate and change the edit being edited currently
 */
public class EditTextTag {
    private Class mFragClass;
    private int index;
    private KeyboardBinderHelper keyboardBinder;

    public EditTextTag(Class fragClass, int ind) {
        mFragClass = fragClass;
        index = ind;
    }

    public Class getFragClass() {
        return mFragClass;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public KeyboardBinderHelper getKeyboardBinder() {
        return keyboardBinder;
    }

    public void setKeyboardBinder(KeyboardBinderHelper keyboardBinder) {
        this.keyboardBinder = keyboardBinder;
    }
}