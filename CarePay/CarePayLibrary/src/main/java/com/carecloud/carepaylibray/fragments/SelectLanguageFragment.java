package com.carecloud.carepaylibray.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.Constants;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.signinsignup.fragments.activity.SigninSignupActivity;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class SelectLanguageFragment extends Fragment{

    private static final String LOG_TAG = SelectLanguageFragment.class.getSimpleName();
    private KeyboardHolderActivity mActivity;

    @Override
    public void onStart() {
        super.onStart();
        mActivity = (KeyboardHolderActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_select_language, container, false);

        final RadioButton radioEng = (RadioButton) view.findViewById(R.id.radioButton_en);
        final RadioButton radioEsp = (RadioButton) view.findViewById(R.id.radioButton_es);

        if(radioEng != null) {
            radioEng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        radioEsp.setChecked(false);
                        mActivity.setLangId(Constants.LANG_EN);
                    } else {
                        radioEsp.setChecked(true);
                        radioEng.setChecked(false);
                    }
                }
            });
        }

        radioEsp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    radioEng.setChecked(false);
                    mActivity.setLangId(Constants.LANG_ES);
                } else {
                    radioEng.setChecked(true);
                    radioEsp.setChecked(false);
                }
            }
        });

        (view.findViewById(R.id.button_next)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mActivity.replaceFragment(SigninFragment.class);
                Intent intent = new Intent(mActivity, SigninSignupActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });

        return view;
    }
}