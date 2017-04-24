package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class DocScannerFragment extends DocumentScannerFragment {

    private static final String LOG_TAG = DocScannerFragment.class.getSimpleName();
    private View view;
    private Button scanFrontButton;
    private Button scanBackButton;
    private DemographicIdDocPayloadDTO model;
    private String documentsdocumentsScanFirstString = null;
    private String documentsScanBackString = null;

    public DocScannerFragment() {

    }

    /**
     *
     * @param model an instance of DemographicIdDocPayloadDTO
     * @return a DocScannerFragment instance
     */
    public static DocScannerFragment newInstance(DemographicIdDocPayloadDTO model) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, model);
        DocScannerFragment fragment = new DocScannerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = DtoHelper.getConvertedDTO(DemographicIdDocPayloadDTO.class, getArguments());
        List<DemographicIdDocPhotoDTO> photoDTOs = model.getIdDocPhothos();
        if (photoDTOs.size() == 0) {
            // create two empty photos DTOs
            photoDTOs.add(new DemographicIdDocPhotoDTO());
            photoDTOs.add(new DemographicIdDocPhotoDTO());
        } else if (photoDTOs.size() == 1) {
            photoDTOs.add(1, new DemographicIdDocPhotoDTO()); // create the second
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // create the view
        view = inflater.inflate(getLayoutRes(), container, false);

        getDocumentsLabels();
        initializeUIFields();
        return view;
    }

    /**
     * documents labels
     */
    public void getDocumentsLabels() {
        documentsdocumentsScanFirstString = Label.getLabel("documents_scan_front_label");
        documentsScanBackString = Label.getLabel("documents_scan_back_label");
    }

    protected int getLayoutRes() {
        return R.layout.fragment_demographics_scan_license;
    }

    private void initializeUIFields() {
        imageFront = (ImageView) view.findViewById(R.id.demogrDocsFrontScanImage);
        imageBack = (ImageView) view.findViewById(R.id.demogrDocsBackScanImage);

        scanFrontButton = (Button) view.findViewById(R.id.demogrDocsFrontScanButton);
        scanFrontButton.setText(documentsdocumentsScanFirstString);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{8, 8, 8, 8, 8, 8, 8, 8});
        shape.setStroke(3, ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        scanFrontButton.setBackgroundDrawable(shape);

        scanFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageFront, true, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
            }
        });

        scanBackButton = (Button) view.findViewById(R.id.demogrDocsBackScanButton);
        scanBackButton.setText(documentsScanBackString);
        scanBackButton.setBackgroundDrawable(shape);
        scanBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageBack, false, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
            }
        });

        setTypefaces(view);

        populateViewsFromModel(view);
    }

    @Override
    protected void updateModel(TextView selectionDestination) {

    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) { // license has been scanned
        if (bitmap != null) {
            if (isFrontScan) {
                // change button caption to 'rescan'
                scanFrontButton.setText(documentsdocumentsScanFirstString);
                // save from image
                String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicIdDocPhotoDTO frontDTO = model.getIdDocPhothos().get(0);
                frontDTO.setIdDocPhoto(imageAsBase64); // create the image dto
            } else {
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
                            .fit()
                            .centerCrop()
                            .transform(new RoundedCornersTransformation(5, 0))
                            .into(imageFront);
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "invalid url: " + frontPic);
                    imageFront.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                            R.drawable.icn_placeholder_document));
                }
            }
            // add back image
            String backPic = model.getIdDocPhothos().get(1).getIdDocPhoto();
            if (!StringUtil.isNullOrEmpty(backPic)) {
                try {
                    URL url = new URL(backPic);
                    Log.v(LOG_TAG, "valid url: " + url.toString());
                    Picasso.with(getContext()).load(backPic)
                            .fit()
                            .centerCrop()
                            .transform(new RoundedCornersTransformation(5, 0))
                            .into(imageBack);
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, "invalid url: " + backPic);
                    imageBack.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                            R.drawable.icn_placeholder_document));
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

        if (isFrontScan) {
            scanFrontButton.setText(documentsdocumentsScanFirstString);
        } else {
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