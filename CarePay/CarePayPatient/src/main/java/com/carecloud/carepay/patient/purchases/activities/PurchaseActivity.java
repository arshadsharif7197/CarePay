package com.carecloud.carepay.patient.purchases.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.purchases.fragments.PurchaseFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;

/**
 * Created by lmenendez on 2/8/17
 */

public class PurchaseActivity extends MenuPatientActivity {

    private PurchaseFragment purchaseFragment;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        AppointmentsResultModel appointmentDTO = getConvertedDTO(AppointmentsResultModel.class);
        purchaseFragment = PurchaseFragment.newInstance(appointmentDTO);

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
