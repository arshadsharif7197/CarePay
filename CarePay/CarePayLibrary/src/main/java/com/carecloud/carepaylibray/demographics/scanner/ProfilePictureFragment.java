package com.carecloud.carepaylibray.demographics.scanner;

import android.app.Activity;
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
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;


/**
 * Created by lsoco_user on 9/17/2016.
 * Fragment for profile picture capturing
 */
public class ProfilePictureFragment extends DocumentScannerFragment {

    private static String LOG_TAG = ProfilePictureFragment.class.getSimpleName();
    private ImageCaptureHelper imageCaptureHelper;
    private Button buttonChangeCurrentPhoto;
    private DemographicPersDetailsPayloadDTO model;
    private String recaptureCaption;
    private DemographicLabelsDTO globalLabelsDTO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set label for capture button
        Activity activity = getActivity();
        DemographicLabelsDTO labelsMetaDTO = null;
        if (activity instanceof DemographicsLabelsHolder) {
            labelsMetaDTO = ((DemographicsLabelsHolder) getActivity()).getLabelsDTO();
        }

        recaptureCaption = labelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : labelsMetaDTO.getDemographicsProfileReCaptureCaption();

        View view = inflater.inflate(getLayoutRes(), container, false);
        ImageView imageViewDetailsImage = (ImageView) view.findViewById(R.id.DetailsProfileImage);
        imageCaptureHelper = new ImageCaptureHelper(getActivity(), imageViewDetailsImage, globalLabelsDTO);
        buttonChangeCurrentPhoto = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        buttonChangeCurrentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageCaptureHelper, ImageCaptureHelper.CameraType.DEFAULT_CAMERA);
            }
        });
        String captureCaption = labelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : labelsMetaDTO.getDemographicsProfileCaptureCaption();
        buttonChangeCurrentPhoto.setText(captureCaption);


        populateViewsFromModel();

        return view;
    }

    private int getLayoutRes() {
        return R.layout.fragment_demographics_picture;
    }

    @Override
    protected void updateModel(TextView selectionDestination) {

    }

    @Override
    protected void updateModelAndViewsAfterScan(ImageCaptureHelper scanner) {
        // save the image as base64 in the model
        if (bitmap != null) {
            String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
            model.setProfilePhoto(imageAsBase64);
        }
    }

    @Override
    public void populateViewsFromModel() {
        if (model != null) {
            String profilePicURL = model.getProfilePhoto();
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
        setGothamRoundedMediumTypeface(getActivity(), buttonChangeCurrentPhoto);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // change the caption of the button
        if (bitmap != null) {
            buttonChangeCurrentPhoto.setText(recaptureCaption);
        }
        buttonsStatusCallback.enableNextButton(true);
    }

    @Override
    public int getImageShape() {
        return ImageCaptureHelper.ROUND_IMAGE;
    }

    public void setPayloadDTO(DemographicPersDetailsPayloadDTO model) {
        this.model = model;
    }

    public void setGlobalLabelsDTO(DemographicLabelsDTO globalLabelsDTO) {
        this.globalLabelsDTO = globalLabelsDTO;
    }
}