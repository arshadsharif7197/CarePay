package com.carecloud.carepaylibray.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class ResponsibilityFragment extends Fragment {

    private static final String LOG_TAG = ResponsibilityFragment.class.getSimpleName();
    private KeyboardHolderActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (KeyboardHolderActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(!isTablet()) {
            Log.v(LOG_TAG, "onCreateView() phone");
            view = inflater.inflate(R.layout.fragment_responsibility, container, false);
        } else {
            Log.v(LOG_TAG, "onCreateView() tablet");
            view = inflater.inflate(R.layout.fragment_responsibility_tablet, container, false);
        }

//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
//        mActivity.setSupportActionBar(toolbar);
//        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mActivity.getSupportActionBar().setTitle("Responsibility");

        return view;
    }

    /**
     * Detect is configuration of a tablet
     * @return True is tablet; false otherwise
     */
    public boolean isTablet() {
        int screenLayoutWidthSize = getActivity().getResources().getConfiguration().screenWidthDp;
        // detect smallest width size
        return  (screenLayoutWidthSize >= 600);
    }

}