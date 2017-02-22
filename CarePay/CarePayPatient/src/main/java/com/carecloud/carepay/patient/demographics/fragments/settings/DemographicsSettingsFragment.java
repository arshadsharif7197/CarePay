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
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.demographics.activities.DemographicsSettingsActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsSettingsFragment extends BaseFragment {
    private static final String LOG_TAG = DemographicsSettingsFragment.class.getSimpleName();
    private AppCompatActivity appCompatActivity;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private String demographicsString = null;
    private String documentsString = null;
    private String creditCardsString = null;
    private String signOutString = null;
    private String editString = null;
    private String settingsString = null;
    private String messagesString = null;
    private CarePayTextView editTextview = null;
    private Button signOutButton = null;
    private CarePayTextView demographicsTextview = null;
    private CarePayTextView documentsTextview = null;
    private CarePayTextView creditCardsTextview;
    private CarePayTextView messagesTextview = null;
    private ImageView profileImageview = null;
    private IDemographicsSettingsFragmentListener activityCallback;

    public interface IDemographicsSettingsFragmentListener {
        void initializeCreditCardListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            activityCallback = (IDemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IDemographicsSettingsFragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String demographicsSettingsDTOString = bundle.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }
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

        getSettingsLabels();
        String userId = getCognitoAppHelper().getCurrUser();
        demographicsTextview = (CarePayTextView) view.findViewById(R.id.demographicsTextView);
        documentsTextview = (CarePayTextView) view.findViewById(R.id.documentsTextView);
        creditCardsTextview = (CarePayTextView) view.findViewById(R.id.creditCardsTextView);
        CarePayTextView creditCardsTextview = (CarePayTextView) view.findViewById(R.id.creditCardsTextView);
        messagesTextview = (CarePayTextView) view.findViewById(R.id.messagesTextView);
        editTextview = (CarePayTextView) view.findViewById(R.id.editTextView);
        signOutButton = (Button) view.findViewById(R.id.signOutButton);
        CarePayTextView patientNameTextview = (CarePayTextView) view.findViewById(R.id.patient_name);
        CarePayTextView patientIdTextview = (CarePayTextView) view.findViewById(R.id.patient_id);
        profileImageview = (ImageView) view.findViewById(R.id.providerPicImageView);

        demographicsTextview.setText(demographicsString);
        documentsTextview.setText(documentsString);
        messagesTextview.setText(messagesString);
        creditCardsTextview.setText(creditCardsString);
        editTextview.setText(editString);
        signOutButton.setText(signOutString);
        title.setText(settingsString);
        patientNameTextview.setText(getUserName());
        patientIdTextview.setText(userId);
        try {
         DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
         DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
         DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
         DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
         String imageUrl = demographicsPersonalDetails.getProfilePhoto();
         if (!StringUtil.isNullOrEmpty(imageUrl)) {
           Picasso.with(getActivity()).load(imageUrl).transform(
                new CircleImageTransform()).resize(160, 160).into(this.profileImageview);
         }
         setClickables(view);
         }catch(Exception e){
            e.printStackTrace();
         }
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
                    messagesString = demographicsSettingsLabelsDTO.getSettingsMessagesLabel();

                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * For tests
     *
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        appCompatActivity = activity;
    }

    private void setClickables(View view) {
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutButton.setEnabled(false);
                getWorkflowServiceHelper().executeApplicationStartRequest(logOutCall);

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
                if (fragment.getArguments() != null) {
                    fragment.getArguments().putAll(bundle);
                } else {
                    fragment.setArguments(bundle);
                }

                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        EditProfileFragment.class.getSimpleName()).addToBackStack(null).commit();


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
                if (fragment.getArguments() != null) {
                    fragment.getArguments().putAll(bundle);
                } else {
                    fragment.setArguments(bundle);
                }

                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        DemographicsInformationFragment.class.getSimpleName()).addToBackStack(null).commit();

            }
        });

        documentsTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String demographicsSettingsDTOString = gson.toJson(demographicsSettingsDTO);
                bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, demographicsSettingsDTOString);

                FragmentManager fm = getActivity().getSupportFragmentManager();
                DemographicsSettingsDocumentsFragment fragment = (DemographicsSettingsDocumentsFragment)
                        fm.findFragmentByTag(DemographicsSettingsDocumentsFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new DemographicsSettingsDocumentsFragment();
                }

                //fix for random crashes
                if (fragment.getArguments() != null) {
                    fragment.getArguments().putAll(bundle);
                } else {
                    fragment.setArguments(bundle);
                }

                fm.beginTransaction().replace(R.id.activity_demographics_settings, fragment,
                        DemographicsSettingsDocumentsFragment.class.getSimpleName()).addToBackStack(null).commit();

            }
        });

        creditCardsTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try {
                if(demographicsSettingsDTO.getPayload().getPatientCreditCards()!=null &&
                        !demographicsSettingsDTO.getPayload().getPatientCreditCards().isEmpty()){
                    activityCallback.initializeCreditCardListFragment();
                } else {
                    ((DemographicsSettingsActivity)getActivity()).initializeAddNewCreditCardFragment();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            }
        });
    }

    private String getUserName() {
        try{
        if(demographicsSettingsDTO!=null) {
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
            DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
            DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
            String firstName = demographicsPersonalDetails.getFirstName();
            String lastName = demographicsPersonalDetails.getLastName();
            String userName = firstName + " " + lastName;
            return userName;
        }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
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
            getCognitoAppHelper().getPool().getUser().signOut();
            getCognitoAppHelper().setUser(null);
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            signOutButton.setEnabled(true);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
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

