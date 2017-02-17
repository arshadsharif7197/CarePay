package com.carecloud.carepay.practice.library.customdialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.scanner.InsuranceScannerFragment;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;

import com.carecloud.carepaylibray.utils.StringUtil;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTextInputLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import com.carecloud.carepaylibray.utils.SystemUtil;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by prem_mourya on 11/23/2016.
 */

public class CheckinInsuranceEditDialog extends BasePracticeDialog {

    private Context context;
    private LayoutInflater inflater;
    private View view;

    private String[] planDataArray;
    private String[] providerDataArray;
    private String[] cardTypeDataArray;

    private ImageCaptureHelper insuranceFrontScanHelper;
    private ImageCaptureHelper insuranceBackScanHelper;
    private Button btnScanFrontInsurance;
    private Button btnScanBackInsurance;
    private EditText insuranceCardNumEditText;
    private EditText insuranceGroupNumEditText;
    private TextView planTextView;
    private TextView providerTextView;
    private TextView cardTypeTextView;
    private ImageView frontInsuranceImageView;
    private ImageView backInsuranceImageView;
    private TextInputLayout insuranceCardNumberTextInput;
    private TextView insurancePlanLabel;
    private TextView insuranceProviderLabel;
    private TextView insuranceTypeLabel;

    private DemographicInsurancePayloadDTO insuranceDTO;
    private DemographicMetadataEntityItemInsuranceDTO insuranceMetadataDTO;
    private DemographicLabelsDTO globalLabelsDTO;
    private DemographicInsurancePhotoDTO insurancefrontPhotoDto;
    private DemographicInsurancePhotoDTO insurancebackPhotoDto;
    private DemographicDTO demographicDTO;
    private byte[] scanDocFrontByte;
    private byte[] scanDocBackByte;
    private Button saveChangesButton;
    private Button removeButton;
    private int index;

    private HealthInsuranceFragment.InsuranceDocumentScannerListener  insuranceDocumentScannerListener;

    protected Bitmap bitmap;


    /**
     * for insurance Dialog edit
     * @param context context
     * @param isFooterVisibity boolean
     *  @param demographicDTO DTO
     *   @param index for index
     */
    public CheckinInsuranceEditDialog(Context context,boolean isFooterVisibity,DemographicDTO demographicDTO,int index, HealthInsuranceFragment.InsuranceDocumentScannerListener  insuranceDocumentScannerListener){
        super(context,demographicDTO.getMetadata().getLabels().getDemographicsCancelLabel(),isFooterVisibity);
        this.insuranceDocumentScannerListener = insuranceDocumentScannerListener;
        this.context = context;
        this.index =index;
        this.demographicDTO =demographicDTO;
        if( index >=0 ){
            insuranceDTO = demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(index);
        } else {
            insuranceDTO = new DemographicInsurancePayloadDTO();
            insuranceDTO.getInsurancePhotos().add(new DemographicInsurancePhotoDTO());
            insuranceDTO.getInsurancePhotos().add(new DemographicInsurancePhotoDTO());
        }
        this.globalLabelsDTO =demographicDTO.getMetadata().getLabels();
        DemographicMetadataEntityInsurancesDTO demographicMetadataEntityInsurancesDTO  = demographicDTO.getMetadata().getDataModels().demographic.insurances;
        insuranceMetadataDTO
                = (demographicMetadataEntityInsurancesDTO == null ? null : demographicMetadataEntityInsurancesDTO.properties.items.insurance);
        inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAddContentView(inflater);
        initializeUIFields();
    }

