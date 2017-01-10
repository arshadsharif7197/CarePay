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
import com.carecloud.carepaylibray.payments.models.PaymentPayloadMetaDataDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLinksDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jorge on 02/01/17.
 */

public class PaymentHistoryFragment extends Fragment implements PaymentBalancesAdapter.OnBalanceListItemClickListener{

    private static final String SECTION_NUMBER = "section_number";
    private PaymentsModel paymentDTO;
    private ProgressBar progressBar;
    private RecyclerView historyRecyclerView;

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
        historyRecyclerView = (RecyclerView) rootView.findViewById(R.id.history_recycler_view);

        setUpRecyclerView();

        return rootView;
    }

    private void setUpRecyclerView() {
        int section_number = getArguments().getInt(SECTION_NUMBER);
        Map<String, String> queryString = new HashMap<>();
        PaymentsLinksDTO paymentsLinks = paymentDTO.getPaymentsMetadata().getPaymentsLinks();
        PaymentPayloadMetaDataDTO metadata = paymentDTO.getPaymentPayload().getPatientPaymentPlans().getMetadata();

        switch (section_number) {
            case 1:
                queryString.put("practice_id",  metadata.getPracticeId() );
                queryString.put("practice_mgmt", metadata.getPracticeMgmt());
                queryString.put("patient_id", metadata.getPatientId());
                WorkflowServiceHelper.getInstance().execute(
                        paymentsLinks.getPaymentsPatientBalances(), balancesCallback, queryString);
                break;

            case 2:
                queryString.put("practice_id",  metadata.getPracticeId() );
                queryString.put("practice_mgmt", metadata.getPracticeMgmt());
                queryString.put("patient_id", metadata.getPatientId());
                queryString.put("start_date", "2015-01-01");
                queryString.put("end_date", "2030-01-01");
                WorkflowServiceHelper.getInstance().execute(
                        paymentsLinks.getPaymentsHistory(), historyCallback, queryString);
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

            PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(
                    getActivity(), paymentDTO, PaymentHistoryFragment.this);
            historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            historyRecyclerView.setAdapter(paymentBalancesAdapter);

            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(getActivity());
            progressBar.setVisibility(View.GONE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback historyCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            paymentDTO = gson.fromJson(workflowDTO.toString(), PaymentsModel.class);

            PaymentHistoryAdapter historyAdapter = new PaymentHistoryAdapter(getActivity(), paymentDTO);
            historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            historyRecyclerView.setAdapter(historyAdapter);

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
        PaymentAmountInfoDialog dialog= new PaymentAmountInfoDialog(getActivity(), model,paymentDTO);
        dialog.show();
    }

    @Override
    public void onBalanceListItemClickListener(int position) {
        PaymentsPatientBalancessDTO model = paymentDTO.getPaymentPayload().getPatientBalances().get(position);
        loadPaymentAmountScreen(model);
    }
}
