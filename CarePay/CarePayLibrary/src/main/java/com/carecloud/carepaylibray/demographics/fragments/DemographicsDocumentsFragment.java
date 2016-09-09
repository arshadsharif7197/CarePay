package com.carecloud.carepaylibray.demographics.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypeface;


/**
 * Created by lsoco_user on 9/2/2016.
 * Demographics documents scanning (driver's license and insurance card)
 */
public class DemographicsDocumentsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_documents, container, false);
        setTypefaces(view);
        return view;
    }

    /**
     * Helper to set the typeface to all textviews
     *
     * @param view The parent view
     */
    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_header_title));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_header_subtitle));
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_scan_license_btn));
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_insurance_scan_insurance_btn));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_license_scan_label));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_insurance_number_label));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_license_state_label));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_insurance_switch));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_provider));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_plan));

        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_tv_state));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_plan));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_provider));
    }
}