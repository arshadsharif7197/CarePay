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
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadDriversLicenseModel;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/13/2016.
 * Fragment for with scanning driver's license functionality
 */
public class LicenseScannerFragment extends DocumentScannerFragment {

    private static final String[] states = new String[]{
            "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY",
            "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND",
            "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",};

    private ImageCaptureHelper                    mLicenseScanHelper;
    private TextView                              tvLicenseNum;
    private Button                                btnScanLicense;
    private TextView                              tvState;
    private DemographicPayloadDriversLicenseModel model;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_scan_license, container, false);

        tvLicenseNum = (TextView) view.findViewById(R.id.demogr_docs_license_num);

        ImageView imLicense = (ImageView) view.findViewById(R.id.demogr_license_image);

        mLicenseScanHelper = new ImageCaptureHelper(getActivity(), imLicense);

        // add click listener
        btnScanLicense = (Button) view.findViewById(R.id.demogr_docs_scan_license_btn);
        btnScanLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "scan license");
                selectImage(mLicenseScanHelper);
            }
        });

        tvState = (TextView) view.findViewById(R.id.demogr_tv_state);
        tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(states, "Select state", tvState);
            }
        });

        setTypefaces(view);

        populateViewsFromModel();

        return view;
    }

    protected void updateModelAndViewsAfterScan() { // license has been scanned
        // TODO: 9/29/2016 implement OCR
        btnScanLicense.setText(R.string.demogr_docs_rescan);

        model.setLicenseNumber("666666666");
        tvLicenseNum.setText("666666666");
        tvLicenseNum.setVisibility(View.VISIBLE);

        model.setLicenseState(states[8]);
        tvState.setText(states[8]);
    }

    @Override
    public void populateViewsFromModel() {
        if (model != null) {
            // check the type of the model
            mLicenseScanHelper.setImageFromCharStream(model.getLicensePhoto());
            String licenseNum = model.getLicenseNumber();
            if (!StringUtil.isNullOrEmpty(licenseNum)) {
                tvLicenseNum.setVisibility(View.VISIBLE);
            } else {
                tvLicenseNum.setVisibility(View.GONE);
            }
            tvLicenseNum.setText(licenseNum);
            tvState.setText(model.getLicenseState());
        }
    }

    @Override
    protected void setTypefaces(View view) {
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_license_scan_label));
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_scan_license_btn));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_license_state_label));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_tv_state));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_license_num));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // invoke parent fragment to enable Next Button
        buttonsStatusCallback.enableNextButton(true);
    }

    @Override
    public int getImageShape() {
        return ImageCaptureHelper.RECTANGULAR_IMAGE;
    }

    public void setModel(DemographicPayloadDriversLicenseModel model) {
        this.model = model;
    }
}