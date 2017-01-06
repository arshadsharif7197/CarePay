package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsAddressDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDataModelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsFirstNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLastNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMiddleNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPropertiesDTO;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {
    private static final String LOG_TAG = DemographicsSettingsFragment.class.getSimpleName();
    private AppCompatActivity appCompatActivity;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private String profileString = null;
    private String firstNameString = null;
    private String middleNameString = null;
    private String lastNameString = null;
    private String emailString = null;
    private String firstNameValString = null;
    private String lastNameValString = null;
    private String middleNameValString = null;
    private Button changeProfileButton = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    public EditProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_update, container, false);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);

        setGothamRoundedMediumTypeface(appCompatActivity, title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String demographicsSettingsDTOString = bundle.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }
        getEditProfileLabels();
        title.setText(profileString);
        CarePayTextView firstNameTextView = (CarePayTextView) view.findViewById(R.id.reviewFirstNameLabel);
        CarePayTextView middleNameTextView = (CarePayTextView) view.findViewById(R.id.reviewMiddleNameLabel);
        CarePayTextView lastNameTextView = (CarePayTextView) view.findViewById(R.id.reviewLastNameLabel);
        CarePayTextView emailTextView = (CarePayTextView) view.findViewById(R.id.reviewEmailLabel);
        String userId = CognitoAppHelper.getCurrUser();
        getProfileProperties();
        getPersonalDetails();

        firstNameTextView.setText(firstNameString);
        middleNameTextView.setText(middleNameString);
        lastNameTextView.setText(lastNameString);
        emailTextView.setText(emailString);

        EditText firstNameEditText = (EditText) view.findViewById(R.id.reviewFirstNameTextView);
        EditText middleNameEditText = (EditText) view.findViewById(R.id.reviewMiddleNameTextView);
        EditText lastNameEditText = (EditText) view.findViewById(R.id.reviewLastNameTextView);
        EditText emailEditText = (EditText) view.findViewById(R.id.reviewEmailTextView);

        firstNameEditText.setText(firstNameValString);
        middleNameEditText.setText(middleNameValString);
        lastNameEditText.setText(lastNameValString);
        emailEditText.setText(userId);
        changeProfileButton =  (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        setClickables(view);

        return view;

    }

    /**
     * demographics Edit Profile labels
     */
    public void getEditProfileLabels() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                if (demographicsSettingsLabelsDTO != null) {
                    profileString = demographicsSettingsLabelsDTO.getProfileHeadingLabel();
                    emailString = demographicsSettingsLabelsDTO.getEmailLabel();

                }
            }
        }
    }

    public void getProfileProperties(){
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                if (demographicsSettingsDataModelsDTO != null) {
                    DemographicsSettingsDetailsDTO demographicsSettingsDetailsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                    if(demographicsSettingsDetailsDTO !=null) {
                        DemographicsSettingsPersonalDetailsPropertiesDTO demographicsSettingsPersonalDetailsPreopertiesDTO = demographicsSettingsDetailsDTO.getPersonalDetails();
                        DemographicsSettingsPersonalDetailsDTO demographicsSettingsPersonalDetailsDTO = demographicsSettingsPersonalDetailsPreopertiesDTO.getProperties();
                        DemographicsSettingsFirstNameDTO demographicsSettingsFirstNameDTO = demographicsSettingsPersonalDetailsDTO.getFirstName();
                        DemographicsSettingsLastNameDTO demographicsSettingsLastNameDTO = demographicsSettingsPersonalDetailsDTO.getLastName();
                        DemographicsSettingsMiddleNameDTO demographicsSettingsMiddleNameDTO = demographicsSettingsPersonalDetailsDTO.getMiddleName();

                        firstNameString = demographicsSettingsFirstNameDTO.getLabel();
                        lastNameString = demographicsSettingsLastNameDTO.getLabel();
                        middleNameString = demographicsSettingsMiddleNameDTO.getLabel();
                    }
                }
            }
        }

    }

    private void getPersonalDetails(){
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if(demographicsSettingsPayloadDTO!=null){
                DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                firstNameValString = demographicsPersonalDetails.getFirstName();
                lastNameValString = demographicsPersonalDetails.getLastName();
                middleNameValString = demographicsPersonalDetails.getMiddleName();

            }
        }

    }

    private void setClickables(View view) {
        changeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }

        });

    }

}
