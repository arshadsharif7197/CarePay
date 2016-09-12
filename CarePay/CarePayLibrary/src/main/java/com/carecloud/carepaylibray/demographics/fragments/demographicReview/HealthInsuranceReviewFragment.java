package com.carecloud.carepaylibray.demographics.fragments.demographicReview;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.Utility;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypeface;

public class HealthInsuranceReviewFragment extends Fragment implements View.OnClickListener {

    Button buttonAddHealthInsuranceInfo;
    View view;
    private ImageCaptureHelper reviewInsuranceScanHelper;
    private ImageCaptureHelper reviewImageCaptureHelper;

    private Button reviewBtnScanInsurance;

    public static HealthInsuranceReviewFragment newInstance() {
        return new HealthInsuranceReviewFragment();
    }

    public HealthInsuranceReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_health_insurance, container, false);

        initialiseUIFields();
        setTypefaces(view);
        return view;
    }

    private void initialiseUIFields() {
        buttonAddHealthInsuranceInfo = (Button) view.findViewById(R.id.buttonAddHealthInsuranceInfo);
        buttonAddHealthInsuranceInfo.setOnClickListener(this);
        reviewBtnScanInsurance =(Button)view.findViewById(R.id.demogr_review_insurance_scan_insurance_btn);
        reviewBtnScanInsurance.setOnClickListener(this);

        ImageView imInsurance = (ImageView) view.findViewById(R.id.demogr_review_insurance_image);
        reviewInsuranceScanHelper = new ImageCaptureHelper(getActivity(), imInsurance);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonAddHealthInsuranceInfo) {
            openNewFragment();
        }else if(view == reviewBtnScanInsurance){

            selectImage(reviewInsuranceScanHelper);

        }
    }

    private void openNewFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = ReviewFragment.newInstance();
        transaction.replace(R.id.root_layout, fragment, HealthInsuranceReviewFragment.class.getName());
        transaction.addToBackStack("HealthInsuranceReviewFragment -> ReviewFragment");
        transaction.commit();
    }


    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.ReviewInsuranceTitle));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewInsuranceSubTitle));

        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_insurance_scan_insurance_btn));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_insurance_number_label));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_docs_provider));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_docs_plan));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_docs_plan));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_docs_provider));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        String userChoosenTask = reviewImageCaptureHelper.getUserChoosenTask();
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[1].toString()))
                        startActivityForResult(Intent.createChooser(reviewImageCaptureHelper.galleryIntent(),
                                ImageCaptureHelper.CHOOSER_NAME),
                                ImageCaptureHelper.SELECT_FILE);
                } else {
                    //code for deny

                }
                break;

            case Utility.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[0].toString()))
                        startActivityForResult(reviewImageCaptureHelper.cameraIntent(), ImageCaptureHelper.REQUEST_CAMERA);
                }
                }
        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageCaptureHelper.SELECT_FILE) {
                reviewImageCaptureHelper.onSelectFromGalleryResult(data, ImageCaptureHelper.RECTANGULAR_IMAGE);
            } else if (requestCode == ImageCaptureHelper.REQUEST_CAMERA) {
                reviewImageCaptureHelper.onCaptureImageResult(data, ImageCaptureHelper.RECTANGULAR_IMAGE);
            }
        }
    }



    public void selectImage(final ImageCaptureHelper imageCaptureHelper) {
        reviewImageCaptureHelper = imageCaptureHelper;
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

}
