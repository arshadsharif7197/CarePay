package com.carecloud.carepaylibray.demographics.scanner;

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

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by lsoco_user on 9/17/2016.
 * Fragment for profile picture capturing
 */
public class ProfilePictureFragment extends DocumentScannerFragment {

    private static String LOG_TAG = ProfilePictureFragment.class.getSimpleName();
    private ImageCaptureHelper imageCaptureHelper;
    private String recaptureCaption;
    private PatientModel demographicPersDetailsPayloadDTO;
    private DemographicLabelsDTO globalLabelsDTO;
//    private boolean isRevScreen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
//        Bundle bundle = getArguments();
//        isRevScreen = bundle.getBoolean(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE, false);
        populateViewsFromModel(view);

        return view;
    }

    protected int getLayoutRes() {
        return R.layout.fragment_demographics_picture;
    }

    @Override
    protected void updateModel(TextView selectionDestination) {

    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        // save the image as base64 in the model
        if (bitmap != null) {
            String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
            demographicPersDetailsPayloadDTO.setProfilePhoto(imageAsBase64);
        }
    }

    @Override
    public void populateViewsFromModel(View view) {
        // set label for capture button
//        Activity activity = getActivity();
//        DemographicLabelsDTO labelsMetaDTO = null;
//        if (activity instanceof DemographicsLabelsHolder) {
//            labelsMetaDTO = ((DemographicsLabelsHolder) getActivity()).getLabelsDTO();
//        }


        recaptureCaption = Label.getLabel("demographics_take_another_picture_button_title");

        ImageView imageViewDetailsImage = (ImageView) view.findViewById(R.id.DetailsProfileImage);
        imageCaptureHelper = new ImageCaptureHelper(getActivity(), imageViewDetailsImage);

        Button buttonChangeCurrentPhoto = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        buttonChangeCurrentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageCaptureHelper, ImageCaptureHelper.CameraType.DEFAULT_CAMERA);
            }
        });

        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        if(isCloverDevice){
            buttonChangeCurrentPhoto.setVisibility(View.INVISIBLE);
        }

//        String captureCaption = isRevScreen? labelsMetaDTO.getDemographicsTakePictureButtonTitle():
//                               labelsMetaDTO.getDemographicsProfileCaptureCaption();
//        buttonChangeCurrentPhoto.setText(captureCaption);

        demographicPersDetailsPayloadDTO = DtoHelper.getConvertedDTO(PatientModel.class, getArguments());

        if (demographicPersDetailsPayloadDTO != null) {
            String profilePicURL = demographicPersDetailsPayloadDTO.getProfilePhoto();
            if (!StringUtil.isNullOrEmpty(profilePicURL)) {
                try {
                    URL url = new URL(profilePicURL);
                    Log.v(LOG_TAG, "valid url: " + url.toString());
                    Picasso.with(getContext())
                            .load(profilePicURL)
                            .transform(new CircleImageTransform())
                            .resize(imageCaptureHelper.getImgWidth(), imageCaptureHelper.getImgWidth())
                            .into(imageCaptureHelper.getImageViewTarget());
                    // successfully load a profile image
                    buttonChangeCurrentPhoto.setText(recaptureCaption);
                    return;
                } catch (MalformedURLException e) {
                    // just log
                    Log.d(LOG_TAG, "invalid url: " + profilePicURL);
                }
            }
        }
        // if no image to load, simply load the placeholder
        imageCaptureHelper.getImageViewTarget()
                .setImageDrawable(ContextCompat.getDrawable(getActivity(),
                        R.drawable.icn_placeholder_user_profile_png));
    }

    @Override
    protected void setTypefaces(View view) {
        Button buttonChangeCurrentPhoto = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        setGothamRoundedMediumTypeface(getActivity(), buttonChangeCurrentPhoto);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // change the caption of the button
        if (hasImageChanged) {
            Button buttonChangeCurrentPhoto = (Button) findViewById(R.id.changeCurrentPhotoButton);
            buttonChangeCurrentPhoto.setText(recaptureCaption);
        }
    }

    @Override
    public ImageCaptureHelper.ImageShape getImageShape() {
        return ImageCaptureHelper.ImageShape.CIRCULAR;
    }

    public void setGlobalLabelsDTO(DemographicLabelsDTO globalLabelsDTO) {
        this.globalLabelsDTO = globalLabelsDTO;
    }

    public PatientModel getDemographicPersDetailsPayloadDTO() {
        return demographicPersDetailsPayloadDTO;
    }

    @Override
    protected void setChangeFocusListeners() {

    }

    @Override
    public void setInsuranceDTO(DemographicInsurancePayloadDTO insuranceDTO, String placeholderBase64) {

    }

    @Override
    public void enablePlanClickable(boolean enabled) {

    }
}