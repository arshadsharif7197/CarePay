package com.carecloud.carepaylibray.demographics.scanner;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment with insurance scanning functionality
 */

public class InsuranceDocumentScannerFragment extends DocumentScannerFragment {

    private static final String LOG_TAG = InsuranceDocumentScannerFragment.class.getSimpleName();

    private String[] planDataArray;
    private String[] providerDataArray;
    private String[] cardTypeDataArray;


    private boolean isFrontScan;


    private DemographicInsurancePayloadDTO            insuranceDTO;
    private DemographicMetadataEntityItemInsuranceDTO insuranceMetadataDTO;
    private DemographicLabelsDTO                      globalLabelsDTO;
    private int                                       index;
    private View                                      view;

    private HealthInsuranceFragment.InsuranceDocumentScannerListener documentCallback;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        initializeDTOs();

        // create the view
        view = inflater.inflate(R.layout.fragment_document_scan_insurance, container, false);

        initializeUIFields(view);
        initializeToolBar(view);
        initUpdateButton(view);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            documentCallback = (HealthInsuranceFragment.InsuranceDocumentScannerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement InsuranceDocumentScannerFragment");
        }
    }

    private void initializeDTOs() {

        insuranceDTO = DtoHelper.getConvertedDTO(DemographicInsurancePayloadDTO.class, getArguments());
        insuranceMetadataDTO = DtoHelper.getConvertedDTO(DemographicMetadataEntityItemInsuranceDTO.class, getArguments());
        globalLabelsDTO = DtoHelper.getConvertedDTO(DemographicLabelsDTO.class, getArguments());
        index = DtoHelper.getConvertedDTO(Integer.class, getArguments());
    }

    private void initUpdateButton(View view){
        Button addButton = (Button)view.findViewById(R.id.saveHealthInsuranceButton);
        addButton.setText(globalLabelsDTO.getDemographicsInsuranceUpdateButton());
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentCallback.updateInsuranceDTO(index, insuranceDTO);
                documentCallback.navigateToParentFragment();
            }
        });
    }

    private void initializeToolBar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.demographics_review_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.demographics_review_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        title.setText(globalLabelsDTO.getDemographicsInsuranceTitle());
        toolbar.setTitle("");
        Drawable closeIcon = ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_close);
        toolbar.setNavigationIcon(closeIcon);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentCallback.navigateToParentFragment();
            }
        });
    }

    private void initializeUIFields(View view) {
        getOptions();

        String label;

        TextView insurancePlanLabel = (TextView) view.findViewById(R.id.demogr_insurance_plan_label);
        label = insuranceMetadataDTO == null ? CarePayConstants.NOT_DEFINED : insuranceMetadataDTO.getProperties().getInsurancePlan().getLabel();
        insurancePlanLabel.setText(label);

        TextView insuranceProviderLabel = (TextView) view.findViewById(R.id.demogr_insurance_provider_label);
        label = globalLabelsDTO.getDemographicsInsurancePayerLabel();
        insuranceProviderLabel.setText(label);

        TextView insuranceTypeLabel = (TextView) view.findViewById(R.id.demogr_insurance_card_type_abel);
        label = globalLabelsDTO.getDemographicsDocumentsInsTypeLabel();
        insuranceTypeLabel.setText(label);

        TextView insuranceCardNumEditText = (EditText) view.findViewById(R.id.reviewinsurncecardnum);
        label = insuranceMetadataDTO == null ? CarePayConstants.NOT_DEFINED : insuranceMetadataDTO.getProperties().getInsuranceMemberId().getLabel();
        insuranceCardNumEditText.setHint(label);

        TextView insuranceGroupNumEditText = (EditText) view.findViewById(R.id.reviewinsurncegroupnum);
        label = globalLabelsDTO.getDemographicsInsuranceGroupNumberLabel();
        insuranceGroupNumEditText.setHint(label);

        Button btnScanFrontInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_frontbtn);
        label = globalLabelsDTO.getDemographicsInsurancePhotoOfCardFront();
        btnScanFrontInsurance.setText(label);
        btnScanFrontInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageFront, true, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
            }
        });

        Button btnScanBackInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_backbtn);
        label = globalLabelsDTO.getDemographicsInsurancePhotoOfCardBack();
        btnScanBackInsurance.setText(label);
        btnScanBackInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "scan insurance");
                selectImage(imageBack, false, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);

            }
        });

        // 'Cancel' label
        final String cancelLabel = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsCancelLabel();
        // 'Choose' label
        label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsChooseLabel();

        final TextView providerTextView = (TextView) view.findViewById(R.id.demogr_docs_provider);
        providerTextView.setText(label);
        final String selectProviderTitle = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsTitleSelectProvider();
        providerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(providerDataArray, selectProviderTitle, cancelLabel, providerTextView);
            }
        });

        final TextView cardTypeTextView = (TextView) view.findViewById(R.id.demogr_insurance_card_type_textview);
        cardTypeTextView.setText(label);
        final String selectTypeTitle = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsTitleCardType();
        cardTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(cardTypeDataArray, selectTypeTitle, cancelLabel, cardTypeTextView);
            }
        });

        final TextView planTextView = (TextView) view.findViewById(R.id.demogr_docs_plan);
        enablePlanClickable(false);
        label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsChoosePlanLabel();
        planTextView.setText(label);
        final String selectPlanTitle = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsTitleSelectPlan();
        planTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(planDataArray, selectPlanTitle, cancelLabel, planTextView);
            }
        });

        setEditTexts(view);
        setTypefaces(view);
        populateViewsFromModel(view);
    }

    private void getOptions() {
        if (insuranceMetadataDTO == null || insuranceMetadataDTO.getProperties() == null) {
            return;
        }

        List<MetadataOptionDTO> optionDTOs;
        // init the providers
        if (insuranceMetadataDTO.getProperties().getInsuranceProvider() != null
                && insuranceMetadataDTO.getProperties().getInsuranceProvider().getOptions() != null) {
            optionDTOs = insuranceMetadataDTO.getProperties().getInsuranceProvider().getOptions();
            List<String> providers = new ArrayList<>();
            for (MetadataOptionDTO o : optionDTOs) {
                providers.add(o.getLabel());
            }
            providerDataArray = providers.toArray(new String[0]);
        } else {
            providerDataArray = new String[1];
            providerDataArray[0] = CarePayConstants.NOT_DEFINED;
        }

        // init the plans
        if (insuranceMetadataDTO.getProperties().getInsurancePlan() != null &&
                insuranceMetadataDTO.getProperties().getInsurancePlan().getOptions() != null) {
            optionDTOs = insuranceMetadataDTO.getProperties().getInsurancePlan().getOptions();
            List<String> plans = new ArrayList<>();
            for (MetadataOptionDTO o : optionDTOs) {
                plans.add(o.getLabel());
            }
            planDataArray = plans.toArray(new String[0]);
        } else {
            planDataArray = new String[1];
            planDataArray[0] = CarePayConstants.NOT_DEFINED;
        }

        // init the type options
        if (insuranceMetadataDTO.getProperties().getInsuranceType() != null &&
                insuranceMetadataDTO.getProperties().getInsuranceType().getOptions() != null) {
            optionDTOs = insuranceMetadataDTO.getProperties().getInsuranceType().getOptions();
            List<String> cardTypes = new ArrayList<>();
            for (MetadataOptionDTO o : optionDTOs) {
                cardTypes.add(o.getLabel());
            }
            cardTypeDataArray = cardTypes.toArray(new String[0]);
        } else {
            cardTypeDataArray = new String[1];
            cardTypeDataArray[0] = CarePayConstants.NOT_DEFINED;
        }
    }

    @Override
    protected void updateModel(TextView selectionDestination) {
        final TextView providerTextView = (TextView) view.findViewById(R.id.demogr_docs_provider);
        final TextView cardTypeTextView = (TextView) view.findViewById(R.id.demogr_insurance_card_type_textview);
        final TextView planTextView = (TextView) view.findViewById(R.id.demogr_docs_plan);

        if (selectionDestination == providerTextView) { // update 'id type' field in the insuranceDTO
            String provider = selectionDestination.getText().toString();
            if (!StringUtil.isNullOrEmpty(provider)) {
                insuranceDTO.setInsuranceProvider(provider);
            }
        } else if (selectionDestination == planTextView) { // update 'state' field in the insuranceDTO
            String plan = planTextView.getText().toString();
            if (!StringUtil.isNullOrEmpty(plan)) {
                insuranceDTO.setInsurancePlan(plan);
            }
        } else if (selectionDestination == cardTypeTextView) {
            String type = cardTypeTextView.getText().toString();
            if (!StringUtil.isNullOrEmpty(type)) {
                insuranceDTO.setInsuranceType(type);
            }
        }
    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        Button btnScanFrontInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_frontbtn);
        Button btnScanBackInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_backbtn);
        if (bitmap != null) {
            if (isFrontScan) {
                // change button caption to 'rescan'
                btnScanFrontInsurance.setText(globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED:globalLabelsDTO.getDemographicsDocumentsRescanFrontLabel());
                // save from image
                String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicInsurancePhotoDTO frontDTO = insuranceDTO.getInsurancePhotos().get(0);
                frontDTO.setInsurancePhoto(imageAsBase64); // create the image dto
            } else {
                // change button caption to 'rescan'
                btnScanBackInsurance.setText(globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED:globalLabelsDTO.getDemographicsDocumentsRescanBackLabel());
                String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicInsurancePhotoDTO backDTO = insuranceDTO.getInsurancePhotos().get(1);
                backDTO.setInsurancePhoto(imageAsBase64); // create the image dto
            }
        }
    }

    /**
     * initializing view from the insuranceDTO
     */
    @Override
    public void populateViewsFromModel(View view) {

        TextView insuranceCardNumEditText = (EditText) view.findViewById(R.id.reviewinsurncecardnum);
        TextView insuranceGroupNumEditText = (EditText) view.findViewById(R.id.reviewinsurncegroupnum);
        imageFront = (ImageView) view.findViewById(R.id.demogr_insurance_frontimage);
        Button btnScanFrontInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_frontbtn);
        imageBack = (ImageView) view.findViewById(R.id.demogr_insurance_backimage);
        Button btnScanBackInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_backbtn);
        final TextView providerTextView = (TextView) view.findViewById(R.id.demogr_docs_provider);
        final TextView cardTypeTextView = (TextView) view.findViewById(R.id.demogr_insurance_card_type_textview);
        final TextView planTextView = (TextView) view.findViewById(R.id.demogr_docs_plan);
        if (insuranceDTO != null) {
            // populate with images
            List<DemographicInsurancePhotoDTO> photos = insuranceDTO.getInsurancePhotos();
            if (photos != null && photos.size() > 0) {
                // add the first photo
                    String photoFrontURL = photos.get(0).getInsurancePhoto();
                    try {
                        URL url = new URL(photoFrontURL);
                        Log.d(LOG_TAG, "valid url: " + url.toString());
                        Picasso.with(getActivity()).load(photoFrontURL)
                                .fit().centerCrop()
                                .into(imageFront);
                        String label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsRescanFrontLabel();
                        btnScanFrontInsurance.setText(label);
                    } catch (MalformedURLException e) {
                        Bitmap bitmap;
                        if (!StringUtil.isNullOrEmpty(photoFrontURL) && (bitmap = SystemUtil.convertStringToBitmap(photoFrontURL)) != null) {
                            Log.v(LOG_TAG, "load as base64");
                            imageFront.setImageBitmap(bitmap);
                        } else {
                            Log.v(LOG_TAG, "load as the placeholder");
                            // if no, (re)-load the placeholder
                            imageBack.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                                                                              R.drawable.icn_camera));
                        }
                    }

                // add the second photo
                if (photos.size() > 1) {
                    String photoBackURL = photos.get(1).getInsurancePhoto();
                    try {
                        URL url = new URL(photoBackURL);
                        Log.d(LOG_TAG, "valid url: " + url.toString());
                        Picasso.with(getActivity()).load(photoBackURL)
                                .fit().centerCrop()
                                .into(imageBack);
                        String label1 = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsRescanBackLabel();
                        btnScanBackInsurance.setText(label1);
                    } catch (MalformedURLException e) {
                        Bitmap bitmap;
                        if (!StringUtil.isNullOrEmpty(photoBackURL) && (bitmap = SystemUtil.convertStringToBitmap(photoBackURL)) != null) {
                            Log.v(LOG_TAG, "load as base64");
                            imageBack.setImageBitmap(bitmap);
                        } else {
                            Log.v(LOG_TAG, "load as the placeholder");
                            // if no, (re)-load the placeholder
                            imageBack.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                                                                              R.drawable.icn_camera));
                        }
                    }
                }
            }

            String insProvider = insuranceDTO.getInsuranceProvider();
            if (!StringUtil.isNullOrEmpty(insProvider)) {
                providerTextView.setText(insuranceDTO.getInsuranceProvider());
            }
            String insPlan = insuranceDTO.getInsurancePlan();
            if (!StringUtil.isNullOrEmpty(insPlan)) {
                planTextView.setText(insPlan);
            }
            String insNum = insuranceDTO.getInsuranceMemberId();
            if (!StringUtil.isNullOrEmpty(insNum)) {
                insuranceCardNumEditText.setText(insNum);
                insuranceCardNumEditText.requestFocus(); // required for CAPS hint
                view.requestFocus();
            }

            String groupNum = insuranceDTO.getInsuranceGroupId();
            if (!StringUtil.isNullOrEmpty(groupNum)) {
                insuranceGroupNumEditText.setText(groupNum);
                insuranceGroupNumEditText.requestFocus();
                view.requestFocus();
            }
            String insCardType = insuranceDTO.getInsuranceType();
            if (!StringUtil.isNullOrEmpty(insCardType)) {
                cardTypeTextView.setText(insCardType);
            }
        }


    }

    private void setEditTexts(final View view) {
        TextInputLayout insuranceCardNumberTextInput = (TextInputLayout) view.findViewById(R.id.insurancecardNumberLabel);
        final EditText insuranceCardNumEditText = (EditText) view.findViewById(R.id.reviewinsurncecardnum);
        String hint = insuranceMetadataDTO == null ? CarePayConstants.NOT_DEFINED : insuranceMetadataDTO.getProperties().getInsuranceMemberId().getLabel();
        insuranceCardNumberTextInput.setTag(hint);
        insuranceCardNumEditText.setTag(insuranceCardNumberTextInput);
        insuranceCardNumEditText.setHint(hint);

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
                    insuranceDTO.setInsuranceMemberId(idNumber);
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

        TextInputLayout insuranceGroupNumberTextInput = (TextInputLayout) view.findViewById(R.id.insurancegroupNumberLabel);
        final EditText insuranceGroupNumEditText = (EditText) view.findViewById(R.id.reviewinsurncegroupnum);
        hint = globalLabelsDTO.getDemographicsInsuranceGroupNumberLabel();
        insuranceGroupNumberTextInput.setTag(hint);
        insuranceGroupNumEditText.setTag(insuranceGroupNumberTextInput);
        insuranceGroupNumEditText.setHint(hint);

        insuranceGroupNumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String groupNumber = insuranceGroupNumEditText.getText().toString();
                if (!StringUtil.isNullOrEmpty(groupNumber)) {
                    insuranceDTO.setInsuranceGroupId(groupNumber);
                }
            }
        });

        insuranceGroupNumEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NONE) {
                    SystemUtil.hideSoftKeyboard(getActivity());
                    insuranceGroupNumEditText.clearFocus();
                    view.requestFocus();
                    return true;
                }
                return false;
            }
        });

        insuranceGroupNumEditText.clearFocus();
    }

    @Override
    protected void setChangeFocusListeners() {
        View.OnFocusChangeListener focusListener =  new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        };
        TextView insuranceCardNumEditText = (EditText) view.findViewById(R.id.reviewinsurncecardnum);
        insuranceCardNumEditText.setOnFocusChangeListener(focusListener);

        TextView insuranceGroupNumEditText = (EditText) view.findViewById(R.id.reviewinsurncegroupnum);
        insuranceGroupNumEditText.setOnFocusChangeListener(focusListener);
    }

    @Override
    protected void setTypefaces(View view) {

        Context context = getActivity();
        Button btnScanFrontInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_frontbtn);
        SystemUtil.setGothamRoundedMediumTypeface(context, btnScanFrontInsurance);
        Button btnScanBackInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_backbtn);
        SystemUtil.setGothamRoundedMediumTypeface(context, btnScanBackInsurance);
        TextView insurancePlanLabel = (TextView) view.findViewById(R.id.demogr_insurance_plan_label);
        SystemUtil.setProximaNovaRegularTypeface(context, insurancePlanLabel);
        final TextView planTextView = (TextView) view.findViewById(R.id.demogr_docs_plan);
        SystemUtil.setProximaNovaSemiboldTypeface(context, planTextView);
        final TextView cardTypeTextView = (TextView) view.findViewById(R.id.demogr_insurance_card_type_textview);
        SystemUtil.setProximaNovaSemiboldTypeface(context, cardTypeTextView);
        TextView insuranceProviderLabel = (TextView) view.findViewById(R.id.demogr_insurance_provider_label);
        SystemUtil.setProximaNovaRegularTypeface(context, insuranceProviderLabel);
        TextView insuranceTypeLabel = (TextView) view.findViewById(R.id.demogr_insurance_card_type_abel);
        SystemUtil.setProximaNovaRegularTypeface(context, insuranceTypeLabel);
        final TextView providerTextView = (TextView) view.findViewById(R.id.demogr_docs_provider);
        SystemUtil.setProximaNovaSemiboldTypeface(context, providerTextView);
        TextView insuranceCardNumEditText = (EditText) view.findViewById(R.id.reviewinsurncecardnum);
        SystemUtil.setProximaNovaRegularTypeface(context, insuranceCardNumEditText);
        TextInputLayout insuranceCardNumberTextInput = (TextInputLayout) view.findViewById(R.id.insurancecardNumberLabel);
        if (!StringUtil.isNullOrEmpty(insuranceCardNumEditText.getText().toString())) {
            SystemUtil.setProximaNovaExtraboldTypefaceInput(context, insuranceCardNumberTextInput);
        } else {
            SystemUtil.setProximaNovaSemiboldTextInputLayout(context, insuranceCardNumberTextInput);
        }

        TextView insuranceGroupNumEditText = (EditText) view.findViewById(R.id.reviewinsurncegroupnum);
        SystemUtil.setProximaNovaRegularTypeface(context, insuranceGroupNumEditText);
        TextInputLayout insuranceGroupNumberTextInput = (TextInputLayout) view.findViewById(R.id.insurancegroupNumberLabel);
        if (!StringUtil.isNullOrEmpty(insuranceGroupNumEditText.getText().toString())) {
            SystemUtil.setProximaNovaExtraboldTypefaceInput(context, insuranceGroupNumberTextInput);
        } else {
            SystemUtil.setProximaNovaSemiboldTextInputLayout(context, insuranceGroupNumberTextInput);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Button btnScanFrontInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_frontbtn);
        Button btnScanBackInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_backbtn);
        String label;
        if (isFrontScan) {
            label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsRescanFrontLabel();
            btnScanFrontInsurance.setText(label);
        } else {
            label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsRescanBackLabel();
            btnScanBackInsurance.setText(label);
        }
    }

    @Override
    public ImageCaptureHelper.ImageShape getImageShape() {
        return ImageCaptureHelper.ImageShape.RECTANGULAR;
    }

    /**
     * Set the insuranceDTO; creates the child models of there are null
     *
     * @param insuranceDTO The DTO
     */
    public void setInsuranceDTO(DemographicInsurancePayloadDTO insuranceDTO, String placeholderBase64) {
        this.insuranceDTO = insuranceDTO;
        List<DemographicInsurancePhotoDTO> photoDTOs = insuranceDTO.getInsurancePhotos();
        if (photoDTOs == null) { // create the list of photos (front and back) if null
            photoDTOs = new ArrayList<>();
            // create two empty photos DTOs
            DemographicInsurancePhotoDTO frontPhoto = new DemographicInsurancePhotoDTO();
            DemographicInsurancePhotoDTO backPhoto = new DemographicInsurancePhotoDTO();
            frontPhoto.setInsurancePhoto(placeholderBase64);
            backPhoto.setInsurancePhoto(placeholderBase64);
            photoDTOs.add(frontPhoto);
            photoDTOs.add(backPhoto);
            this.insuranceDTO.setInsurancePhotos(photoDTOs);
            Log.v("ins_scanner", "loaded both placeholder (null)");
        } else {
            if (photoDTOs.size() == 0) {
                // create two empty photos DTOs
                DemographicInsurancePhotoDTO frontPhoto = new DemographicInsurancePhotoDTO();
                DemographicInsurancePhotoDTO backPhoto = new DemographicInsurancePhotoDTO();
                frontPhoto.setInsurancePhoto(placeholderBase64);
                backPhoto.setInsurancePhoto(placeholderBase64);
                photoDTOs.add(frontPhoto);
                photoDTOs.add(backPhoto);
                Log.v("ins_scanner", "loaded both placeholder (size 0)");
            } else if (photoDTOs.size() == 1) {
                DemographicInsurancePhotoDTO backPhoto = new DemographicInsurancePhotoDTO();
                backPhoto.setInsurancePhoto(placeholderBase64);
                photoDTOs.add(1, backPhoto); // create the second
                Log.v("ins_scanner", "loaded second placeholder");
            }
        }
    }

    public DemographicInsurancePayloadDTO getInsuranceDTO() {
        return insuranceDTO;
    }

    @Override
    protected void enablePlanClickable(boolean enabled) {
        final TextView planTextView = (TextView) view.findViewById(R.id.demogr_docs_plan);
        if (enabled) {
            String label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsChooseLabel();
            planTextView.setText(label);
            planTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            planTextView.setEnabled(true);
        } else {
            planTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.light_gray));
            planTextView.setEnabled(false);
        }
    }

    @Override
    protected void showChooseDialog(final String[] options, String title, String cancelLabel,
                                    final TextView selectionDestination) {
        final TextView providerTextView = (TextView) view.findViewById(R.id.demogr_docs_provider);

        SystemUtil.showChooseDialog(getActivity(),
                                    options, title, cancelLabel,
                                    selectionDestination,
                                    new SystemUtil.OnClickItemCallback() {
                                        @Override
                                        public void executeOnClick(TextView destination, String selectedOption) {
                                            updateModel(selectionDestination);
                                            if (selectionDestination == providerTextView) {
                                                enablePlanClickable(true);
                                            }
                                        }
                                    });
    }
}