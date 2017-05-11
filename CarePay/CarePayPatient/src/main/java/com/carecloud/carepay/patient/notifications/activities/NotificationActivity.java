package com.carecloud.carepay.patient.notifications.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.notifications.fragments.NotificationFragment;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.service.library.CarePayConstants;

/**
 * Created by lmenendez on 2/8/17.
 */

public class NotificationActivity extends MenuPatientActivity {


    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.activity_navigation);
        toolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.toolbar);
        drawer = (DrawerLayout) findViewById(com.carecloud.carepaylibrary.R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(com.carecloud.carepaylibrary.R.id.nav_view);
        appointmentsDrawerUserIdTextView = (TextView) navigationView.getHeaderView(0)
                .findViewById(com.carecloud.carepaylibrary.R.id.appointmentsDrawerIdTextView);

        NotificationsDTO notificationsDTO = getConvertedDTO(NotificationsDTO.class);
        NotificationFragment notificationFragment = NotificationFragment.newInstance(notificationsDTO);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_main, notificationFragment, null);
        transaction.commit();


        inflateDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_NOTIFICATION).setChecked(true);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_NOTIFICATION).getTitle());
        }
    }

}
