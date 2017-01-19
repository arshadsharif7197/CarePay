package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemIdDocDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;
import com.carecloud.carepaylibray.demographics.misc.DemographicsReviewLabelsHolder;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

public class DocScannerFragment extends DocumentScannerFragment {

    private static final String LOG_TAG = DocScannerFragment.class.getSimpleName();
    private static String[] states;
    private View view;
    private ImageCaptureHelper scannerFront;
    private ImageCaptureHelper scannerBack;
    private Button scanFrontButton;
    private Button scanBackButton;
    private EditText idNumberEdit;
    private TextInputLayout idNumberInputText;
    private TextView idStateClickable;
    private TextView stateLabel;
    private DemographicIdDocPayloadDTO model;
    private DemographicMetadataEntityItemIdDocDTO idDocsMetaDTO;
    private DemographicLabelsDTO globalLabelsDTO;
    private DemographicsSettingsDTO demographicsSettingsDTO;
    private String documentsdocumentsScanFirstString = null;
    private String documentsScanBackString = null;
    private String documentsDlNumberString = null;
    private String documentsDlStateString = null;
    private String documentsHealthInsuranceString = null;
    private String documentsHaveHealthInsuranceString = null;
    private String documentsAddnotherInsuranceString = null;
    private String documentsGoldenCrossString = null;
    private String languageString = null;
    private String documentsTypeString = null;
    private String documentsCancelString = null;
    private DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = null;
    private AppCompatActivity appCompatActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // create the view
        view = inflater.inflate(getLayoutRes(), container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String demographicsSettingsDTOString = bundle.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }

        getDocumentsLabels();
        initializeUIFields();

