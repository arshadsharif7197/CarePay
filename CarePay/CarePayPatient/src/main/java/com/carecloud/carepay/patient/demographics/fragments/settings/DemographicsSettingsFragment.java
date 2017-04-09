package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsSettingsFragment extends BaseFragment {


    private DemographicsSettingsDTO demographicsSettingsDTO;
    private Button signOutButton;
    private IDemographicsSettingsFragmentListener callback;
    private CheckBox pushNotificationCheckBox;
    private CheckBox emailNotificationCheckBox;

    public interface IDemographicsSettingsFragmentListener {
        void initializeCreditCardListFragment();

        void showHelpFragment();
    }

    private DemographicsSettingsFragment() {
    }

    public static DemographicsSettingsFragment newInstance(DemographicsSettingsDTO demographicsSettingsDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicsSettingsDTO);
        DemographicsSettingsFragment demographicsSettingsFragment = new DemographicsSettingsFragment();
        demographicsSettingsFragment.setArguments(args);
        return demographicsSettingsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (IDemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IDemographicsSettingsFragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = DtoHelper.getConvertedDTO(DemographicsSettingsDTO.class, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demographics_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("settings_heading"));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setUpUi(view);
        setClickListeners(view);
    }

    private void setUpUi(View view) {
        CarePayTextView patientNameTextView = (CarePayTextView) view.findViewById(R.id.patient_name);
        patientNameTextView.setText(getCapitalizedUserName());
        CarePayTextView patientIdTextView = (CarePayTextView) view.findViewById(R.id.patient_id);
        patientIdTextView.setText(getAppAuthorizationHelper().getCurrUser());

        initializeHelpButton(view);

        PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload().getDemographics()
                .getPayload().getPersonalDetails();
        String imageUrl = demographicsPersonalDetails.getProfilePhoto();
        if (!StringUtil.isNullOrEmpty(imageUrl)) {
            ImageView profileImageview = (ImageView) view.findViewById(R.id.providerPicImageView);
            Picasso.with(getActivity()).load(imageUrl).transform(
                    new CircleImageTransform()).resize(160, 160).into(profileImageview);

        }

        pushNotificationCheckBox = (CheckBox) view.findViewById(R.id.pushNotificationCheckBox);
        pushNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload().getDemographicSettingsNotificationDTO().getPayload().isPush());
        emailNotificationCheckBox = (CheckBox) view.findViewById(R.id.emailNotificationCheckBox);
        emailNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload().getDemographicSettingsNotificationDTO().getPayload().isEmail());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    private void initializeHelpButton(View view) {
        TextView textView = (TextView) view.findViewById(R.id.helpTextView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.showHelpFragment();
                }
            }
        });
    }

    private void setClickListeners(View view) {
        signOutButton = (Button) view.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutButton.setEnabled(false);
                getWorkflowServiceHelper().executeApplicationStartRequest(logOutCall);

            }
        });

        CarePayTextView editTextView = (CarePayTextView) view.findViewById(R.id.editTextView);
        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                EditProfileFragment fragment = (EditProfileFragment)
                        fm.findFragmentByTag(EditProfileFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = EditProfileFragment.newInstance(demographicsSettingsDTO);
                }
                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        EditProfileFragment.class.getSimpleName()).addToBackStack(null).commit();

            }
        });

        CarePayTextView demographicsTextview = (CarePayTextView) view.findViewById(R.id.demographicsTextView);
        demographicsTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DemographicsInformationFragment fragment = (DemographicsInformationFragment)
                        fm.findFragmentByTag(DemographicsInformationFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = DemographicsInformationFragment.newInstance(demographicsSettingsDTO);
                }
                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        DemographicsInformationFragment.class.getSimpleName()).addToBackStack(null).commit();

            }
        });

        CarePayTextView documentsTextview = (CarePayTextView) view.findViewById(R.id.documentsTextView);
        documentsTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DemographicsSettingsDocumentsFragment fragment = (DemographicsSettingsDocumentsFragment)
                        fm.findFragmentByTag(DemographicsSettingsDocumentsFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = DemographicsSettingsDocumentsFragment.newInstance(demographicsSettingsDTO);
                }
                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        DemographicsSettingsDocumentsFragment.class.getSimpleName()).addToBackStack(null).commit();

            }
        });

        CarePayTextView creditCardsTextView = (CarePayTextView) view.findViewById(R.id.creditCardsTextView);
        creditCardsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.initializeCreditCardListFragment();
            }
        });

        View.OnClickListener checkBoxClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNotificationPreferences();
            }
        };
        pushNotificationCheckBox.setOnClickListener(checkBoxClickListener);
        emailNotificationCheckBox.setOnClickListener(checkBoxClickListener);
    }

    private void updateNotificationPreferences() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("push", pushNotificationCheckBox.isChecked());
        jsonObject.addProperty("email", emailNotificationCheckBox.isChecked());
        TransitionDTO transitionDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO()
                .getTransitions().getUpdateNotifications();
        getWorkflowServiceHelper().execute(transitionDTO, updateNotificationPreferencesCallback, jsonObject.toString());
    }

    private String getCapitalizedUserName() {
        PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload().getDemographics()
                .getPayload().getPersonalDetails();
        String firstName = demographicsPersonalDetails.getFirstName();
        String lastName = demographicsPersonalDetails.getLastName();
        return (StringUtil.capitalize(firstName + " " + lastName));
    }

    WorkflowServiceCallback logOutCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            signOutButton.setEnabled(true);
            // log out previous user from Cognito
            if (!HttpConstants.isUseUnifiedAuth()) {
                getAppAuthorizationHelper().getPool().getUser().signOut();
                getAppAuthorizationHelper().setUser(null);
            }
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            signOutButton.setEnabled(true);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback updateNotificationPreferencesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("success_notifications_settings_update_message"));
            demographicsSettingsDTO.getPayload().getDemographicSettingsNotificationDTO()
                    .getPayload().setPush(pushNotificationCheckBox.isChecked());
            demographicsSettingsDTO.getPayload().getDemographicSettingsNotificationDTO()
                    .getPayload().setEmail(emailNotificationCheckBox.isChecked());
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            pushNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload()
                    .getDemographicSettingsNotificationDTO().getPayload().isPush());
            emailNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload()
                    .getDemographicSettingsNotificationDTO().getPayload().isEmail());

        }
    };

    /**
     * Update credit cards list.
     *
     * @param demographicsSettingsDTO the demographics settings dto
     */
    public void updateCreditCardsList(DemographicsSettingsDTO demographicsSettingsDTO) {
        if (demographicsSettingsDTO != null) {
            this.demographicsSettingsDTO = demographicsSettingsDTO;
        }
    }
}