    private void initializeUIFields() {

        getOptions();


        saveChangesButton = (Button)view.findViewById(R.id.saveChangesButton);
        removeButton = (Button)view.findViewById(R.id.removeutton);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insuranceDocumentScannerListener.updateInsuranceDTO(index,
                                                                    insuranceDTO);
                dismiss();
            }
        });
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index>=0) {
                    onRemoveChanges();
                }
                dismiss();
            }
        });

        saveChangesButton.setText(this.globalLabelsDTO.getDemographicsInsuranceSave());
        removeButton.setText(this.globalLabelsDTO.getDocumentsRemove());
        insurancePlanLabel = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.demogr_insurance_plan_label);
        insurancePlanLabel.setText(this.globalLabelsDTO.getDemographicsDocumentsChoosePlanLabel() );

        insuranceProviderLabel = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.demogr_insurance_provider_label);
        insuranceProviderLabel.setText(this.globalLabelsDTO.getDemographicsTitleSelectProvider() );

        insuranceTypeLabel = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.demogr_insurance_card_type_abel);
        insuranceTypeLabel.setText(this.globalLabelsDTO.getDemographicsDocumentsInsTypeLabel());

        insuranceCardNumEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.reviewinsurncecardnum);
        insuranceCardNumEditText.setHint(this.globalLabelsDTO.getDemographicsInsuranceScanInsuranceCard());

        frontInsuranceImageView = (ImageView) view.findViewById(com.carecloud.carepaylibrary.R.id.demogr_insurance_frontimage);
        insuranceFrontScanHelper = new ImageCaptureHelper((Activity) context, frontInsuranceImageView, globalLabelsDTO);

        btnScanFrontInsurance = (Button) view.findViewById(com.carecloud.carepaylibrary.R.id.demogr_insurance_scan_insurance_frontbtn);
        btnScanFrontInsurance.setText(this.globalLabelsDTO.getDemographicsDocumentsRescanFrontLabel());
        btnScanFrontInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScanViewDialog(0);
            }
        });

        backInsuranceImageView = (ImageView) view.findViewById(com.carecloud.carepaylibrary.R.id.demogr_insurance_backimage);
        insuranceBackScanHelper = new ImageCaptureHelper((Activity) context, backInsuranceImageView, globalLabelsDTO);
        btnScanBackInsurance = (Button) view.findViewById(com.carecloud.carepaylibrary.R.id.demogr_insurance_scan_insurance_backbtn);
        btnScanBackInsurance.setText(this.globalLabelsDTO.getDemographicsDocumentsRescanBackLabel());
        btnScanBackInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "scan insurance");
                onScanViewDialog(1);

            }
        });

        final String cancelLabel = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsCancelLabel();
        String label;
        label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsChooseLabel();

        providerTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.demogr_docs_provider);
        providerTextView.setText(label);
        final String selectProviderTitle = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsTitleSelectProvider();
        providerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(providerDataArray, selectProviderTitle, cancelLabel, providerTextView);
            }
        });

        cardTypeTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.demogr_insurance_card_type_textview);
        cardTypeTextView.setText(label);
        final String selectTypeTitle = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsTitleCardType();
        cardTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(cardTypeDataArray, selectTypeTitle, cancelLabel, cardTypeTextView);
            }
        });

        planTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.demogr_docs_plan);
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
        populateViewsFromModel();
        setDialogTitle(globalLabelsDTO.getDemographicsUpdateInsuranceToolbarTitle().toUpperCase());
    }

    private void setEditTexts(final View view) {
        insuranceCardNumberTextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.insurancecardNumberLabel);
        insuranceCardNumEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.reviewinsurncecardnum);
        String hint = insuranceMetadataDTO == null ? CarePayConstants.NOT_DEFINED : insuranceMetadataDTO.properties.insuranceMemberId.getLabel();
        insuranceCardNumberTextInput.setTag(hint);
        insuranceCardNumEditText.setTag(insuranceCardNumberTextInput);
        insuranceCardNumEditText.setHint(hint);

        /*TextInputLayout insuranceGroupNumberTextInput = (TextInputLayout) view.findViewById(R.id.insuranceGroupNumberLabel);
        EditText insuranceGroupNumEditText = (EditText) view.findViewById(R.id.reviewinsurnceGroupnum);
        hint = insuranceMetadataDTO == null ? CarePayConstants.NOT_DEFINED : insuranceMetadataDTO.properties.insuranceMemberId.getLabel();*/


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
                    SystemUtil.hideSoftKeyboard((Activity) context);
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
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard((Activity) context);
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
    }


    protected void setTypefaces(View view) {
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

    private void getOptions() {
        if (insuranceMetadataDTO == null || insuranceMetadataDTO.properties == null) {
            return;
        }

        List<MetadataOptionDTO> optionDTOs;
        // init the providers
        if(insuranceMetadataDTO.properties.insuranceProvider != null
                && insuranceMetadataDTO.properties.insuranceProvider.options != null) {
            optionDTOs = insuranceMetadataDTO.properties.insuranceProvider.options;
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
        if(insuranceMetadataDTO.properties.insurancePlan != null &&
                insuranceMetadataDTO.properties.insurancePlan.options != null) {
            optionDTOs = insuranceMetadataDTO.properties.insurancePlan.options;
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
        if(insuranceMetadataDTO.properties.insuranceType != null &&
                insuranceMetadataDTO.properties.insuranceType.options != null) {
            optionDTOs = insuranceMetadataDTO.properties.insuranceType.options;
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

    /**
     * Creates a generic dialog that contains a list of choices
     *
     * @param options              The choices
     * @param title                The dlg title
     * @param selectionDestination The textview where the selected option will be displayed
     */
    protected void showChooseDialog(final String[] options, String title, String cancelLabel,
                                    final TextView selectionDestination) {
        SystemUtil.showChooseDialog((Activity) context,
                options, title, cancelLabel,
                selectionDestination,
                new SystemUtil.OnClickItemCallback() {
                    @Override
                    public void executeOnClick(TextView destination, String selectedOption) {
                        updateModel(selectionDestination);
                        onEnableSaveButton();
                    }
                });
    }



    protected void updateModel(TextView selectionDestination) {
        if (selectionDestination == providerTextView) { // update 'id type' field in the insuranceDTO
            String provider = selectionDestination.getText().toString();
            if (!StringUtil.isNullOrEmpty(provider)) {
                insuranceDTO.setInsuranceProvider(provider);
                onEnableSaveButton();
            }
        } else if (selectionDestination == planTextView) { // update 'state' field in the insuranceDTO
            String plan = planTextView.getText().toString();
            if (!StringUtil.isNullOrEmpty(plan)) {
                insuranceDTO.setInsurancePlan(plan);
                onEnableSaveButton();
            }
        } else if (selectionDestination == cardTypeTextView) {
            String type = cardTypeTextView.getText().toString();
            if (!StringUtil.isNullOrEmpty(type)) {
                insuranceDTO.setInsuranceType(type);
                onEnableSaveButton();
            }
        }
    }

    protected void updateModelAndViewsAfterScan(Bitmap bitmapScan, int typeScan) {
        if (bitmapScan != null) {
            if (typeScan == 0) {
                // save from image
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmapScan, Bitmap.CompressFormat.JPEG, 90);
                DemographicInsurancePhotoDTO frontDTO = insuranceDTO.getInsurancePhotos().get(0);
                frontDTO.setInsurancePhoto(imageAsBase64); // create the image dto
            } else if (typeScan == 1) {
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmapScan, Bitmap.CompressFormat.JPEG, 90);
                DemographicInsurancePhotoDTO backDTO = insuranceDTO.getInsurancePhotos().get(1);
                backDTO.setInsurancePhoto(imageAsBase64); // create the image dto
            }
        }
    }

    /**
     * initializing view from the insuranceDTO
     */
    public void populateViewsFromModel() {
        if (insuranceDTO != null) {
            // populate with images
            List<DemographicInsurancePhotoDTO> photos = insuranceDTO.getInsurancePhotos();
            if (photos == null) {
                Log.v(LOG_TAG, InsuranceScannerFragment.class.getSimpleName() + " no insurance photos");
            } else {
                if (photos.size() > 0) {
                    String photoFrontURL = photos.get(0).getInsurancePhoto();
                    try {
                        URL url = new URL(photoFrontURL);
                        Log.d(LOG_TAG, "valid url: " + url.toString());
                        Picasso.with(context).load(photoFrontURL)
                                .resize(insuranceFrontScanHelper.getImgWidth(), insuranceFrontScanHelper.getImgHeight())
                                .into(frontInsuranceImageView);
                        String label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsRescanFrontLabel();
                        btnScanFrontInsurance.setText(label);
                    } catch (MalformedURLException e) {
                        Log.d(LOG_TAG, "invalid url: " + photoFrontURL);
                        // (re)load the placeholder
                        frontInsuranceImageView.setImageDrawable(ContextCompat.getDrawable(context,
                                com.carecloud.carepaylibrary.R.drawable.icn_camera));
                    }
                }

                if (photos.size() > 1) {
                    String photoBackURL = photos.get(1).getInsurancePhoto();
                    try {
                        URL url = new URL(photoBackURL);
                        Log.d(LOG_TAG, "valid url: " + url.toString());
                        Picasso.with(context).load(photoBackURL)
                                .resize(insuranceBackScanHelper.getImgWidth(), insuranceBackScanHelper.getImgHeight())
                                .into(backInsuranceImageView);
                        String label1 = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsRescanBackLabel();
                        btnScanBackInsurance.setText(label1);
                    } catch (MalformedURLException e) {
                        Log.d(LOG_TAG, "invalid url: " + photoBackURL);
                        // (re)load  the placeholder
                        backInsuranceImageView.setImageDrawable(ContextCompat.getDrawable(context,
                                com.carecloud.carepaylibrary.R.drawable.icn_camera));
                    }
                }
            }

            // (used for Review)
            insurancebackPhotoDto = new DemographicInsurancePhotoDTO();
            insurancefrontPhotoDto = new DemographicInsurancePhotoDTO();

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

    private void onScanViewDialog(final  int typeFace){

        new ScanDocDialog(context, this.globalLabelsDTO, false, new ScanDocDialog.SaveScanDocListener() {
            @Override
            public void onSaveScanDoc(byte[] bytes) {
                if(bytes != null && bytes.length > 0) {
                    if (typeFace == 0) {
                        onScanDocSetImage(frontInsuranceImageView, bytes,typeFace);
                    } else {
                        onScanDocSetImage(backInsuranceImageView, bytes,typeFace);
                    }
                    onEnableSaveButton();
                }
            }
        }).show();

    }

    private void onScanDocSetImage(ImageView imageView,byte[] bytes, int typeScan){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);
        updateModelAndViewsAfterScan(bitmap,typeScan);
    }

    @Override
    protected void onAddContentView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.dialog_checkin_insurance_edit, null);
        ((FrameLayout)findViewById(R.id.base_dialog_content_layout)).addView(view);
    }

    @Override
    protected void onAddFooterView(LayoutInflater inflater) {

    }

    private  void onRemoveChanges(){
            demographicDTO.getPayload().getDemographics().getPayload().getInsurances().remove(index);
            scanDocBackByte = null;
            scanDocFrontByte = null;
    }

    @Override
    protected void onDialogCancel() {
        super.onDialogCancel();
    }

    private void onEnableSaveButton(){
        saveChangesButton.setEnabled(true);
    }
}
