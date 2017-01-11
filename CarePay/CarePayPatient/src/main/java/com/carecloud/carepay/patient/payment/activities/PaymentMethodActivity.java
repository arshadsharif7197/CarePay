package com.carecloud.carepay.patient.payment.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.payment.ResponsibilityFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodFragment;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.services.PaymentsService;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jorge on 10/01/17.
 */

public class PaymentMethodActivity extends BasePatientActivity {
    private PaymentsModel paymentsDTO;
    private String paymentsDTOString;
    private Bundle bundle ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_payment);

        paymentsDTO = getConvertedDTO(PaymentsModel.class);
        double total=0;
        Bundle bundle = this.getIntent().getBundleExtra(PatientNavigationHelper.class.getSimpleName());

        if (bundle != null) {
            total = bundle.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
        }
        getPaymentInformation();
        doPayment(total);


    }

    private void getPaymentInformation() {
        PaymentsService paymentService = (new BaseServiceGenerator(PaymentMethodActivity.this))
                .createService(PaymentsService.class);
        Call<PaymentsModel> call = paymentService.fetchPaymentInformation();
        call.enqueue(new Callback<PaymentsModel>() {
            @Override
            public void onResponse(Call<PaymentsModel> call, Response<PaymentsModel> response) {
                PaymentsModel paymentsDTO = response.body();
            }

            @Override
            public void onFailure(Call<PaymentsModel> call, Throwable throwable) {
                SystemUtil.showFaultDialog(PaymentMethodActivity.this);
                Log.e(PaymentMethodActivity.this.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), "");
            }
        });
    }

    private void doPayment(double total) {
        FragmentManager fragmentmanager = PaymentMethodActivity.this.getSupportFragmentManager();
        PaymentMethodFragment fragment = (PaymentMethodFragment)
                fragmentmanager.findFragmentByTag(PaymentMethodFragment.class.getSimpleName());

        if (fragment == null) {
            fragment = new PaymentMethodFragment();
        }

        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsDTO);
        bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, total);
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
        fragmentTransaction.replace(com.carecloud.carepaylibrary.R.id.payment_frag_holder, fragment);
        fragmentTransaction.addToBackStack(PaymentMethodFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }
}

