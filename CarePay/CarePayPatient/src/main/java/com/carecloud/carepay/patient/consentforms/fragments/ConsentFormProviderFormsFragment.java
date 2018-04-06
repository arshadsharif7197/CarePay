package com.carecloud.carepay.patient.consentforms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.consentforms.adapters.ProviderConsentFormsAdapter;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormsFormsInterface;
import com.carecloud.carepaylibray.adhoc.SelectedAdHocForms;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.consentforms.models.payload.FormDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

import java.util.List;

/**
 * @author pjohnson on 6/04/18.
 */
public class ConsentFormProviderFormsFragment extends BaseFragment implements ConsentFormsFormsInterface {

    private FragmentActivityInterface callback;
    private ConsentFormDTO consentFormDto;
    private SelectedAdHocForms selectedForms;
    private Button signSelectedFormsButton;

    public static ConsentFormProviderFormsFragment newInstance(int selectedProviderIndex) {
        Bundle args = new Bundle();
        args.putInt("selectedProviderIndex", selectedProviderIndex);
        ConsentFormProviderFormsFragment fragment = new ConsentFormProviderFormsFragment();
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
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        consentFormDto = (ConsentFormDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consent_forms_provider, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectedForms = new SelectedAdHocForms();
        signSelectedFormsButton = (Button) view.findViewById(R.id.signSelectedFormsButton);
        FormDTO providerForms = consentFormDto.getPayload().getForms()
                .get(getArguments().getInt("selectedProviderIndex"));
        setModifiedDates(providerForms.getPracticeForms(), providerForms.getPatientFormsFilled());
        RecyclerView providerConsentFormsRecyclerView = (RecyclerView) view.findViewById(R.id.providerConsentFormsRecyclerView);
        providerConsentFormsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ProviderConsentFormsAdapter adapter = new ProviderConsentFormsAdapter(providerForms.getPracticeForms());
        adapter.setCallback(this);
        providerConsentFormsRecyclerView.setAdapter(adapter);
    }


    private void setModifiedDates(List<PracticeForm> allPracticeForms,
                                  List<ConsentFormUserResponseDTO> patientFormsFilled) {
        for (PracticeForm practiceForm : allPracticeForms) {
            for (ConsentFormUserResponseDTO consentFormUserResponseDTO : patientFormsFilled) {
                if (consentFormUserResponseDTO.getFormId().equals(practiceForm.getPayload()
                        .get("uuid").toString().replace("\"", ""))) {
                    practiceForm.setLastModifiedDate(consentFormUserResponseDTO.getMetadata()
                            .get("updated_dt").toString());
                }
            }
        }
    }

    @Override
    public void onPendingFormSelected(PracticeForm form, boolean selected) {
        if (selected) {
            selectedForms.getForms()
                    .add(form.getPayload().get("uuid").getAsString());
        } else {
            selectedForms.getForms()
                    .remove(form.getPayload().get("uuid").getAsString());
        }
        signSelectedFormsButton.setEnabled(!selectedForms.getForms().isEmpty());
    }


}
