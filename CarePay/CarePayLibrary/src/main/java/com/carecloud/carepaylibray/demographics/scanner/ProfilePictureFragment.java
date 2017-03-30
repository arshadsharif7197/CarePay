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
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Callback;
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
    private String recaptureCaption;
    private PatientModel demographicPersDetailsPayloadDTO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
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
        recaptureCaption = Label.getLabel("demographics_take_another_picture_button_title");

        Button buttonChangeCurrentPhoto = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        buttonChangeCurrentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageFront, true, ImageCaptureHelper.CameraType.DEFAULT_CAMERA);
            }
        });

        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        if(isCloverDevice){
            buttonChangeCurrentPhoto.setVisibility(View.INVISIBLE);
        }

        imageFront = (ImageView) view.findViewById(R.id.DetailsProfileImage);
        demographicPersDetailsPayloadDTO = DtoHelper.getConvertedDTO(PatientModel.class, getArguments());

        if (demographicPersDetailsPayloadDTO != null) {
            String profilePicURL = demographicPersDetailsPayloadDTO.getProfilePhoto();
            if (!StringUtil.isNullOrEmpty(profilePicURL)) {
                try {
                    URL url = new URL(profilePicURL);
                    Log.v(LOG_TAG, "valid url: " + url.toString());
                    Callback callback = new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            loadFrontPlaceHolder();
                        }
                    };

                    Picasso.with(getContext())
                            .load(profilePicURL)
                            .transform(new CircleImageTransform())
                            .resize(100, 100)
                            .into(imageFront, callback);
                    // successfully load a profile image
                    buttonChangeCurrentPhoto.setText(recaptureCaption);
                } catch (MalformedURLException e) {
                    // just log
                    Log.d(LOG_TAG, "invalid url: " + profilePicURL);
                    loadFrontPlaceHolder();
                }
            }
        }
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