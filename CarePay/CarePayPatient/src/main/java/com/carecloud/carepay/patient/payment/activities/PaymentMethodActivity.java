package com.carecloud.carepay.patient.payment.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.payment.fragments.AddNewCreditCardFragment;
import com.carecloud.carepay.patient.payment.fragments.ChooseCreditCardFragment;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentPlanFragment;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.payments.fragments.PaymentMethodFragment;
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

public class PaymentMethodActivity extends BasePatientActivity implements PaymentMethodFragment.PaymentMethodActionCallback {
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
        PaymentsService paymentService = (new BaseServiceGenerator())
                .createService(getCognitoAppHelper(), PaymentsService.class);
        Call<PaymentsModel> call = paymentService.fetchPaymentInformation();
        call.enqueue(new Callback<PaymentsModel>() {
            @Override
            public void onResponse(Call<PaymentsModel> call, Response<PaymentsModel> response) {
                PaymentsModel paymentsDTO = response.body();
            }

            @Override
            public void onFailure(Call<PaymentsModel> call, Throwable throwable) {
                SystemUtil.showDefaultFailureDialog(PaymentMethodActivity.this);
                Log.e(PaymentMethodActivity.this.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), "");
            }
        });
    }

    private void doPayment(double total) {
        PatientPaymentMethodFragment fragment = new PatientPaymentMethodFragment();

        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsDTO);
        bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, total);
        bundle.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(com.carecloud.carepaylibrary.R.id.payment_frag_holder, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    public void onPaymentMethodAction(String selectedPaymentMethod, double amount) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        Fragment fragment;
        String paymentsDTOString = gson.toJson(paymentsDTO);
        if(paymentsDTO.getPaymentPayload().getPatientCreditCards()!=null && !paymentsDTO.getPaymentPayload().getPatientCreditCards().isEmpty()){
            args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethod);
            args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);//TODO remove DUPlicate dto
            args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
            args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
            fragment = new ChooseCreditCardFragment();
        } else {
            args.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
            args.putString(CarePayConstants.PAYEEZY_MERCHANT_SERVICE_BUNDLE, gson.toJson(paymentsDTO.getPaymentPayload().getPapiAccounts()));//TODO this is already included in main DTO, just extract it
            args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
            args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE,  amount);
            fragment = new AddNewCreditCardFragment();
        }

        fragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(com.carecloud.carepaylibrary.R.id.payment_frag_holder, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.commit();

    }

    @Override
    public void onPaymentPlanAction() {
        PaymentPlanFragment fragment = new PaymentPlanFragment();

        Bundle args = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsDTO);
        args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        fragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(com.carecloud.carepaylibrary.R.id.payment_frag_holder, fragment);
        fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }
}

