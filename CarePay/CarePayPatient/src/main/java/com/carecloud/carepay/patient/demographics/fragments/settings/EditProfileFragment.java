package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
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
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends DocumentScannerFragment {
    private static final String LOG_TAG = DemographicsSettingsFragment.class.getSimpleName();
    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private String firstNameValString = null;
    private String lastNameValString = null;
    private String middleNameValString = null;

    private ImageView profileImageView;
    private DemographicsSettingsFragmentListener callback;

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
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicsSettingsDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_update, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("profile_heading"));
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        toolbar.setNavigationIcon(ContextCompat.getDrawable(appCompatActivity, R.drawable.icn_nav_back));
        appCompatActivity.setSupportActionBar(toolbar);

        getPersonalDetails();

        DemographicPayloadResponseDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
        DemographicPayloadInfoDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
        DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
        PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();
        String imageUrl = demographicsPersonalDetails.getProfilePhoto();
        profileImageView = (ImageView) view.findViewById(R.id.providerPicImageView);
        if (!StringUtil.isNullOrEmpty(imageUrl)) {
            Picasso.with(appCompatActivity).load(imageUrl).transform(
                    new CircleImageTransform()).resize(160, 160).into(profileImageView);
        }
        String userId = demographicsSettingsDTO.getPayload().getCurrentEmail();
        CarePayTextView patientNameValue = (CarePayTextView) view.findViewById(R.id.patientNameTextView);
        patientNameValue.setText(StringUtil.capitalize(firstNameValString + " " + middleNameValString + " " + lastNameValString));

        CarePayTextView patientEmailValue = (CarePayTextView) view.findViewById(R.id.patientEmailTextView);
        patientEmailValue.setText(userId);

        setClickListeners(view);

    }

    private void getPersonalDetails() {
        PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload().getDemographics()
                .getPayload().getPersonalDetails();
        firstNameValString = demographicsPersonalDetails.getFirstName();
        lastNameValString = demographicsPersonalDetails.getLastName();
        middleNameValString = demographicsPersonalDetails.getMiddleName();
        hideSoftKeyboard(getActivity());
    }

    private void setClickListeners(View view) {
        Button changeProfilePictureButton = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        changeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageFront, true, ImageCaptureHelper.CameraType.DEFAULT_CAMERA);
            }

        });

        Button saveChangesButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionDTO demographicsSettingsUpdateDemographicsDTO = demographicsSettingsDTO
                        .getMetadata().getTransitions().getUpdateDemographics();
                DemographicPayloadDTO demographicPayload = demographicsSettingsDTO.getPayload().getDemographics()
                        .getPayload();
                Gson gson = new Gson();
                String jsonInString = gson.toJson(demographicPayload);
                getWorkflowServiceHelper().execute(demographicsSettingsUpdateDemographicsDTO,
                        updateProfileCallback, jsonInString, null);
            }
        });

        CarePayTextView patientNameLabel = (CarePayTextView) view.findViewById(R.id.patientChangeNameTextView);
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
                    fragment = DemographicsSettingsUpdateNameFragment.newInstance();
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

        CarePayTextView patientEmailLabel = (CarePayTextView) view.findViewById(R.id.patientChangeEmailTextView);
        patientEmailLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DemographicsSettingUpdateEmailFragment fragment = (DemographicsSettingUpdateEmailFragment)
                        fm.findFragmentByTag(DemographicsSettingUpdateEmailFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = DemographicsSettingUpdateEmailFragment.newInstance();
                }

                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        DemographicsSettingUpdateEmailFragment.class.getSimpleName()).addToBackStack(null).commit();

            }

        });

        CarePayTextView patientPasswordLabel = (CarePayTextView) view.findViewById(R.id.patientChangePasswordTextView);
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
                    fragment = DemographicsSettingsChangePasswordFragment.newInstance();
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
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
            PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);

        }
    };

    @Override
    public ImageCaptureHelper.ImageShape getImageShape() {
        return ImageCaptureHelper.ImageShape.CIRCULAR;
    }

    @Override
    public void populateViewsFromModel(View view) {
        DemographicPayloadResponseDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
        if (demographicsSettingsPayloadDTO != null) {
            DemographicPayloadInfoDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
            DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
            PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();
            String profilePicURL = demographicsPersonalDetails.getProfilePhoto();
            if (!StringUtil.isNullOrEmpty(profilePicURL)) {
                try {
                    URL url = new URL(profilePicURL);
                    Log.v(LOG_TAG, "valid url: " + url.toString());
                    Picasso.with(getContext())
                            .load(profilePicURL)
                            .transform(new CircleImageTransform())
                            .fit().centerCrop()
                            .into(profileImageView);
                    // successfully load a profile image
                    return;
                } catch (MalformedURLException e) {
                    // just log
                    Log.d(LOG_TAG, "invalid url: " + profilePicURL);
                }
            }
        }
        // if no image to load, simply load the placeholder
        profileImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_placeholder_user_profile_png));
    }

    @Override
    protected void updateModel(TextView selectionDestination) {

    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        // save the image as base64 in the model
        if (bitmap != null) {
            String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
            DemographicPayloadResponseDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if (demographicsSettingsPayloadDTO != null) {
                DemographicPayloadInfoDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                demographicsPersonalDetails.setProfilePhoto(imageAsBase64);
            }
        }
    }

    @Override
    public void onCaptureFail() {

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
