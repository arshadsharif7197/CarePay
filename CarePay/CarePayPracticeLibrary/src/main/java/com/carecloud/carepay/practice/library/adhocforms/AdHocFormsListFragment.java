package com.carecloud.carepay.practice.library.adhocforms;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adhoc.SelectedAdHocForms;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdHocFormsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdHocFormsListFragment extends BaseDialogFragment
        implements AdHocFormRecyclerViewAdapter.AdHocFormsListInterface {

    private AppointmentsResultModel dto;
    private SelectedAdHocForms selectedForms;
    private Button fillNowFormButton;
    private Button sendFormButton;
    private String patientId;

    public AdHocFormsListFragment() {
        // Required empty public constructor
    }

    /**
     * @param appointmentsResultModel the appoinment model
     * @param patientId               the appointment id
     * @return a new instance of AdHocFormsListFragment
     */
    public static AdHocFormsListFragment newInstance(AppointmentsResultModel appointmentsResultModel,
                                                     String patientId) {
        AdHocFormsListFragment fragment = new AdHocFormsListFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentsResultModel);
        args.putString("patientId", patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dto = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, getArguments());
        patientId = getArguments().getString("patientId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adhoc_forms_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectedForms = new SelectedAdHocForms();
        if (!dto.getMetadata().getDataModels().getAllPracticeForms().isEmpty()) {
            setModifiedDates(dto.getMetadata().getDataModels().getAllPracticeForms(),
                    dto.getPayload().getPatientFormsFilled());
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.formsRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            AdHocFormRecyclerViewAdapter adapter = new AdHocFormRecyclerViewAdapter(dto.getMetadata().getDataModels()
                    .getAllPracticeForms());
            adapter.setCallback(this);
            recyclerView.setAdapter(adapter);

            fillNowFormButton = (Button) view.findViewById(R.id.fillNowFormButton);
            fillNowFormButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, String> queryMap = new HashMap<>();
                    queryMap.put("patient_id", patientId);
                    TransitionDTO adHocFormsTransition = dto.getMetadata().getTransitions().getAdHocFormsPatientMode();
                    JsonObject jsonObject = new JsonObject();
                    JsonArray jsonArray = new JsonArray();
                    for (String uuidForm : selectedForms.getForms()) {
                        JsonObject uuiiJson = new JsonObject();
                        uuiiJson.addProperty("form_uuid", uuidForm);
                        jsonArray.add(uuiiJson);
                    }
                    jsonObject.add("adhoc_forms", jsonArray);
                    getWorkflowServiceHelper().execute(adHocFormsTransition, adHocServiceCallback,
                            jsonObject.toString(), queryMap);
                }
            });

            sendFormButton = (Button) view.findViewById(R.id.sendFormButton);
            sendFormButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, String> queryMap = new HashMap<>();
                    queryMap.put("patient_id", patientId);
                    queryMap.put("practice_mgmt", dto.getPayload().getUserPractices().get(0).getPracticeMgmt());
                    queryMap.put("practice_id", dto.getPayload().getUserPractices().get(0).getPracticeId());
                    JsonObject jsonObject = new JsonObject();
                    JsonArray jsonArray = new JsonArray();
                    for (String uuidForm : selectedForms.getForms()) {
                        JsonObject uuiiJson = new JsonObject();
                        uuiiJson.addProperty("form_uuid", uuidForm);
                        jsonArray.add(uuiiJson);
                    }
                    jsonObject.add("pending_forms", jsonArray);
                    TransitionDTO adHocFormsTransition = dto.getMetadata().getTransitions().getUpdatePendingForms();
                    getWorkflowServiceHelper().execute(adHocFormsTransition, sendFormsServiceCallback,
                            jsonObject.toString(), queryMap);
                }
            });

            //TODO: delete this line when pending forms are ready (SHMRK-5240)
            sendFormButton.setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.noFormsContainer).setVisibility(View.VISIBLE);
        }

        View close = view.findViewById(R.id.closeViewLayout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    WorkflowServiceCallback sendFormsServiceCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            dismiss();
            String successMessage = Label.getLabel("adhoc.forms.message.label.formSentSingular");
            if (selectedForms.getForms().size() > 1) {
                successMessage = Label.getLabel("adhoc.forms.message.label.formSentPlural");
            }
            ((BaseActivity) getActivity()).showSuccessToast(successMessage);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback adHocServiceCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.SELECTED_FORMS, selectedForms);
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, bundle);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void setModifiedDates(List<PracticeForm> allPracticeForms,
                                  List<ConsentFormUserResponseDTO> patientFormsFilled) {
        for (PracticeForm practiceForm : allPracticeForms) {
            for (ConsentFormUserResponseDTO consentFormUserResponseDTO : patientFormsFilled) {
                if (consentFormUserResponseDTO.getFormId().equals(practiceForm.getPayload()
                        .get("uuid").getAsString())) {
                    practiceForm.setLastModifiedDate(consentFormUserResponseDTO.getMetadata()
                            .get("updated_dt").getAsString());
                }
            }
        }
    }

    @Override
    public void onFormSelected(PracticeForm practiceForm, boolean selected) {
        if (selected) {
            selectedForms.getForms()
                    .add(practiceForm.getPayload().get("uuid").toString().replace("\"", ""));
        } else {
            selectedForms.getForms()
                    .remove(practiceForm.getPayload().get("uuid").toString().replace("\"", ""));
        }
        fillNowFormButton.setEnabled(!selectedForms.getForms().isEmpty());
        sendFormButton.setEnabled(!selectedForms.getForms().isEmpty());
    }
}
