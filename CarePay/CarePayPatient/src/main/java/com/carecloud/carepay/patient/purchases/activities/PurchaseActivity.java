package com.carecloud.carepay.patient.purchases.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.purchases.fragments.PurchaseFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.google.gson.Gson;

/**
 * Created by lmenendez on 2/8/17
 */

public class PurchaseActivity extends MenuPatientActivity {

    private PurchaseFragment purchaseFragment;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Gson gson = new Gson();
        AppointmentsResultModel appointmentDTO = getConvertedDTO(AppointmentsResultModel.class);
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.APPOINTMENT_INFO_BUNDLE, gson.toJson(appointmentDTO));
        purchaseFragment = new PurchaseFragment();
        purchaseFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_main, purchaseFragment, null);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_purchase);
        menuItem.setChecked(true);
        displayToolbar(true, menuItem.getTitle().toString());
    }

    @Override
    public void onBackPressed(){
        if(purchaseFragment == null || !purchaseFragment.handleBackButton()){
            super.onBackPressed();
        }
    }
}
