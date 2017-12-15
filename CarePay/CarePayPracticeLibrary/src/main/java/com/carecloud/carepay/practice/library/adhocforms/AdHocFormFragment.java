package com.carecloud.carepay.practice.library.adhocforms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.checkout.BaseWebFormFragment;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.intake.models.AppointmentMetadataModel;
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

public class AdHocFormFragment extends BaseWebFormFragment {

    private AdHocFormsModel adhocFormsModel;
    private List<JsonObject> jsonFormSaveResponseArray = new ArrayList<>();
    private List<PracticeForm> formsList;
    private AdHocFormsInterface callback;

    /**
     * @return a new instance of AdHocFormFragment
     */
    public static AdHocFormFragment newInstance() {
        return new AdHocFormFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (AdHocFormsInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement FragmentActivityInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adhocFormsModel = (AdHocFormsModel) callback.getDto();
        formsList = callback.getFormsList();
        if(formsList != null) {
            setTotalForms(formsList.size());
        }

        setHeader(Label.getLabel("adhoc_form_title"));
        lastFormButtonLabel = Label.getLabel("adhoc_sign_form_button_label");
        nextButton.setText(Label.getLabel("adhoc_sign_form_button_label"));
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
        }
        callback.highlightFormName(getDisplayedFormsIndex());
    }

    @Override
    protected void displayNextForm() {
        int displayedFormsIndex = getDisplayedFormsIndex();
        if (getDisplayedFormsIndex() < getTotalForms()) {
            Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar_layout);
            if (getDisplayedFormsIndex() == 0) {
                toolbar.setNavigationIcon(null);
            } else {
                toolbar.setNavigationIcon(R.drawable.icn_nav_back);
            }
            PracticeForm practiceForm = formsList.get(displayedFormsIndex);
            JsonObject payload = practiceForm.getPayload();
            JsonObject userResponse = null;
            if (!jsonFormSaveResponseArray.isEmpty() && jsonFormSaveResponseArray.size() > displayedFormsIndex) {
                userResponse = jsonFormSaveResponseArray.get(displayedFormsIndex);
            } else {
                String uuid = payload.get("uuid").toString().replace("\"", "");
                for (ConsentFormUserResponseDTO response : adhocFormsModel.getPayload().getPatientFormsResponse()) {
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
            callback.highlightFormName(getDisplayedFormsIndex());
        }
    }

    @Override
    protected String getBaseUrl() {
        return HttpConstants.getFormsUrl() + "/practice-forms/";
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
        AppointmentMetadataModel patientInfo = adhocFormsModel.getPayload()
                .getAdhocFormsPatientModeInfo().getMetadata();
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", patientInfo.getPracticeMgmt());
        queries.put("practice_id", patientInfo.getPracticeId());
        queries.put("patient_id", patientInfo.getPatientId());

        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");
        header.put("username_patient", patientInfo.getUsername());

        Gson gson = new Gson();
        String body = gson.toJson(jsonFormSaveResponseArray);
        TransitionDTO transitionDTO = adhocFormsModel.getMetadata().getTransitions().getUpdateForms();
        getWorkflowServiceHelper().execute(transitionDTO, updateFormCallBack, body, queries, header);
    }

    WorkflowServiceCallback updateFormCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            callback.showAllDone(workflowDTO);
            nextButton.setEnabled(true);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            nextButton.setEnabled(true);
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    protected void validateForm() {
        validateForm("save_form");
    }

    /**
     * @return a boolean indicating if it intercepts the event
     */
    public boolean navigateBack() {
        if (getTotalForms() > 1 && getDisplayedFormsIndex() > 0) {
            setDisplayedFormsIndex(getDisplayedFormsIndex() - 1);
            displayNextForm();
            return true;
        }
        return false;
    }
}