        return view;
    }

    /**
     * documents labels
     */
    public void getDocumentsLabels() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                if (demographicsSettingsLabelsDTO != null) {
                    documentsdocumentsScanFirstString = demographicsSettingsLabelsDTO.getDocumentsScanFirstLabel();
                    documentsScanBackString = demographicsSettingsLabelsDTO.getDocumentsScanBackLabel();
                    documentsDlNumberString = demographicsSettingsLabelsDTO.getDocumentsDlNumberLabel();
                    documentsDlStateString = demographicsSettingsLabelsDTO.getDocumentsDlStateLabel();
                    documentsHealthInsuranceString = demographicsSettingsLabelsDTO.getDocumentsHealthInsuranceLabel();
                    documentsHaveHealthInsuranceString = demographicsSettingsLabelsDTO.getDocumentsHaveHealthInsuranceLabel();
                    documentsAddnotherInsuranceString = demographicsSettingsLabelsDTO.getDocumentsAddnotherInsuranceLabel();
                    documentsGoldenCrossString = demographicsSettingsLabelsDTO.getDocumentsGoldenCrossLabel();
                    documentsTypeString = demographicsSettingsLabelsDTO.getDocumentsTypeLabel();
                    documentsCancelString = demographicsSettingsLabelsDTO.getDemographicsCancelLabel();

                }
            }
        }
    }

    protected int getLayoutRes() {
        return R.layout.fragment_demographics_scan_license;
    }

    private void getOptions() {
        // init states
        if (idDocsMetaDTO != null
                && idDocsMetaDTO.properties != null
                && idDocsMetaDTO.properties.identityDocumentState != null) {
            List<MetadataOptionDTO> optionDTOs = idDocsMetaDTO.properties.identityDocumentState.options;
            List<String> statesStrings = new ArrayList<>();
            for (MetadataOptionDTO optionDTO : optionDTOs) {
                statesStrings.add(optionDTO.getLabel());
            }
            states = statesStrings.toArray(new String[0]);
        } else {
            states = new String[1];
            states[0] = CarePayConstants.NOT_DEFINED;
        }
    }

    private void initializeUIFields() {
        // fetch the options
        getOptions();
        setEditText();
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
        }
        ImageView imageFront = (ImageView) view.findViewById(R.id.demogrDocsFrontScanImage);
        scannerFront = new ImageCaptureHelper(getActivity(), imageFront, demographicsSettingsLabelsDTO);

        ImageView imageBack = (ImageView) view.findViewById(R.id.demogrDocsBackScanImage);
        scannerBack = new ImageCaptureHelper(getActivity(), imageBack, demographicsSettingsLabelsDTO);

        scanFrontButton = (Button) view.findViewById(R.id.demogrDocsFrontScanButton);
        scanFrontButton.setText(documentsdocumentsScanFirstString);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[] { 8, 8, 8, 8, 8, 8, 8, 8 });
        shape.setStroke(3, ContextCompat.getColor(getActivity(), R.color.settings_toolbar_color));
        scanFrontButton.setBackgroundDrawable(shape);

        scanFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(scannerFront, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
            }
        });

        scanBackButton = (Button) view.findViewById(R.id.demogrDocsBackScanButton);
        scanBackButton.setText(documentsScanBackString);
        scanBackButton.setBackgroundDrawable(shape);
        scanBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(scannerBack, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
            }
        });

        stateLabel = (TextView) view.findViewById(R.id.demogrDocsLicenseStateLabel);
        stateLabel.setTextSize(17);
        setProximaNovaRegularTypeface(appCompatActivity, stateLabel);

        stateLabel.setText(documentsDlStateString);

        idStateClickable = (TextView) view.findViewById(R.id.demogrDocsStateClickable);
        idStateClickable.setText(documentsDlStateString);
        idStateClickable.setTextColor(ContextCompat.getColor(getActivity(), R.color.settings_toolbar_color));
        idStateClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(states, documentsDlStateString, documentsCancelString, idStateClickable);
            }
        });

        setTypefaces(view);

        populateViewsFromModel();
    }

    private void setEditText() {
        String label;

        idNumberEdit = (EditText) view.findViewById(R.id.demogrDocsLicenseNumEdit);
        idNumberInputText = (TextInputLayout) view.findViewById(R.id.demogrDocsNumberInputLayout);
        idNumberEdit.setTextColor(ContextCompat.getColor(getActivity(), R.color.settings_toolbar_color));

        idNumberInputText.setTag(documentsDlNumberString);
        idNumberEdit.setTag(idNumberInputText);
        idNumberEdit.setHint(documentsDlNumberString);

        idNumberEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.v(LOG_TAG, "focus changed: " + hasFocus);
                if (hasFocus) { // show the keyboard
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });

        idNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int length, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int length, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String idNumber = idNumberEdit.getText().toString();
                if (!StringUtil.isNullOrEmpty(idNumber)) {
                    model.setIdNumber(idNumber);
                }
            }
        });

        idNumberEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NONE) {
                    Log.v(LOG_TAG, "ID scanneer IME_ACTION_DONE");
                    SystemUtil.hideSoftKeyboard(getActivity());
                    idNumberEdit.clearFocus();
                    view.requestFocus();
                    return true;
                }
                return false;
            }
        });
        idNumberEdit.clearFocus();
    }

    @Override
    protected void updateModel(TextView selectionDestination) {
        if (selectionDestination == idStateClickable) { // update 'state' field in the model
            String state = idStateClickable.getText().toString();
            if (!StringUtil.isNullOrEmpty(state)) {
                model.setIdState(state);
            }
        }
    }

    @Override
    protected void updateModelAndViewsAfterScan(ImageCaptureHelper scanner) { // license has been scanned
        if (bitmap != null) {
            if (scanner == scannerFront) {
                // change button caption to 'rescan'
                scanFrontButton.setText(documentsdocumentsScanFirstString);
                // save from image
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicIdDocPhotoDTO frontDTO = model.getIdDocPhothos().get(0);
                frontDTO.setIdDocPhoto(imageAsBase64); // create the image dto
            } else if (scanner == scannerBack) {
                // change button caption to 'rescan'
                scanBackButton.setText(documentsScanBackString);
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicIdDocPhotoDTO backDTO = model.getIdDocPhothos().get(1);
                backDTO.setIdDocPhoto(imageAsBase64); // create the image dto
            }
        }
    }

    @Override
    public void populateViewsFromModel() {
        if (model != null) {
            String licenseNum = model.getIdNumber();
            if (!StringUtil.isNullOrEmpty(licenseNum)) {
                idNumberEdit.setText(licenseNum);
                idNumberEdit.requestFocus(); // required for CAPS hint
                view.requestFocus();
            }
            String state = model.getIdState();
            if (!StringUtil.isNullOrEmpty(state)) {
                idStateClickable.setText(state);
            }

            // add front image
            String frontPic = model.getIdDocPhothos().get(0).getIdDocPhoto();
            if (!StringUtil.isNullOrEmpty(frontPic)) {
                try {
                    URL url = new URL(frontPic);
                    Log.v(LOG_TAG, "valid url: " + url.toString());
                    Picasso.with(getContext()).load(frontPic)
                            .resize(scannerFront.getImgWidth(), scannerFront.getImgHeight())
                            .into(scannerFront.getImageViewTarget());
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "invalid url: " + frontPic);
                    scannerFront.getImageViewTarget().setImageDrawable(ContextCompat.getDrawable(getActivity(),
                            R.drawable.icn_camera));
                }
            }
            // add back image
            String backPic = model.getIdDocPhothos().get(1).getIdDocPhoto();
            if (!StringUtil.isNullOrEmpty(backPic)) {
                try {
                    URL url = new URL(backPic);
                    Log.v(LOG_TAG, "valid url: " + url.toString());
                    Picasso.with(getContext()).load(backPic)
                            .resize(scannerBack.getImgWidth(), scannerBack.getImgHeight())
                            .into(scannerBack.getImageViewTarget());
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "invalid url: " + backPic);
                    scannerBack.getImageViewTarget().setImageDrawable(ContextCompat.getDrawable(getActivity(),
                            R.drawable.icn_camera));
                }
            }
        }
    }

    @Override
    protected void setTypefaces(View view) {
        Context context = getActivity();
        SystemUtil.setGothamRoundedMediumTypeface(context, scanFrontButton);
        SystemUtil.setGothamRoundedMediumTypeface(context, scanBackButton);

        SystemUtil.setProximaNovaRegularTypeface(context, stateLabel);
        SystemUtil.setProximaNovaSemiboldTypeface(context, idStateClickable);
        SystemUtil.setProximaNovaSemiboldTypeface(context, idNumberEdit);

        if (!StringUtil.isNullOrEmpty(idNumberEdit.getText().toString())) {
            SystemUtil.setProximaNovaExtraboldTypefaceInput(context, idNumberInputText);
        } else {
            SystemUtil.setProximaNovaSemiboldTextInputLayout(context, idNumberInputText);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (imageCaptureHelper == scannerFront) {
            scanFrontButton.setText(documentsdocumentsScanFirstString);
        } else if (imageCaptureHelper == scannerBack) {
            scanBackButton.setText(documentsScanBackString);
        }
        stateLabel.setText(documentsDlStateString);

    }

    @Override
    public int getImageShape() {
        return ImageCaptureHelper.RECTANGULAR_IMAGE;
    }

    public DemographicIdDocPayloadDTO getModel() {
        return model;
    }

    /**
     * Sets the DTO for this fragment; it creates the required child DTO if they are null
     *
     * @param model The model
     */
    public void setModel(@NonNull DemographicIdDocPayloadDTO model) {
        this.model = model;
        List<DemographicIdDocPhotoDTO> photoDTOs = model.getIdDocPhothos();
        if (photoDTOs == null) { // create the list of photos (front and back) if null
            photoDTOs = new ArrayList<>();
            // create two empty photos DTOs
            photoDTOs.add(new DemographicIdDocPhotoDTO());
            photoDTOs.add(new DemographicIdDocPhotoDTO());
            this.model.setIdDocPhothos(photoDTOs);
        } else {
            if (photoDTOs.size() == 0) {
                // create two empty photos DTOs
                photoDTOs.add(new DemographicIdDocPhotoDTO());
                photoDTOs.add(new DemographicIdDocPhotoDTO());
            } else if (photoDTOs.size() == 1) {
                photoDTOs.add(1, new DemographicIdDocPhotoDTO()); // create the second
            }
        }
    }

    public void setIdDocsMetaDTO(DemographicMetadataEntityItemIdDocDTO idDocsMetaDTO) {
        this.idDocsMetaDTO = idDocsMetaDTO;
    }
}