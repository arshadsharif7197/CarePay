package com.carecloud.carepay.practice.library.payments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.payments.adapter.PaymentBalancesAdapter;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

import java.util.List;

/**
 * Created by pjohnson on 16/03/17.
 */

public class MainPaymentActivity extends BasePracticeActivity {

    private PaymentsModel paymentResultModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_payment);
        paymentResultModel = getConvertedDTO(PaymentsModel.class);

        if (paymentResultModel.getPaymentPayload().getPatientBalances().isEmpty()) {
            showNoPaymentsImage();
        } else {
            showPaymentsRecyclerView(paymentResultModel.getPaymentPayload().getPatientBalances());
        }
    }

    private void showPaymentsRecyclerView(List<PatientBalanceDTO> patientBalances) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.appointmentsRecyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.emptyPaymentsImageView).setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new PaymentBalancesAdapter(patientBalances));

    }

    private void showNoPaymentsImage() {
        findViewById(R.id.emptyPaymentsImageView).setVisibility(View.VISIBLE);
        findViewById(R.id.appointmentsRecyclerView).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.title)).setText(Label.getLabel("no_payment_title"));
        ((TextView) findViewById(R.id.subtitle)).setText(Label.getLabel("no_payment_description"));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
