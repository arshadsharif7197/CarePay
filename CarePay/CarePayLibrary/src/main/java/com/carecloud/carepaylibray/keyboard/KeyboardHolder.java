package com.carecloud.carepaylibray.keyboard;

import android.widget.EditText;

import java.util.List;

/**
 * Created by lsoco_user on 8/31/2016.
 */
public interface KeyboardHolder {
    void toggleKeyboardVisible(boolean visible);
    void bindKeyboardToEdits(List<EditText> edits, int langId);
    boolean isKeyboardVisible();
    int getLangId();
    void setLangId(int id);
}
