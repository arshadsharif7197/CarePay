package com.carecloud.carepaylibray.keyboard;

import android.app.Activity;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Binds edit text to a custom keyboard
 */
public class KeyboardBinderHelper {

    private Activity              mActivity;
    private List<EditText>        mEdits;
    private List<EditTextWrapper> mWrappers;
    private MyKeyboard            mKeyboard;

    public KeyboardBinderHelper(Activity activity, MyKeyboard keyboard, List<EditText> edits) {
        mActivity = activity;
        mKeyboard = keyboard;
        mKeyboard.setEdits(edits);
        mEdits = edits;
        mWrappers = new ArrayList<>();
    }

    public void bindEditsToKeyboard() {
        for (int i = 0; i < mEdits.size(); i++) {
            EditText ed = mEdits.get(i);
            mWrappers.add(new EditTextWrapper(mActivity, ed, i, mKeyboard, ed.getText().toString()));
        }
    }
}
