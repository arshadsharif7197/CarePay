package com.carecloud.carepaylibray.demographics.fragments;

import android.os.Bundle;
import android.view.View;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by lmenendez on 3/23/17.
 */

public class FormsFragment extends BaseWebFormFragment {

    private ConsentFormDTO consentFormDTO;
    private List<JsonObject> jsonFormSaveResponseArray = new ArrayList<>();
    private List<PracticeForm> consentFormList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        String jsonString = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
        consentFormDTO = gson.fromJson(jsonString, ConsentFormDTO.class);
        consentFormList = consentFormDTO.getMetadata().getDataModels().getPracticeForms();
        setTotalForms(consentFormList.size());
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        setHeader(Label.getLabel("demographics_consent_forms_title"));
    }


    @Override
    protected void displayNextForm() {
        int displayedFormsIndex = getDisplayedFormsIndex();
        if (getDisplayedFormsIndex() < getTotalForms()) {
            PracticeForm practiceForm = consentFormList.get(displayedFormsIndex);
            JsonObject payload = practiceForm.getPayload();
            JsonObject userResponse = null;
            if (!jsonFormSaveResponseArray.isEmpty() && jsonFormSaveResponseArray.size() > displayedFormsIndex) {
                userResponse = jsonFormSaveResponseArray.get(displayedFormsIndex);
            }
            JsonObject form = new JsonObject();
            form.add("formData", payload);
            form.add("userData", userResponse);
            String formString = form.toString().replaceAll("\'", Matcher.quoteReplacement("\\\'"));

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
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPracticeMgmt());
        queries.put("practice_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPracticeId());
        queries.put("appointment_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getAppointmentId());
        queries.put("patient_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPatientId());


        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");
        header.put("username_patient", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getUsername());

        Gson gson = new Gson();
        String body = gson.toJson(jsonFormSaveResponseArray);
        TransitionDTO transitionDTO = consentFormDTO.getMetadata().getTransitions().getUpdateConsent();
        getWorkflowServiceHelper().execute(transitionDTO, updateformCallBack, body, queries, header);

    }

    @Override
    protected void validateForm() {
        validateForm("save_form");
    }

    @Override
    protected CheckinFlowState getCheckinFlowState() {
        return CheckinFlowState.CONSENT;
    }


}