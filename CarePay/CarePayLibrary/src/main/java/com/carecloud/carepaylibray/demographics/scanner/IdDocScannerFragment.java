package com.carecloud.carepaylibray.demographics.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

/**
 * Created by lsoco_user on 9/13/2016.
 * Fragment for with scanning driver's license functionality
 */
public class IdDocScannerFragment extends DocumentScannerFragment {

    private static final String LOG_TAG = IdDocScannerFragment.class.getSimpleName();
    private View view;
    private ImageCaptureHelper scannerFront;
    private ImageCaptureHelper scannerBack;
    private Button scanFrontButton;
    private Button scanBackButton;
    private DemographicIdDocPayloadDTO model;
    private DemographicLabelsDTO globalLabelsDTO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // fetch the labels
        Activity activity = getActivity();
        if (activity instanceof DemographicsLabelsHolder) {
            globalLabelsDTO = ((DemographicsLabelsHolder) getActivity()).getLabelsDTO();
        }

        // create the view
        view = inflater.inflate(getLayoutRes(), container, false);

        initializeUIFields();

        return view;
    }

    protected int getLayoutRes() {
        return R.layout.fragment_demographics_scan_license;
    }

    private void initializeUIFields() {
        model = DtoHelper.getConvertedDTO(DemographicIdDocPayloadDTO.class, getArguments());

        initializePhotos();

        ImageView imageFront = (ImageView) view.findViewById(R.id.demogrDocsFrontScanImage);
        scannerFront = new ImageCaptureHelper(getActivity(), imageFront, globalLabelsDTO);

        ImageView imageBack = (ImageView) view.findViewById(R.id.demogrDocsBackScanImage);
        scannerBack = new ImageCaptureHelper(getActivity(), imageBack, globalLabelsDTO);

        // init views (labels and logic)
        String label;
        // add click listener
        scanFrontButton = (Button) view.findViewById(R.id.demogrDocsFrontScanButton);
        label = globalLabelsDTO.getDemographicsDocumentsPictureOfFront();
        scanFrontButton.setText(label);
        scanFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(scannerFront, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
            }
        });

        scanBackButton = (Button) view.findViewById(R.id.demogrDocsBackScanButton);
        label = globalLabelsDTO.getDemographicsDocumentsPictureOfBack();
        scanBackButton.setText(label);
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
        setGothamRoundedMediumTypeface(context, scanFrontButton);
        setGothamRoundedMediumTypeface(context, scanBackButton);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String label;
        if (imageCaptureHelper == scannerFront) {
            label = globalLabelsDTO.getDemographicsDocumentsPictureOfFront();
            scanFrontButton.setText(label);
        } else if (imageCaptureHelper == scannerBack) {
            label = globalLabelsDTO.getDemographicsDocumentsPictureOfBack();
            scanBackButton.setText(label);
        }
    }

    @Override
    public ImageCaptureHelper.ImageShape getImageShape() {
        return ImageCaptureHelper.ImageShape.RECTANGULAR;
    }

    public DemographicIdDocPayloadDTO getModel() {
        return model;
    }

    private void initializePhotos() {
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
    public void setInsuranceDTO(DemographicInsurancePayloadDTO insuranceDTO, String placeholderBase64) {

    }

    @Override
    public void enablePlanClickable(boolean enabled) {

    }

    @Override
    protected void setChangeFocusListeners() {

    }
}