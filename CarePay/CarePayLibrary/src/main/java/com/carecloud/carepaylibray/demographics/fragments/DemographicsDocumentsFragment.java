package com.carecloud.carepaylibray.demographics.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private static final String[] states = new String[] {
            "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY",
            "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND",
            "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",};
    private static final String LOG_TAG  = DemographicsDocumentsFragment.class.getSimpleName();
    private ImageView imInsurance;
    private ImageView imLicense;
    private TextView tvState;
    private TextView tvPlan;
    private TextView tvProvider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_documents, container, false);

        imLicense = (ImageView) view.findViewById(R.id.demogr_license_image);
        imInsurance = (ImageView) view.findViewById(R.id.demogr_insurance_image);

        final Button btnScanLicense = (Button)view.findViewById(R.id.demogr_docs_scan_license_btn);
        btnScanLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanAndShowIn(imLicense);
                Log.v(LOG_TAG, "scan license");
                btnScanLicense.setText(R.string.demogr_docs_rescan);
            }
        });

        final Button btnScanInsurance = (Button)view.findViewById(R.id.demogr_insurance_scan_insurance_btn);
        btnScanInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanAndShowIn(imInsurance);
                Log.v(LOG_TAG, "scan insurance");
                btnScanInsurance.setText(R.string.demogr_docs_rescan);
            }
        });

        tvState = (TextView) view.findViewById(R.id.demogr_tv_state);
        tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectState();
            }
        });

        tvPlan = (TextView) view.findViewById(R.id.demogr_docs_plan);
        tvPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePlan();
            }
        });

        tvProvider = (TextView) view.findViewById(R.id.demogr_docs_provider);
        tvProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProvider();
            }
        });

        final LinearLayout llInsuranceSection = (LinearLayout) view.findViewById(R.id.demogr_docs_insurance_container);
        SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.demogr_insurance_switch);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                if(on) {
                    // show insurance section
                    llInsuranceSection.setVisibility(View.VISIBLE);
                } else {
                    // hide insurance section
                    llInsuranceSection.setVisibility(View.INVISIBLE);
                }
            }
        });
        setTypefaces(view);

        return view;
    }

    private void chooseProvider() {
     Log.v(LOG_TAG, "choose provider");
    }


    private void choosePlan() {
        Log.v(LOG_TAG, "choose plan");
    }

    private void selectState() {
        Log.v(LOG_TAG, "select state");
    }

    private void scanAndShowIn(ImageView imLicense) {

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