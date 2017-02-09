package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.dtos.DemographicSettingsCurrentPasswordDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsEmailProperties;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsHeaderDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsLoginEmailDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsMaintainanceDTO;
import com.carecloud.carepay.service.library.dtos.DemographicsSettingsProposedEmailDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;

import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.api.client.util.Base64;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsSettingUpdateEmailFragment extends Fragment {
    private AppCompatActivity appCompatActivity;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;

    private String changeEmailString = null;
    private String saveChangesString = null;
    private String emailString = null;

    private EditText emailEditText = null;
    private Button updateEmailButton = null;
    private boolean isEmailEmpty;
    private TextInputLayout emailLabel = null;
    private LinearLayout rootview;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    public DemographicsSettingUpdateEmailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_email_update, container, false);
        rootview = (LinearLayout) view.findViewById(R.id.settings_root);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(appCompatActivity, title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String demographicsSettingsDTOString = bundle.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }

        emailEditText = (EditText) view.findViewById(R.id.signinEmailEditText);
        updateEmailButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);

        getSettingsLabels();
        title.setText(changeEmailString);
        updateEmailButton.setText(saveChangesString);

        initialiseUIFields(view);
        setEditTexts(view);

        getPersonalDetails();
        setClickables(view);
        isEmailEmpty = true;
        return view;

    }

    /**
     * demographics settings labels
     */
    public void getSettingsLabels() {
        try{
            if (demographicsSettingsDTO != null) {
                DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                if (demographicsSettingsMetadataDTO != null) {
                    DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                    if (demographicsSettingsLabelsDTO != null) {

                        changeEmailString = demographicsSettingsLabelsDTO.getDemographicsChangeEmailLabel();
                        saveChangesString = demographicsSettingsLabelsDTO.getDemographicsSaveChangesLabel();
                        emailString = demographicsSettingsLabelsDTO.getEmailLabel();

                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initialiseUIFields(View view) {

        emailLabel = (TextInputLayout) view.findViewById(R.id.signInEmailTextInputLayout);
    }

    private void setEditTexts(View view) {
        emailLabel.setTag(emailString);
        emailEditText.setTag(emailLabel);
        emailEditText.setHint(emailString);
        setChangeFocusListeners();
    }

    private void setChangeFocusListeners() {
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

    }

    private void getPersonalDetails() {
        String userId = CognitoAppHelper.getCurrUser();

        if (SystemUtil.isNotEmptyString(userId)) {
            emailEditText.setText(userId);
            emailEditText.requestFocus();
        }

        rootview.requestFocus();
        SystemUtil.hideSoftKeyboard(getActivity());

    }

    private boolean checkEmail() {
        String email = emailEditText.getText().toString();
        isEmailEmpty = StringUtil.isNullOrEmpty(email);
        emailLabel.setErrorEnabled(isEmailEmpty); // enable for error if either empty or invalid first name
        if (isEmailEmpty) {
            emailLabel.setError("");
        } else {
            emailLabel.setError(null);
        }
        return !isEmailEmpty;
    }

    private boolean isEmailValid() {
        boolean isEmailValid = checkEmail();
        if (!isEmailValid) {
            emailEditText.requestFocus();
        }
        return !isEmailEmpty ;
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
        updateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isEmailValid() ) {
                        if (demographicsSettingsDTO != null) {
                            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                            if (demographicsSettingsMetadataDTO != null) {
                                DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                                TransitionDTO demographicsSettingsUpdateEmailDTO = demographicsSettingsTransitionsDTO.getChangeLoginEmail();
                                DemographicsSettingsHeaderDTO demographicsSettingsHeaderDTO =  demographicsSettingsUpdateEmailDTO.getHeader();
                                DemographicsSettingsMaintainanceDTO demographicsSettingsMaintainanceDTO = demographicsSettingsHeaderDTO.getMaintenance();
                                DemographicsSettingsEmailProperties demographicsSettingsEmailProperties = demographicsSettingsMaintainanceDTO.getProperties();
                                DemographicsSettingsLoginEmailDTO demographicsSettingsLoginEmailDTO = demographicsSettingsEmailProperties.getLoginEmail();
                                DemographicsSettingsProposedEmailDTO demographicsSettingsProposedEmailDTO = demographicsSettingsEmailProperties.getProposedEmail();
                                DemographicSettingsCurrentPasswordDTO demographicSettingsCurrentPasswordDTO = demographicsSettingsEmailProperties.getCurrentPassword();

                                demographicsSettingsEmailProperties.setLoginEmail(demographicsSettingsLoginEmailDTO);
                                demographicsSettingsEmailProperties.setProposedEmail(demographicsSettingsProposedEmailDTO);
                                demographicsSettingsEmailProperties.setCurrentPassword(demographicSettingsCurrentPasswordDTO);
                                String encodedProperties = SystemUtil.encodeToBase64(demographicsSettingsEmailProperties.getLoginEmail().toString()+ SystemUtil.encodeToBase64(demographicsSettingsEmailProperties.getProposedEmail().toString()+SystemUtil.encodeToBase64(demographicsSettingsEmailProperties.getCurrentPassword().toString())));
                                try {
                                    ByteArrayOutputStream bo = new ByteArrayOutputStream();
                                    ObjectOutputStream so = new ObjectOutputStream(bo);
                                    so.writeObject(demographicsSettingsEmailProperties);
                                    so.flush();
                                    String redisString = new String(Base64.encodeBase64(bo.toByteArray()));
                                    //demographicsSettingsMaintainanceDTO.setProperties(redisString);

                                }
                                catch (Exception e) {
                                    e.printStackTrace();        }
                           /*     Map<String, String> properties = null;
                                properties = new HashMap<>();
                                properties.put("login_email","harshalp@carecloud.com");
                                properties.put("proposed_email", "harshalpatil@carecloud.com");
                                properties.put("current_password","Test123");
                                JSONObject jso = new JSONObject( properties );
                                String encoded = new String(Base64.encodeBase64( jso.toString().getBytes()));*/

                                JSONObject payload = new JSONObject();
                                Map<String, String> queries = null;
                                Map<String, String> header = null;
                                header = new HashMap<>();
                                header.put("maintenance","eyJsb2dpbl9lbWFpbCI6ImhhcnNoYWxwQGNhcmVjbG91ZC5jb20iLCJwcm9wb3NlZF9lbWFpbCI6ImhhcnNoYWxwYXRpbEBjYXJlY2xvdWQuY29tIiwiY3VycmVudF9wYXNzd29yZCI6IlRlc3QxMjMhIn0");
                                //header.put("transition", "true");
                                try {
                                    if (demographicsSettingsDTO != null) {
                                        DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                                        if (demographicsSettingsPayloadDTO != null) {
                                            //DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                                            //DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload().;
                                            //DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();

                                            Gson gson = new Gson();
                                            String jsonInString = gson.toJson(demographicsSettingsPayloadDTO);
                                            WorkflowServiceHelper.getInstance().execute(demographicsSettingsUpdateEmailDTO, updateEmailCallback,null, queries, header);
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    WorkflowServiceCallback updateEmailCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Log.d("SETING",workflowDTO.toString());
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {

            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}

