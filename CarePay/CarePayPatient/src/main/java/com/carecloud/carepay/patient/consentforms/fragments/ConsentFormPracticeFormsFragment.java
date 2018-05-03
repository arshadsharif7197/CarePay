package com.carecloud.carepay.patient.consentforms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.consentforms.adapters.PracticeConsentPracticeFormsAdapter;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormPracticeFormInterface;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormsFormsInterface;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.consentforms.models.payload.FormDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 6/04/18.
 */
public class ConsentFormPracticeFormsFragment extends BaseFragment implements ConsentFormsFormsInterface {

    private ConsentFormPracticeFormInterface callback;
    private ConsentFormDTO consentFormDto;
    private List<PracticeForm> selectedForms;
    private Button signSelectedFormsButton;
    private int selectedPracticeIndex;

    public static ConsentFormPracticeFormsFragment newInstance(int selectedProviderIndex) {
        Bundle args = new Bundle();
        args.putInt("selectedPracticeIndex", selectedProviderIndex);
        ConsentFormPracticeFormsFragment fragment = new ConsentFormPracticeFormsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ConsentFormPracticeFormInterface) {
            callback = (ConsentFormPracticeFormInterface) context;
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        consentFormDto = (ConsentFormDTO) callback.getDto();
        selectedPracticeIndex = getArguments().getInt("selectedPracticeIndex");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consent_forms_provider, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectedForms = new ArrayList<>();
        signSelectedFormsButton = (Button) view.findViewById(R.id.signSelectedFormsButton);
        signSelectedFormsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.showForms(selectedForms, selectedPracticeIndex, true);
            }
        });
        FormDTO practiceForms = consentFormDto.getPayload().getForms().get(selectedPracticeIndex);
        setModifiedDates(practiceForms.getPracticeForms(), practiceForms.getPatientFormsFilled());
        RecyclerView practiceConsentFormsRecyclerView = (RecyclerView) view
                .findViewById(R.id.providerConsentFormsRecyclerView);
        practiceConsentFormsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PracticeConsentPracticeFormsAdapter adapter = new PracticeConsentPracticeFormsAdapter(practiceForms.getPracticeForms(),
                practiceForms.getPendingForms());
        adapter.setCallback(this);
        practiceConsentFormsRecyclerView.setAdapter(adapter);
        setUpToolbar(view, practiceForms);
    }

    protected void setUpToolbar(View view, FormDTO practiceForms) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        String practiceName = "";
        for (UserPracticeDTO practiceInformation : consentFormDto.getPayload().getPracticesInformation()) {
            if (practiceForms.getMetadata().getPracticeId().equals(practiceInformation.getPracticeId())) {
                practiceName = practiceInformation.getPracticeName();
            }
        }
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(practiceName);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        callback.setToolbar(toolbar);
    }


    private void setModifiedDates(List<PracticeForm> allPracticeForms,
                                  List<ConsentFormUserResponseDTO> patientFormsFilled) {
        for (PracticeForm practiceForm : allPracticeForms) {
            for (ConsentFormUserResponseDTO consentFormUserResponseDTO : patientFormsFilled) {
                if (consentFormUserResponseDTO.getFormId().equals(practiceForm.getPayload()
                        .get("uuid").getAsString())) {
                    practiceForm.setLastModifiedDate(consentFormUserResponseDTO.getMetadata()
                            .get("updated_dt").toString());
                }
                practiceForm.setSelected(false);
            }
        }
    }

    @Override
    public void onPendingFormSelected(PracticeForm form, boolean selected) {
        if (selected) {
            selectedForms.add(form);
        } else {
            selectedForms.remove(form);
        }
        signSelectedFormsButton.setEnabled(!selectedForms.isEmpty());
    }

    @Override
    public void onFilledFormSelected(PracticeForm form) {
        List<PracticeForm> localSelectedForm = new ArrayList<>();
        PracticeForm localPracticeForm = new PracticeForm();
        Gson gson = new Gson();
        localPracticeForm.setPayload(gson.fromJson(gson.toJson(form.getPayload()), JsonObject.class));
        localPracticeForm.getPayload().getAsJsonObject("fields").addProperty("readonly", true);
        localSelectedForm.add(localPracticeForm);
        callback.showForms(localSelectedForm, selectedPracticeIndex, false);
    }


}
