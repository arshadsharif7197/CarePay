package com.carecloud.carepaylibray.fragments.demographics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;

/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsMoreDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demogr_more_details, container, false);
        return view;
    }
}
