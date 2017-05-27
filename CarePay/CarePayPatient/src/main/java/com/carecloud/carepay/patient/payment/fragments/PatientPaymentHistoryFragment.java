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
import com.carecloud.carepay.patient.payment.adapters.PaymentHistoryAdapter;
import com.carecloud.carepay.patient.payment.interfaces.PaymentFragmentActivityInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 02/01/17.
 */
public class PatientPaymentHistoryFragment extends BaseFragment implements PaymentBalancesAdapter.OnBalanceListItemClickListener {

    public static final int SECTION_PENDING = 1;
    public static final int SECTION_HISTORY = 2;

    private PaymentsModel paymentsDTO;
    private int sectionNumber;
    private View noPaymentsLayout;
    private PaymentFragmentActivityInterface callback;

    /**
     * @param sectionNumber the section number
     * @return an instance of PatientPaymentHistoryFragment
     */
    public static PatientPaymentHistoryFragment newInstance(int sectionNumber) {
        PatientPaymentHistoryFragment fragment = new PatientPaymentHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(CarePayConstants.TAB_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

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
        sectionNumber = getArguments().getInt(CarePayConstants.TAB_SECTION_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_balance_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView(view);
    }

    private void setUpRecyclerView(View view) {
        RecyclerView historyRecyclerView = (RecyclerView) view.findViewById(R.id.history_recycler_view);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        switch (sectionNumber) {
            case SECTION_PENDING: {
                if (paymentsDTO.getPaymentPayload().getPatientBalances().size() > 0
                        && paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().size() > 0
                        && paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0)
                        .getPayload().size() > 0) {
                    PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(
                            getActivity(), getPendingBalancesList(paymentsDTO), PatientPaymentHistoryFragment.this);
                    historyRecyclerView.setAdapter(paymentBalancesAdapter);
                } else {
                    showNoPaymentsLayout();
                }
                break;
            }
            case SECTION_HISTORY: {
                if (paymentsDTO.getPaymentPayload().getPatientHistory()
                        .getPaymentsPatientCharges().getCharges().size() > 0) {
                    PaymentHistoryAdapter historyAdapter = new PaymentHistoryAdapter(getActivity(), paymentsDTO);
                    historyRecyclerView.setAdapter(historyAdapter);
                } else {
                    showNoPaymentsLayout();
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    private void showNoPaymentsLayout() {
        noPaymentsLayout = getView().findViewById(R.id.no_payment_layout);
        noPaymentsLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBalanceListItemClickListener(int position) {
        PaymentsBalancesItem selectedBalancesItem = getPendingBalancesList(paymentsDTO).get(position);
//        PendingBalancePayloadDTO selectedBalance = paymentsDTO.getPaymentPayload().getPatientBalances()
//                .get(0).getBalances().get(0).getPayload().get(position);
        callback.loadPaymentAmountScreen(selectedBalancesItem, paymentsDTO);
    }

    private List<PaymentsBalancesItem> getPendingBalancesList(PaymentsModel paymentModel) {
        List<PaymentsBalancesItem> list = new ArrayList<>();
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
        return list;
    }

}
