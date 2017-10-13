package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPaymentSettingsDTO;
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
    private DemographicsSettingsFragmentListener callback;
    private CheckBox pushNotificationCheckBox;
    private CheckBox emailNotificationCheckBox;

    /**
     * @return an instance of DemographicsSettingsFragment
     */
    public static DemographicsSettingsFragment newInstance() {
        return new DemographicsSettingsFragment();
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
        demographicsSettingsDTO = (DemographicsSettingsDTO) callback.getDto();
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
        patientIdTextView.setText(demographicsSettingsDTO.getPayload().getCurrentEmail());

        initializeHelpButton(view);

        PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload().getDemographics()
                .getPayload().getPersonalDetails();
        String imageUrl = demographicsPersonalDetails.getProfilePhoto();
        if (!StringUtil.isNullOrEmpty(imageUrl)) {
            ImageView profileImageview = (ImageView) view.findViewById(R.id.providerPicImageView);
            Picasso.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.icn_placeholder_user_profile_png)
                    .resize(160, 160)
                    .centerCrop()
                    .transform(new CircleImageTransform())
                    .into(profileImageview);

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
                    callback.displayHelpFragment();
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
                callback.displayEditProfileFragment();
            }
        });

        CarePayTextView demographicsTextview = (CarePayTextView) view.findViewById(R.id.demographicsTextView);
        demographicsTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.displayDemographicsFragment();
            }
        });

        CarePayTextView documentsTextview = (CarePayTextView) view.findViewById(R.id.documentsTextView);
        documentsTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.displayDocumentsFragment();
            }
        });

        CarePayTextView creditCardsTextView = (CarePayTextView) view.findViewById(R.id.creditCardsTextView);
        creditCardsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.displayCreditCardListFragment();
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
        jsonObject.addProperty("device_token", ((AndroidPlatform) Platform.get()).openDefaultSharedPreferences()
                .getString(CarePayConstants.FCM_TOKEN, null));
        jsonObject.addProperty("device_type", "android");
        TransitionDTO transitionDTO = demographicsSettingsDTO.getMetadata()
                .getTransitions().getUpdateNotifications();
        getWorkflowServiceHelper().execute(transitionDTO, updateNotificationPreferencesCallback, jsonObject.toString());
    }

    private String getCapitalizedUserName() {
        PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload().getDemographics()
                .getPayload().getPersonalDetails();
        String firstName = demographicsPersonalDetails.getFirstName();
        String lastName = demographicsPersonalDetails.getLastName();
        if(firstName == null){
            firstName = "";
        }
        if(lastName == null){
            lastName = "";
        }
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
            PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
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
            DemographicsSettingsPaymentSettingsDTO settingsPayloadDTO = DtoHelper.getConvertedDTO(DemographicsSettingsPaymentSettingsDTO.class, workflowDTO);

            boolean hasPush = settingsPayloadDTO.getPayload().getDemographicSettingsNotificationDTO().getPayload().isPush();
            demographicsSettingsDTO.getPayload().getDemographicSettingsNotificationDTO().getPayload().setPush(hasPush);
            pushNotificationCheckBox.setChecked(hasPush);

            boolean hasEmail = settingsPayloadDTO.getPayload().getDemographicSettingsNotificationDTO().getPayload().isEmail();
            demographicsSettingsDTO.getPayload().getDemographicSettingsNotificationDTO().getPayload().setEmail(hasEmail);
            emailNotificationCheckBox.setChecked(hasEmail);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(exceptionMessage);
            pushNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload()
                    .getDemographicSettingsNotificationDTO().getPayload().isPush());
            emailNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload()
                    .getDemographicSettingsNotificationDTO().getPayload().isEmail());

        }
    };

}

