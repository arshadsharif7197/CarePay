package com.carecloud.carepaylibray.keyboard;

import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;

/**
 * Binds edit text to a custom keyboard
 */
public class KeyboardBinderHelper {

    private KeyboardHolderActivity              mActivity;
    private List<EditText>        mEdits;
    private List<EditTextWrapper> mWrappers;
    private MyKeyboard            mKeyboard;

    public KeyboardBinderHelper(KeyboardHolderActivity activity, MyKeyboard keyboard, List<EditText> edits) {
        mActivity = activity;
        mKeyboard = keyboard;
        mKeyboard.setEdits(edits);
        mEdits = edits;
        mWrappers = new ArrayList<>();
    }

    public void bindEditsToKeyboard() {
        for (int i = 0; i < mEdits.size(); i++) {
            EditText ed = mEdits.get(i);
            mWrappers.add(new EditTextWrapper(mActivity, ed, this, i));
            setCurrentlyActiveEditIndexInKeyb(i);
        }
    }

    public void setCurrentlyActiveEditIndexInKeyb(int index) {
        mKeyboard.setTargetEditIndex(index);
    }

    // keep the index
    // keep a map
}
