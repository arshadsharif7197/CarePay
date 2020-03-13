package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.ApplicationPreferences;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
    private TextView initials;
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
        Toolbar toolbar = view.findViewById(R.id.settings_toolbar);
        TextView title = toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("profile_heading"));
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        callback.setToolbar(toolbar);

        profileImageView = view.findViewById(R.id.providerPicImageView);
        initials = view.findViewById(R.id.editProfileAvatarTextView);
        patientNameValue = view.findViewById(R.id.patientNameTextView);
        patientEmailValue = (CarePayTextView) view.findViewById(R.id.patientEmailTextView);

        getPersonalDetails();

        setClickListeners(view);

        if (demographicsSettingsDTO.getPayload().getDelegate() != null) {
            view.findViewById(R.id.editAccountCredentialsContainer).setVisibility(View.GONE);
        }
        setUpBottomSheet(view);
    }

    private void getPersonalDetails() {
        PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload()
                .getDemographics().getPayload().getPersonalDetails();
        String imageUrl = demographicsPersonalDetails.getProfilePhoto();
        if (!StringUtil.isNullOrEmpty(ApplicationPreferences.getInstance().getFullName())) {
            initials.setText(StringUtil.getShortName(ApplicationPreferences.getInstance().getFullName()));
        }

        if (!StringUtil.isNullOrEmpty(imageUrl)) {
            displayImage(imageUrl, profileImageView);
        }

        patientNameValue.setText(StringUtil.capitalize(demographicsPersonalDetails.getFullName()));
        patientEmailValue.setText(demographicsSettingsDTO.getPayload().getCurrentEmail());
    }

    private void setClickListeners(View view) {
        Button saveChangesButton = view.findViewById(R.id.buttonAddDemographicInfo);
        saveChangesButton.setOnClickListener(view1 -> {
            setupImageBase64();
            TransitionDTO demographicsSettingsUpdateDemographicsDTO = demographicsSettingsDTO
                    .getMetadata().getTransitions().getUpdateDemographics();
            DemographicPayloadDTO demographicPayload = demographicsSettingsDTO.getPayload()
                    .getDemographics().getPayload();
            Gson gson = new Gson();
            String jsonInString = gson.toJson(demographicPayload);
            getWorkflowServiceHelper().execute(demographicsSettingsUpdateDemographicsDTO,
                    updateProfileCallback, jsonInString, null);
        });

        TextView patientNameLabel = view.findViewById(R.id.patientChangeNameTextView);
        patientNameLabel.setOnClickListener(view12 -> callback.displayUpdateNameFragment());

        TextView patientEmailLabel = view.findViewById(R.id.patientChangeEmailTextView);
        patientEmailLabel.setOnClickListener(view13 -> callback.displayUpdateEmailFragment());

        TextView patientPasswordLabel = view.findViewById(R.id.patientChangePasswordTextView);
        patientPasswordLabel.setOnClickListener(view14 -> callback.displayUpdatePasswordFragment());
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

        File file = new File(filePath);
        Uri fileUri;
        if (file.exists()) {
            fileUri = Uri.fromFile(file);
        } else {
            fileUri = Uri.parse(filePath);
        }

        int size = getResources().getDimensionPixelSize(R.dimen.dialog_profile_pic_size);
        Picasso.with(getContext()).load(fileUri)
                .resize(size, size)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setVisibility(View.VISIBLE);
                        initials.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        imageView.setVisibility(View.GONE);
                        initials.setVisibility(View.VISIBLE);
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

    private void setUpBottomSheet(View view) {

        final View shadow = view.findViewById(R.id.shadow);
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    shadow.setClickable(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                shadow.setAlpha(slideOffset);
            }
        });

        View.OnClickListener bottomSheetClickListener = v -> {
            bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_EXPANDED);
            shadow.setClickable(true);
        };
        Button changeProfilePictureButton = view.findViewById(R.id.changeCurrentPhotoButton);
        changeProfilePictureButton.setOnClickListener(bottomSheetClickListener);

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN));
        shadow.setOnClickListener(view1 -> bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN));
        shadow.setClickable(false);

        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this, profileImageView,
                CarePayCameraPreview.CameraType.CAPTURE_PHOTO);
        View takePhotoContainer = view.findViewById(R.id.takePhotoContainer);
        takePhotoContainer.setOnClickListener(view12 -> {
            mediaScannerPresenter.handlePictureAction();
            bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN);
        });

        View chooseFileContainer = view.findViewById(R.id.chooseFileContainer);
        chooseFileContainer.setOnClickListener(view13 -> {
            mediaScannerPresenter.selectFile();
            bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN);
        });
    }

    private void bottomMenuAction(BottomSheetBehavior bottomSheetBehavior, int stateHidden) {
        bottomSheetBehavior.setState(stateHidden);
    }

}
