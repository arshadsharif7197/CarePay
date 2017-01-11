package com.carecloud.carepay.patient.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jorge on 10/01/17.
 */

public class MenuPatientActivity extends BasePatientActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String LOG_TAG =  MenuPatientActivity.class.getSimpleName();
    //keys
    protected String practiceId;
    protected String practiceMgmt;
    protected String patientId;
    //transitions
    protected TransitionDTO transitionBalance;
    protected TransitionDTO transitionProfile;
    protected TransitionDTO transitionAppointments;
    protected TransitionDTO transitionLogout;

    protected TextView appointmentsDrawerUserIdTextView;
    protected NavigationView navigationView;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;


    protected void inflateDrawer() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.carecloud.carepaylibrary.R.string.navigation_drawer_open, com.carecloud.carepaylibrary.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        String userId = CognitoAppHelper.getCurrUser();
        if (userId != null) {
            appointmentsDrawerUserIdTextView.setText(userId);
        } else {
            appointmentsDrawerUserIdTextView.setText("");
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == com.carecloud.carepaylibrary.R.id.nav_appointments && transitionAppointments != null) {
                Map<String, String> queryString = new HashMap<>();
                queryString.put("practice_id", practiceId);
                queryString.put("practice_mgmt", practiceMgmt);
                queryString.put("patient_id", patientId);
                WorkflowServiceHelper.getInstance().execute(transitionAppointments, appointmentsWorkflowCallback, queryString);
        } else if (id == com.carecloud.carepaylibrary.R.id.nav_payments && transitionBalance != null) {
                Map<String, String> queryString = new HashMap<>();
                queryString.put("practice_id", practiceId);
                queryString.put("practice_mgmt", practiceMgmt);
                queryString.put("patient_id", patientId);
                WorkflowServiceHelper.getInstance().execute(transitionBalance, paymentsCallBack, queryString);

        } else if (id == com.carecloud.carepaylibrary.R.id.nav_settings && transitionProfile != null) {
                Map<String, String> queryString = new HashMap<>();
                queryString.put("practice_id", practiceId);
                queryString.put("practice_mgmt", practiceMgmt);
                queryString.put("patient_id", patientId);
                WorkflowServiceHelper.getInstance().execute(transitionProfile, demographicsSettingsCallBack, queryString);

        } else if (id == com.carecloud.carepaylibrary.R.id.nav_logout && transitionLogout != null) {
            // perform log out, of course
            String userName = CognitoAppHelper.getCurrUser();
            if (userName != null) {
                Log.v(LOG_TAG, "sign out");
                Map<String, String> headersMap = new HashMap<>();
                headersMap.put("x-api-key", HttpConstants.getApiStartKey());
                headersMap.put("Authorization", CognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
                headersMap.put("transition", "true");
                Map<String, String> queryMap = new HashMap<>();
                WorkflowServiceHelper.getInstance().execute(transitionLogout, appointmentsWorkflowCallback, queryMap, headersMap);
            }
        } else if (id == com.carecloud.carepaylibrary.R.id.nav_purchase) {
            Log.v(LOG_TAG, "Purchase");
        } else if (id == com.carecloud.carepaylibrary.R.id.nav_notification) {
            Log.v(LOG_TAG, "Notification");
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(com.carecloud.carepaylibrary.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private WorkflowServiceCallback paymentsCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(MenuPatientActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            //SystemUtil.showFaultDialog(InTakeWebViewActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback demographicsSettingsCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(MenuPatientActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback appointmentsWorkflowCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(MenuPatientActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(MenuPatientActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
