package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.adapter.PaymentBalancesAdapter;
import com.carecloud.carepay.patient.payment.adapter.PaymentHistoryAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
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

public class PaymentHistoryFragment extends BaseFragment implements PaymentBalancesAdapter.OnBalanceListItemClickListener{

    public static final int SECTION_PENDING = 1;
    public static final int SECTION_HISTORY = 2;


    private static final String LOG = PaymentHistoryFragment.class.getSimpleName();
    private PaymentsModel paymentDTO;
    private ProgressBar progressBar;
    private RecyclerView historyRecyclerView;
    private double total;
    private EmptyPaymentListCallback emptyPaymentListCallback;
    private int sectionNumber;
    private View noPaymentsLayout;
    private TextView noPaymentTitle;
    private TextView noPaymentDesc;

    private PaymentDetailsDialog.PayNowClickListener payNowClickListener;


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
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            payNowClickListener = (PaymentDetailsDialog.PayNowClickListener) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement PayNowClickListener");
        }
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
        noPaymentsLayout = rootView.findViewById(R.id.no_payment_layout);
        noPaymentTitle = (TextView) rootView.findViewById(com.carecloud.carepaylibrary.R.id.no_payment_message_title);
        noPaymentDesc = (TextView) rootView.findViewById(com.carecloud.carepaylibrary.R.id.no_payment_message_desc);

        hideNoPaymentsLayout();
        setUpRecyclerView();

        return rootView;
    }

    private void setUpRecyclerView() {
        sectionNumber = getArguments().getInt(CarePayConstants.TAB_SECTION_NUMBER);
        Map<String, String> queryString = new HashMap<>();
        PaymentsLinksDTO paymentsLinks = paymentDTO.getPaymentsMetadata().getPaymentsLinks();

        switch (sectionNumber){
            case SECTION_PENDING:{
                //check if we have valid data
                if(!paymentDTO.getPaymentPayload().getPatientBalances().isEmpty() &&
                        paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).hasValidMetadata()){
                    PaymentPayloadMetaDataDTO metadata = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();

                    queryString.put("practice_id",  metadata.getPracticeId() );
                    queryString.put("practice_mgmt", metadata.getPracticeMgmt());
                    queryString.put("patient_id", metadata.getPatientId());
                    getWorkflowServiceHelper().execute(paymentsLinks.getPaymentsPatientBalances(), balancesCallback, queryString);

                }else{
                    showNoPaymentsLayout();
                    emptyPaymentListCallback.listIsEmpty(sectionNumber);
                }

                break;
            }
            case SECTION_HISTORY:{
                //check if we have valid data
                if(paymentDTO.getPaymentPayload().getPatientHistory().getPaymentsPatientCharges().hasValidMetadata()){
                    PaymentPayloadMetaDataDTO metadata = paymentDTO.getPaymentPayload().getPatientHistory().getPaymentsPatientCharges().getMetadata();

                    queryString.put("practice_id",  metadata.getPracticeId() );
                    queryString.put("practice_mgmt", metadata.getPracticeMgmt());
                    queryString.put("patient_id", metadata.getPatientId());
                    queryString.put("start_date", "2015-01-01");
                    queryString.put("end_date", "2030-01-01");
                    getWorkflowServiceHelper().execute(paymentsLinks.getPaymentsHistory(), historyCallback, queryString);

                }else{
                    showNoPaymentsLayout();
                    emptyPaymentListCallback.listIsEmpty(sectionNumber);
                }

                break;
            }
        }
    }

    private void showNoPaymentsLayout(){
        noPaymentsLayout.setVisibility(View.VISIBLE);
    }

    private void hideNoPaymentsLayout(){
        noPaymentsLayout.setVisibility(View.GONE);
    }


    private WorkflowServiceCallback balancesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Gson gson = new Gson();
            try {
                paymentDTO = gson.fromJson(workflowDTO.toString(), PaymentsModel.class);
                noPaymentTitle.setText(paymentDTO.getPaymentsMetadata().getPaymentsLabel().getNoPaymentTitle());
                noPaymentDesc.setText(paymentDTO.getPaymentsMetadata().getPaymentsLabel().getNoPendingPaymentDescription());


                PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(
                        getActivity(), paymentDTO, PaymentHistoryFragment.this);
                historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                historyRecyclerView.setAdapter(paymentBalancesAdapter);

                if(paymentBalancesAdapter.getItemCount()<1){
                    //show noPayment view
                    showNoPaymentsLayout();
                    if(emptyPaymentListCallback!=null) {
                        emptyPaymentListCallback.listIsEmpty(sectionNumber);
                    }
                }

            } catch (Exception e){
                Log.e(LOG, e.getMessage());
                SystemUtil.showDefaultFailureDialog(getActivity());
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback historyCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Gson gson = new Gson();
            try {
                paymentDTO = gson.fromJson(workflowDTO.toString(), PaymentsModel.class);
                noPaymentTitle.setText(paymentDTO.getPaymentsMetadata().getPaymentsLabel().getNoPaymentTitle());
                noPaymentDesc.setText(paymentDTO.getPaymentsMetadata().getPaymentsLabel().getNoPaymentHistoryDescription());

                PaymentHistoryAdapter historyAdapter = new PaymentHistoryAdapter(getActivity(), paymentDTO);
                historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                historyRecyclerView.setAdapter(historyAdapter);

                if(historyAdapter.getItemCount()<1){
                    //show noPayment view
                    showNoPaymentsLayout();
                    if(emptyPaymentListCallback!=null) {
                        emptyPaymentListCallback.listIsEmpty(sectionNumber);
                    }
                }

            } catch (Exception e){
                Log.e(LOG, e.getMessage());
                SystemUtil.showDefaultFailureDialog(getActivity());
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void loadPaymentAmountScreen(PatiencePayloadDTO model) {
        PaymentDetailsDialog detailsDialog = new PaymentDetailsDialog(getContext() , paymentDTO, model, payNowClickListener);
        detailsDialog.show();
    }

    @Override
    public void onBalanceListItemClickListener(int position) {
        PatiencePayloadDTO model = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload().get(position);
        total = model.getAmount();
        loadPaymentAmountScreen(model);
    }


    public void setEmptyPaymentListCallback(EmptyPaymentListCallback emptyPaymentListCallback) {
        this.emptyPaymentListCallback = emptyPaymentListCallback;
    }

    public interface EmptyPaymentListCallback{
        void listIsEmpty(int sectionNumber);
    }

}
