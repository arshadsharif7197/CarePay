package com.carecloud.carepay.patient.payment.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPayloadMetaDataDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLinksDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jorge on 02/01/17.
 */

public class PaymentHistoryFragment extends Fragment implements PaymentBalancesAdapter.OnBalanceListItemClickListener, PaymentDetailsDialog.PayNowClickListener{

    private static final String LOG = PaymentHistoryFragment.class.getSimpleName();
    private PaymentsModel paymentDTO;
    private ProgressBar progressBar;
    private RecyclerView historyRecyclerView;
    private double total;

    public static PaymentHistoryFragment newInstance(int sectionNumber, PaymentsModel paymentDTO) {
        PaymentHistoryFragment fragment = new PaymentHistoryFragment();
        Gson gson = new Gson();
        Bundle args = new Bundle();
        args.putInt(CarePayConstants.TAB_SECTION_NUMBER, sectionNumber);
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
        int section_number = getArguments().getInt(CarePayConstants.TAB_SECTION_NUMBER);
        Map<String, String> queryString = new HashMap<>();
        PaymentsLinksDTO paymentsLinks = paymentDTO.getPaymentsMetadata().getPaymentsLinks();

        if(paymentDTO.getPaymentPayload().getPatientHistory() != null && paymentDTO.getPaymentPayload().getPatientHistory().getPaymentsPatientCharges() != null)
        {
            PaymentPayloadMetaDataDTO metadata = paymentDTO.getPaymentPayload().getPatientHistory().getPaymentsPatientCharges().getMetadata();


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
    }

    private WorkflowServiceCallback balancesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            try {
                paymentDTO = gson.fromJson(workflowDTO.toString(), PaymentsModel.class);

                PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(
                        getActivity(), paymentDTO, PaymentHistoryFragment.this);
                historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                historyRecyclerView.setAdapter(paymentBalancesAdapter);

                progressBar.setVisibility(View.GONE);
            } catch (Exception e){
                Log.e(LOG, e.getMessage());
                SystemUtil.showDefaultFailureDialog(getActivity());
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDefaultFailureDialog(getActivity());
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
            try {
                paymentDTO = gson.fromJson(workflowDTO.toString(), PaymentsModel.class);

                PaymentHistoryAdapter historyAdapter = new PaymentHistoryAdapter(getActivity(), paymentDTO);
                historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                historyRecyclerView.setAdapter(historyAdapter);
            } catch (Exception e){
                Log.e(LOG, e.getMessage());
                SystemUtil.showDefaultFailureDialog(getActivity());
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDefaultFailureDialog(getActivity());
            progressBar.setVisibility(View.GONE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void loadPaymentAmountScreen(PatiencePayloadDTO model) {
        PaymentDetailsDialog detailsDialog = new PaymentDetailsDialog(getContext() , paymentDTO, model, this);
        detailsDialog.show();
    }

    @Override
    public void onBalanceListItemClickListener(int position) {
        PatiencePayloadDTO model = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload().get(position);
        total = model.getAmount();
        loadPaymentAmountScreen(model);
    }

    @Override
    public void onPayNowButtonClicked() {
        try{
            FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
            PaymentMethodFragment fragment = (PaymentMethodFragment)
                    fragmentmanager.findFragmentByTag(PaymentMethodFragment.class.getSimpleName());

            if (fragment == null) {
                fragment = new PaymentMethodFragment();
            }
            Bundle bundle = new Bundle();
            Gson gson = new Gson();
            String paymentsDTOString = gson.toJson(paymentDTO);
            bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, total);
            bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
            fragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
            fragmentTransaction.replace(com.carecloud.carepaylibrary.R.id.add_balance_history_frag_holder, fragment);
            fragmentTransaction.addToBackStack(PaymentMethodFragment.class.getSimpleName());
            fragmentTransaction.commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
