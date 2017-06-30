package com.carecloud.carepaylibray.checkout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author pjohnson on 30/05/17.
 */

public class CheckOutFormFragment extends BaseWebFormFragment {

    private AppointmentsResultModel appointmentsResultModel;
    private List<JsonObject> jsonFormSaveResponseArray = new ArrayList<>();
    private List<PracticeForm> formsList;
    private CheckOutInterface callback;

    /**
     * @param appointmentsResultModel the appointments dto
     * @return a new instance of CheckOutFormFragment
     */
    public static CheckOutFormFragment newInstance(AppointmentsResultModel appointmentsResultModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentsResultModel);
        CheckOutFormFragment fragment = new CheckOutFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (CheckOutInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement ResponsibilityActionCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, getArguments());
        formsList = appointmentsResultModel.getMetadata().getDataModels().getPracticeForms();
        setTotalForms(formsList.size());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHeader(Label.getLabel("checkout_rating_form_title"));

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar != null && !callback.shouldAllowNavigateBack()) {
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(null);
        }
    }

    @Override
    protected void displayNextForm() {
        int displayedFormsIndex = getDisplayedFormsIndex();
        if (getDisplayedFormsIndex() < getTotalForms()) {
            PracticeForm practiceForm = formsList.get(displayedFormsIndex);
            JsonObject payload = practiceForm.getPayload();
            JsonObject userResponse = null;
            if (!jsonFormSaveResponseArray.isEmpty() && jsonFormSaveResponseArray.size() > displayedFormsIndex) {
                userResponse = jsonFormSaveResponseArray.get(displayedFormsIndex);
            } else {
                String uuid = payload.get("uuid").toString().replace("\"", "");
                for (ConsentFormUserResponseDTO response : appointmentsResultModel.getPayload().getResponses()) {
                    if (uuid.equals(response.getFormId())) {
                        JsonObject json = new JsonObject();
                        json.addProperty("uuid", response.getFormId());
                        json.add("response", response.getResponse());
                        userResponse = json;
                        break;
                    }
                }
            }
            JsonObject form = new JsonObject();
            form.add("formData", payload);
            form.add("userData", userResponse);
            String formString = form.toString().replaceAll("\\\\", Matcher.quoteReplacement("\\\\")).replaceAll("\'", Matcher.quoteReplacement("\\\'"));

            loadFormUrl(formString, "load_form");

        }
    }

    @Override
    protected String getBaseUrl() {
        return "file:///android_asset/breeze-practice-forms/dist/index.html";
    }

    @Override
    protected void saveForm(JsonObject jsonResponse) {
        int displayedFormsIndex = getDisplayedFormsIndex();
        if (jsonFormSaveResponseArray.size() > displayedFormsIndex) {
            jsonFormSaveResponseArray.set(displayedFormsIndex, jsonResponse);
        } else {
            jsonFormSaveResponseArray.add(jsonResponse);
        }
    }

    @Override
    protected void submitAllForms() {
        AppointmentDTO appointmentDTO = appointmentsResultModel.getPayload().getAppointments().get(0);
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queries.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());
        queries.put("patient_id", appointmentDTO.getMetadata().getPatientId());

        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");
        header.put("username_patient", appointmentDTO.getMetadata().getUsername());

        Gson gson = new Gson();
        String body = gson.toJson(jsonFormSaveResponseArray);
        TransitionDTO transitionDTO = appointmentsResultModel.getMetadata().getTransitions().getUpdateForms();
        getWorkflowServiceHelper().execute(transitionDTO, updateformCallBack, body, queries, header);
    }

    WorkflowServiceCallback updateformCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            callback.showAllDone(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    protected CheckinFlowState getCheckinFlowState() {
        return CheckinFlowState.CONSENT;
    }

    @Override
    protected void validateForm() {
        validateForm("save_form");
    }
}
