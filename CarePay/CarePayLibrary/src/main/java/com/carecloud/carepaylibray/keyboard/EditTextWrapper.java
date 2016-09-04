package com.carecloud.carepaylibray.keyboard;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private boolean    touched;

    public EditTextWrapper(Activity activity, EditText editText, int editIndex, MyKeyboard keyboard) {
        mActivity = activity;
        mTargetEditText = editText;
        mTargetEditText.setFocusableInTouchMode(false);
        myKeyboard = keyboard;
        mEditIndex = editIndex;

        mTargetEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                touched = true;
                if (motionEvent.getAction() == ACTION_DOWN) { // action down to execute the block only once
                    Log.v(LOG_TAG, "onTouch()");
                    // toggle selection of text
                    int startSel = mTargetEditText.getSelectionStart();
                    int endSel = mTargetEditText.getSelectionEnd();
                    Log.v(LOG_TAG, "selection: " + startSel + " -> " + endSel);
                    if (endSel - startSel <= 0) { // if no selection, select
                        mTargetEditText.setSelection(0, mTargetEditText.getText().length());
                    } else { // if selection, remove
                        mTargetEditText.setSelection(endSel, endSel);
                    }

                    myKeyboard.setTargetEditIndex(mEditIndex);
                    mTargetEditText.setFocusableInTouchMode(true);
                    ((KeyboardHolder) mActivity).toggleKeyboardVisible(true);
                    view.requestFocus();
                }
                return true;
            }
        });

        mTargetEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Log.v(LOG_TAG, "hasFocus()");
//                    if (touched) {
//                    } else {
//                        mTargetEditText.setSelection(0, mTargetEditText.getText().length());
//                    }
                } else {
                    Log.v(LOG_TAG, "lostFocus()");
                    mTargetEditText.setSelection(mTargetEditText.getText().length(), mTargetEditText.getText().length());
                    mTargetEditText.setFocusableInTouchMode(false);
                    touched = false; // reset
                }
            }
        });
    }

    private void hideSystemkeyboard(View view) {
//        Log.v(LOG_TAG, "system keyboard hide");
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
    }
}