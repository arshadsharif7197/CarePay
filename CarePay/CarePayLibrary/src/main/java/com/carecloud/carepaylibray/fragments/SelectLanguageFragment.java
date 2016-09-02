package com.carecloud.carepaylibray.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class SelectLanguageFragment extends Fragment{

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
        View view = inflater.inflate(R.layout.fragment_select_language, container, false);
        (view.findViewById(R.id.button_next)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 9/2/2016 go to next fragment (SignIN)
                mActivity.replaceFragment(SignInFragment.class);
            }
        });

        return view;
    }
}