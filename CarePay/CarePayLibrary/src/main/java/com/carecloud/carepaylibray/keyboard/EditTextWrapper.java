package com.carecloud.carepaylibray.keyboard;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import static android.view.MotionEvent.ACTION_DOWN;

/**
 * Wrapper for an EditText that is used with MyKeyboard
 */
public class EditTextWrapper {

    private static final String LOG_TAG = EditTextWrapper.class.getSimpleName();
    private KeyboardHolderActivity mActivity;
    private EditText               mTargetEditText;
    private KeyboardBinderHelper   mKeyboardBinder;
    private int                    mEditIndex; // TODO: 9/4/2016 get rid of

    public EditTextWrapper(KeyboardHolderActivity activity, EditText editText,
                           KeyboardBinderHelper keyboardBinderHelper,
                           int editIndex) {
        mActivity = activity;
        mTargetEditText = editText;
        mTargetEditText.setFocusableInTouchMode(false);
        mKeyboardBinder = keyboardBinderHelper;
        mEditIndex = editIndex;

        mTargetEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == ACTION_DOWN) { // action down to execute the block only once
                    Log.v(LOG_TAG, "onTouch()");

                    // toggle selection of text
                    int startSel = mTargetEditText.getSelectionStart();
                    int endSel = mTargetEditText.getSelectionEnd();
                    if (endSel - startSel <= 0) { // if no selection, select
                        mTargetEditText.setSelection(0, mTargetEditText.getText().length());
                    } else { // if selection, remove
                        mTargetEditText.setSelection(endSel, endSel);
                    }
                    mKeyboardBinder.setCurrentlyActiveEditIndexInKeyb(mEditIndex);
                    mTargetEditText.setFocusableInTouchMode(true);

                    mActivity.toggleKeyboardVisible(true);
                    view.requestFocus();
                }
                return true;
            }
        });

        mTargetEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Log.v(LOG_TAG, "lostFocus()");
                    mTargetEditText.setSelection(mTargetEditText.getText().length(), mTargetEditText.getText().length());
                    mTargetEditText.setFocusableInTouchMode(false);
                }
            }
        });
    }
}