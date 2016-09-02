package com.carecloud.carepaylibray.keyboard;

import android.app.Activity;
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
    private Activity   mActivity;
    private EditText   mTargetEditText;
    private MyKeyboard myKeyboard;
    private int        mEditIndex;

    public EditTextWrapper(Activity activity, EditText editText, int editIndex, MyKeyboard keyboard, String text) {
        mActivity = activity;
        mTargetEditText = editText;
        myKeyboard = keyboard;
        mEditIndex = editIndex;

        mTargetEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == ACTION_DOWN) {
                    Log.v(LOG_TAG, "onTouch()");
                    myKeyboard.setTargetEditIndex(mEditIndex);
                    view.requestFocus();
                    ((KeyboardHolder) mActivity).toggleKeyboardVisible(true); // todo test if activity is KeyboardHolder
                }
                return true;
            }
        });

        mTargetEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    Log.v(LOG_TAG, "hasFocus()");
                    mTargetEditText.setSelection(0, mTargetEditText.getText().length());
                } else {
                    mTargetEditText.setSelection(0);
                }
            }
        });
    }
}
