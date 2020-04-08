package com.carecloud.carepay.patient.consentforms.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.checkout.BaseWebFormFragment;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.UserFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author pjohnson on 2/05/18.
 */
public class FilledFormFragment extends BaseWebFormFragment {

    private ConsentFormDTO consentFormDto;
    private ConsentFormInterface callback;
    private List<JsonObject> jsonFormSaveResponseArray = new ArrayList<>();
    private UserFormDTO formDto;
    private boolean showSignButton;
    private TextView title;

    /**
     * @param selectedProviderIndex the provider index
     * @param showSignButton        to show or hide the bottom button
     * @return a new instance of FilledFormFragment
     */
    public static FilledFormFragment newInstance(int selectedProviderIndex, boolean showSignButton,
                                                 List<ConsentFormUserResponseDTO> formsResponses) {
        Bundle args = new Bundle();
        args.putInt("selectedProviderIndex", selectedProviderIndex);
        args.putBoolean("showSignButton", showSignButton);

        ConsentFormPayloadDTO responsePayload = new ConsentFormPayloadDTO();
        responsePayload.setResponses(formsResponses);
        DtoHelper.bundleDto(args, responsePayload);

        FilledFormFragment fragment = new FilledFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ConsentFormInterface) {
            callback = (ConsentFormInterface) context;
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        consentFormDto = (ConsentFormDTO) callback.getDto();
        formDto = consentFormDto.getPayload().getUserForms()
                .get(getArguments().getInt("selectedProviderIndex"));
        ConsentFormPayloadDTO responsePayload = DtoHelper
                .getConvertedDTO(ConsentFormPayloadDTO.class, getArguments());
        filledForms = responsePayload.getResponses();
        formsList = callback.getAllFormsToShow();
        setTotalForms(formsList.size());
    }


    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        lastFormButtonLabel = Label.getLabel("adhoc_sign_form_button_label");
        showSignButton = getArguments().getBoolean("showSignButton", false);
        nextButton.setVisibility(showSignButton ? View.VISIBLE : View.GONE);

        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(Label.getLabel("consentForms.consentForm.title.label.form"));
        callback.setToolbar(toolbar);
        toolbar.setNavigationOnClickListener(v -> navigateBack());
        nextButton.setBackgroundResource(R.drawable.button_green_selector);
    }

    @Override
    protected void displayNextForm(List<ConsentFormUserResponseDTO> filledForms) {
        int displayedFormsIndex = getDisplayedFormsIndex();
        if (showSignButton) {
            title.setText(String.format(
                    Label.getLabel("consentForms.consentForm.title.label.formCount"),
                    getDisplayedFormsIndex() + 1, getTotalForms()));
        }
        if (getDisplayedFormsIndex() < getTotalForms()) {
            PracticeForm practiceForm = formsList.get(displayedFormsIndex);
            JsonObject payload = practiceForm.getPayload();
            JsonObject userResponse = null;
            if (!jsonFormSaveResponseArray.isEmpty() && jsonFormSaveResponseArray.size() > displayedFormsIndex) {
                userResponse = jsonFormSaveResponseArray.get(displayedFormsIndex);
            } else {
                String uuid = payload.get("uuid").getAsString();
                for (ConsentFormUserResponseDTO response : filledForms) {
                    if (uuid.equals(response.getFormId())) {
                        JsonObject json = new JsonObject();
                        json.addProperty("uuid", response.getFormId());
                        json.add("response", response.getResponse());
                        if (!showSignButton) {
                            json.addProperty("updated_dt", practiceForm.getLastModifiedDate());
                        }
                        userResponse = json;
                        break;
                    }
                }
            }

            JsonObject form = new JsonObject();
            form.add("formData", payload);
            form.add("userData", userResponse);
            String formString = form.toString().replaceAll("\\\\", Matcher.quoteReplacement("\\\\"))
                    .replaceAll("\'", Matcher.quoteReplacement("\\\'"));

            loadFormUrl(formString, "load_form");

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
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", formDto.getMetadata().getPracticeMgmt());
        queries.put("practice_id", formDto.getMetadata().getPracticeId());
        queries.put("patient_id", formDto.getMetadata().getPatientId());

        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        Gson gson = new Gson();
        String body = gson.toJson(jsonFormSaveResponseArray);
        TransitionDTO transitionDTO = consentFormDto.getMetadata().getLinks().getUpdateForms();
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

    public boolean navigateBack() {
        if (getTotalForms() > 1 && getDisplayedFormsIndex() > 0) {
            setDisplayedFormsIndex(getDisplayedFormsIndex() - 1);
            displayNextForm(filledForms);
            return true;
        }
        getActivity().onBackPressed();
        return false;
    }
}
