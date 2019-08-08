package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.adapters.PaymentHistoryAdapter;
import com.carecloud.carepay.patient.payment.dialogs.PaymentHistoryDetailDialogFragment;
import com.carecloud.carepay.patient.payment.interfaces.PaymentFragmentActivityInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.NextPagingDTO;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carecloud.carepay.patient.payment.fragments.PaymentBalanceHistoryFragment.PAGE_HISTORY;

/**
 * Created by jorge on 02/01/17
 */
public class PatientPaymentHistoryFragment extends BaseFragment
        implements PaymentHistoryAdapter.HistoryItemClickListener {
    private static final int ITEMS_PER_PAGE = 50;
    private static final int BOTTOM_ROW_OFFSET = (int) (ITEMS_PER_PAGE * 0.33);

    private PaymentsModel paymentsModel;
    private Paging paging;
    private List<PaymentHistoryItem> paymentHistoryItems = new ArrayList<>();
    private PaymentFragmentActivityInterface callback;

    private RecyclerView historyRecyclerView;
    private View noPaymentsLayout;
    private SwipeRefreshLayout refreshLayout;
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
                        .getTransactionHistory().getPaymentHistoryList(),
                paymentsModel.getPaymentPayload().getUserPractices());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_balance_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noPaymentsLayout = view.findViewById(R.id.no_payment_layout);
        setUpRecyclerView(view);
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(() -> callback.onRequestRefresh(PAGE_HISTORY));
    }

    private void setUpRecyclerView(View view) {
        historyRecyclerView = view.findViewById(R.id.payment_list_recycler);
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
            Map<String, UserPracticeDTO> practiceMap = new HashMap<>();
            for (UserPracticeDTO userPracticeDTO : paymentsModel.getPaymentPayload().getUserPractices()) {
                practiceMap.put(userPracticeDTO.getPracticeId(), userPracticeDTO);
            }
            historyAdapter = new PaymentHistoryAdapter(getContext(), paymentHistoryItems, this, practiceMap);
            historyRecyclerView.setAdapter(historyAdapter);
        }
    }

    private void showNoPaymentsLayout() {
        noPaymentsLayout.setVisibility(View.VISIBLE);
    }

    private List<PaymentHistoryItem> filterPaymentHistory(List<PaymentHistoryItem> paymentHistoryItems,
                                                          List<UserPracticeDTO> userPractices) {
        List<PaymentHistoryItem> output = new ArrayList<>();
        for (PaymentHistoryItem item : paymentHistoryItems) {
            for (UserPracticeDTO userPracticeDTO : userPractices) {
                if (paymentsModel.getPaymentPayload().canViewBalanceAndHistoricalPayments(userPracticeDTO.getPracticeId())) {
                    if ((item.getPayload().getTotalPaid() > 0
                            || item.getPayload().getProcessingErrors().isEmpty())
                            && item.getMetadata().getPracticeId().equals(userPracticeDTO.getPracticeId())) {
                        output.add(item);
                    }

                }
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
        next.setPageCount(ITEMS_PER_PAGE);

        TransitionDTO nextPageTransition = paymentsModel.getPaymentsMetadata().getPaymentsLinks()
                .getPaymentTransactionHistory();
        String payload = new Gson().toJson(next);
        getWorkflowServiceHelper().execute(nextPageTransition, nextPageCallback, payload);
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            super.onScrolled(recyclerView, dx, dy);
            if (hasMorePages()) {
                int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (last > recyclerView.getAdapter().getItemCount() - BOTTOM_ROW_OFFSET && !isPaging) {
                    isPaging = true;
                    loadNextPage();
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
            PaymentsModel localPaymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            Paging nextPage = localPaymentsModel.getPaymentPayload().getTransactionHistory().getPageDetails();
            if (nextPage.getCurrentPage() != paging.getCurrentPage()) {
                paging = nextPage;
                List<PaymentHistoryItem> newItems = localPaymentsModel.getPaymentPayload()
                        .getTransactionHistory().getPaymentHistoryList();
                List<PaymentHistoryItem> filteredItems = filterPaymentHistory(newItems,
                        localPaymentsModel.getPaymentPayload().getDelegate() == null
                                ? paymentsModel.getPaymentPayload().getUserPractices()
                                : localPaymentsModel.getPaymentPayload().getUserLinks().getDelegatePracticeInformation());
                setAdapter(filteredItems);
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
        if (!refreshLayout.isRefreshing()) {
            displayPaymentHistoryDetails(item);
        }
    }

    private void displayPaymentHistoryDetails(PaymentHistoryItem item) {
        PaymentHistoryDetailDialogFragment fragment = PaymentHistoryDetailDialogFragment
                .newInstance(item, paymentsModel.getPaymentPayload().getUserPractice(item.getMetadata().getPracticeId()));
        callback.displayDialogFragment(fragment, false);
    }
}
