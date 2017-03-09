package com.carecloud.carepaylibray.keyboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;

import com.carecloud.carepaylibray.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsoco_user on 9/4/2016.
 * Base class for the fragments containing edit texts that are to be used with the custom keyboard
 */
public class GenericEditsFragment extends BaseFragment {

    private static final String LOG_TAG = GenericEditsFragment.class.getSimpleName();
    protected KeyboardHolderActivity mActivity;
    protected List<EditText> mEdits;
    private int index;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        index = 0;
        mEdits = new ArrayList<>();
        mActivity = (KeyboardHolderActivity) getActivity();
    }

    @Override
    public void onStart() {
        Log.v(LOG_TAG, "onStart()");
        super.onStart();
        mActivity.bindKeyboardToEdits(mEdits);
    }

    /**
     * Adds a edit text to the list of edit text to be bound to the custom keyboard
     * @param editText The edit text
     */
    protected void addEditForBindingWithKeyboard(EditText editText) {
        editText.setTag(new EditTextTag(this.getClass(), index++));
        mEdits.add(editText);
    }
}