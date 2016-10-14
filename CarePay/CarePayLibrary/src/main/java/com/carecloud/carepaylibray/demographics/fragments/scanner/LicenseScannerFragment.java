package com.carecloud.carepaylibray.demographics.fragments.scanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.models.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

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

    private ImageCaptureHelper scannerFront;
    private ImageCaptureHelper scannerBack;
    private TextView           documentTypeClickable;
    private Button             scanFrontButton;
    private Button             scanBackButton;
    private EditText           idNumberEdit;
    private TextInputLayout    idNumberInputText;
    private TextView           stateClickable;

    private DemographicIdDocPayloadDTO model;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_scan_license, container, false);

        documentTypeClickable = (TextView) view.findViewById(R.id.demogrDocTypeClickable);

        idNumberEdit = (EditText) view.findViewById(R.id.demogr_docs_license_num);
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
        

        stateClickable = (TextView) view.findViewById(R.id.demogr_tv_state);
        stateClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(states, "Select state", stateClickable);
            }
        });

        setTypefaces(view);

//        populateViewsFromModel();

        return view;
    }

    @Override
    protected void updateModelAndViewsAfterScan(ImageCaptureHelper scanner) { // license has been scanned
        if(scanner == scannerFront) {
            if(bitmap != null) {
                // change button caption to 'rescan'
                scanFrontButton.setText(R.string.demogr_docs_rescan_front);
                // set the front image
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
//                model.setProfilePhoto(imageAsBase64);
            }
        } else if(scanner == scannerBack) {
            if(bitmap != null) {
                // change button caption to 'rescan'
                scanBackButton.setText(R.string.demogr_docs_rescan_back);
                // set the back image
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
//                model.setProfilePhoto(imageAsBase64);
            }
        }
    }

    @Override
    public void populateViewsFromModel() {
        if (model != null) {
            String idType = model.getIdType();
            if(!StringUtil.isNullOrEmpty(idType)) {
                documentTypeClickable.setText(idType);
            }
            String licenseNum = model.getIdNumber();
            if (!StringUtil.isNullOrEmpty(licenseNum)) {
                idNumberEdit.setText(licenseNum);
            }
            String state = model.getIdState();
            if(!StringUtil.isNullOrEmpty(state)) {
                stateClickable.setText(state);
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
    }
}