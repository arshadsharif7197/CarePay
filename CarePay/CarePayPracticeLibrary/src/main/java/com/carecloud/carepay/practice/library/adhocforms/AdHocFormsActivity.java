package com.carecloud.carepay.practice.library.adhocforms;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.customdialog.ConfirmationPinDialog;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.CompleteCheckActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.interfaces.DTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdHocFormsActivity extends BasePracticeActivity implements AdHocFormsInterface {

    private AppointmentsResultModel appointmentModel;
    private ArrayList<PracticeForm> forms;
    private AdHocRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_hoc_forms);
        appointmentModel = getConvertedDTO(AppointmentsResultModel.class);
        Bundle bundle = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        SelectedAdHocForms selectedAdHocForms = (SelectedAdHocForms) bundle.getSerializable("selectedForms");

        forms = new ArrayList<>();
        for (String uuid : selectedAdHocForms.getForms()) {
            for (PracticeForm practiceForm : appointmentModel.getMetadata().getDataModels()
                    .getPracticeForms()) {
                if (uuid.equals(practiceForm.getPayload().get("uuid").toString().replace("\"", ""))) {
                    forms.add(practiceForm);
                    break;
                }
            }
        }
        RecyclerView formsNamesRecyclerView = (RecyclerView) findViewById(R.id.formsNamesRecyclerView);
        formsNamesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdHocRecyclerViewAdapter(forms);
        formsNamesRecyclerView.setAdapter(adapter);

        View.OnClickListener goBackClicListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPinDialog();
            }
        };
        findViewById(R.id.goBackImageView).setOnClickListener(goBackClicListener);
        findViewById(R.id.goBackTextView).setOnClickListener(goBackClicListener);

        if (savedInstanceState == null) {
            addFragment(AdHocFormFragment.newInstance(), false);
        }
    }

    private void showPinDialog() {
        ConfirmationPinDialog confirmationPinDialog = new ConfirmationPinDialog(getContext(),
                appointmentModel.getMetadata().getLinks().getPinpad(), false);
        confirmationPinDialog.show();
    }

    private void createLabel(ViewGroup viewGroup, PracticeForm practiceForm) {
        CarePayTextView label = new CarePayTextView(getContext());

        viewGroup.addView(label);
    }

    @Override
    public DTO getDto() {
        return appointmentModel;
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
//        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.formsNamesContainer);
//        for (int index = 0; index < viewGroup.getChildCount(); ++index) {
//            CarePayTextView nextChild = (CarePayTextView) viewGroup.getChildAt(index);
//            if (displayedFormsIndex == index) {
//                nextChild.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_BOLD);
//            } else {
//                nextChild.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_LIGHT);
//            }
//        }
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
    }

    @Override
    public void onBackPressed() {
        AdHocFormFragment adHocFormFragment = (AdHocFormFragment) getSupportFragmentManager()
                .findFragmentById(R.id.formLayout);
        adHocFormFragment.navigateBack();
    }

    @Override
    public void onPinConfirmationCheck(boolean isCorrectPin, String pin) {
        TransitionDTO transitionDTO = appointmentModel.getMetadata().getTransitions().getPracticeMode();
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
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
