package com.carecloud.carepaylibray.keyboard;

import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;

/**
 * Supporting fragment for MyKeyboard
 */
public class MyKeyboardFragment extends Fragment {

    // hash map with references to all mEdits
    private MyKeyboard mKeyboard; // the keyboard
    private int mLangId = Constants.LANG_EN; // keyboard language id; en by default
    private KeyboardView view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLangId = ((KeyboardHolder)getActivity()).getLangId();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = (KeyboardView) inflater.inflate(R.layout.keyboard, container, false);
        createKeyboard(mLangId);
        return view;
    }

    public MyKeyboard getKeyboard() {
        return mKeyboard;
    }

    public void createKeyboard(int langId) {
        mLangId = langId;
        mKeyboard = new MyKeyboard(getActivity(), view, mLangId);
    }
}