package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.adapters.PaymentBalancesAdapter;
import com.carecloud.carepay.patient.payment.interfaces.PaymentFragmentActivityInterface;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentListItem;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.viewModel.PatientResponsibilityViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carecloud.carepay.patient.payment.fragments.PaymentBalanceHistoryFragment.PAGE_BALANCES;

/**
 * Created by jorge on 02/01/17
 */
public class PatientPendingPaymentFragment extends BaseFragment
        implements PaymentBalancesAdapter.OnBalanceListItemClickListener {

    private View noPaymentsLayout;
    private SwipeRefreshLayout refreshLayout;
    private PaymentFragmentActivityInterface callback;
    private PatientResponsibilityViewModel patientResponsibilityViewModel;

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
        patientResponsibilityViewModel = new ViewModelProvider(requireActivity()).get(PatientResponsibilityViewModel.class);
        patientResponsibilityViewModel.setPaymentsModel(paymentsModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_balance_pending, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noPaymentsLayout = view.findViewById(R.id.no_payment_layout);
        setUpRecyclerView(view);
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callback.onRequestRefresh(PAGE_BALANCES);
            }
        });
    }

    private void setUpRecyclerView(View view) {
        RecyclerView historyRecyclerView = view.findViewById(R.id.payment_list_recycler);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Map<String, UserPracticeDTO> practiceMap = new HashMap<>();
        for (UserPracticeDTO userPracticeDTO : paymentsModel.getPaymentPayload().getUserPractices()) {
            practiceMap.put(userPracticeDTO.getPracticeId(), userPracticeDTO);
        }
        if (hasPayments() || hasPaymentPlans()) {
            filterPaymentPlans(paymentsModel.getPaymentPayload().getUserPractices());
            PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(
                    getActivity(), getPendingBalancesList(paymentsModel, practiceMap),
                    PatientPendingPaymentFragment.this, paymentsModel, practiceMap);
            historyRecyclerView.setAdapter(paymentBalancesAdapter);
        } else {
            showNoPaymentsLayout();
        }

    }

    private void filterPaymentPlans(List<UserPracticeDTO> userPractices) {
        List<PaymentPlanDTO> filteredList = new ArrayList<>();
        for (PaymentPlanDTO paymentPlanDTO : paymentsModel.getPaymentPayload().getPatientPaymentPlans()) {
            for (UserPracticeDTO userPracticeDTO : userPractices) {
                if (paymentsModel.getPaymentPayload().canViewBalanceAndHistoricalPayments(userPracticeDTO.getPracticeId())
                        && paymentPlanDTO.getMetadata().getPracticeId().equals(userPracticeDTO.getPracticeId())) {
                    filteredList.add(paymentPlanDTO);
                }
            }
        }

        paymentsModel.getPaymentPayload().setPatientPaymentPlans(filteredList);
    }

    private void showNoPaymentsLayout() {
        noPaymentsLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBalanceListItemClickListener(PaymentsBalancesItem pendingBalance) {
        if (!refreshLayout.isRefreshing()) {
            callback.loadPaymentAmountScreen(pendingBalance, paymentsModel);
        }
    }

    @Override
    public void onPaymentPlanItemClickListener(PaymentPlanDTO paymentPlan) {
        if (!refreshLayout.isRefreshing()) {
            callback.loadPaymentPlanScreen(paymentsModel, paymentPlan);
        }
    }

    private List<PaymentListItem> getPendingBalancesList(PaymentsModel paymentModel,
                                                         Map<String, UserPracticeDTO> practiceMap) {
        List<PaymentListItem> list = new ArrayList<>();
        //add regular balance items
        for (PatientBalanceDTO patientBalanceDTO : paymentModel.getPaymentPayload().getPatientBalances()) {
            for (PendingBalanceDTO pendingBalanceDTO : patientBalanceDTO.getBalances()) {
                PendingBalanceMetadataDTO metadataDTO = pendingBalanceDTO.getMetadata();
                for (PendingBalancePayloadDTO pendingBalancePayloadDTO : pendingBalanceDTO.getPayload()) {
                    PaymentsBalancesItem paymentsBalancesItem = new PaymentsBalancesItem();
                    paymentsBalancesItem.setMetadata(metadataDTO);
                    paymentsBalancesItem.setBalance(pendingBalancePayloadDTO);
                    list.add(paymentsBalancesItem);
                }
            }
        }
        //add payment plans
        if (!paymentModel.getPaymentPayload().getActivePlans(null).isEmpty()) {
            for (PaymentPlanDTO paymentPlanDTO : paymentModel.getPaymentPayload().getActivePlans(null)) {
                if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getPaymentPlanStatus()
                        .equals(PaymentPlanDetailsDTO.STATUS_PROCESSING)) {
                    paymentPlanDTO.getMetadata().setPracticeName(practiceMap.get(paymentPlanDTO
                            .getMetadata().getPracticeId()).getPracticeName());
                    list.add(paymentPlanDTO);
                }
            }
        }
        return list;
    }

    private boolean hasPayments() {
        if (!paymentsModel.getPaymentPayload().getPatientBalances().isEmpty()) {
            for (PatientBalanceDTO patientBalanceDTO : paymentsModel.getPaymentPayload().getPatientBalances()) {
                if (!patientBalanceDTO.getBalances().isEmpty()) {
                    for (PendingBalanceDTO pendingBalanceDTO : patientBalanceDTO.getBalances()) {
                        if (!pendingBalanceDTO.getPayload().isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean hasPaymentPlans() {
        if (!paymentsModel.getPaymentPayload().getActivePlans(null).isEmpty()) {
            for (PaymentPlanDTO paymentPlanDTO : paymentsModel.getPaymentPayload().getActivePlans(null)) {
                if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getPaymentPlanStatus()
                        .equals(PaymentPlanDetailsDTO.STATUS_PROCESSING)) {
                    return true;
                }
            }
        }
        return false;
    }


}
