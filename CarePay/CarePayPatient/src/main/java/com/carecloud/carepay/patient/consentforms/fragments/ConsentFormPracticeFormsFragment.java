package com.carecloud.carepay.patient.consentforms.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.consentforms.adapters.ConsentFormsAdapter;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormPracticeFormInterface;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormsFormsInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.PendingFormDTO;
import com.carecloud.carepaylibray.consentforms.models.UserFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
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

    private static final int ITEMS_PER_PAGE = 20;
    private static final int BOTTOM_ROW_OFFSET = (int) (ITEMS_PER_PAGE * 0.33);

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consent_forms_provider, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectedForms = new ArrayList<>();
        signSelectedFormsButton = view.findViewById(R.id.signSelectedFormsButton);
        if (mode == ConsentFormViewPagerFragment.PENDING_MODE) {
            signSelectedFormsButton.setOnClickListener(v -> {
                List<ConsentFormUserResponseDTO> localSelectedFormResponse = new ArrayList<>();
                for (PracticeForm form : selectedForms) {
                    localSelectedFormResponse.add(form.getFormUserResponseDTO());
                }
                callback.showForms(selectedForms, localSelectedFormResponse, selectedPracticeIndex, true);
            });
            if (canReviewForms()) {
                signSelectedFormsButton.setVisibility(View.VISIBLE);
            }
        }

        setUpRecyclerView(view);
    }

    protected void setUpRecyclerView(View view) {
        UserFormDTO userFormDto = consentFormDto.getPayload().getUserForms().get(selectedPracticeIndex);
        RecyclerView practiceConsentFormsRecyclerView = view
                .findViewById(R.id.providerConsentFormsRecyclerView);
        practiceConsentFormsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<PracticeForm> practiceForms = getPracticeForms(userFormDto);
        if (!consentFormDto.getPayload().canViewForms(userFormDto.getMetadata().getPracticeId())) {
            showNoPermissionScreen(view);
            practiceConsentFormsRecyclerView.setVisibility(View.GONE);
            return;
        }
        if (practiceForms.isEmpty()) {
            showEmptyScreen(view);
            practiceConsentFormsRecyclerView.setVisibility(View.GONE);
            return;
        }
        adapter = new ConsentFormsAdapter(getContext(), this, practiceForms, mode);
        practiceConsentFormsRecyclerView.setAdapter(adapter);

        if (mode == ConsentFormViewPagerFragment.HISTORIC_MODE) {
            paging = userFormDto.getHistoryForms().getPaging();
            practiceConsentFormsRecyclerView.addOnScrollListener(scrollListener);
        }
    }

    private void showNoPermissionScreen(View view) {
        view.findViewById(R.id.emptyStateScreen).setVisibility(View.VISIBLE);
        TextView title = view.findViewById(R.id.emptyStateTitleTextView);
        title.setText(Label.getLabel("patient.delegation.delegates.permissions.label.noPermission"));
        signSelectedFormsButton.setVisibility(View.GONE);
        view.findViewById(R.id.emptyStateSubTitleTextView).setVisibility(View.GONE);
    }

    private void showEmptyScreen(View view) {
        view.findViewById(R.id.emptyStateScreen).setVisibility(View.VISIBLE);
        TextView title = view.findViewById(R.id.emptyStateTitleTextView);
        title.setText(Label.getLabel(mode == ConsentFormViewPagerFragment.HISTORIC_MODE ?
                "adhoc.historyforms.empty.label.title" : "adhoc.pendingforms.empty.label.title"));
        signSelectedFormsButton.setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.emptyStateSubTitleTextView)).setText(
                mode == ConsentFormViewPagerFragment.HISTORIC_MODE ?
                        Label.getLabel("adhoc.historyforms.empty.label.description") :
                        Label.getLabel("adhoc.forms.empty.label.description"));
    }

    private List<PracticeForm> getPracticeForms(UserFormDTO userFormDTO) {
        List<PracticeForm> practiceForms = new ArrayList<>();
        if (mode == ConsentFormViewPagerFragment.PENDING_MODE) {
            for (PendingFormDTO pendingForm : userFormDTO.getPendingForms().getForms()) {
                pendingForm.getForm().setLastModifiedDate(pendingForm.getPayload().getUpdatedDate());
                pendingForm.getForm().setFormUserResponseDTO(pendingForm.getPayload());
                practiceForms.add(pendingForm.getForm());
            }
        } else {
            for (PendingFormDTO pendingForm : userFormDTO.getHistoryForms().getForms()) {
                pendingForm.getForm().setLastModifiedDate(pendingForm.getPayload().getUpdatedDate());
                pendingForm.getForm().setFormUserResponseDTO(pendingForm.getPayload());
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
        localPracticeForm.setLastModifiedDate(form.getLastModifiedDate());
        localSelectedForm.add(localPracticeForm);
        List<ConsentFormUserResponseDTO> localSelectedFormResponse = new ArrayList<>();
        localSelectedFormResponse.add(form.getFormUserResponseDTO());
        callback.showForms(localSelectedForm, localSelectedFormResponse, selectedPracticeIndex, false);
    }

    @Override
    public boolean canReviewForms() {
        String practiceId = consentFormDto.getPayload().getUserForms()
                .get(selectedPracticeIndex).getMetadata().getPracticeId();
        return consentFormDto.getPayload().canReviewForms(practiceId);
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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
        query.put("page_count", String.valueOf(ITEMS_PER_PAGE));

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
