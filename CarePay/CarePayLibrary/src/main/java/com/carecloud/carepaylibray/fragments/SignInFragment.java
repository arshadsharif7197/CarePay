package com.carecloud.carepaylibray.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.GenericEditsFragment;


/**
 * Created by lsoco_user on 9/2/2016.
 * Sign in fragment
 */
public class SignInFragment extends GenericEditsFragment {

    private static final String LOG_TAG = SignInFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        view.findViewById(R.id.signin_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.replaceFragment(HomeFragment.class);
            }
        });

        view.findViewById(R.id.signin_new_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.replaceFragment(SignUpFragment.class);
            }
        });

        addEditForBindingWithKeyboard((EditText) view.findViewById(R.id.signin_edit_name));
        addEditForBindingWithKeyboard((EditText) view.findViewById(R.id.signin_edit_password));

        return view;
    }
}