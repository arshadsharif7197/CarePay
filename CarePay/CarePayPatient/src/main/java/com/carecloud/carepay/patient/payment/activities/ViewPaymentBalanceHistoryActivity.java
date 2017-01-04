package com.carecloud.carepay.patient.payment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.payment.fragments.PaymentBalanceHistoryFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.google.gson.Gson;

/**
 * Created by jorge on 29/12/16.
 */

public class ViewPaymentBalanceHistoryActivity extends BasePatientActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_balance_history);

        PaymentsModel paymentsDTO = getConvertedDTO(PaymentsModel.class);

        TextView toolbarText = (TextView) findViewById(R.id.balance_history_toolbar_title);
        toolbarText.setText("PAYMENTS");//no label in end point yet
        FragmentManager fm = getSupportFragmentManager();
        PaymentBalanceHistoryFragment paymentBalanceHistoryFragment = (PaymentBalanceHistoryFragment)
                fm.findFragmentByTag(PaymentBalanceHistoryFragment.class.getSimpleName());
        if (paymentBalanceHistoryFragment== null) {
            paymentBalanceHistoryFragment = new PaymentBalanceHistoryFragment();
        }
        //params
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsDTO);
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
        if(paymentBalanceHistoryFragment.getArguments() !=null){
            paymentBalanceHistoryFragment.getArguments().putAll(bundle);
        }else{
            paymentBalanceHistoryFragment.setArguments(bundle);
        }
        //include fragment
        fm.beginTransaction().replace(R.id.add_balance_history_frag_holder, paymentBalanceHistoryFragment,
                PaymentBalanceHistoryFragment.class.getSimpleName()).commit();

    }
}
