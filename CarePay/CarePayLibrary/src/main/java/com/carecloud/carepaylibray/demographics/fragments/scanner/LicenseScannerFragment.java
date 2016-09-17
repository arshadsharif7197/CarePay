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
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/13/2016.
 * Fragment for with scanning driver's license functionality
 */
public class LicenseScannerFragment extends DocumentScannerFragment {

    private static final String[] states = new String[]{
            "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY",
            "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND",
            "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",};

    private ImageCaptureHelper mLicenseScanHelper;
    private TextView           tvLicenseNum;
    private Button             btnScanLicense;
    private TextView           tvState;

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

        return view;
    }

    protected void updateDetailViewsAfterScan() { // license has been scanned
        btnScanLicense.setText(R.string.demogr_docs_rescan);
        tvLicenseNum.setText("123456789");
        tvLicenseNum.setVisibility(View.VISIBLE);
        tvState.setText(states[8]);
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
}