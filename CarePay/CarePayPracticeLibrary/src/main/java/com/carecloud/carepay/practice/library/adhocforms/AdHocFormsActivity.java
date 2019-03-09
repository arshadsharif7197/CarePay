package com.carecloud.carepay.practice.library.adhocforms;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.checkin.adapters.LanguageAdapter;
import com.carecloud.carepay.practice.library.customdialog.ConfirmationPinDialog;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.CompleteCheckActivity;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerLanguage;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adhoc.AdhocFormsPatientModeInfo;
import com.carecloud.carepaylibray.adhoc.SelectedAdHocForms;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdHocFormsActivity extends BasePracticeActivity implements AdHocFormsInterface {

    private AdHocFormsModel adhocFormsModel;
    private SelectedAdHocForms selectedAdHocForms;
    private ArrayList<PracticeForm> forms;
    private AdHocRecyclerViewAdapter adapter;

    private AdhocFormsPatientModeInfo patientModeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
        setContentView(R.layout.activity_ad_hoc_forms);
        adhocFormsModel = getConvertedDTO(AdHocFormsModel.class);
        Bundle bundle = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        selectedAdHocForms = (SelectedAdHocForms) bundle.getSerializable(CarePayConstants.SELECTED_FORMS);

        switchToPatientMode();
        patientModeInfo = adhocFormsModel.getPayload().getAdhocFormsPatientModeInfo();

        initView();

        if (savedInstanceState == null) {
            addFragment(AdHocFormFragment.newInstance(), false);
        }
    }

    private void initView() {
        forms = new ArrayList<>();
        if (selectedAdHocForms != null) {
            for (String uuid : selectedAdHocForms.getForms()) {
                for (PracticeForm practiceForm : adhocFormsModel.getMetadata().getDataModels()
                        .getPracticeForms()) {
                    if (uuid.equals(practiceForm.getPayload().get("uuid").getAsString())) {
                        forms.add(practiceForm);
                        break;
                    }
                }
            }
        }
        RecyclerView formsNamesRecyclerView = (RecyclerView) findViewById(R.id.formsNamesRecyclerView);
        formsNamesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdHocRecyclerViewAdapter(forms);
        formsNamesRecyclerView.setAdapter(adapter);

        TextView header = (TextView) findViewById(R.id.adhoc_forms_header);
        header.setText(Label.getLabel(forms.size() > 1 ?
                "adhoc_form_left_message" : "adhoc_form_left_message_singular"));

        View.OnClickListener goBackClicListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPinDialog();
            }
        };
        findViewById(R.id.goBackImageView).setOnClickListener(goBackClicListener);

        TextView backTextView = findViewById(R.id.goBackTextView);
        backTextView.setText(Label.getLabel("demographics_back_text"));
        backTextView.setOnClickListener(goBackClicListener);
        initializeLanguageSpinner();
    }

    private void initializeLanguageSpinner() {
        String selectedLanguageStr = getApplicationPreferences().getUserLanguage();
        OptionDTO selectedLanguage = adhocFormsModel.getPayload().getLanguages().get(0);
        for (OptionDTO language : adhocFormsModel.getPayload().getLanguages()) {
            if (selectedLanguageStr.equals(language.getCode())) {
                selectedLanguage = language;
            }
        }

        final TextView languageSwitch = (TextView) findViewById(R.id.languageSpinner);
        final PopupPickerLanguage popupPickerLanguage = new PopupPickerLanguage(getContext(), true);
        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int offsetX = view.getWidth() / 2 - popupPickerLanguage.getWidth() / 2;
                int offsetY = -view.getHeight() - popupPickerLanguage.getHeight();
                popupPickerLanguage.showAsDropDown(view, offsetX, offsetY);
            }
        });
        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
        final Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
