package com.carecloud.carepaylibray.demographics.fragments.scanner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/13/2016.
 * Fragment with insurance scanning functionality
 */

public class InsuranceScannerFragment extends DocumentScannerFragment {

    private static final String[] plans     = {"Plan1", "Plan2", "Plan3"};
    private static final String[] providers = {"Provider1", "Provider2", "Provider3", "Provider4"};

    private ImageCaptureHelper mInsuranceScanHelper;
    private Button             btnScanInsurance;
    private TextView           tvInsuranceNum;
    private TextView           tvPlan;
    private TextView           tvProvider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_scan_insurance, container, false);

        tvInsuranceNum = (TextView) view.findViewById(R.id.demogr_insurance_num);

        ImageView imInsurance = (ImageView) view.findViewById(R.id.demogr_insurance_image);
        mInsuranceScanHelper = new ImageCaptureHelper(getActivity(), imInsurance);

        btnScanInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_btn);
        btnScanInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "scan insurance");
                selectImage(mInsuranceScanHelper);
            }
        });


        tvPlan = (TextView) view.findViewById(R.id.demogr_docs_plan);
        tvPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(plans, "Choose plan", tvPlan);
            }
        });

        tvProvider = (TextView) view.findViewById(R.id.demogr_docs_provider);
        tvProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(providers, "Choose provider", tvProvider);
            }
        });

        setTypefaces(view);

        return view;
    }

    @Override
    protected void updateDetailViewsAfterScan() {
        btnScanInsurance.setText(R.string.demogr_docs_rescan);
        tvInsuranceNum.setText("98765431");
        tvInsuranceNum.setVisibility(View.VISIBLE);
        tvPlan.setText(plans[0]);
        tvProvider.setText(providers[0]);
    }

    @Override
    protected void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_insurance_scan_insurance_btn));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_insurance_plan_label));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_plan));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_insurance_provider_label));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_provider));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_insurance_number_label));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_insurance_num));
    }
}