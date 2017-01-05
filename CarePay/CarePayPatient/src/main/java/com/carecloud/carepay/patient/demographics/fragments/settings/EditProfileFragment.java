package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
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

                }
            }
        }
    }

}
