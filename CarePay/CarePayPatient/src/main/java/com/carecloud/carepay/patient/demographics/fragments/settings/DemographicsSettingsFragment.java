package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.patient.payment.fragments.CreditCardListFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPaymentSettingsDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsSettingsFragment extends BaseFragment {


    private DemographicDTO demographicsSettingsDTO;
    private DemographicsSettingsFragmentListener callback;
    private CheckBox pushNotificationCheckBox;
    private CheckBox emailNotificationCheckBox;
    private CheckBox smsNotificationCheckBox;

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
        demographicsSettingsDTO = (DemographicDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demographics_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Toolbar toolbar = view.findViewById(R.id.settings_toolbar);
        TextView title = toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("settings_heading"));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setUpUi(view);
        setClickListeners(view);
    }

    private void setUpUi(View view) {
        CarePayTextView patientNameTextView = view.findViewById(R.id.patient_name);
        PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload().getDemographics()
                .getPayload().getPersonalDetails();
        String firstName = demographicsPersonalDetails.getFirstName();
        String lastName = demographicsPersonalDetails.getLastName();
        patientNameTextView.setText(StringUtil.getCapitalizedUserName(firstName, lastName));
        CarePayTextView patientIdTextView = view.findViewById(R.id.patient_id);
        patientIdTextView.setText(demographicsSettingsDTO.getPayload().getCurrentEmail());

        initializeHelpButton(view);

        String imageUrl = demographicsPersonalDetails.getProfilePhoto();
        if (!StringUtil.isNullOrEmpty(imageUrl)) {
            ImageView profileImageview = view.findViewById(R.id.providerPicImageView);
            Picasso.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.icn_placeholder_user_profile_png)
                    .resize(160, 160)
                    .centerCrop()
                    .transform(new CircleImageTransform())
                    .into(profileImageview);

        }

        pushNotificationCheckBox = view.findViewById(R.id.pushNotificationCheckBox);
        pushNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload().getDemographicSettingsNotificationDTO().getPayload().isPush());
        emailNotificationCheckBox = view.findViewById(R.id.emailNotificationCheckBox);
        emailNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload().getDemographicSettingsNotificationDTO().getPayload().isEmail());
        smsNotificationCheckBox = view.findViewById(R.id.smsNotificationCheckBox);
        smsNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload().getDemographicSettingsNotificationDTO().getPayload().isSms());
        smsNotificationCheckBox.setVisibility(View.VISIBLE);

        MixPanelUtil.addCustomPeopleProperty(getString(R.string.people_enabled_push), pushNotificationCheckBox.isChecked());
        MixPanelUtil.addCustomPeopleProperty(getString(R.string.people_enabled_email), emailNotificationCheckBox.isChecked());
        MixPanelUtil.addCustomPeopleProperty(getString(R.string.people_enabled_sms), smsNotificationCheckBox.isChecked());
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    private void initializeHelpButton(View view) {
        TextView textView = view.findViewById(R.id.helpTextView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.replaceFragment(new HelpFragment(), true);
                MixPanelUtil.logEvent(getString(R.string.event_help_clicked));
            }
        });
    }

    private void setClickListeners(View view) {
        Button signOutButton = view.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.logOut();
            }
        });

        CarePayTextView editTextView = view.findViewById(R.id.editTextView);
        editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileFragment editProfileFragment = EditProfileFragment.newInstance();
                callback.replaceFragment(editProfileFragment, true);
            }
        });

        CarePayTextView demographicsTextView = view.findViewById(R.id.demographicsTextView);
        demographicsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DemographicsInformationFragment demographicsInformationFragment =
                        DemographicsInformationFragment.newInstance();
                callback.replaceFragment(demographicsInformationFragment, true);
            }
        });

        CarePayTextView documentsTextview = view.findViewById(R.id.documentsTextView);
        documentsTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsDocumentsFragment settingsDocumentsFragment = SettingsDocumentsFragment.newInstance();
                callback.replaceFragment(settingsDocumentsFragment, true);
            }
        });

        CarePayTextView creditCardsTextView = view.findViewById(R.id.creditCardsTextView);
        creditCardsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreditCardListFragment creditCardListFragment = new CreditCardListFragment();
                callback.replaceFragment(creditCardListFragment, true);
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
        smsNotificationCheckBox.setOnClickListener(checkBoxClickListener);
    }

    private void updateNotificationPreferences() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("push", pushNotificationCheckBox.isChecked());
        jsonObject.addProperty("email", emailNotificationCheckBox.isChecked());
        jsonObject.addProperty("sms", smsNotificationCheckBox.isChecked());
        jsonObject.addProperty("device_token", ((AndroidPlatform) Platform.get()).openDefaultSharedPreferences()
                .getString(CarePayConstants.FCM_TOKEN, null));
        jsonObject.addProperty("device_type", "android");
        TransitionDTO transitionDTO = demographicsSettingsDTO.getMetadata()
                .getTransitions().getUpdateNotifications();
        getWorkflowServiceHelper().execute(transitionDTO, updateNotificationPreferencesCallback, jsonObject.toString());
    }

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

            boolean hasSms = settingsPayloadDTO.getPayload().getDemographicSettingsNotificationDTO().getPayload().isSms();
            demographicsSettingsDTO.getPayload().getDemographicSettingsNotificationDTO().getPayload().setSms(hasSms);
            smsNotificationCheckBox.setChecked(hasSms);

            MixPanelUtil.logEvent(getString(R.string.event_updated_notifications));

            MixPanelUtil.addCustomPeopleProperty(getString(R.string.people_enabled_push), pushNotificationCheckBox.isChecked());
            MixPanelUtil.addCustomPeopleProperty(getString(R.string.people_enabled_email), emailNotificationCheckBox.isChecked());
            MixPanelUtil.addCustomPeopleProperty(getString(R.string.people_enabled_sms), smsNotificationCheckBox.isChecked());
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(exceptionMessage);
            pushNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload()
                    .getDemographicSettingsNotificationDTO().getPayload().isPush());
            emailNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload()
                    .getDemographicSettingsNotificationDTO().getPayload().isEmail());
            smsNotificationCheckBox.setChecked(demographicsSettingsDTO.getPayload()
                    .getDemographicSettingsNotificationDTO().getPayload().isSms());

        }
    };

}

