package com.carecloud.carepay.patient.payment.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.adapter.PaymentBalancesAdapter;
import com.carecloud.carepay.patient.payment.adapter.PaymentHistoryAdapter;
import com.carecloud.carepay.patient.payment.dialogs.PaymentAmountInfoDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentChargeDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLocationDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jorge on 02/01/17.
 */

public class PaymentHistoryFragment extends Fragment implements PaymentBalancesAdapter.OnBalanceListItemClickListener{

    private static final String SECTION_NUMBER = "section_number";
    private PaymentsModel paymentDTO;
    private ProgressBar progressBar;
    private RecyclerView historyReciclerView;

    public static PaymentHistoryFragment newInstance(int sectionNumber, PaymentsModel paymentDTO) {
        PaymentHistoryFragment fragment = new PaymentHistoryFragment();
        Gson gson = new Gson();
        Bundle args = new Bundle();
        args.putInt(SECTION_NUMBER, sectionNumber);
        String paymentsDTOString = gson.toJson(paymentDTO);
        args.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_balance_history, container, false);
        progressBar = (ProgressBar)rootView.findViewById(R.id.history_progress_bar);
        Gson gson = new Gson();
        String paymentsDTOString = getArguments().getString(CarePayConstants.INTAKE_BUNDLE);
        paymentDTO = gson.fromJson(paymentsDTOString, PaymentsModel.class);
        historyReciclerView =
                (RecyclerView) rootView.findViewById(R.id.history_recycler_view);

        setUpReciclerView();

        return rootView;
    }


    private void setUpReciclerView() {
        int section_number = getArguments().getInt(SECTION_NUMBER);
        switch (section_number) {
            case 1:
                Map<String, String> queryString = new HashMap<>();
                queryString.put("practice_id",  paymentDTO.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPracticeId() );
                queryString.put("practice_mgmt", paymentDTO.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPracticeMgmt());
                queryString.put("patient_id", paymentDTO.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPatientId());
                WorkflowServiceHelper.getInstance().execute(paymentDTO.getPaymentsMetadata().getPaymentsLinks().getPaymentsPatientBalances(), balancesCallback, queryString);
                break;
            case 2:
                progressBar.setVisibility(View.VISIBLE);
                List<AppointmentChargeDTO> historyList = new ArrayList<>();
                //dummy data
                AppointmentChargeDTO one= new AppointmentChargeDTO();
                one.setAmount("123.45");
                one.setPostingDate("2016-12-04T23:39:12.837UTC");
                AppointmentLocationDTO lone= new AppointmentLocationDTO();
                lone.setName(CarePayConstants.NOT_DEFINED);
                one.setLocation(lone);
                historyList.add(one);
                one= new AppointmentChargeDTO();
                one.setAmount("168.00");
                one.setPostingDate("2016-12-22T23:39:12.837UTC");
                lone= new AppointmentLocationDTO();
                lone.setName(CarePayConstants.NOT_DEFINED);
                one.setLocation(lone);
                historyList.add(one);
                //end dummy data
                PaymentHistoryAdapter historyAdapter = new PaymentHistoryAdapter(getActivity(), historyList);
                historyReciclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                historyReciclerView.setAdapter(historyAdapter );
                progressBar.setVisibility(View.GONE);
                break;
        }
    }

    private WorkflowServiceCallback balancesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            paymentDTO = gson.fromJson(workflowDTO.toString(), PaymentsModel.class);

            PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(getActivity(), paymentDTO, PaymentHistoryFragment.this);
            historyReciclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            historyReciclerView.setAdapter(paymentBalancesAdapter);

            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(getActivity());
            progressBar.setVisibility(View.GONE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private void loadPaymentAmountScreen(PaymentsPatientBalancessDTO model) {
        PaymentAmountInfoDialog dialog= new PaymentAmountInfoDialog(getActivity(), model);
        dialog.show();
    }

    @Override
    public void onBalanceListItemClickListener(int position) {
        PaymentsPatientBalancessDTO model = paymentDTO.getPaymentPayload().getPatientBalances().get(position);
        loadPaymentAmountScreen(model);
    }

}
