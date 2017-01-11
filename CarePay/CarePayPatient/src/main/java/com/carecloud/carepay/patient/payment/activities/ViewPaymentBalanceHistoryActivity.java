package com.carecloud.carepay.patient.payment.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.payment.fragments.PaymentBalanceHistoryFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

/**
 * Created by jorge on 29/12/16.
 */

public class ViewPaymentBalanceHistoryActivity extends MenuPatientActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_balance_history);

        toolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.balance_history_toolbar);
        drawer = (DrawerLayout) findViewById(com.carecloud.carepaylibrary.R.id.drawer_layout_hist);
        navigationView = (NavigationView) findViewById(com.carecloud.carepaylibrary.R.id.nav_view_hist);
        appointmentsDrawerUserIdTextView = (TextView) navigationView.getHeaderView(0)
                .findViewById(com.carecloud.carepaylibrary.R.id.appointmentsDrawerIdTextView);
        PaymentsModel paymentsDTO = getConvertedDTO(PaymentsModel.class);

        practiceId = paymentsDTO.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPracticeId();
        practiceMgmt =paymentsDTO.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPracticeMgmt();
        patientId = paymentsDTO.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPatientId();

        transitionBalance = paymentsDTO.getPaymentsMetadata().getPaymentsLinks().getPaymentsPatientBalances();
        transitionAppointments = paymentsDTO.getPaymentsMetadata().getPaymentsLinks().getAppointments();
        //transitionLogout = paymentsDTO.getPaymentsMetadata().getPaymentsTransitions().getLogout();
        transitionProfile = paymentsDTO.getPaymentsMetadata().getPaymentsLinks().getPaymentsDemographics();

        TextView toolbarText = (TextView) findViewById(R.id.balance_history_toolbar_title);
        String toolBarTitle = paymentsDTO.getPaymentsMetadata().getPaymentsLabel().getPaymentPatientBalanceToolbar();
        toolbarText.setText(StringUtil.isNullOrEmpty(toolBarTitle)? CarePayConstants.NOT_DEFINED : toolBarTitle);

        inflateDrawer();
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
