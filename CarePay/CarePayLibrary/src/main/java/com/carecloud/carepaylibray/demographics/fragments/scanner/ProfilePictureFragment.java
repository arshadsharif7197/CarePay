package com.carecloud.carepaylibray.demographics.fragments.scanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.models.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

/**
 * Created by lsoco_user on 9/17/2016.
 * Fragment for profile picture capturing
 */
public class ProfilePictureFragment extends DocumentScannerFragment {

    private ImageCaptureHelper               imageCaptureHelper;
    private Button                           buttonChangeCurrentPhoto;
    private DemographicPersDetailsPayloadDTO model;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_picture, container, false);

        ImageView imageViewDetailsImage = (ImageView) view.findViewById(R.id.DetailsProfileImage);
        imageCaptureHelper = new ImageCaptureHelper(getActivity(), imageViewDetailsImage);
        buttonChangeCurrentPhoto = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        buttonChangeCurrentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageCaptureHelper);
            }
        });

        populateViewsFromModel();

        return view;
    }

    @Override
    protected void updateModelAndViewsAfterScan(ImageCaptureHelper scanner) {
        // save the image as base64 in the model
        if(bitmap != null) {
            String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
            model.setProfilePhoto(imageAsBase64);
        }
    }

    @Override
    public void populateViewsFromModel() {
        if(model != null) {
            String profilePicURL = model.getProfilePhoto();
            if(!StringUtil.isNullOrEmpty(profilePicURL)) {
                try {
                    URL url = new URL(profilePicURL);
                    Picasso.with(getContext()).load(url.toString()).into(imageCaptureHelper.getImageViewTarget());
                } catch (MalformedURLException e) {
                    Log.e(LOG_TAG, ProfilePictureFragment.class.getSimpleName(), e);
                }
            }
        }
    }

    @Override
    protected void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), buttonChangeCurrentPhoto);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // change the caption of the button
        if(bitmap != null) {
            buttonChangeCurrentPhoto.setText(getString(R.string.changeCurrentPhotoButton));
        }
        buttonsStatusCallback.enableNextButton(true);
    }

    @Override
    public int getImageShape() {
        return ImageCaptureHelper.ROUND_IMAGE;
    }

    public void setModel(DemographicPersDetailsPayloadDTO model) {
        this.model = model;
    }
}