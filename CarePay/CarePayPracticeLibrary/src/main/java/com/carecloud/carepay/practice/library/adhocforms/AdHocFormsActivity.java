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
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adhoc.SelectedAdHocForms;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdHocFormsActivity extends BasePracticeActivity implements AdHocFormsInterface {

    private AdHocFormsModel adhocFormsModel;
    private ArrayList<PracticeForm> forms;
    private AdHocRecyclerViewAdapter adapter;

    private boolean isUserInteraction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
        setContentView(R.layout.activity_ad_hoc_forms);
        adhocFormsModel = getConvertedDTO(AdHocFormsModel.class);
        Bundle bundle = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        SelectedAdHocForms selectedAdHocForms = (SelectedAdHocForms) bundle.getSerializable(CarePayConstants.SELECTED_FORMS);

        switchToPatientMode();

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
        findViewById(R.id.goBackTextView).setOnClickListener(goBackClicListener);
        initializeLanguageSpinner();
        if (savedInstanceState == null) {
            addFragment(AdHocFormFragment.newInstance(), false);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        isUserInteraction = true;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.languageContainer).setVisibility(View.GONE);
            }
        }, 25);
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
        final View languageContainer = findViewById(R.id.languageContainer);
        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageContainer.setVisibility(languageContainer.getVisibility() == View.VISIBLE
                        ? View.GONE : View.VISIBLE);
            }
        });
        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
        final Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
//        headers.put("username", getApplicationPreferences().getUserName());
        headers.put("username_patient", getApplicationPreferences().getPatientId());
        RecyclerView languageList = (RecyclerView) findViewById(R.id.languageList);
        LanguageAdapter languageAdapter = new LanguageAdapter(adhocFormsModel.getPayload().getLanguages(),
                selectedLanguage);
        languageList.setAdapter(languageAdapter);
        languageList.setLayoutManager(new LinearLayoutManager(getContext()));
        languageAdapter.setCallback(new LanguageAdapter.LanguageInterface() {
            @Override
            public void onLanguageSelected(OptionDTO language) {
                languageContainer.setVisibility(View.GONE);
                if (!isUserInteraction) {
                    return;
                }
                changeLanguage(adhocFormsModel.getMetadata().getLinks().getLanguage(),
                        language.getCode().toLowerCase(), headers);

            }
        });
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
        bundle.putLong(CarePayConstants.EXTRA_WORKFLOW, workFlowRecord.save());
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
