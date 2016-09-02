package com.carecloud.carepaylibray.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.activities.DemographicsActivity;
import com.carecloud.carepaylibray.activities.SignatureActivity;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class HomeFragment extends Fragment {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.home_button_demogr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DemographicsActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.home_button_signature).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignatureActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.home_button_resp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.replaceFragment(ResponsibilityFragment.class);
            }
        });
        return view;
    }
}
