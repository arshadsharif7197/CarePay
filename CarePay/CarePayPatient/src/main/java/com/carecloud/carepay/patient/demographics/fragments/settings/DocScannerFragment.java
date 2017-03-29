package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
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

public class DocScannerFragment extends DocumentScannerFragment {

    private static final String LOG_TAG = DocScannerFragment.class.getSimpleName();
    private View view;
    private ImageCaptureHelper scannerFront;
    private ImageCaptureHelper scannerBack;
    private Button scanFrontButton;
    private Button scanBackButton;
    private DemographicIdDocPayloadDTO model;
    private DemographicsSettingsDTO demographicsSettingsDTO;
    private String documentsdocumentsScanFirstString = null;
    private String documentsScanBackString = null;
    private DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                }
            }
        }
    }

    protected int getLayoutRes() {
        return R.layout.fragment_demographics_scan_license;
    }

    private void initializeUIFields() {
        // fetch the options
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
        }
        ImageView imageFront = (ImageView) view.findViewById(R.id.demogrDocsFrontScanImage);
        scannerFront = new ImageCaptureHelper(getActivity(), imageFront);

        ImageView imageBack = (ImageView) view.findViewById(R.id.demogrDocsBackScanImage);
        scannerBack = new ImageCaptureHelper(getActivity(), imageBack);

        scanFrontButton = (Button) view.findViewById(R.id.demogrDocsFrontScanButton);
        scanFrontButton.setText(documentsdocumentsScanFirstString);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[] { 8, 8, 8, 8, 8, 8, 8, 8 });
        shape.setStroke(3, ContextCompat.getColor(getActivity(), R.color.colorPrimary));
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

        setTypefaces(view);

        populateViewsFromModel(view);
    }

    @Override
    protected void updateModel(TextView selectionDestination) {

    }

    @Override
    protected void updateModelAndViewsAfterScan(ImageCaptureHelper scanner, Bitmap bitmap) { // license has been scanned
        if (bitmap != null) {
            if (scanner == scannerFront) {
                // change button caption to 'rescan'
                scanFrontButton.setText(documentsdocumentsScanFirstString);
                // save from image
                String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicIdDocPhotoDTO frontDTO = model.getIdDocPhothos().get(0);
                frontDTO.setIdDocPhoto(imageAsBase64); // create the image dto
            } else if (scanner == scannerBack) {
                // change button caption to 'rescan'
                scanBackButton.setText(documentsScanBackString);
                String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicIdDocPhotoDTO backDTO = model.getIdDocPhothos().get(1);
                backDTO.setIdDocPhoto(imageAsBase64); // create the image dto
            }
        }
    }

    @Override
    public void populateViewsFromModel(View view) {
        if (model != null) {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (imageCaptureHelper == scannerFront) {
            scanFrontButton.setText(documentsdocumentsScanFirstString);
        } else if (imageCaptureHelper == scannerBack) {
            scanBackButton.setText(documentsScanBackString);
        }
    }

    @Override
    public ImageCaptureHelper.ImageShape getImageShape() {
        return ImageCaptureHelper.ImageShape.RECTANGULAR;
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

    @Override
    protected void setInsuranceDTO(DemographicInsurancePayloadDTO insuranceDTO, String placeholderBase64) {

    }

    @Override
    protected void setChangeFocusListeners() {

    }

    @Override
    protected void enablePlanClickable(boolean enabled) {

    }
}