//        headers.put("username", getApplicationPreferences().getUserName());
        headers.put("username_patient", getApplicationPreferences().getPatientId());
        LanguageAdapter languageAdapter = new LanguageAdapter(adhocFormsModel.getPayload().getLanguages(),
                selectedLanguage);
        popupPickerLanguage.setAdapter(languageAdapter);
        languageAdapter.setCallback(new LanguageAdapter.LanguageInterface() {
            @Override
            public void onLanguageSelected(OptionDTO language) {
                popupPickerLanguage.dismiss();
                changeLanguage(adhocFormsModel.getMetadata().getLinks().getLanguage(),
                        language.getCode().toLowerCase(), headers, new SimpleCallback() {
                            @Override
                            public void callback() {
                                refreshSelfDto();
                            }
                        });

            }
        });
    }

    private void refreshSelfDto() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", patientModeInfo.getMetadata().getPatientId());
        TransitionDTO self = adhocFormsModel.getMetadata().getLinks().getSelf();
        getWorkflowServiceHelper().execute(self, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                adhocFormsModel = DtoHelper.getConvertedDTO(AdHocFormsModel.class, workflowDTO);
                adhocFormsModel.getPayload().setAdhocFormsPatientModeInfo(patientModeInfo);
                initView();
                addFragment(AdHocFormFragment.newInstance(), false);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        }, queryMap);
    }

    private void showPinDialog() {
        ConfirmationPinDialog confirmationPinDialog = new ConfirmationPinDialog(getContext(),
                adhocFormsModel.getMetadata().getLinks().getPinpad(), false,
                adhocFormsModel.getMetadata().getLinks().getLanguage());
        confirmationPinDialog.show();
    }

    @Override
    public DTO getDto() {
        return adhocFormsModel;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.formLayout, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.formLayout, fragment, addToBackStack);
    }

    @Override
    public List<PracticeForm> getFormsList() {
        return forms;
    }

    @Override
    public void highlightFormName(int displayedFormsIndex) {
        adapter.highlightFormName(displayedFormsIndex);
    }

    @Override
    public void showAllDone(WorkflowDTO workflowDTO) {
        WorkFlowRecord workFlowRecord = new WorkFlowRecord(workflowDTO);
        workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));
        Bundle bundle = new Bundle();
        bundle.putLong(CarePayConstants.EXTRA_WORKFLOW, workFlowRecord.save(getContext()));
        bundle.putBoolean(CarePayConstants.ADHOC_FORMS, true);
        Intent intent = new Intent(this, CompleteCheckActivity.class);
        intent.putExtra(CarePayConstants.EXTRA_BUNDLE, bundle);
        startActivity(intent);

        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_patient_id),
                getString(R.string.param_forms_count)};
        Object[] values = {getApplicationMode().getUserPracticeDTO().getPracticeId(),
                adhocFormsModel.getPayload().getAdhocFormsPatientModeInfo().getMetadata().getPatientId(),
                forms.size()};
        MixPanelUtil.logEvent(getString(R.string.event_adhoc_forms_completed), params, values);
    }

    @Override
    public void onBackPressed() {
        AdHocFormFragment adHocFormFragment = (AdHocFormFragment) getSupportFragmentManager()
                .findFragmentById(R.id.formLayout);
        adHocFormFragment.navigateBack();
    }

    @Override
    public void onPinConfirmationCheck(boolean isCorrectPin, String pin) {
        TransitionDTO transitionDTO = adhocFormsModel.getMetadata().getTransitions().getPracticeMode();
        Map<String, String> query = new HashMap<>();
        query.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        query.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
        getWorkflowServiceHelper().execute(transitionDTO, patientHomeCallback, query);
    }

    WorkflowServiceCallback patientHomeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);

            String[] params = {getString(R.string.param_practice_id),
                    getString(R.string.param_patient_id)};
            Object[] values = {getApplicationMode().getUserPracticeDTO().getPracticeId(),
                    adhocFormsModel.getPayload().getAdhocFormsPatientModeInfo().getMetadata().getPatientId()};
            MixPanelUtil.logEvent(getString(R.string.event_adhoc_forms_cancelled), params, values);

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void switchToPatientMode() {
        getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
        getAppAuthorizationHelper().setUser(adhocFormsModel.getPayload().getAdhocFormsPatientModeInfo().getMetadata().getUsername());

        MixPanelUtil.setUser(getContext(), adhocFormsModel.getPayload().getAdhocFormsPatientModeInfo().getMetadata().getUserId(), null);

        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_patient_id)};
        Object[] values = {getApplicationMode().getUserPracticeDTO().getPracticeId(),
                adhocFormsModel.getPayload().getAdhocFormsPatientModeInfo().getMetadata().getPatientId()};
        MixPanelUtil.logEvent(getString(R.string.event_adhoc_forms_started), params, values);

    }
}
