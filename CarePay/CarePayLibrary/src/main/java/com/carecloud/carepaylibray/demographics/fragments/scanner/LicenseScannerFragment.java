package com.carecloud.carepaylibray.demographics.fragments.scanner;

import android.app.usage.UsageEvents;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
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
import com.carecloud.carepaylibray.demographics.models.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/13/2016.
 * Fragment for with scanning driver's license functionality
 */
public class LicenseScannerFragment extends DocumentScannerFragment {

    private static final String[] states = new String[]{
            "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY",
            "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND",
            "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",};

    private View               view;
    private ImageCaptureHelper scannerFront;
    private ImageCaptureHelper scannerBack;
    private TextView           idTypeClickable;
    private Button             scanFrontButton;
    private Button             scanBackButton;
    private EditText           idNumberEdit;
    private TextInputLayout    idNumberInputText;
    private TextView           idStateClickable;

    private DemographicIdDocPayloadDTO model;
    private String[] docTypes = {"Driver's License", "Passport"}; // these will come from b/e

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_scan_license, container, false);

        idTypeClickable = (TextView) view.findViewById(R.id.demogrDocTypeClickable);
        idTypeClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(docTypes, "Select document type", idTypeClickable);

            }
        });

        setEditText();

        ImageView imageFront = (ImageView) view.findViewById(R.id.demogrDocsFrontScanImage);
        scannerFront = new ImageCaptureHelper(getActivity(), imageFront);

        ImageView imageBack = (ImageView) view.findViewById(R.id.demogrDocsBackScanImage);
        scannerBack = new ImageCaptureHelper(getActivity(), imageBack);

        // add click listener
        scanFrontButton = (Button) view.findViewById(R.id.demogrDocsFrontScanButton);
        scanFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "scan front");
                selectImage(scannerFront);
            }
        });

        scanBackButton = (Button) view.findViewById(R.id.demogrDocsBackScanButton);
        scanBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG, "scan back");
                selectImage(scannerBack);
            }
        });


        idStateClickable = (TextView) view.findViewById(R.id.demogr_tv_state);
        idStateClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(states, "Select state", idStateClickable);
            }
        });

        setTypefaces(view);

        populateViewsFromModel();

        return view;
    }

    private void setEditText() {

        idNumberEdit = (EditText) view.findViewById(R.id.demogr_docs_license_num);
        idNumberInputText = (TextInputLayout) view.findViewById(R.id.demogrDocsNumberInputLayout);
        idNumberInputText.setTag(getString(R.string.demogrDocsNumberHint));
        idNumberEdit.setTag(idNumberInputText);

        idNumberEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) { // show the keyboard
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });

        idNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    idNumberEdit.clearFocus();
                    view.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void updateModel(TextView selectionDestination) {
        if (selectionDestination == idTypeClickable) { // update 'id type' field in the model
            String idType = selectionDestination.getText().toString();
            if (!StringUtil.isNullOrEmpty(idType)) {
                model.setIdType(idType);
            }
        } else if (selectionDestination == idStateClickable) { // update 'state' field in the model
            String state = idStateClickable.getText().toString();
            if (!StringUtil.isNullOrEmpty(state)) {
                model.setIdState(state);
            }
        }
    }

    @Override
    protected void updateModelAndViewsAfterScan(ImageCaptureHelper scanner) { // license has been scanned
        if (scanner == scannerFront) {
            if (bitmap != null) {
                // change button caption to 'rescan'
                scanFrontButton.setText(R.string.demogr_docs_rescan_front);
                // save from image
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicIdDocPhotoDTO frontDTO = model.getIdDocPhothos().get(0);
                frontDTO.setIdDocPhoto(imageAsBase64); // create the image dto
            }
        } else if (scanner == scannerBack) {
            if (bitmap != null) {
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
            String idType = model.getIdType();
            if (!StringUtil.isNullOrEmpty(idType)) {
                idTypeClickable.setText(idType);
            }
            String licenseNum = model.getIdNumber();
            if (!StringUtil.isNullOrEmpty(licenseNum)) {
                idNumberEdit.setText(licenseNum);
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
                    Picasso.with(getContext()).load(url.toString()).into(scannerFront.getImageViewTarget());
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, ProfilePictureFragment.class.getSimpleName(), e);
                }
            }
            // add back image
            String backPic = model.getIdDocPhothos().get(1).getIdDocPhoto();
            if (!StringUtil.isNullOrEmpty(frontPic)) {
                try {
                    URL url = new URL(backPic);
                    Picasso.with(getContext()).load(url.toString()).into(scannerBack.getImageViewTarget());
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, ProfilePictureFragment.class.getSimpleName(), e);
                }
            }
        }
    }

    @Override
    protected void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (Button) view.findViewById(R.id.demogrDocsFrontScanButton));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_license_state_label));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_tv_state));
//        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_license_num));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // invoke parent fragment to enable Next Button
        buttonsStatusCallback.enableNextButton(true);
    }

    @Override
    public int getImageShape() {
        return ImageCaptureHelper.RECTANGULAR_IMAGE;
    }

    public void setModel(DemographicIdDocPayloadDTO model) {
        this.model = model;
        List<DemographicIdDocPhotoDTO> photoDTOs = model.getIdDocPhothos();
        if (photoDTOs == null || photoDTOs.size() < 2) { // create the list of photos (front and back) if null
            photoDTOs = new ArrayList<>();
            // create two empty photos DTOs
            photoDTOs.add(new DemographicIdDocPhotoDTO());
            photoDTOs.add(new DemographicIdDocPhotoDTO());
            this.model.setIdDocPhothos(photoDTOs);
        }
    }

    public DemographicIdDocPayloadDTO getModel() {
        return model;
    }
}