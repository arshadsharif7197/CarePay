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
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.PendingFormDTO;
import com.carecloud.carepaylibray.consentforms.models.UserFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author pjohnson on 6/04/18.
 */
public class ConsentFormPracticeFormsFragment extends BaseFragment implements ConsentFormsFormsInterface {

    private static final int BOTTOM_ROW_OFFSET = 2;

    private ConsentFormPracticeFormInterface callback;
    private ConsentFormDTO consentFormDto;
    private List<PracticeForm> selectedForms;
    private Button signSelectedFormsButton;
    private int selectedPracticeIndex;
    private int mode;
    private Paging paging;
    private boolean isPaging;
    private ConsentFormsAdapter adapter;

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
        adapter = new ConsentFormsAdapter(practiceForms, mode);
        adapter.setCallback(this);
        practiceConsentFormsRecyclerView.setAdapter(adapter);

        if (mode == ConsentFormViewPagerFragment.HISTORIC_MODE) {
            paging = userFormDto.getHistoryForms().getPaging();
            practiceConsentFormsRecyclerView.addOnScrollListener(scrollListener);
        }
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

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            super.onScrolled(recyclerView, dx, dy);
            if (hasMorePages()) {
                int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (last > recyclerView.getAdapter().getItemCount() - BOTTOM_ROW_OFFSET && !isPaging) {
                    loadNextPage();
                    isPaging = true;
                }
            }

        }
    };

    private boolean hasMorePages() {
        return paging.getCurrentPage() != paging.getTotalPages();
    }

    private void loadNextPage() {
        PendingBalanceMetadataDTO metadata = consentFormDto.getPayload().getUserForms()
                .get(selectedPracticeIndex).getMetadata();
        HashMap<String, String> query = new HashMap<>();
        query.put("practice_mgmt", metadata.getPracticeMgmt());
        query.put("practice_id", metadata.getPracticeId());
        query.put("page_number", String.valueOf(paging.getCurrentPage() + 1));
        query.put("page_count", String.valueOf(paging.getResultsPerPage()));

        TransitionDTO nextPageTransition = consentFormDto.getMetadata().getLinks().getHistoryForms();
        getWorkflowServiceHelper().execute(nextPageTransition, nextPageCallback, query);
    }

    private WorkflowServiceCallback nextPageCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            adapter.setLoading(true);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            adapter.setLoading(false);
            ConsentFormDTO consentFormModel = DtoHelper.getConvertedDTO(ConsentFormDTO.class, workflowDTO);
            Paging nextPage = consentFormModel.getPayload().getUserForms().get(0).getHistoryForms().getPaging();
            if (nextPage.getCurrentPage() != paging.getCurrentPage()) {
                paging = nextPage;
                List<PracticeForm> newItems = getPracticeForms(consentFormModel.getPayload().getUserForms()
                        .get(0));
                adapter.addHistoryList(newItems);
            }
            isPaging = false;
        }

        @Override
        public void onFailure(String exceptionMessage) {
            adapter.setLoading(false);
            isPaging = false;
            showErrorNotification(exceptionMessage);
        }
    };


}
