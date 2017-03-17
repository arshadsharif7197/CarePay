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
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

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
            showPaymentsRecyclerView(paymentResultModel);
        }
        findViewById(R.id.btnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void showPaymentsRecyclerView(PaymentsModel paymentsModel) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.appointmentsRecyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.emptyPaymentsImageView).setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new PaymentBalancesAdapter(paymentsModel.getPaymentPayload().getPatientBalances(),
                paymentsModel.getPaymentPayload().getUserPractices().get(0)));
        //TODO: Should change this to the proper keys
        ((TextView) findViewById(R.id.title)).setText("Select a pending payment");
        ((TextView) findViewById(R.id.subtitle)).setText(Label.getLabel("no_payment_description"));

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
