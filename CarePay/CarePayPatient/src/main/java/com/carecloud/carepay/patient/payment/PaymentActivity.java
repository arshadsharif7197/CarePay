package com.carecloud.carepay.patient.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

public class PaymentActivity extends BasePatientActivity {
    PaymentsModel paymentsDTO;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        paymentsDTO = getConvertedDTO(PaymentsModel.class);


        FragmentManager fm = getSupportFragmentManager();
        ResponsibilityFragment fragment = (ResponsibilityFragment)
                fm.findFragmentByTag(ResponsibilityFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new ResponsibilityFragment();
        }
        Bundle bundle = new Bundle();

        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsDTO);
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
            //fix for random crashes
            if(fragment.getArguments() !=null){
                fragment.getArguments().putAll(bundle);
            }else{
                fragment.setArguments(bundle);
            }

            fm.beginTransaction().replace(R.id.payment_frag_holder, fragment,
                    ResponsibilityFragment.class.getSimpleName()).commit();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SystemUtil.hideSoftKeyboard(this);
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
