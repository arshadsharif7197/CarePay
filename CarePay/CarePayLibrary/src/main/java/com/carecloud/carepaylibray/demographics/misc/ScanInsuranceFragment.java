package com.carecloud.carepaylibray.demographics.misc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
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

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.DemographicMetadataDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataInsuranceOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTextInputLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ScanInsuranceFragment extends DocumentScannerFragment {

    private String[] planDataArray;
    private String[] providerDataArray;
    private String[] cardTypeDataArray;

    private View view;
    private Button btnScanFrontInsurance;
    private Button btnScanBackInsurance;
    private EditText insuranceCardNumEditText;
    private TextView planTextView;
    private TextView providerTextView;
    private TextView cardTypeTextView;
    private TextInputLayout insuranceCardNumberTextInput;
    private TextView insurancePlanLabel;
    private TextView insuranceProviderLabel;
    private TextView insuranceTypeLabel;

    private DemographicInsurancePayloadDTO insuranceDTO;
    private DemographicMetadataEntityInsurancesDTO insuranceMetadataDTO;
    private DemographicsSettingsDTO demographicsSettingsDTO;
    private String documentsdocumentsScanFirstString = null;
    private String documentsScanBackString = null;
    private String documentsDlNumberString = null;
    private String documentsDlStateString = null;
    private String documentsHealthInsuranceString = null;
    private String documentsHaveHealthInsuranceString = null;
    private String documentsAddnotherInsuranceString = null;
    private String documentsGoldenCrossString = null;
    private String documentsTypeString = null;
    private String documentsCancelString = null;
    private String documentsRemoveString = null;
    private String documentsPlanString = null;
    private String documentsProviderString = null;
    private String documentsCardTypeString = null;
    private String documentsCardNumberString = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // create the view
        view = inflater.inflate(R.layout.fragment_demographics_scan_insurance, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String demographicsSettingsDTOString = bundle.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }
        getInsuranceLabels();

        initializeUIFields();

        return view;
    }

    /**
     * documents labels
     */
    public void getInsuranceLabels() {
        documentsdocumentsScanFirstString = Label.getLabel("documents_scan_front_label");
        documentsScanBackString = Label.getLabel("documents_scan_back_label");
        documentsDlNumberString = Label.getLabel("documents_dl_number_label");
        documentsDlStateString = Label.getLabel("documents_dl_state_label");
        documentsHealthInsuranceString = Label.getLabel("documents_health_insurance_label");
        documentsHaveHealthInsuranceString = Label.getLabel("documents_have_health_insurance_label");
        documentsAddnotherInsuranceString = Label.getLabel("demographics_add_another_insurance_link");
        documentsGoldenCrossString = Label.getLabel("demographics_golden_cross_Premium");
        documentsTypeString = Label.getLabel("documents_document_type_label");
        documentsCancelString = Label.getLabel("demographics_cancel_label");
        documentsRemoveString = Label.getLabel("remove_link");
        documentsPlanString = Label.getLabel("demographics_plan_label");
        documentsProviderString = Label.getLabel("demographics_provider_label");
        documentsCardNumberString = Label.getLabel("demographics_card_number_label");
        documentsCardTypeString = Label.getLabel("demographics_card_type_label");
    }

    private void initializeUIFields() {
        getOptions();

        insurancePlanLabel = (TextView) view.findViewById(R.id.demogr_insurance_plan_label);
        insurancePlanLabel.setText(documentsPlanString);

        insuranceProviderLabel = (TextView) view.findViewById(R.id.demogr_insurance_provider_label);
        insuranceProviderLabel.setText(documentsProviderString);

        insuranceTypeLabel = (TextView) view.findViewById(R.id.demogr_insurance_card_type_abel);
        insuranceTypeLabel.setText(documentsCardTypeString);

        insuranceCardNumEditText = (EditText) view.findViewById(R.id.reviewinsurncecardnum);
        insuranceCardNumEditText.setHint(documentsCardNumberString);
        insuranceCardNumEditText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        imageFront = (ImageView) view.findViewById(R.id.demogr_insurance_frontimage);

        btnScanFrontInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_frontbtn);
        btnScanFrontInsurance.setText(documentsdocumentsScanFirstString);
        btnScanFrontInsurance.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{8, 8, 8, 8, 8, 8, 8, 8});
        shape.setStroke(3, ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        btnScanFrontInsurance.setBackgroundDrawable(shape);

        btnScanFrontInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageFront, true, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
            }
        });

        imageBack = (ImageView) view.findViewById(R.id.demogr_insurance_backimage);
        btnScanBackInsurance = (Button) view.findViewById(R.id.demogr_insurance_scan_insurance_backbtn);
        btnScanBackInsurance.setText(documentsScanBackString);
        btnScanBackInsurance.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        btnScanBackInsurance.setBackgroundDrawable(shape);

        btnScanBackInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "scan insurance");
                selectImage(imageBack, false, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);

            }
        });

        providerTextView = (TextView) view.findViewById(R.id.demogr_docs_provider);
        providerTextView.setText(documentsProviderString);
        providerTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        providerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(providerDataArray, documentsProviderString, documentsCancelString, providerTextView);
            }
        });

        cardTypeTextView = (TextView) view.findViewById(R.id.demogr_insurance_card_type_textview);
        cardTypeTextView.setText(documentsCardTypeString);
        cardTypeTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        cardTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(cardTypeDataArray, documentsCardTypeString, documentsCancelString, cardTypeTextView);
            }
        });

        planTextView = (TextView) view.findViewById(R.id.demogr_docs_plan);
        enablePlanClickable(false);
        planTextView.setText(documentsPlanString);
        planTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        planTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(planDataArray, documentsPlanString, documentsCancelString, planTextView);
            }
        });

        setEditTexts(view);
        setTypefaces(view);
        populateViewsFromModel(view);
    }

    private void getOptions() {
        if (demographicsSettingsDTO != null) {
            DemographicMetadataDTO demographicMetadataDTO = demographicsSettingsDTO.getMetadata();
            if (demographicMetadataDTO != null) {
                DemographicDataModel demographicDataModel = demographicMetadataDTO.getNewDataModel();
                DemographicDataModel.Demographic demographicsSettingsDemographicsDTO = demographicDataModel.getDemographic();
                insuranceMetadataDTO = demographicsSettingsDemographicsDTO.getInsurances();
            }
        }

        // init the providers
        if (insuranceMetadataDTO.getProperties().getItems().getInsurance().getProperties().getInsuranceProvider() != null
                && insuranceMetadataDTO.getProperties().getItems().getInsurance().getProperties().getInsuranceProvider().getOptions() != null) {
            List<MetadataInsuranceOptionDTO> optionDTOs = insuranceMetadataDTO.getProperties().getItems().getInsurance().getProperties().getInsuranceProvider().getOptions();
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
        if (insuranceMetadataDTO.getProperties().getItems().getInsurance().getProperties().getInsurancePlan() != null &&
                insuranceMetadataDTO.getProperties().getItems().getInsurance().getProperties().getInsurancePlan().getOptions() != null) {
            List<MetadataOptionDTO> optionDTOs = insuranceMetadataDTO.getProperties().getItems().getInsurance().getProperties().getInsurancePlan().getOptions();
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
        if (insuranceMetadataDTO.getProperties().getItems().getInsurance().getProperties().getInsuranceType() != null &&
                insuranceMetadataDTO.getProperties().getItems().getInsurance().getProperties().getInsuranceType().getOptions() != null) {
            List<MetadataOptionDTO> optionDTOs = insuranceMetadataDTO.getProperties().getItems().getInsurance().getProperties().getInsuranceType().getOptions();
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
        if (bitmap != null) {
            if (isFrontScan) {
                // change button caption to 'rescan'
                btnScanFrontInsurance.setText(documentsdocumentsScanFirstString);
                // save from image
                String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicInsurancePhotoDTO frontDTO = insuranceDTO.getInsurancePhotos().get(0);
                frontDTO.setInsurancePhoto(imageAsBase64); // create the image dto
            } else {
                // change button caption to 'rescan'
                btnScanBackInsurance.setText(documentsScanBackString);
                String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicInsurancePhotoDTO backDTO = insuranceDTO.getInsurancePhotos().get(1);
                backDTO.setInsurancePhoto(imageAsBase64); // create the image dto
            }
        }
    }

    @Override
    public void onCaptureFail() {

    }

    /**
     * initializing view from the insuranceDTO
     */
    @Override
    public void populateViewsFromModel(View view) {
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
                    btnScanFrontInsurance.setText(documentsdocumentsScanFirstString);
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
                        //String label1 = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsRescanBackLabel();
                        btnScanBackInsurance.setText(documentsScanBackString);
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
            String insCardType = insuranceDTO.getInsuranceType();
            if (!StringUtil.isNullOrEmpty(insCardType)) {
                cardTypeTextView.setText(insCardType);
            }
        }


    }

    private void setEditTexts(final View view) {
        insuranceCardNumberTextInput = (TextInputLayout) view.findViewById(R.id.insurancecardNumberLabel);
        insuranceCardNumEditText = (EditText) view.findViewById(R.id.reviewinsurncecardnum);
        insuranceCardNumberTextInput.setTag(documentsCardNumberString);
        insuranceCardNumEditText.setTag(insuranceCardNumberTextInput);
        insuranceCardNumEditText.setHint(documentsCardNumberString);

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
    }

    @Override
    protected void setChangeFocusListeners() {
        insuranceCardNumEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
    }

    @Override
    protected void setTypefaces(View view) {
        Context context = getActivity();
        setGothamRoundedMediumTypeface(context, btnScanFrontInsurance);
        setGothamRoundedMediumTypeface(context, btnScanBackInsurance);
        setProximaNovaRegularTypeface(context, insurancePlanLabel);
        setProximaNovaSemiboldTypeface(context, planTextView);
        setProximaNovaSemiboldTypeface(context, cardTypeTextView);
        setProximaNovaRegularTypeface(context, insuranceProviderLabel);
        setProximaNovaRegularTypeface(context, insuranceTypeLabel);
        setProximaNovaSemiboldTypeface(context, providerTextView);
        setProximaNovaRegularTypeface(context, insuranceCardNumEditText);

        if (!StringUtil.isNullOrEmpty(insuranceCardNumEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(context, insuranceCardNumberTextInput);
        } else {
            setProximaNovaSemiboldTextInputLayout(context, insuranceCardNumberTextInput);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (isFrontScan) {
            btnScanFrontInsurance.setText(documentsdocumentsScanFirstString);
            btnScanFrontInsurance.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        } else {
            btnScanBackInsurance.setText(documentsScanBackString);
            btnScanBackInsurance.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

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

    @Override
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

    public void setInsuranceMetadataDTO(DemographicMetadataEntityInsurancesDTO insuranceMetadataDTO) {
        this.insuranceMetadataDTO = insuranceMetadataDTO;
    }

    @Override
    public void enablePlanClickable(boolean enabled) {
        if (enabled) {
            planTextView.setText(documentsPlanString);
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