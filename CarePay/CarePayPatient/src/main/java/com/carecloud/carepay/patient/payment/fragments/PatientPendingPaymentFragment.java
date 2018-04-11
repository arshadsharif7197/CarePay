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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jorge on 02/01/17
 */
public class PatientPendingPaymentFragment extends BaseFragment implements PaymentBalancesAdapter.OnBalanceListItemClickListener {
    private PaymentsModel paymentsDTO;
    private View noPaymentsLayout;
    private PaymentFragmentActivityInterface callback;

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
        paymentsDTO = (PaymentsModel) callback.getDto();
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
        RecyclerView historyRecyclerView = (RecyclerView) view.findViewById(R.id.payment_list_recycler);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (hasPayments() || hasPaymentPlans()) {
            PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(
                    getActivity(), getPendingBalancesList(paymentsDTO), PatientPendingPaymentFragment.this, paymentsDTO);
            historyRecyclerView.setAdapter(paymentBalancesAdapter);
        } else {
            showNoPaymentsLayout();
        }

    }

    private void showNoPaymentsLayout() {
        noPaymentsLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBalanceListItemClickListener(PaymentsBalancesItem pendingBalance) {
        callback.loadPaymentAmountScreen(pendingBalance, paymentsDTO);
    }

    @Override
    public void onPaymentPlanItemClickListener(PaymentPlanDTO paymentPlan) {
        callback.loadPaymentPlanScreen(paymentsDTO, paymentPlan);
    }

    private List<PaymentListItem> getPendingBalancesList(PaymentsModel paymentModel) {
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
        if(!paymentModel.getPaymentPayload().getActivePlans(null).isEmpty()) {
            Map<String, UserPracticeDTO> practiceMap = new HashMap<>();
            for(UserPracticeDTO userPracticeDTO : paymentModel.getPaymentPayload().getUserPractices()){
                practiceMap.put(userPracticeDTO.getPracticeId(), userPracticeDTO);
            }

            for (PaymentPlanDTO paymentPlanDTO : paymentModel.getPaymentPayload().getActivePlans(null)) {
                if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getPaymentPlanStatus().equals(PaymentPlanDetailsDTO.STATUS_PROCESSING)) {
                    paymentPlanDTO.getMetadata().setPracticeName(practiceMap.get(paymentPlanDTO.getMetadata().getPracticeId()).getPracticeName());
                    list.add(paymentPlanDTO);
                }
            }
        }
        return list;
    }

    private boolean hasPayments() {
        if(!paymentsDTO.getPaymentPayload().getPatientBalances().isEmpty()){
            for(PatientBalanceDTO patientBalanceDTO : paymentsDTO.getPaymentPayload().getPatientBalances()){
                if(!patientBalanceDTO.getBalances().isEmpty()){
                    for(PendingBalanceDTO pendingBalanceDTO : patientBalanceDTO.getBalances()){
                        if(!pendingBalanceDTO.getPayload().isEmpty()){
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean hasPaymentPlans(){
        if(!paymentsDTO.getPaymentPayload().getActivePlans(null).isEmpty()){
            for(PaymentPlanDTO paymentPlanDTO : paymentsDTO.getPaymentPayload().getActivePlans(null)){
                if(paymentPlanDTO.getPayload().getPaymentPlanDetails().getPaymentPlanStatus().equals(PaymentPlanDetailsDTO.STATUS_PROCESSING)){
                    return true;
                }
            }
        }
        return false;
    }


}
