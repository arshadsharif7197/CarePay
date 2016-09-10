package com.carecloud.carepaylibray.demographics.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.Utility;

import java.util.Arrays;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Demographics documents scanning (driver's license and insurance card)
 */
public class DemographicsDocumentsFragment extends Fragment {

    private static final String   LOG_TAG   = DemographicsDocumentsFragment.class.getSimpleName();
    public static final  int      LICENSE   = 0;
    public static final  int      INSURANCE = 1;
    private static final String[] states    = new String[]{
            "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY",
            "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND",
            "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",};
    private static final String[] plans     = {"Plan1", "Plan2", "Plan3"};
    private static final String[] providers = {"Provider1", "Provider2", "Provider3", "Provider4"};
    private ImageCaptureHelper mImageCaptureHelper;
    private ImageCaptureHelper mLicenseScanHelper;
    private ImageCaptureHelper mInsuranceScanHelper;
    private TextView           tvLicenseNum;
    private TextView           tvInsuranceNum;
    private Button             btnScanInsurance;
    private Button             btnScanLicense;
    private TextView           tvState;
    private TextView           tvPlan;
    private TextView           tvProvider;
    private int                crtScannedDocFlag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_demographics_documents, container, false);

        // get the imageviews
        ImageView imLicense = (ImageView) view.findViewById(R.id.demogr_license_image);
        ImageView imInsurance = (ImageView) view.findViewById(R.id.demogr_insurance_image);
        tvLicenseNum = (TextView) view.findViewById(R.id.demogr_docs_license_num);
        tvInsuranceNum = (TextView) view.findViewById(R.id.demogr_insurance_num);

        // create scan helpers
        int thumbWidth = (int) getActivity().getResources().getDimension(R.dimen.demogr_docs_thumbnail_width);
        int thumbHeight= (int) getActivity().getResources().getDimension(R.dimen.demogr_docs_thumbnail_height);
        Log.v(LOG_TAG, "width=" + thumbWidth + " height="+thumbHeight);
        mLicenseScanHelper = new ImageCaptureHelper(getActivity(), imLicense);
        mInsuranceScanHelper = new ImageCaptureHelper(getActivity(), imInsurance);

        // add click listener
        btnScanLicense = (Button) view.findViewById(R.id.demogr_docs_scan_license_btn);
        btnScanLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "scan license");
                crtScannedDocFlag = LICENSE;
                selectImage(mLicenseScanHelper);
            }
        });

        btnScanInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_btn);
        btnScanInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "scan insurance");
                crtScannedDocFlag = INSURANCE;
                selectImage(mInsuranceScanHelper);
            }
        });

        tvState = (TextView) view.findViewById(R.id.demogr_tv_state);
        tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(states, "Select state", tvState);
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

        final LinearLayout llInsuranceSection = (LinearLayout) view.findViewById(R.id.demogr_docs_insurance_container);
        SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.demogr_insurance_switch);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                if (on) {
                    // show insurance section
                    llInsuranceSection.setVisibility(View.VISIBLE);
                } else {
                    // hide insurance section
                    llInsuranceSection.setVisibility(View.INVISIBLE);
                }
            }
        });
        switchCompat.setChecked(false);

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.v(LOG_TAG, "onReauestPermissionsResult()");
        String userChoosenTask = mImageCaptureHelper.getUserChoosenTask();
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[1].toString()))
                        startActivityForResult(Intent.createChooser(mImageCaptureHelper.galleryIntent(),
                                                                    ImageCaptureHelper.CHOOSER_NAME),
                                               ImageCaptureHelper.SELECT_FILE);
                } else {
                    //code for deny
                    Log.v(LOG_TAG, "read external denied");
                }
                break;

            case Utility.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[0].toString()))
                        startActivityForResult(mImageCaptureHelper.cameraIntent(), ImageCaptureHelper.REQUEST_CAMERA);
                } else {
                    //code for deny
                    Log.v(LOG_TAG, "camera denied");
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageCaptureHelper.SELECT_FILE) {
                mImageCaptureHelper.onSelectFromGalleryResult(data, ImageCaptureHelper.RECTANGULAR_IMAGE);
            } else if (requestCode == ImageCaptureHelper.REQUEST_CAMERA) {
                mImageCaptureHelper.onCaptureImageResult(data, ImageCaptureHelper.RECTANGULAR_IMAGE);
            }
            updateDetailViewsAfterScan();
        }
    }

    /**
     * Updates the number the button label and the number textview accoring to doc scanned (license or insurance)
     */
    private void updateDetailViewsAfterScan() { // TODO: 9/10/2016 turn into a callback when real scanning in place
        // update the fields related to the last scanned doc
        if (crtScannedDocFlag == LICENSE) { // license has been scanned
            btnScanLicense.setText(R.string.demogr_docs_rescan);
            tvLicenseNum.setText("123456789");
            tvLicenseNum.setVisibility(View.VISIBLE);
            tvState.setText(states[8]);
        } else { // insurance has been
            btnScanInsurance.setText(R.string.demogr_docs_rescan);
            tvInsuranceNum.setText("98765431");
            tvInsuranceNum.setVisibility(View.VISIBLE);
            tvPlan.setText(plans[0]);
            tvProvider.setText(providers[0]);
        }
    }

    /**
     * Starts Camera or Gallery to caprture/select an image
     *
     * @param imageCaptureHelper The camera helper used with a particular imageview
     */
    public void selectImage(final ImageCaptureHelper imageCaptureHelper) {
        mImageCaptureHelper = imageCaptureHelper;
        // create the chooser dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(ImageCaptureHelper.chooseActionDlgTitle);
        builder.setItems(ImageCaptureHelper.chooseActionDlOptions,
                         new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int item) {
                                 if (item == 0) { // "Take picture" chosen
                                     imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[0].toString());
                                     boolean result = Utility.checkPermissionCamera(getActivity());
                                     if (result) {
                                         startActivityForResult(imageCaptureHelper.cameraIntent(), ImageCaptureHelper.REQUEST_CAMERA);
                                     }
                                 } else if (item == 1) {  // "Select from Gallery" chosen
                                     imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[1].toString());
                                     boolean result = Utility.checkPermission(getActivity());
                                     if (result) {
                                         startActivityForResult(Intent.createChooser(imageCaptureHelper.galleryIntent(),
                                                                                     ImageCaptureHelper.CHOOSER_NAME),
                                                                ImageCaptureHelper.SELECT_FILE);
                                     }
                                 } else if (item == 3) { // "Cancel"
                                     dialog.dismiss();
                                 }
                             }
                         });
        builder.show();
    }

    /**
     * Creates a generic dialog that contains a list of choices
     *
     * @param options              The choices
     * @param title                The dlg title
     * @param selectionDestination The textview where the selected option will be displayed
     */
    private void showChooseDialog(final String[] options, String title, final TextView selectionDestination) {
        final String cancelLabel = "Cancel";
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        // add cancel button
        dialog.setNegativeButton(cancelLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // create dialog layout
        View customView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        // create the adapter
        CustomAlertAdapter mAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(options));
        listView.setAdapter(mAdapter);
        // show the dialog
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();
        // set item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedOption = options[position];
                selectionDestination.setText(selectedOption);
                alert.dismiss();
            }
        });
    }
}