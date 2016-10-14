package com.carecloud.carepaylibray.demographics.fragments.scanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.demographics.models.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;


import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;


/**
 * Created by lsoco_user on 9/13/2016.
 * Fragment with insurance scanning functionality
 */

public class InsuranceScannerFragment extends DocumentScannerFragment {


    private String[] planDataArray;
    private String[] providerDataArray;
    private String[] cardTypeDataArray;

    private ImageCaptureHelper insuranceFrontScanHelper;
    private ImageCaptureHelper insuranceBackScanHelper;
    private Button btnScanFrontInsurance;
    private Button btnScanBackInsurance;
    private EditText insuranceCardNumEditText;
    private TextView planTextView;
    private TextView providerTextView;
    private TextView cardTypeTextView;
    private ImageView frontInsuranceImageView;
    private ImageView backInsuranceImageView;
    private DemographicInsurancePayloadDTO model;
    private TextInputLayout insuranceCardNumberLabel;

    int selectedDataArray;
    private DemographicInsurancePhotoDTO insurancefrontPhotoDto;
    private DemographicInsurancePhotoDTO insurancebackPhotoDto;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_scan_insurance, container, false);


        insuranceCardNumEditText = (EditText) view.findViewById(R.id.reviewinsurncecardnumber);

        frontInsuranceImageView = (ImageView) view.findViewById(R.id.demogr_insurance_frontimage);
        insuranceFrontScanHelper = new ImageCaptureHelper(getActivity(), frontInsuranceImageView);

        btnScanFrontInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_frontbtn);
        btnScanFrontInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "scan insurance");
                selectImage(insuranceFrontScanHelper);
            }
        });
        backInsuranceImageView = (ImageView) view.findViewById(R.id.demogr_insurance_backimage);
        insuranceBackScanHelper = new ImageCaptureHelper(getActivity(), backInsuranceImageView);
        btnScanBackInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_backbtn);
        btnScanBackInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "scan insurance");
                selectImage(insuranceBackScanHelper);

            }
        });
        providerTextView = (TextView) view.findViewById(R.id.demogr_docs_provider);
        providerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDataArray = 1;
                showAlertDialogWithListview(providerDataArray, "Select Provider");
            }
        });
        planTextView = (TextView) view.findViewById(R.id.demogr_docs_plan);
        planTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDataArray = 2;
                showAlertDialogWithListview(planDataArray, "Select Plan");
            }
        });
        cardTypeTextView = (TextView) view.findViewById(R.id.demogr_insurance_card_type_textview);
        cardTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDataArray = 3;
                showAlertDialogWithListview(cardTypeDataArray, "Select Card Type");
            }
        });

        providerDataArray = getResources().getStringArray(R.array.providers);
        planDataArray = getResources().getStringArray(R.array.plans);
        cardTypeDataArray = getResources().getStringArray(R.array.cardtypes);

        setTypefaces(view);
        populateViewsFromModel();
        setEditTexts(view);
        return view;
    }


    @Override
    protected void updateModelAndViewsAfterScan() {
        btnScanFrontInsurance.setText(R.string.demogr_docs_rescan_front);
        btnScanBackInsurance.setText(R.string.demogr_docs_rescan_back);

        //  model.setInsuranceMemberId(insuranceCardNumEditText.getText().toString());
        insuranceCardNumEditText.setText(model.getInsuranceMemberId());

        //  model.setInsurancePlan(planDataArray[0]);
        planTextView.setText(planDataArray[0]);

        //   model.setInsuranceProvider(providerDataArray[0]);
        providerTextView.setText(providerDataArray[0]);


    }

    /** initializing view from the model
     */
    @Override
    public void populateViewsFromModel() {
        if (model != null) {
            Log.v(LOG_TAG, "InsuranceScannerFrag - populateFromModel()");
            updateModelAndViewsAfterScan();
            List<DemographicInsurancePhotoDTO> photos = model.getInsurancePhotos();
            if (photos == null) {
                Log.v(LOG_TAG, InsuranceScannerFragment.class.getSimpleName() + " no ins photos");
            } else {
                if (photos.size() > 0) {
                    String photoBackURL = photos.get(0).getInsurancePhoto();
                    Picasso.with(getActivity()).load(photoBackURL).into(backInsuranceImageView);
                }

                if (photos.size() > 1) {
                    String photoFrontURL = photos.get(1).getInsurancePhoto();
                    Picasso.with(getActivity()).load(photoFrontURL).into(frontInsuranceImageView);
                }
            }

            insurancebackPhotoDto = new DemographicInsurancePhotoDTO();
            insurancefrontPhotoDto = new DemographicInsurancePhotoDTO();
            String insNum = model.getInsuranceMemberId();
            insuranceCardNumEditText.setText(insNum);
            planTextView.setText(model.getInsurancePlan());
            providerTextView.setText(model.getInsuranceProvider());
        } else {
            resetViewsContent();
        }
    }

    /** Showing alert boxes for list
     * @param listArray
     * @param title
     */
    private void showAlertDialogWithListview(final String[] listArray, String title) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        View customView = LayoutInflater.from(getActivity()).inflate(
                R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter alertAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(listArray));
        listView.setAdapter(alertAdapter);
        dialog.setView(customView);

        final AlertDialog alert = dialog.create();
        alert.show();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
             @Override
             public void onShow(DialogInterface dialog) {
                 alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                         getResources().getColor(R.color.blue_cerulian));
             }
         });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (selectedDataArray) {
                    case 1:
                        providerTextView.setText(providerDataArray[position]);

                        break;
                    case 2:
                        planTextView.setText(planDataArray[position]);

                        break;
                    case 3:
                        cardTypeTextView.setText(cardTypeDataArray[position]);

                        break;
                }
                alert.dismiss();
            }
        });
    }

    /** Converting bitmap images to base64 and posting to server in model
     * @return insurance model
     */
    public DemographicInsurancePayloadDTO getBitmapsFromImageViews() {
        Bitmap bitmapFront;
        Bitmap bitmapBack;
        bitmapBack = ((BitmapDrawable) insuranceBackScanHelper
                .getImageViewTarget().getDrawable()).getBitmap();
        bitmapFront = ((BitmapDrawable) insuranceFrontScanHelper
                .getImageViewTarget().getDrawable()).getBitmap();

        insurancebackPhotoDto = new DemographicInsurancePhotoDTO();
        insurancefrontPhotoDto = new DemographicInsurancePhotoDTO();

        insurancebackPhotoDto.setInsurancePhoto(SystemUtil.encodeToBase64(
                bitmapBack, Bitmap.CompressFormat.JPEG, 100));

        insurancefrontPhotoDto.setInsurancePhoto(SystemUtil.encodeToBase64(
                bitmapFront, Bitmap.CompressFormat.JPEG, 100));

        List<DemographicInsurancePhotoDTO> photos = model.getInsurancePhotos();
        if (photos == null) {
            photos = new ArrayList<>();
        } else {
            photos.clear();
        }

        photos.add(0, insurancefrontPhotoDto);
        photos.add(1, insurancebackPhotoDto);
        model.setInsurancePhotos(photos);
        model.setInsuranceMemberId(insuranceCardNumEditText.getText().toString());
        model.setInsurancePlan(planTextView.getText().toString());
        model.setInsuranceProvider(providerTextView.getText().toString());
        return model;

    }


    public void resetViewsContent() {
        Log.v(LOG_TAG, "resetViewsContent()");
        btnScanFrontInsurance.setText(R.string.demogr_docs_scan_insurance_front_label);
        btnScanBackInsurance.setText(R.string.demogr_docs_scan_insurance_backlabel);
        insuranceCardNumEditText.setText("");
        planTextView.setText(getString(R.string.demogr_tv_choose_label));
        providerTextView.setText(getString(R.string.demogr_docs_tv_chose_company));
        cardTypeTextView.setText(getString(R.string.demogr_tv_choose_label));
        insuranceFrontScanHelper.resetTargetView();
        // additional data deletion may be added when real data is used...
    }

    private void setEditTexts(View view) {
        insuranceCardNumberLabel = (TextInputLayout) view.findViewById(R.id.insurancecardNumberLabel);
        insuranceCardNumberLabel.setTag(getString(R.string.review_insurancecardnumber_hint));
        insuranceCardNumEditText = (EditText) view.findViewById(R.id.reviewinsurncecardnumber);
        insuranceCardNumEditText.setTag(insuranceCardNumberLabel);
        setChangeFocusListeners();
        insuranceCardNumEditText.clearFocus();
    }

    private void setChangeFocusListeners() {
        insuranceCardNumEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
    }

    @Override
    protected void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demogr_insurance_scan_insurance_frontbtn));
        setGothamRoundedMediumTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demogr_insurance_scan_insurance_backbtn));
        setProximaNovaRegularTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demogr_insurance_plan_label));
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demogr_docs_plan));
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demogr_insurance_card_type_textview));
        setProximaNovaRegularTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demogr_insurance_provider_label));
        setProximaNovaRegularTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demogr_insurance_card_type_abel));
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demogr_docs_provider));
        setProximaNovaRegularTypeface(getActivity(),
                (EditText) view.findViewById(R.id.reviewinsurncecardnumber));
        setProximaNovaRegularTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demogr_insurance_card_type_abel));
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demogr_insurance_card_type_textview));


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (buttonsStatusCallback != null) {
            // invoke parent fragment to enable 'Next' button
            buttonsStatusCallback.enableNextButton(true);
            buttonsStatusCallback.scrollToBottom();
        }
    }

    @Override
    public int getImageShape() {
        return ImageCaptureHelper.RECTANGULAR_IMAGE;
    }

    public void setModel(DemographicInsurancePayloadDTO model) {
        this.model = model;
    }
}