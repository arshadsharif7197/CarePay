package com.carecloud.carepay.patient.purchases.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

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

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.activity_navigation);
        toolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.toolbar);
        drawer = (DrawerLayout) findViewById(com.carecloud.carepaylibrary.R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(com.carecloud.carepaylibrary.R.id.nav_view);
        appointmentsDrawerUserIdTextView = (TextView) navigationView.getHeaderView(0)
                .findViewById(com.carecloud.carepaylibrary.R.id.appointmentsDrawerIdTextView);

        Gson gson = new Gson();
        AppointmentsResultModel appointmentDTO = getConvertedDTO(AppointmentsResultModel.class);
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.APPOINTMENT_INFO_BUNDLE, gson.toJson(appointmentDTO));
        PurchaseFragment purchaseFragment = new PurchaseFragment();
        purchaseFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_main, purchaseFragment, null);
        transaction.commit();

        inflateDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_PURCHASE).setChecked(true);
        displayToolbar(true, navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_PURCHASE).getTitle().toString());
    }
}
