package com.carecloud.carepay.patient.consentforms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormInterface;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.checkout.BaseWebFormFragment;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author pjohnson on 2/05/18.
 */
public class FilledFormFragment extends BaseWebFormFragment {

    private ConsentFormDTO consentFormDto;
    private ConsentFormInterface callback;
    private List<PracticeForm> formsList;
    private List<JsonObject> jsonFormSaveResponseArray = new ArrayList<>();

    /**
     * @param selectedProviderIndex the provider index
     * @param showSignButton        to show or hide the bottom button
     * @return a new instance of FilledFormFragment
     */
    public static FilledFormFragment newInstance(int selectedProviderIndex, boolean showSignButton) {
        Bundle args = new Bundle();
        args.putInt("selectedProviderIndex", selectedProviderIndex);
        args.putBoolean("showSignButton", showSignButton);
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
        filledForms = consentFormDto.getPayload().getForms()
                .get(getArguments().getInt("selectedProviderIndex")).getPatientFormsResponses();
        formsList = callback.getAllFormsToShow();
        setTotalForms(formsList.size());
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        lastFormButtonLabel = Label.getLabel("adhoc_sign_form_button_label");
        nextButton.setVisibility(getArguments()
                .getBoolean("showSignButton", false) ? View.VISIBLE : View.GONE);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        ((TextView) toolbar.findViewById(R.id.toolbar_title))
                .setText(Label.getLabel("consentForms.consentForm.title.label.form"));
        callback.setToolbar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });
    }

    @Override
    protected void displayNextForm(List<ConsentFormUserResponseDTO> filledForms) {
        int displayedFormsIndex = getDisplayedFormsIndex();
        if (getDisplayedFormsIndex() < getTotalForms()) {
            PracticeForm practiceForm = formsList.get(displayedFormsIndex);
            JsonObject payload = practiceForm.getPayload();
            JsonObject userResponse = null;
            if (!jsonFormSaveResponseArray.isEmpty() && jsonFormSaveResponseArray.size() > displayedFormsIndex) {
                userResponse = jsonFormSaveResponseArray.get(displayedFormsIndex);
            } else {
                String uuid = payload.get("uuid").toString().replace("\"", "");
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
        /*NOT IMPLEMENTED YET*/
    }

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
