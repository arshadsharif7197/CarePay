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
import com.carecloud.carepay.patient.consentforms.adapters.ConsentFormsAdapter;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormPracticeFormInterface;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormsFormsInterface;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.PendingFormDTO;
import com.carecloud.carepaylibray.consentforms.models.UserFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
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
    private int mode;

    public static ConsentFormPracticeFormsFragment newInstance(int selectedProviderIndex, int mode) {
        Bundle args = new Bundle();
        args.putInt("selectedPracticeIndex", selectedProviderIndex);
        args.putInt("mode", mode);
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
        mode = getArguments().getInt("mode");
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
        if (mode == ConsentFormViewPagerFragment.PENDING_MODE) {
            signSelectedFormsButton = (Button) view.findViewById(R.id.signSelectedFormsButton);
            signSelectedFormsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.showForms(selectedForms, selectedPracticeIndex, true);
                }
            });
            signSelectedFormsButton.setVisibility(View.VISIBLE);
        }

        setUpRecyclerView(view);
    }

    protected void setUpRecyclerView(View view) {
        UserFormDTO userFormDto = consentFormDto.getPayload().getUserForms().get(selectedPracticeIndex);
        RecyclerView practiceConsentFormsRecyclerView = (RecyclerView) view
                .findViewById(R.id.providerConsentFormsRecyclerView);
        practiceConsentFormsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<PracticeForm> practiceForms = getPracticeForms(userFormDto);
        ConsentFormsAdapter adapter = new ConsentFormsAdapter(practiceForms, mode);
        adapter.setCallback(this);
        practiceConsentFormsRecyclerView.setAdapter(adapter);
    }

    private List<PracticeForm> getPracticeForms(UserFormDTO userFormDTO) {
        List<PracticeForm> practiceForms = new ArrayList<>();
        if (mode == ConsentFormViewPagerFragment.PENDING_MODE) {
            for (PendingFormDTO pendingForm : userFormDTO.getPendingForms().getForms()) {
                pendingForm.getForm().setLastModifiedDate(pendingForm.getPayload().getUpdatedDate());
                practiceForms.add(pendingForm.getForm());
            }
        } else {
            for (PendingFormDTO pendingForm : userFormDTO.getHistoryForms().getForms()) {
                pendingForm.getForm().setLastModifiedDate(pendingForm.getForm().getMetadata().getUpdatedDate());
                practiceForms.add(pendingForm.getForm());
            }
        }

        return practiceForms;
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
