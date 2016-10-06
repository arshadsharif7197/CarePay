package com.carecloud.carepaylibray.demographics.fragments.scanner;

import android.content.Intent;
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
import com.carecloud.carepaylibray.demographics.models.DemInsurancePayloadPojo;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/13/2016.
 * Fragment with insurance scanning functionality
 */

public class InsuranceScannerFragment extends DocumentScannerFragment {

    private static final String[] plans     = {"Aetna Select", "Aetna Value Network HMO", "Elect Choice EPO", "HMO"};
    private static final String[] providers = {"Aetna", "BlueCross Blue Shield", "Cigna", "GHI", "HIP"};

    private ImageCaptureHelper      mInsuranceScanHelper;
    private Button                  btnScanInsurance;
    private TextView                tvInsuranceNum;
    private TextView                tvPlan;
    private TextView                tvProvider;
    private DemInsurancePayloadPojo model;

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

        populateViewsFromModel();

        return view;
    }

    @Override
    protected void updateModelAndViewsAfterScan() {
        btnScanInsurance.setText(R.string.demogr_docs_rescan);

        model.setInsuranceMemberId("98765431");
        tvInsuranceNum.setText("98765431");
        tvInsuranceNum.setVisibility(View.VISIBLE);

        model.setInsurancePlan(plans[0]);
        tvPlan.setText(plans[0]);

        model.setInsuranceProvider(providers[0]);
        tvProvider.setText(providers[0]);
    }

    @Override
    public void populateViewsFromModel() {
        if (model != null) {
            Log.v(LOG_TAG, "InsuranceScannerFrag - populateFromModel()");

            // check the type of the model
        //    mInsuranceScanHelper.setImageFromCharStream(model.getProfilePhoto(0));
            String insNum = model.getInsuranceMemberId();
            tvInsuranceNum.setText(insNum);
            tvPlan.setText(model.getInsurancePlan());
            tvProvider.setText(model.getInsuranceProvider());
        }
    }


    public void resetViewsContent() {
        Log.v(LOG_TAG, "resetViewsContent()");
        btnScanInsurance.setText(R.string.demogr_docs_scan_insurance_label);
        tvInsuranceNum.setText("");
        tvInsuranceNum.setVisibility(View.GONE);
        tvPlan.setText(getString(R.string.demogr_tv_choose_label));
        tvProvider.setText(getString(R.string.demogr_docs_tv_chose_company));
        mInsuranceScanHelper.resetTargetView();
        // additional data deletion may be added when real data is used...
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // invoke parent fragment to enable 'Next' button
        buttonsStatusCallback.enableNextButton(true);
        buttonsStatusCallback.scrollToBottom();
    }

    @Override
    public int getImageShape() {
        return ImageCaptureHelper.RECTANGULAR_IMAGE;
    }

    public void setModel(DemInsurancePayloadPojo model) {
        this.model = model;
    }
}