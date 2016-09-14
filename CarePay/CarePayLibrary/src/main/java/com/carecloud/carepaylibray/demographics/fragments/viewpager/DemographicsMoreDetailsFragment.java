package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

import java.util.ArrayList;
import java.util.List;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsMoreDetailsFragment extends Fragment implements View.OnClickListener {
    View view;
    String[] getUpdateItemList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_moredetails, container, false);

        getUpdateItemList = getResources().getStringArray(R.array.UpdatesMode);
        setTypefaces(view);
        return view;
    }

    @Override
    public void onClick(View view) {
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.moreDetailsHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.moreDetailsSubHeading));
        setGothamRoundedMediumTypeface(getActivity(),(Button)view.findViewById(R.id.goToCarePayButton));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.placeHolderIconTextViewId));
    }



}
