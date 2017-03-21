package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.adapters.PaymentBalancesAdapter;
import com.carecloud.carepay.patient.payment.adapters.PaymentHistoryAdapter;
import com.carecloud.carepay.patient.payment.interfaces.PaymentPatientInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsLinksDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jorge on 02/01/17.
 */
public class PatientPaymentHistoryFragment extends BaseFragment implements PaymentBalancesAdapter.OnBalanceListItemClickListener {

    public static final int SECTION_PENDING = 1;
    public static final int SECTION_HISTORY = 2;


    private static final String LOG = PatientPaymentHistoryFragment.class.getSimpleName();
    private PaymentsModel paymentDTO;
    private RecyclerView historyRecyclerView;
    private int sectionNumber;
    private View noPaymentsLayout;
    private PaymentPatientInterface callback;

    /**
     * @param sectionNumber the section number
     * @param paymentDTO    the payment dto
     * @return an instance of PatientPaymentHistoryFragment
     */
    public static PatientPaymentHistoryFragment newInstance(int sectionNumber, PaymentsModel paymentDTO) {
        PatientPaymentHistoryFragment fragment = new PatientPaymentHistoryFragment();
        Gson gson = new Gson();
        Bundle args = new Bundle();
        args.putInt(CarePayConstants.TAB_SECTION_NUMBER, sectionNumber);
        String paymentsDTOString = gson.toJson(paymentDTO);
        args.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PaymentPatientInterface) getTargetFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_balance_history, container, false);

        Gson gson = new Gson();
        String paymentsDTOString = getArguments().getString(CarePayConstants.INTAKE_BUNDLE);
        paymentDTO = gson.fromJson(paymentsDTOString, PaymentsModel.class);
        historyRecyclerView = (RecyclerView) rootView.findViewById(R.id.history_recycler_view);
        noPaymentsLayout = rootView.findViewById(R.id.no_payment_layout);

        hideNoPaymentsLayout();
        setUpRecyclerView();

        return rootView;
    }

    private void setUpRecyclerView() {
        sectionNumber = getArguments().getInt(CarePayConstants.TAB_SECTION_NUMBER);
        Map<String, String> queryString = new HashMap<>();
        PaymentsLinksDTO paymentsLinks = paymentDTO.getPaymentsMetadata().getPaymentsLinks();

        switch (sectionNumber) {
            case SECTION_PENDING: {
                //check if we have valid data
                if (!paymentDTO.getPaymentPayload().getPatientBalances().isEmpty() &&
                        paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).hasValidMetadata()) {
                    PendingBalanceMetadataDTO metadata = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();

                    queryString.put("practice_id", metadata.getPracticeId());
                    queryString.put("practice_mgmt", metadata.getPracticeMgmt());
                    queryString.put("patient_id", metadata.getPatientId());
                    getWorkflowServiceHelper().execute(paymentsLinks.getPaymentsPatientBalances(), balancesCallback, queryString);

                } else {
                    showNoPaymentsLayout();
                    callback.showNoPaymentsLayout(sectionNumber);
                }

                break;
            }
            case SECTION_HISTORY: {
                //check if we have valid data
                if (paymentDTO.getPaymentPayload().getPatientHistory().getPaymentsPatientCharges().hasValidMetadata()) {
                    PendingBalanceMetadataDTO metadata = paymentDTO.getPaymentPayload().getPatientHistory().getPaymentsPatientCharges().getMetadata();

                    queryString.put("practice_id", metadata.getPracticeId());
                    queryString.put("practice_mgmt", metadata.getPracticeMgmt());
                    queryString.put("patient_id", metadata.getPatientId());
                    queryString.put("start_date", "2015-01-01");
                    queryString.put("end_date", "2030-01-01");
                    getWorkflowServiceHelper().execute(paymentsLinks.getPaymentsHistory(), historyCallback, queryString);

                } else {
                    showNoPaymentsLayout();
                    callback.showNoPaymentsLayout(sectionNumber);
                }

                break;
            }
            default: {
                break;
            }
        }
    }

    private void showNoPaymentsLayout() {
        noPaymentsLayout.setVisibility(View.VISIBLE);
    }

    private void hideNoPaymentsLayout() {
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
                PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(
                        getActivity(), paymentDTO, PatientPaymentHistoryFragment.this);
                historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                historyRecyclerView.setAdapter(paymentBalancesAdapter);

                if (paymentBalancesAdapter.getItemCount() < 1) {
                    //show noPayment view
                    showNoPaymentsLayout();
                    callback.showNoPaymentsLayout(sectionNumber);
                }

            } catch (Exception e) {
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
                PaymentHistoryAdapter historyAdapter = new PaymentHistoryAdapter(getActivity(), paymentDTO);
                historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                historyRecyclerView.setAdapter(historyAdapter);

                if (historyAdapter.getItemCount() < 1) {
                    //show noPayment view
                    showNoPaymentsLayout();
                    callback.showNoPaymentsLayout(sectionNumber);
                }

            } catch (Exception e) {
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

    private void loadPaymentAmountScreen(PendingBalancePayloadDTO model) {
        callback.loadPaymentAmountScreen(model, paymentDTO);
    }

    @Override
    public void onBalanceListItemClickListener(int position) {
        PendingBalancePayloadDTO model = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload().get(position);
        loadPaymentAmountScreen(model);
    }

}
