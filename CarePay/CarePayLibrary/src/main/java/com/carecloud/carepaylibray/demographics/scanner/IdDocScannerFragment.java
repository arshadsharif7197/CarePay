package com.carecloud.carepaylibray.demographics.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemIdDocDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;
import com.carecloud.carepaylibray.demographics.misc.DemographicsReviewLabelsHolder;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTextInputLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/13/2016.
 * Fragment for with scanning driver's license functionality
 */
public class IdDocScannerFragment extends DocumentScannerFragment {

    private static final String LOG_TAG = IdDocScannerFragment.class.getSimpleName();
    private static String[]                              states;
    private        View                                  view;
    private        ImageCaptureHelper                    scannerFront;
    private        ImageCaptureHelper                    scannerBack;
    private        Button                                scanFrontButton;
    private        Button                                scanBackButton;
    private        EditText                              idNumberEdit;
    private        TextInputLayout                       idNumberInputText;
    private        TextView                              idStateClickable;
    private        TextView                              stateLabel;
    private        DemographicIdDocPayloadDTO            model;
    private        DemographicMetadataEntityItemIdDocDTO idDocsMetaDTO;
    private        DemographicLabelsDTO                  globalLabelsDTO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // fetch the labels
        Activity activity = getActivity();
        if (activity instanceof DemographicsLabelsHolder) {
            globalLabelsDTO = ((DemographicsLabelsHolder) getActivity()).getLabelsDTO();
        } else if (activity instanceof DemographicsReviewLabelsHolder) {
            // instantiate the global labels here
        }

        // create the view
        view = inflater.inflate(R.layout.fragment_demographics_scan_license, container, false);

        initializeUIFields();

        return view;
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

        ImageView imageFront = (ImageView) view.findViewById(R.id.demogrDocsFrontScanImage);
        scannerFront = new ImageCaptureHelper(getActivity(), imageFront);

        ImageView imageBack = (ImageView) view.findViewById(R.id.demogrDocsBackScanImage);
        scannerBack = new ImageCaptureHelper(getActivity(), imageBack);

        // init views (labels and logic)
        String label;
        final String labelCancel = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsCancelLabel();
        // add click listener
        scanFrontButton = (Button) view.findViewById(R.id.demogrDocsFrontScanButton);
        label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsScanFrontLabel();
        scanFrontButton.setText(label);
        scanFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(scannerFront);
            }
        });

        scanBackButton = (Button) view.findViewById(R.id.demogrDocsBackScanButton);
        label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsScanBackLabel();
        scanBackButton.setText(label);
        scanBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(scannerBack);
            }
        });

        stateLabel = (TextView) view.findViewById(R.id.demogrDocsLicenseStateLabel);
        label = idDocsMetaDTO == null ? CarePayConstants.NOT_DEFINED : idDocsMetaDTO.properties.identityDocumentState.getLabel();
        stateLabel.setText(label);

        idStateClickable = (TextView) view.findViewById(R.id.demogrDocsStateClickable);
        label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsChooseLabel();
        idStateClickable.setText(label);
        final String titleSelectState = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsTitleSelectState();
        idStateClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(states, titleSelectState, labelCancel, idStateClickable);
            }
        });

        setTypefaces(view);

        populateViewsFromModel();
    }

    private void setEditText() {
        String label;

        idNumberEdit = (EditText) view.findViewById(R.id.demogrDocsLicenseNumEdit);
        idNumberInputText = (TextInputLayout) view.findViewById(R.id.demogrDocsNumberInputLayout);

        label = idDocsMetaDTO == null ? CarePayConstants.NOT_DEFINED : idDocsMetaDTO.properties.identityDocumentNumber.getLabel();
        idNumberInputText.setTag(label);
        idNumberEdit.setTag(idNumberInputText);
        idNumberEdit.setHint(label);

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
                scanFrontButton.setText(R.string.demogr_docs_rescan_front);
                // save from image
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicIdDocPhotoDTO frontDTO = model.getIdDocPhothos().get(0);
                frontDTO.setIdDocPhoto(imageAsBase64); // create the image dto
            } else if (scanner == scannerBack) {
                // change button caption to 'rescan'
                scanBackButton.setText(R.string.demogr_docs_rescan_back);
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
        setGothamRoundedMediumTypeface(context, scanFrontButton);
        setGothamRoundedMediumTypeface(context, scanBackButton);

        setProximaNovaRegularTypeface(context, stateLabel);
        setProximaNovaSemiboldTypeface(context, idStateClickable);
        setProximaNovaSemiboldTypeface(context, idNumberEdit);

        if (!StringUtil.isNullOrEmpty(idNumberEdit.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(context, idNumberInputText);
        } else {
            setProximaNovaSemiboldTextInputLayout(context, idNumberInputText);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String label;
        if (imageCaptureHelper == scannerFront) {
            label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsRescanFrontLabel();
            scanFrontButton.setText(label);
        } else if (imageCaptureHelper == scannerBack) {
            label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsDocumentsRescanBackLabel();
            scanBackButton.setText(label);
        }

        // invoke parent fragment to enable Next Button
        buttonsStatusCallback.enableNextButton(true);
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