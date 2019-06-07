package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends BaseFragment implements MediaViewInterface {
    private DemographicDTO demographicsSettingsDTO = null;

    private ImageView profileImageView;
    private TextView patientNameValue;
    private TextView patientEmailValue;

    private DemographicsSettingsFragmentListener callback;

    private MediaScannerPresenter mediaScannerPresenter;

    private boolean hasNewImage = false;

    /**
     * @return an instance of EditProfileFragment
     */
    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DemographicsSettingsFragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_update, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("profile_heading"));
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        callback.setToolbar(toolbar);

        profileImageView = (ImageView) view.findViewById(R.id.providerPicImageView);
        patientNameValue = (TextView) view.findViewById(R.id.patientNameTextView);
        patientEmailValue = (CarePayTextView) view.findViewById(R.id.patientEmailTextView);

        getPersonalDetails();

        setClickListeners(view);

        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this, profileImageView,
                CarePayCameraPreview.CameraType.CAPTURE_PHOTO);
        if (demographicsSettingsDTO.getPayload().getDelegate() != null) {
            view.findViewById(R.id.editAccountCredentialsContainer).setVisibility(View.GONE);
        }
    }

    private void getPersonalDetails() {
        PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload()
                .getDemographics().getPayload().getPersonalDetails();
        String imageUrl = demographicsPersonalDetails.getProfilePhoto();
        if (!StringUtil.isNullOrEmpty(imageUrl)) {
            displayImage(imageUrl, profileImageView);
        }

        patientNameValue.setText(StringUtil.capitalize(demographicsPersonalDetails.getFullName()));
        patientEmailValue.setText(demographicsSettingsDTO.getPayload().getCurrentEmail());
    }

    private void setClickListeners(View view) {
        Button changeProfilePictureButton = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        changeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaScannerPresenter.selectImage(true);
            }

        });

        Button saveChangesButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupImageBase64();
                TransitionDTO demographicsSettingsUpdateDemographicsDTO = demographicsSettingsDTO
                        .getMetadata().getTransitions().getUpdateDemographics();
                DemographicPayloadDTO demographicPayload = demographicsSettingsDTO.getPayload()
                        .getDemographics().getPayload();
                Gson gson = new Gson();
                String jsonInString = gson.toJson(demographicPayload);
                getWorkflowServiceHelper().execute(demographicsSettingsUpdateDemographicsDTO,
                        updateProfileCallback, jsonInString, null);
            }
        });

        TextView patientNameLabel = (TextView) view.findViewById(R.id.patientChangeNameTextView);
        patientNameLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.displayUpdateNameFragment();
            }

        });

        TextView patientEmailLabel = (TextView) view.findViewById(R.id.patientChangeEmailTextView);
        patientEmailLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.displayUpdateEmailFragment();
            }

        });

        TextView patientPasswordLabel = (TextView) view.findViewById(R.id.patientChangePasswordTextView);
        patientPasswordLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.displayUpdatePasswordFragment();
            }

        });
    }

    WorkflowServiceCallback updateProfileCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            DemographicDTO updatedModel = DtoHelper
                    .getConvertedDTO(DemographicDTO.class, workflowDTO);
            demographicsSettingsDTO.getPayload().getDemographics().getPayload()
                    .setPersonalDetails(updatedModel.getPayload().getDemographics().getPayload()
                            .getPersonalDetails());
            getApplicationPreferences().setUserPhotoUrl(updatedModel.getPayload().getDemographics()
                    .getPayload().getPersonalDetails().getProfilePhoto());
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
            getActivity().onBackPressed();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);

        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mediaScannerPresenter != null) {
            mediaScannerPresenter.handleRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mediaScannerPresenter != null && mediaScannerPresenter.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setCapturedBitmap(String filePath, View view) {
        if (filePath != null) {
            displayImage(filePath, view);

            PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
            demographicsPersonalDetails.setLocalUriPhoto(filePath);
            hasNewImage = true;

        }
    }

    private void displayImage(String filePath, View view) {
        final ImageView imageView = (ImageView) view;

        imageView.measure(0, 0);
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        final int width = Math.max(imageView.getMeasuredWidth(), lp.width);
        final int height = Math.max(imageView.getMeasuredHeight(), lp.height);

        File file = new File(filePath);
        Uri fileUri;
        if (file.exists()) {
            fileUri = Uri.fromFile(file);
        } else {
            fileUri = Uri.parse(filePath);
        }

        Picasso.with(getContext()).load(fileUri)
                .placeholder(R.drawable.icn_placeholder_user_profile_png)
                .resize(width, height)
                .centerCrop()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .transform(new CircleImageTransform())
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                        lp.width = width;
                        lp.height = height;
                        imageView.setLayoutParams(lp);
                    }

                    @Override
                    public void onError() {
                        imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                R.drawable.icn_placeholder_user_profile_png));
                    }
                });

    }

    @Override
    public void handleStartActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public Fragment getCallingFragment() {
        return this;
    }

    @Override
    public void setupImageBase64() {
        if (hasNewImage) {
            String filePath = demographicsSettingsDTO.getPayload().getDemographics().getPayload()
                    .getPersonalDetails().getLocalUriPhoto();
            File file = new File(filePath);
            Bitmap bitmap = null;
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(filePath);
            } else {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                            Uri.parse(filePath));
                } catch (IOException ioe) {
                    //do nothing
                }
            }
            if (bitmap != null) {
                String imageAsBase64 = SystemUtil.convertBitmapToString(
                        SystemUtil.getScaledBitmap(bitmap, CarePayConstants.IMAGE_QUALITY_MAX_PX),
                        Bitmap.CompressFormat.JPEG, 90);
                PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload()
                        .getDemographics().getPayload().getPersonalDetails();
                demographicsPersonalDetails.setProfilePhoto(imageAsBase64);
            }
        }
    }
}
