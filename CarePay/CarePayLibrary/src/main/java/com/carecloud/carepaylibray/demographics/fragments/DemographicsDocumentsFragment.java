package com.carecloud.carepaylibray.demographics.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.carecloud.carepaylibray.utils.CameraScannerHelper;
import com.carecloud.carepaylibray.utils.Utility;

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
    private CameraScannerHelper mCameraScannerHelper;
    private CameraScannerHelper mLicenseScanHelper;
    private CameraScannerHelper mInsuranceScanHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_documents, container, false);

        // ge the imageviews
        imLicense = (ImageView) view.findViewById(R.id.demogr_license_image);
        imInsurance = (ImageView) view.findViewById(R.id.demogr_insurance_image);

        // create scan helpers
        mLicenseScanHelper = new CameraScannerHelper(getActivity(), imLicense, 129); // TODO: 9/9/2016 use dimens
        mInsuranceScanHelper = new CameraScannerHelper(getActivity(), imInsurance, 129); // TODO: 9/9/2016 use dimens

        // add click listenter
        final Button btnScanLicense = (Button)view.findViewById(R.id.demogr_docs_scan_license_btn);
        btnScanLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanAndShowIn(imLicense);
                Log.v(LOG_TAG, "scan license");
                btnScanLicense.setText(R.string.demogr_docs_rescan);
                selectImage(mLicenseScanHelper);
            }
        });

        final Button btnScanInsurance = (Button)view.findViewById(R.id.demogr_insurance_scan_insurance_btn);
        btnScanInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanAndShowIn(imInsurance);
                Log.v(LOG_TAG, "scan insurance");
                btnScanInsurance.setText(R.string.demogr_docs_rescan);
                selectImage(mInsuranceScanHelper);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        String userChoosenTask = mCameraScannerHelper.getUserChoosenTask();

        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(CameraScannerHelper.chooseActionDlOptions[1].toString()))
                        startActivityForResult(Intent.createChooser(mCameraScannerHelper.galleryIntent(),
                                                                    CameraScannerHelper.CHOOSER_NAME),
                                               CameraScannerHelper.SELECT_FILE);
                } else {
                    //code for deny
                }
                break;

            case Utility.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(CameraScannerHelper.chooseActionDlOptions[0].toString()))
                        startActivityForResult(mCameraScannerHelper.cameraIntent(), CameraScannerHelper.REQUEST_CAMERA);
                } else {
                    //code for deny
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CameraScannerHelper.SELECT_FILE)
                mCameraScannerHelper.onSelectFromGalleryResult(data);
            else if (requestCode == CameraScannerHelper.REQUEST_CAMERA)
                mCameraScannerHelper.onCaptureImageResult(data);
        }
    }

    public void selectImage(final CameraScannerHelper cameraScannerHelper) {
        mCameraScannerHelper = cameraScannerHelper;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(CameraScannerHelper.chooseActionDlgTitle);
        builder.setItems(CameraScannerHelper.chooseActionDlOptions,
                         new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int item) {
                                 if (CameraScannerHelper.chooseActionDlOptions[item].equals(CameraScannerHelper.chooseActionDlOptions[0])) {
                                     cameraScannerHelper.setUserChoosenTask(CameraScannerHelper.chooseActionDlOptions[0].toString());
                                     boolean result = Utility.checkPermissionCamera(getActivity());
                                     if (result) {
                                         startActivityForResult(cameraScannerHelper.cameraIntent(), CameraScannerHelper.REQUEST_CAMERA);
                                     }
                                 } else if (CameraScannerHelper.chooseActionDlOptions[item].equals(CameraScannerHelper.chooseActionDlOptions[1])) {
                                     cameraScannerHelper.setUserChoosenTask(CameraScannerHelper.chooseActionDlOptions[1].toString());
                                     boolean result = Utility.checkPermission(getActivity());
                                     if (result) {
                                         startActivityForResult(Intent.createChooser(cameraScannerHelper.galleryIntent(),
                                                                                     CameraScannerHelper.CHOOSER_NAME),
                                                                CameraScannerHelper.SELECT_FILE);
                                     }
                                 } else if (CameraScannerHelper.chooseActionDlOptions[item].equals(CameraScannerHelper.chooseActionDlOptions[2])) {
                                     dialog.dismiss();
                                 }
                             }
                         });
        builder.show();
    }


}