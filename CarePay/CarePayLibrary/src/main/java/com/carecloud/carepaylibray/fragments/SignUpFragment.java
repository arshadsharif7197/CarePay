package com.carecloud.carepaylibray.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class SignUpFragment extends Fragment {

    private static final String LOG_TAG = SignUpFragment.class.getSimpleName();
    private KeyboardHolderActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (KeyboardHolderActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        view.findViewById(R.id.signup_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.replaceFragment(HomeFragment.class);
            }
        });

        List<EditText> edits = new ArrayList<>();
        edits.add((EditText) view.findViewById(R.id.signup_name));

        // bind the keyboard to the edits
        mActivity.bindKeyboardToEdits(edits);

        return view;
    }
}
