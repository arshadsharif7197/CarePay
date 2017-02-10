package com.carecloud.carepay.patient.base;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
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
    protected String prefix;
    protected String userId;
    //transitions
    private static TransitionDTO transitionBalance;
    private static TransitionDTO transitionProfile;
    private static TransitionDTO transitionAppointments;
    private static TransitionDTO transitionLogout;

    protected ActionBarDrawerToggle toggle;
    protected TextView appointmentsDrawerUserIdTextView;
    protected NavigationView navigationView;
    protected DrawerLayout drawer;
    protected Toolbar toolbar;


    protected void inflateDrawer() {
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(
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
        patientId = ApplicationPreferences.Instance.getPatientId();
        practiceId = ApplicationPreferences.Instance.getPracticeId();
        practiceMgmt = ApplicationPreferences.Instance.getPracticeManagement();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == com.carecloud.carepaylibrary.R.id.nav_appointments && transitionAppointments != null) {
                Map<String, String> queryString = new HashMap<>();
                queryString.put("practice_id", practiceId == null ? "" : practiceId);
                queryString.put("practice_mgmt", practiceMgmt == null ? "" : practiceMgmt);
                queryString.put("patient_id", patientId == null ? "" : patientId);
                WorkflowServiceHelper.getInstance().execute(transitionAppointments, appointmentsWorkflowCallback, queryString);

        } else if (id == com.carecloud.carepaylibrary.R.id.nav_payments && transitionBalance != null) {
                Map<String, String> queryString = new HashMap<>();
                queryString.put("practice_id", practiceId == null ? "" : practiceId);
                queryString.put("practice_mgmt", practiceMgmt == null ? "" : practiceMgmt);
                queryString.put("patient_id", patientId == null ? "" : patientId);
                WorkflowServiceHelper.getInstance().execute(transitionBalance, paymentsCallBack, queryString);

        } else if (id == com.carecloud.carepaylibrary.R.id.nav_settings && transitionProfile != null) {
                Map<String, String> queryString = new HashMap<>();
                queryString.put("practice_id", practiceId == null ? "" : practiceId);
                queryString.put("practice_mgmt", practiceMgmt == null ? "" : practiceMgmt);
                queryString.put("patient_id", patientId == null ? "" : patientId);
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
            Map<String, String> queryString = new HashMap<>();
            queryString.put("practice_id", practiceId == null ? "" : practiceId);
            queryString.put("practice_mgmt", practiceMgmt == null ? "" : practiceMgmt);
            queryString.put("patient_id", patientId == null ? "" : patientId);
            WorkflowServiceHelper.getInstance().execute(transitionAppointments, purchaseWorkflowCallback, queryString);

        } else if (id == com.carecloud.carepaylibrary.R.id.nav_notification) {
            Log.v(LOG_TAG, "Notification");
            //Temporary link to landing Activity for blank purchase screen
            Map<String, String> queryString = new HashMap<>();
            queryString.put("practice_id", practiceId == null ? "" : practiceId);
            queryString.put("practice_mgmt", practiceMgmt == null ? "" : practiceMgmt);
            queryString.put("patient_id", patientId == null ? "" : patientId);
            WorkflowServiceHelper.getInstance().execute(transitionAppointments, notificationsWorkflowCallback, queryString);
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(com.carecloud.carepaylibrary.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private WorkflowServiceCallback paymentsCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            PatientNavigationHelper.setAccessPaymentsBalances(true);
            PatientNavigationHelper.getInstance(MenuPatientActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            //SystemUtil.showDefaultFailureDialog(InTakeWebViewActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback demographicsSettingsCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {

            ProgressDialogUtil.getInstance(getContext()).dismiss();

            PatientNavigationHelper.getInstance(MenuPatientActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDefaultFailureDialog(MenuPatientActivity.this);

            ProgressDialogUtil.getInstance(getContext()).dismiss();

            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback appointmentsWorkflowCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            PatientNavigationHelper.getInstance(MenuPatientActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            SystemUtil.showDefaultFailureDialog(MenuPatientActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback purchaseWorkflowCallback = new WorkflowServiceCallback() {//TODO this is currently pointed at appointments endpoint
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            //need to manually redirect this response to the notifications screen temporarily
            workflowDTO.setState(PatientNavigationStateConstants.PURCHASE);
            PatientNavigationHelper.getInstance(MenuPatientActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            SystemUtil.showDefaultFailureDialog(MenuPatientActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
    private WorkflowServiceCallback notificationsWorkflowCallback = new WorkflowServiceCallback() {//TODO this is currently pointed at appointments endpoint
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            //need to manually redirect this response to the notifications screen temporarily
            workflowDTO.setState(PatientNavigationStateConstants.NOTIFICATION);
            PatientNavigationHelper.getInstance(MenuPatientActivity.this).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            SystemUtil.showDefaultFailureDialog(MenuPatientActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    public static void setTransitionBalance(TransitionDTO transitionBalance) {
        MenuPatientActivity.transitionBalance = transitionBalance;
    }

    public static void setTransitionProfile(TransitionDTO transitionProfile) {
        MenuPatientActivity.transitionProfile = transitionProfile;
    }

    public static void setTransitionAppointments(TransitionDTO transitionAppointments) {
        MenuPatientActivity.transitionAppointments = transitionAppointments;
    }

    public static void setTransitionLogout(TransitionDTO transitionLogout) {
        MenuPatientActivity.transitionLogout = transitionLogout;
    }

}
