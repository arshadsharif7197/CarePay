package com.carecloud.carepay.patient.payment.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.adapter.PaymentBalancesAdapter;
import com.carecloud.carepay.patient.payment.adapter.PaymentHistoryAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.AppointmentChargeDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLocationDTO;
import com.carecloud.carepaylibray.payments.models.PatienceBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 02/01/17.
 */

public class PaymentHistoryFragment extends Fragment {

    private static final String SECTION_NUMBER = "section_number";
    private PaymentsModel paymentDTO;

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
        Gson gson = new Gson();
        String paymentsDTOString = getArguments().getString(CarePayConstants.INTAKE_BUNDLE);
        paymentDTO = gson.fromJson(paymentsDTOString, PaymentsModel.class);
        RecyclerView historyReciclerView =
                (RecyclerView) rootView.findViewById(R.id.history_recycler_view);

        setUpReciclerView(historyReciclerView);

        return rootView;
    }


    private void setUpReciclerView(RecyclerView historyReciclerView) {
        int section_number = getArguments().getInt(SECTION_NUMBER);
        switch (section_number) {
            case 1:
                PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(getActivity(), paymentDTO);
                historyReciclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                historyReciclerView.setAdapter(paymentBalancesAdapter);
                break;
            case 2:
                List<AppointmentChargeDTO> historyList = new ArrayList<>();
                //dummy data
                AppointmentChargeDTO one= new AppointmentChargeDTO();
                one.setAmount("123.45");
                one.setPostingDate("2016-12-04T23:39:12.837UTC");
                AppointmentLocationDTO lone= new AppointmentLocationDTO();
                lone.setName("CareCloud Clinic");
                one.setLocation(lone);
                historyList.add(one);
                one= new AppointmentChargeDTO();
                one.setAmount("168.00");
                one.setPostingDate("2016-12-22T23:39:12.837UTC");
                lone= new AppointmentLocationDTO();
                lone.setName("Miami Healt Center");
                one.setLocation(lone);
                historyList.add(one);
                //end dummy data
                PaymentHistoryAdapter historyAdapter = new PaymentHistoryAdapter(getActivity(), historyList);
                historyReciclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                historyReciclerView.setAdapter(historyAdapter );
                break;
        }
    }


}
