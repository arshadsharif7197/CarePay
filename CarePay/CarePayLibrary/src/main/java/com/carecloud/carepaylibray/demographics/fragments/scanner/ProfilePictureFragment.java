package com.carecloud.carepaylibray.demographics.fragments.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadDriversLicenseModel;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

/**
 * Created by lsoco_user on 9/17/2016.
 * Fragment for profile picture capturing
 */
public class ProfilePictureFragment extends DocumentScannerFragment {

    private ImageCaptureHelper imageCaptureHelper;
    private Button             buttonChangeCurrentPhoto;
    private Object             model;

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
    protected void updateDetailViewsAfterScan() {
        // maybe later
    }

    @Override
    public void populateViewsFromModel() {
        if(model != null) {
            // TODO: 9/28/2016 populate the views
        }
    }

    @Override
    protected void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), buttonChangeCurrentPhoto);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        buttonsStatusCallback.enableNextButton(true);
    }

    @Override
    public int getImageShape() {
        return ImageCaptureHelper.ROUND_IMAGE;
    }

    public void setModel(Object model) {
        this.model = model;
    }
}