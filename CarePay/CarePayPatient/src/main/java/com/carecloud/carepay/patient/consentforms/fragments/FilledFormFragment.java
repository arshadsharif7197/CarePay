package com.carecloud.carepay.patient.consentforms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.checkout.BaseWebFormFragment;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author pjohnson on 2/05/18.
 */
public class FilledFormFragment extends BaseWebFormFragment {

    private ConsentFormDTO consentFormDto;
    private FragmentActivityInterface callback;
    private List<PracticeForm> formsList;


    /**
     * @param form the form dto
     * @return a new instance of FilledFormFragment
     */
    public static FilledFormFragment newInstance(PracticeForm form, int selectedProviderIndex) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, form);
        args.putInt("selectedProviderIndex", selectedProviderIndex);
        FilledFormFragment fragment = new FilledFormFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInterface) {
            callback = (FragmentActivityInterface) context;
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        consentFormDto = (ConsentFormDTO) callback.getDto();
        formsList = new ArrayList<>();
        formsList.add(DtoHelper.getConvertedDTO(PracticeForm.class, getArguments()));
        setTotalForms(formsList.size());
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        nextButton.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        ((TextView) toolbar.findViewById(R.id.toolbar_title))
                .setText(Label.getLabel("consentForms.consentForm.title.label.form"));
        callback.setToolbar(toolbar);
    }

    @Override
    protected void displayNextForm() {
        int displayedFormsIndex = getDisplayedFormsIndex();
        if (getDisplayedFormsIndex() < getTotalForms()) {
            PracticeForm practiceForm = formsList.get(displayedFormsIndex);
            JsonObject payload = practiceForm.getPayload();
            JsonObject userResponse = null;

            String uuid = payload.get("uuid").toString().replace("\"", "");
            for (ConsentFormUserResponseDTO response : consentFormDto.getPayload().getForms()
                    .get(getArguments().getInt("selectedProviderIndex")).getPatientFormsResponses()) {
                if (uuid.equals(response.getFormId())) {
                    JsonObject json = new JsonObject();
                    json.addProperty("uuid", response.getFormId());
                    json.add("response", response.getResponse());
                    userResponse = json;
                    break;
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
        /*NOT NECESSARY*/
    }

    @Override
    protected void submitAllForms() {
        /*NOT NECESSARY*/
    }

    @Override
    protected void validateForm() {
        /*NOT NECESSARY*/
    }
}
