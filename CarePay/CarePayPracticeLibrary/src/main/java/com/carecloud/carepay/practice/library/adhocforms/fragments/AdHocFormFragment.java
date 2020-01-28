package com.carecloud.carepay.practice.library.adhocforms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.carecloud.carepay.practice.library.adhocforms.AdHocFormCompletedDialogFragment;
import com.carecloud.carepay.practice.library.adhocforms.AdHocFormsInterface;
import com.carecloud.carepay.practice.library.adhocforms.AdHocFormsModel;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.checkout.BaseWebFormFragment;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.intake.models.AppointmentMetadataModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        adhocFormsModel = (AdHocFormsModel) callback.getDto();
        filledForms = adhocFormsModel.getPayload().getPatientFormsResponse();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        formsList = callback.getFormsList();
        if (formsList != null) {
            setTotalForms(formsList.size());
        }

        setHeader(Label.getLabel("adhoc_form_title"));
        lastFormButtonLabel = Label.getLabel("adhoc_sign_form_button_label");
        nextButton.setText(Label.getLabel("adhoc_sign_form_button_label"));
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(view1 -> getActivity().onBackPressed());
        }
        callback.highlightFormName(getDisplayedFormsIndex());
    }

    @Override
    protected void displayNextForm(List<ConsentFormUserResponseDTO> filledForms) {
        int displayedFormsIndex = getDisplayedFormsIndex();
        if (getDisplayedFormsIndex() < getTotalForms()) {
            Toolbar toolbar = getView().findViewById(R.id.toolbar_layout);
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
                String uuid = payload.get("uuid").getAsString();
                if (adhocFormsModel.getPayload().getPatientFormsResponse() != null) {
                    for (ConsentFormUserResponseDTO response : filledForms) {
                        if (uuid.equals(response.getFormId())) {
                            JsonObject json = new JsonObject();
                            json.addProperty("uuid", response.getFormId());
                            json.add("response", response.getResponse());
                            userResponse = json;
                            break;
                        }
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
        return HttpConstants.getFormsUrl() + "/practice-forms/index.html";
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

    private WorkflowServiceCallback updateFormCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            showAllDone(workflowDTO);
            nextButton.setEnabled(true);
        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            nextButton.setEnabled(true);
            hideProgressDialog();
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
            Log.e(getString(R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };

    private void showAllDone(WorkflowDTO workflowDTO) {
        AdHocFormsModel localAdHocFormsModel = DtoHelper.getConvertedDTO(AdHocFormsModel.class, workflowDTO);
        adhocFormsModel.getPayload().setFilledForms(localAdHocFormsModel.getPayload().getFilledForms());
        adhocFormsModel.getPayload().setDemographicDTO(localAdHocFormsModel.getPayload().getDemographicDTO());
        callback.showAllDone(AdHocFormCompletedDialogFragment.newInstance());
    }

    @Override
    protected void validateForm() {
        validateForm("save_form");
    }

    /**
     */
    public void navigateBack() {
        if (getTotalForms() > 1 && getDisplayedFormsIndex() > 0) {
            setDisplayedFormsIndex(getDisplayedFormsIndex() - 1);
            displayNextForm(filledForms);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("formIndex", getDisplayedFormsIndex());
        Gson gson = new Gson();
        outState.putString("formResponses", gson.toJson(jsonFormSaveResponseArray));
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            int formIndex = savedInstanceState.getInt("formIndex", 0);
            setDisplayedFormsIndex(formIndex);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<JsonObject>>() {
            }.getType();
            jsonFormSaveResponseArray = gson.fromJson(savedInstanceState.getString("formResponses"), listType);
        }
    }
}
