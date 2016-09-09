package com.carecloud.carepaylibray.demographics.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

import static com.carecloud.carepaylibray.utils.Utility.setTypefaceFromAssets;


/**
 * Created by lsoco_user on 9/2/2016.
 * Demographics documents scanning (driver's license and insurance card)
 */
public class DemographicsDocumentsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_documents, container, false);
        setTypefaceFromAssets(getActivity(), "fonts/gotham_rounded_medium.otf", (TextView) view.findViewById(R.id.demogr_docs_header_title));
        setTypefaceFromAssets(getActivity(), "fonts/proximanova_regular.otf", (TextView) view.findViewById(R.id.demogr_docs_header_subtitle));
        setTypefaceFromAssets(getActivity(), "fonts/gotham_rounded_medium.otf", (TextView) view.findViewById(R.id.demogr_docs_scan_license_btn));
        setTypefaceFromAssets(getActivity(), "fonts/gotham_rounded_medium.otf", (TextView) view.findViewById(R.id.demogr_insurance_scan_insurance_btn));
        setTypefaceFromAssets(getActivity(), "fonts/proximanova_regular.otf", (TextView) view.findViewById(R.id.demogr_license_scan_label));
        setTypefaceFromAssets(getActivity(), "fonts/proximanova_regular.otf", (TextView) view.findViewById(R.id.demogr_insurance_number_label));

        setTypefaceFromAssets(getActivity(), "fonts/proximanova_regular.otf", (TextView) view.findViewById(R.id.demogr_license_state_label));
        setTypefaceFromAssets(getActivity(), "fonts/proximanova_regular.otf", (TextView) view.findViewById(R.id.demogr_insurance_switch));
        setTypefaceFromAssets(getActivity(), "fonts/proximanova_regular.otf", (TextView) view.findViewById(R.id.demogr_docs_provider));
        setTypefaceFromAssets(getActivity(), "fonts/proximanova_regular.otf", (TextView) view.findViewById(R.id.demogr_docs_plan));

        setTypefaceFromAssets(getActivity(), "fonts/proximanova_semibold.otf", (TextView) view.findViewById(R.id.demogr_tv_state));
        setTypefaceFromAssets(getActivity(), "fonts/proximanova_semibold.otf", (TextView) view.findViewById(R.id.demogr_docs_plan));
        setTypefaceFromAssets(getActivity(), "fonts/proximanova_semibold.otf", (TextView) view.findViewById(R.id.demogr_docs_provider));


        return view;

    }
}