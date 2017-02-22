package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.utils.PatientAppUtil;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends DocumentScannerFragment {
    private static final String LOG_TAG = DemographicsSettingsFragment.class.getSimpleName();
    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private String profileString = null;
    private String firstNameValString = null;
    private String lastNameValString = null;
    private String middleNameValString = null;
    private String changeNameString = null;
    private String changeEmailString = null;
    private String changePasswordString = null;
    private String changePhotoString = null;
    private String saveChangesString = null;

    private DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_update, container, false);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        setGothamRoundedMediumTypeface(appCompatActivity, title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(appCompatActivity, R.drawable.icn_patient_mode_nav_back));
        appCompatActivity.setSupportActionBar(toolbar);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String demographicsSettingsDTOString = bundle.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }
        getEditProfileLabels();

        Button changeProfilePictureButton = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        changeProfilePictureButton.setText(changePhotoString);
        Button updateProfileButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        updateProfileButton.setText(saveChangesString);

        ImageView profileImageview = (ImageView) view.findViewById(R.id.providerPicImageView);

        if (demographicsSettingsDTO != null) {
                DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                imageCaptureHelper = new ImageCaptureHelper(appCompatActivity, profileImageview, demographicsSettingsLabelsDTO);
        }

        getPersonalDetails();
        title.setText(profileString);

        DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
        DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
        DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
        DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
        String imageUrl = demographicsPersonalDetails.getProfilePhoto();
        if (!StringUtil.isNullOrEmpty(imageUrl)) {
            Picasso.with(appCompatActivity).load(imageUrl).transform(
                    new CircleImageTransform()).resize(160, 160).into(profileImageview);
        }
        String userId = CognitoAppHelper.getCurrUser();

        CarePayTextView patientNameValue = (CarePayTextView) view.findViewById(R.id.patientNameTextView);
        patientNameValue.setText(firstNameValString + " " + middleNameValString+" " + lastNameValString);

        CarePayTextView patientEmailValue = (CarePayTextView) view.findViewById(R.id.patientEmailTextView);
        patientEmailValue.setText(userId);

        CarePayTextView patientNameLabel = (CarePayTextView) view.findViewById(R.id.patientChangeNameTextView);
        patientNameLabel.setText(changeNameString);

        CarePayTextView patientEmailLabel = (CarePayTextView) view.findViewById(R.id.patientChangeEmailTextView);
        patientEmailLabel.setText(changeEmailString);

        CarePayTextView patientPasswordLabel = (CarePayTextView) view.findViewById(R.id.patientChangePasswordTextView);
        patientPasswordLabel.setText(changePasswordString);

        setClickables(patientNameLabel, patientEmailLabel, patientPasswordLabel, changeProfilePictureButton, updateProfileButton);

        return view;

    }

    /**
     * demographics Edit Profile labels
     */
    public void getEditProfileLabels() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                if (demographicsSettingsLabelsDTO != null) {
                    profileString = demographicsSettingsLabelsDTO.getProfileHeadingLabel();
                    changeNameString = demographicsSettingsLabelsDTO.getDemographicsChangeNameLabel();
                    changeEmailString = demographicsSettingsLabelsDTO.getDemographicsChangeEmailLabel();
                    changePasswordString = demographicsSettingsLabelsDTO.getSettingschangePasswordLabel();
                    changePhotoString = demographicsSettingsLabelsDTO.getDemographicsChangePhotoLabel();
                    saveChangesString = demographicsSettingsLabelsDTO.getDemographicsSaveChangesLabel();

                }
            }
        }

    }

    private void getPersonalDetails() {

        if (demographicsSettingsDTO != null) {
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if (demographicsSettingsPayloadDTO != null) {
                DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                firstNameValString = demographicsPersonalDetails.getFirstName();
                lastNameValString = demographicsPersonalDetails.getLastName();
                middleNameValString = demographicsPersonalDetails.getMiddleName();
                hideSoftKeyboard(getActivity());
            }
        }

    }

    private void setClickables(CarePayTextView patientNameLabel, CarePayTextView patientEmailLabel, CarePayTextView patientPasswordLabel, Button changeProfilePictureButton, final Button updateProfileButton) {
        changeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageCaptureHelper, ImageCaptureHelper.CameraType.DEFAULT_CAMERA);
            }

        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                updateProfileButton.setEnabled(false);
                try{
                    if (demographicsSettingsDTO != null) {
                        DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                        if (demographicsSettingsMetadataDTO != null) {
                            DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                            TransitionDTO demographicsSettingsUpdateDemographicsDTO = demographicsSettingsTransitionsDTO.getUpdateDemographics();
                            Map<String, String> header = null;
                            try {
                                if (demographicsSettingsDTO != null) {
                                    DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                                    if (demographicsSettingsPayloadDTO != null) {
                                        DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                                        DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();

                                        Gson gson = new Gson();
                                        String jsonInString = gson.toJson(demographicPayload);
                                        getWorkflowServiceHelper().execute(demographicsSettingsUpdateDemographicsDTO, updateProfileCallback, jsonInString, header);
                                    }
                                }
                                header = new HashMap<>();
                                header.put("transition", "true");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }

        });

        patientNameLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String demographicsSettingsDTOString = gson.toJson(demographicsSettingsDTO);
                bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, demographicsSettingsDTOString);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                DemographicsSettingsUpdateNameFragment fragment = (DemographicsSettingsUpdateNameFragment)
                        fm.findFragmentByTag(DemographicsSettingsUpdateNameFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new DemographicsSettingsUpdateNameFragment();
                }

                //fix for random crashes
                if (fragment.getArguments() != null) {
                    fragment.getArguments().putAll(bundle);
                } else {
                    fragment.setArguments(bundle);
                }

                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        DemographicsSettingsUpdateNameFragment.class.getSimpleName()).addToBackStack(null).commit();

            }

        });

        patientEmailLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String demographicsSettingsDTOString = gson.toJson(demographicsSettingsDTO);
                bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, demographicsSettingsDTOString);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                DemographicsSettingUpdateEmailFragment fragment = (DemographicsSettingUpdateEmailFragment)
                        fm.findFragmentByTag(DemographicsSettingUpdateEmailFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new DemographicsSettingUpdateEmailFragment();
                }

                //fix for random crashes
                if (fragment.getArguments() != null) {
                    fragment.getArguments().putAll(bundle);
                } else {
                    fragment.setArguments(bundle);
                }

                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        DemographicsSettingUpdateEmailFragment.class.getSimpleName()).addToBackStack(null).commit();

            }

        });

        patientPasswordLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String demographicsSettingsDTOString = gson.toJson(demographicsSettingsDTO);
                bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, demographicsSettingsDTOString);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                DemographicsSettingsChangePasswordFragment fragment = (DemographicsSettingsChangePasswordFragment)
                        fm.findFragmentByTag(DemographicsSettingsChangePasswordFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new DemographicsSettingsChangePasswordFragment();
                }

                //fix for random crashes
                if (fragment.getArguments() != null) {
                    fragment.getArguments().putAll(bundle);
                } else {
                    fragment.setArguments(bundle);
                }

                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        DemographicsSettingsChangePasswordFragment.class.getSimpleName()).addToBackStack(null).commit();

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
            PatientAppUtil.showSuccessNotification(getActivity(), getView(), demographicsSettingsDTO.getDemographicsSettingsMetadataDTO().getLabels().getSettingsSavedSuccessMessage());
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);

        }
    };

    @Override
    public ImageCaptureHelper.ImageShape getImageShape() {
        return ImageCaptureHelper.ImageShape.CIRCULAR;
    }

    @Override
    public void populateViewsFromModel(View view) {
        DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
        if (demographicsSettingsPayloadDTO != null) {
            DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
            DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
            DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
            String profilePicURL = demographicsPersonalDetails.getProfilePhoto();
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
    protected void updateModel(TextView selectionDestination) {

    }

    @Override
    protected void updateModelAndViewsAfterScan(ImageCaptureHelper scanner, Bitmap bitmap) {
        // save the image as base64 in the model
        if (bitmap != null) {
            String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if (demographicsSettingsPayloadDTO != null) {
                DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                demographicsPersonalDetails.setProfilePhoto(imageAsBase64);
            }
        }
    }

    @Override
    protected void setTypefaces(View view) {

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
