package com.carecloud.carepay.practice.library.adhocforms;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.adhocforms.adapters.AdHocRecyclerViewAdapter;
import com.carecloud.carepay.practice.library.adhocforms.fragments.AdHocFormFragment;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.customdialog.ConfirmationPinDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerLanguage;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adhoc.AdhocFormsPatientModeInfo;
import com.carecloud.carepaylibray.adhoc.SelectedAdHocForms;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.interfaces.DTO;
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
        RecyclerView formsNamesRecyclerView = findViewById(R.id.formsNamesRecyclerView);
        formsNamesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdHocRecyclerViewAdapter(forms);
        formsNamesRecyclerView.setAdapter(adapter);

        TextView header = findViewById(R.id.adhoc_forms_header);
        header.setText(Label.getLabel(forms.size() > 1 ?
                "adhoc_form_left_message" : "adhoc_form_left_message_singular"));

        View.OnClickListener goBackClicListener = view -> showPinDialog();
        findViewById(R.id.goBackImageView).setOnClickListener(goBackClicListener);

        TextView backTextView = findViewById(R.id.goBackTextView);
        backTextView.setText(Label.getLabel("demographics_back_text"));
        backTextView.setOnClickListener(goBackClicListener);
        initializeLanguageSpinner();
    }

    private void initializeLanguageSpinner() {
        final Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        headers.put("username_patient", getApplicationPreferences().getPatientId());
        final PopupPickerLanguage popupPickerLanguage = new PopupPickerLanguage(getContext(), true,
                adhocFormsModel.getPayload().getLanguages(), language -> changeLanguage(adhocFormsModel
                        .getMetadata().getLinks().getLanguage(),
                language.getCode().toLowerCase(), headers, () -> refreshSelfDto()));
        final TextView languageSwitch = findViewById(R.id.languageSpinner);
        languageSwitch.setOnClickListener(view -> {
            int offsetX = view.getWidth() / 2 - popupPickerLanguage.getWidth() / 2;
            int offsetY = -view.getHeight() - popupPickerLanguage.getHeight();
            popupPickerLanguage.showAsDropDown(view, offsetX, offsetY);
        });
        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
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
            public void onFailure(ServerErrorDTO serverErrorDto) {
                hideProgressDialog();
                showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        }, queryMap);
    }

    private void showPinDialog() {
        ConfirmationPinDialog confirmationPinDialog = ConfirmationPinDialog.newInstance(
                adhocFormsModel.getMetadata().getLinks().getPinpad(),
                false,
                adhocFormsModel.getMetadata().getLinks().getLanguage());
        displayDialogFragment(confirmationPinDialog, false);
    }

    @Override
    public DTO getDto() {
        if (adhocFormsModel == null) {
            adhocFormsModel = getConvertedDTO(AdHocFormsModel.class);
        }
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
    public void showAllDone(AdHocFormCompletedDialogFragment fragment) {
        findViewById(R.id.allDoneContainer).setVisibility(View.VISIBLE);
        addFragment(R.id.allDoneContainer, fragment, true);
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
        public void onFailure(ServerErrorDTO serverErrorDto) {
            hideProgressDialog();
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
            Log.e(getString(R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
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

    @Override
    public boolean manageSession() {
        return true;
    }

    @Override
    public TransitionDTO getLogoutTransition() {
        return adhocFormsModel.getMetadata().getTransitions().getLogout();
    }
}
