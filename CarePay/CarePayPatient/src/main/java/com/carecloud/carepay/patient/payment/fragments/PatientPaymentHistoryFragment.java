package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.adapters.PaymentHistoryAdapter;
import com.carecloud.carepay.patient.payment.interfaces.PaymentFragmentActivityInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.NextPagingDTO;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 02/01/17
 */
public class PatientPaymentHistoryFragment extends BaseFragment
        implements PaymentHistoryAdapter.HistoryItemClickListener {
    private static final int BOTTOM_ROW_OFFSET = 2;

    private PaymentsModel paymentsModel;
    private Paging paging;
    private List<PaymentHistoryItem> paymentHistoryItems = new ArrayList<>();
    private PaymentFragmentActivityInterface callback;

    private RecyclerView historyRecyclerView;
    private View noPaymentsLayout;
    private boolean isPaging = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PaymentFragmentActivityInterface) getContext();
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentPatientInterface");
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
        paymentsModel = (PaymentsModel) callback.getDto();
        paging = paymentsModel.getPaymentPayload().getTransactionHistory().getPageDetails();
        paymentHistoryItems = filterPaymentHistory(paymentsModel.getPaymentPayload()
                .getTransactionHistory().getPaymentHistoryList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_balance_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noPaymentsLayout = view.findViewById(R.id.no_payment_layout);
        setUpRecyclerView(view);
    }

    private void setUpRecyclerView(View view) {
        historyRecyclerView = (RecyclerView) view.findViewById(R.id.payment_list_recycler);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        historyRecyclerView.addOnScrollListener(scrollListener);

        if (hasCharges()) {
            setAdapter(null);
        } else {
            showNoPaymentsLayout();
        }

    }

    private void setAdapter(List<PaymentHistoryItem> newItems) {
        PaymentHistoryAdapter historyAdapter = (PaymentHistoryAdapter) historyRecyclerView.getAdapter();
        if (historyAdapter != null) {
            if (newItems != null && !newItems.isEmpty()) {
                historyAdapter.addHistoryList(newItems);
            } else {
                historyAdapter.setPaymentHistoryItems(paymentHistoryItems);
            }
        } else {
            historyAdapter = new PaymentHistoryAdapter(getContext(), paymentHistoryItems,
                    paymentsModel.getPaymentPayload().getUserPractices(), this);
            historyRecyclerView.setAdapter(historyAdapter);
        }
    }

    private void showNoPaymentsLayout() {
        noPaymentsLayout.setVisibility(View.VISIBLE);
    }

    private List<PaymentHistoryItem> filterPaymentHistory(List<PaymentHistoryItem> paymentHistoryItems) {
        List<PaymentHistoryItem> output = new ArrayList<>();
        for (PaymentHistoryItem item : paymentHistoryItems) {
            if (item.getPayload().getTotalPaid() > 0
                    || item.getPayload().getProcessingErrors().isEmpty()) {
                output.add(item);
            }
        }
        return output;
    }

    private boolean hasCharges() {
        return !paymentsModel.getPaymentPayload().getTransactionHistory().getPaymentHistoryList().isEmpty();
    }

    private boolean hasMorePages() {
        return paging.getCurrentPage() != paging.getTotalPages();
    }

    private void loadNextPage() {
        NextPagingDTO next = new NextPagingDTO();
        next.setNextPage(paging.getCurrentPage() + 1);
        next.setPageCount(paging.getResultsPerPage());

        TransitionDTO nextPageTransition = paymentsModel.getPaymentsMetadata().getPaymentsLinks()
                .getPaymentTransactionHistory();
        String payload = new Gson().toJson(next);
        getWorkflowServiceHelper().execute(nextPageTransition, nextPageCallback, payload);
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

    private WorkflowServiceCallback nextPageCallback = new WorkflowServiceCallback() {
        PaymentHistoryAdapter adapter;

        @Override
        public void onPreExecute() {
            adapter = (PaymentHistoryAdapter) historyRecyclerView.getAdapter();
            adapter.setLoading(true);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            adapter.setLoading(false);
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            Paging nextPage = paymentsModel.getPaymentPayload().getTransactionHistory().getPageDetails();
            if (nextPage.getCurrentPage() != paging.getCurrentPage()) {
                paging = nextPage;
                List<PaymentHistoryItem> newItems = paymentsModel.getPaymentPayload()
                        .getTransactionHistory().getPaymentHistoryList();
                List<PaymentHistoryItem> filteredItems = filterPaymentHistory(newItems);
                setAdapter(filteredItems);
                paymentHistoryItems.addAll(filteredItems);
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

    @Override
    public void onHistoryItemClicked(PaymentHistoryItem item) {
        callback.displayPaymentHistoryDetails(item);
    }
}
