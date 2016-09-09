package com.carecloud.carepaylibray.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.activities.SignatureActivity;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class HomeFragment extends Fragment {

    private Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen_activity, container, false);
        view.findViewById(R.id.home_button_demogr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DemographicsActivity.class);
                if(mActivity instanceof KeyboardHolderActivity) {
                    intent.putExtra(KeyboardHolderActivity.KEY_LANG_ID, ((KeyboardHolderActivity) mActivity).getLangId());
                }
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
                if(mActivity instanceof KeyboardHolderActivity) {
                    ((KeyboardHolderActivity)mActivity).replaceFragment(ResponsibilityFragment.class);
                }
            }
        });
        return view;
    }
}
