package com.carecloud.carepaylibray.demographics.fragments;

import android.os.Bundle;
import android.view.View;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.intake.models.IntakeFindings;
import com.carecloud.carepaylibray.intake.models.IntakeForm;
import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by lmenendez on 3/23/17
 */

public class IntakeFormsFragment extends BaseWebFormFragment {

    private IntakeResponseModel intakeResponseModel;
    private List<JsonObject> jsonFormSaveResponseArray = new ArrayList<>();
    private List<IntakeForm> intakeFormList;
    private IntakeFindings intakeFindings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        String jsonString = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
        intakeResponseModel = gson.fromJson(jsonString, IntakeResponseModel.class);
        intakeFormList = intakeResponseModel.getPayload().getIntakeForms();
        intakeFindings = intakeResponseModel.getPayload().getFindings();
        setTotalForms(intakeFormList.size());
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        setHeader(Label.getLabel("practice_chekin_section_intake_forms"));
    }

    @Override
    protected void displayNextForm() {
        int displayedFormsIndex = getDisplayedFormsIndex();
        if (getDisplayedFormsIndex() < getTotalForms()) {
            IntakeForm intakeForm = intakeFormList.get(displayedFormsIndex);
            JsonObject payload = intakeForm.getPayload();
            JsonObject userResponse = null;
            if (!jsonFormSaveResponseArray.isEmpty() && jsonFormSaveResponseArray.size() > displayedFormsIndex) {
                userResponse = jsonFormSaveResponseArray.get(displayedFormsIndex);
            }

            JsonObject form = new JsonObject();
            form.add("formData", payload);
            if (userResponse == null && displayedFormsIndex == 0) {//only send this the first time
                form.add("userData", intakeFindings.getPayload());
            } else {
                form.add("userData", userResponse);
            }
            String formString = form.toString()
                    .replaceAll("\'", Matcher.quoteReplacement("\\\'"))
                    .replaceAll("\"", Matcher.quoteReplacement("\\\""))
                    .replace("\\n", "");

            loadFormUrl(formString, "load_intake");
        }
    }

    @Override
    protected String getBaseUrl() {
        return HttpConstants.getIntakeFormsUrl();
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
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", intakeResponseModel.getPayload().getAppointments().get(0).getMetadata().getPracticeMgmt());
        queries.put("practice_id", intakeResponseModel.getPayload().getAppointments().get(0).getMetadata().getPracticeId());
        queries.put("appointment_id", intakeResponseModel.getPayload().getAppointments().get(0).getMetadata().getAppointmentId());
        queries.put("patient_id", intakeResponseModel.getPayload().getAppointments().get(0).getMetadata().getPatientId());


        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");
        header.put("username_patient", intakeResponseModel.getPayload().getAppointments().get(0).getMetadata().getUsername());

        Gson gson = new Gson();
        String body = gson.toJson(jsonFormSaveResponseArray);
        TransitionDTO transitionDTO = intakeResponseModel.getMetadata().getTransitions().getUpdateIntake();
        getWorkflowServiceHelper().execute(transitionDTO, updateformCallBack, body, queries, header);

    }

    @Override
    protected void validateForm() {
        validateForm("save_intake");
    }

    @Override
    protected CheckinFlowState getCheckinFlowState() {
        return CheckinFlowState.INTAKE;
    }
}
