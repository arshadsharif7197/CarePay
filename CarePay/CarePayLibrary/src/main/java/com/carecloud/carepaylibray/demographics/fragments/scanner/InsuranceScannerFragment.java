package com.carecloud.carepaylibray.demographics.fragments.scanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.models.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;


import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTextInputLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

    private View                           view;
    private ImageCaptureHelper             insuranceFrontScanHelper;
    private ImageCaptureHelper             insuranceBackScanHelper;
    private Button                         btnScanFrontInsurance;
    private Button                         btnScanBackInsurance;
    private EditText                       insuranceCardNumEditText;
    private TextView                       planTextView;
    private TextView                       providerTextView;
    private TextView                       cardTypeTextView;
    private ImageView                      frontInsuranceImageView;
    private ImageView                      backInsuranceImageView;
    private DemographicInsurancePayloadDTO model;
    private TextInputLayout                insuranceCardNumberTextInput;

    private DemographicInsurancePhotoDTO insurancefrontPhotoDto;
    private DemographicInsurancePhotoDTO insurancebackPhotoDto;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_scan_insurance, container, false);

        insuranceCardNumEditText = (EditText) view.findViewById(R.id.reviewinsurncecardnum);

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
                showChooseDialog(providerDataArray, "Select Provider", providerTextView);
            }
        });
        planTextView = (TextView) view.findViewById(R.id.demogr_docs_plan);
        planTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(planDataArray, "Select Plan", planTextView);
            }
        });
        cardTypeTextView = (TextView) view.findViewById(R.id.demogr_insurance_card_type_textview);
        cardTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(cardTypeDataArray, "Select Card Type", cardTypeTextView);
            }
        });

        providerDataArray = getResources().getStringArray(R.array.providers);
        planDataArray = getResources().getStringArray(R.array.plans);
        cardTypeDataArray = getResources().getStringArray(R.array.cardtypes);

        setEditTexts(view);
        setTypefaces(view);
        populateViewsFromModel();
        return view;
    }

    @Override
    protected void updateModel(TextView selectionDestination) {
        if (selectionDestination == providerTextView) { // update 'id type' field in the model
            String provider = selectionDestination.getText().toString();
            if (!StringUtil.isNullOrEmpty(provider)) {
                model.setInsuranceProvider(provider);
            }
        } else if (selectionDestination == planTextView) { // update 'state' field in the model
            String plan = planTextView.getText().toString();
            if (!StringUtil.isNullOrEmpty(plan)) {
                model.setInsurancePlan(plan);
            }
        } else if (selectionDestination == cardTypeTextView) {
            String type = cardTypeTextView.getText().toString();
            if (!StringUtil.isNullOrEmpty(type)) {
                Log.v(LOG_TAG, "needed field in json");
            }
        }
    }

    @Override
    protected void updateModelAndViewsAfterScan(ImageCaptureHelper scanner) {
        if (bitmap != null) {
            if (scanner == insuranceFrontScanHelper) {
                // change button caption to 'rescan'
                btnScanFrontInsurance.setText(R.string.demogr_docs_rescan_front);
                // save from image
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicInsurancePhotoDTO frontDTO = model.getInsurancePhotos().get(0);
                frontDTO.setInsurancePhoto(imageAsBase64); // create the image dto
            } else if (scanner == insuranceBackScanHelper) {
                // change button caption to 'rescan'
                btnScanBackInsurance.setText(R.string.demogr_docs_rescan_back);
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicInsurancePhotoDTO backDTO = model.getInsurancePhotos().get(1);
                backDTO.setInsurancePhoto(imageAsBase64); // create the image dto
            }
        }
    }

    /**
     * initializing view from the model
     */
    @Override
    public void populateViewsFromModel() {
        resetViewsContent();
        if (model != null) {
            // populate with images
            List<DemographicInsurancePhotoDTO> photos = model.getInsurancePhotos();
            if (photos == null) {
                Log.v(LOG_TAG, InsuranceScannerFragment.class.getSimpleName() + " no insurance photos");
            } else {
                if (photos.size() > 0) {
                    String photoFrontURL = photos.get(0).getInsurancePhoto();
                    try {
                        URL url = new URL(photoFrontURL);
                        Log.d(LOG_TAG, "valid url: " + url.toString());
                        Picasso.with(getContext()).load(photoFrontURL)
                                .resize(insuranceFrontScanHelper.getImgWidth(), insuranceFrontScanHelper.getImgHeight())
                                .into(frontInsuranceImageView);
                    } catch (MalformedURLException e) {
                        Log.d(LOG_TAG, "invalid url: " + photoFrontURL);
                        // (re)load the placeholder
                        frontInsuranceImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                                                                           R.drawable.icn_camera));
                    }
                }

                if (photos.size() > 1) {
                    String photoBackURL = photos.get(1).getInsurancePhoto();
                    try {
                        URL url = new URL(photoBackURL);
                        Log.d(LOG_TAG, "valid url: " + url.toString());
                        Picasso.with(getContext()).load(photoBackURL)
                                .resize(insuranceBackScanHelper.getImgWidth(), insuranceBackScanHelper.getImgHeight())
                                .into(backInsuranceImageView);
                    } catch (MalformedURLException e) {
                        Log.d(LOG_TAG, "invalid url: " + photoBackURL);
                        // (re)load  the placeholder
                        backInsuranceImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                                                                          R.drawable.icn_camera));
                    }
                }
            }

            // (used for Review)
            insurancebackPhotoDto = new DemographicInsurancePhotoDTO();
            insurancefrontPhotoDto = new DemographicInsurancePhotoDTO();

            String insProvider = model.getInsuranceProvider();
            if (!StringUtil.isNullOrEmpty(insProvider)) {
                providerTextView.setText(model.getInsuranceProvider());
            }
            String insPlan = model.getInsurancePlan();
            if (!StringUtil.isNullOrEmpty(insPlan)) {
                planTextView.setText(insPlan);
            }
            String insNum = model.getInsuranceMemberId();
            if (!StringUtil.isNullOrEmpty(insNum)) {
                insuranceCardNumEditText.setText(insNum);
                insuranceCardNumEditText.requestFocus(); // required for CAPS hint
                view.requestFocus();
            }
        }
    }

    /**
     * Converting bitmap images to base64 and posting to server in model
     *
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
        insuranceBackScanHelper.resetTargetView();
    }

    private void setEditTexts(final View view) {
        insuranceCardNumberTextInput = (TextInputLayout) view.findViewById(R.id.insurancecardNumberLabel);
        insuranceCardNumberTextInput.setTag(getString(R.string.review_insurancecardnumber_hint));
        insuranceCardNumEditText = (EditText) view.findViewById(R.id.reviewinsurncecardnum);
        insuranceCardNumEditText.setTag(insuranceCardNumberTextInput);
        setChangeFocusListeners();
        insuranceCardNumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String idNumber = insuranceCardNumEditText.getText().toString();
                if (!StringUtil.isNullOrEmpty(idNumber)) {
                    model.setInsuranceMemberId(idNumber);
                }
            }
        });

        insuranceCardNumEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NONE) {
                    SystemUtil.hideSoftKeyboard(getActivity());
                    insuranceCardNumEditText.clearFocus();
                    view.requestFocus();
                    return true;
                }
                return false;
            }
        });

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
        Context context = getActivity();
        setGothamRoundedMediumTypeface(context,
                                       (TextView) view.findViewById(R.id.demogr_insurance_scan_insurance_frontbtn));
        setGothamRoundedMediumTypeface(context,
                                       (TextView) view.findViewById(R.id.demogr_insurance_scan_insurance_backbtn));
        setProximaNovaRegularTypeface(context,
                                      (TextView) view.findViewById(R.id.demogr_insurance_plan_label));
        setProximaNovaSemiboldTypeface(context,
                                       (TextView) view.findViewById(R.id.demogr_docs_plan));
        setProximaNovaSemiboldTypeface(context,
                                       (TextView) view.findViewById(R.id.demogr_insurance_card_type_textview));
        setProximaNovaRegularTypeface(context,
                                      (TextView) view.findViewById(R.id.demogr_insurance_provider_label));
        setProximaNovaRegularTypeface(context,
                                      (TextView) view.findViewById(R.id.demogr_insurance_card_type_abel));
        setProximaNovaSemiboldTypeface(context,
                                       (TextView) view.findViewById(R.id.demogr_docs_provider));
        setProximaNovaRegularTypeface(context,
                                      (EditText) view.findViewById(R.id.reviewinsurncecardnum));
        setProximaNovaRegularTypeface(context,
                                      (TextView) view.findViewById(R.id.demogr_insurance_card_type_abel));
        setProximaNovaSemiboldTypeface(context,
                                       (TextView) view.findViewById(R.id.demogr_insurance_card_type_textview));

        if (!StringUtil.isNullOrEmpty(insuranceCardNumEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(context, insuranceCardNumberTextInput);
        } else {
            setProximaNovaSemiboldTextInputLayout(context, insuranceCardNumberTextInput);
        }
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

    /**
     * Set the model; creates the child models of there are null
     *
     * @param model The DTO
     */
    public void setModel(DemographicInsurancePayloadDTO model) {
        this.model = model;
        List<DemographicInsurancePhotoDTO> photoDTOs = model.getInsurancePhotos();
        if (photoDTOs == null) { // create the list of photos (front and back) if null
            photoDTOs = new ArrayList<>();
            // create two empty photos DTOs
            photoDTOs.add(new DemographicInsurancePhotoDTO());
            photoDTOs.add(new DemographicInsurancePhotoDTO());
            this.model.setInsurancePhotos(photoDTOs);
        } else {
            if (photoDTOs.size() == 0) {
                // create two empty photos DTOs
                photoDTOs.add(new DemographicInsurancePhotoDTO());
                photoDTOs.add(new DemographicInsurancePhotoDTO());
            } else if (photoDTOs.size() == 1) {
                photoDTOs.add(1, new DemographicInsurancePhotoDTO()); // create the second
            }
        }

    }
}