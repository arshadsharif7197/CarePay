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
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsSettingsFragment extends Fragment {
    private static final String LOG_TAG = DemographicsSettingsFragment.class.getSimpleName();
    private AppCompatActivity appCompatActivity;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private String demographicsString = null;
    private String documentsString = null;
    private String creditCardsString = null;
    private String signOutString = null;
    private String editString = null;
    private String settingsString = null;
    private CarePayTextView editTextview = null;
    private Button signOutButton = null;
    private CarePayTextView demographicsTextview = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    public DemographicsSettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_settings, container, false);

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
        getSettingsLabels();
        String userId = CognitoAppHelper.getCurrUser();
        demographicsTextview = (CarePayTextView) view.findViewById(R.id.demographicsTextView);
        CarePayTextView documentsTextview = (CarePayTextView) view.findViewById(R.id.documentsTextView);
        CarePayTextView creditCardsTextview = (CarePayTextView) view.findViewById(R.id.creditCardsTextView);
        editTextview = (CarePayTextView) view.findViewById(R.id.editTextView);
        signOutButton = (Button) view.findViewById(R.id.signOutButton);
        CarePayTextView patientNameTextview = (CarePayTextView) view.findViewById(R.id.patient_name);
        CarePayTextView patientIdTextview = (CarePayTextView) view.findViewById(R.id.patient_id);

        demographicsTextview.setText(demographicsString);
        documentsTextview.setText(documentsString);
        creditCardsTextview.setText(creditCardsString);
        editTextview.setText(editString);
        signOutButton.setText(signOutString);
        title.setText(settingsString);
        patientNameTextview.setText(getUserName());
        patientIdTextview.setText(userId);
        setClickables(view);
        return view;

    }
    /**
     * demographics settings labels
     */
    public void getSettingsLabels() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                if (demographicsSettingsLabelsDTO != null) {
                    demographicsString = demographicsSettingsLabelsDTO.getDemographicsLabel();
                    documentsString = demographicsSettingsLabelsDTO.getDocumentsLabel();
                    creditCardsString = demographicsSettingsLabelsDTO.getCreditCardsLabel();
                    signOutString = demographicsSettingsLabelsDTO.getSignOutLabel();
                    editString = demographicsSettingsLabelsDTO.getEditButtonLabel();
                    settingsString = demographicsSettingsLabelsDTO.getSettingsHeading();
                }
            }
        }
    }

    private void setClickables(View view) {
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CognitoAppHelper.getPool().getUser() != null){
                    CognitoAppHelper.getPool().getUser().signOut();
                    CognitoAppHelper.setUser(null);
                    getActivity().finish();
                }

            }
        });
        editTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String demographicsSettingsDTOString = gson.toJson(demographicsSettingsDTO);
                bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, demographicsSettingsDTOString);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                EditProfileFragment fragment = (EditProfileFragment)
                        fm.findFragmentByTag(EditProfileFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new EditProfileFragment();
                }

                //fix for random crashes
                if(fragment.getArguments() !=null){
                    fragment.getArguments().putAll(bundle);
                }else{
                    fragment.setArguments(bundle);
                }

                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        EditProfileFragment.class.getSimpleName()).commit();



            }
        });
        demographicsTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String demographicsSettingsDTOString = gson.toJson(demographicsSettingsDTO);
                bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, demographicsSettingsDTOString);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                DemographicsInformationFragment fragment = (DemographicsInformationFragment)
                        fm.findFragmentByTag(DemographicsInformationFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new DemographicsInformationFragment();
                }

                //fix for random crashes
                if(fragment.getArguments() !=null){
                    fragment.getArguments().putAll(bundle);
                }else{
                    fragment.setArguments(bundle);
                }

                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        DemographicsInformationFragment.class.getSimpleName()).commit();



            }
        });
    }

    private String getUserName(){
        DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
        DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
        DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
        DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
        String firstName = demographicsPersonalDetails.getFirstName();
        String lastName = demographicsPersonalDetails.getLastName();
        String userName = firstName +" " +lastName;
        return userName;
    }
}

