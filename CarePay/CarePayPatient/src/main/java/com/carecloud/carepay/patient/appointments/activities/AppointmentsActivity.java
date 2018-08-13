package com.carecloud.carepay.patient.appointments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.carecloud.carepay.patient.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepay.patient.appointments.presenter.PatientAppointmentPresenter;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.HashMap;
import java.util.Map;

public class AppointmentsActivity extends MenuPatientActivity implements AppointmentViewHandler {

    private AppointmentsResultModel appointmentsResultModel;
    private PaymentsModel paymentsModel;
    private PatientAppointmentPresenter presenter;

    private boolean toolbarHidden = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        boolean forceRefresh = false;
        if (extra != null) {
            forceRefresh = extra.getBoolean(CarePayConstants.REFRESH, false);
        }
        appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
        if (appointmentsResultModel == null || forceRefresh) {
            callAppointmentService();
        } else {
            resumeOnCreate();
        }

    }

    private void callAppointmentService() {
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(getTransitionAppointments(), new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                replaceFragment(ShimmerFragment.newInstance(R.layout.appointment_list_header,
                        R.layout.item_appointment), false);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                resumeOnCreate();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();

            }
        }, queryMap);
    }

    private void resumeOnCreate() {
        paymentsModel = getConvertedDTO(PaymentsModel.class);
        initPresenter();
        gotoAppointmentListFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
                presenter.forwardAndroidPayResult(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_appointments);
        menuItem.setChecked(true);
        if (!toolbarHidden) {
            displayToolbar(true, menuItem.getTitle().toString());
        }
    }

    private void gotoAppointmentListFragment() {
        AppointmentsListFragment fragment = AppointmentsListFragment.newInstance(appointmentsResultModel);
        replaceFragment(fragment, false);
    }

    private void initPresenter() {
        presenter = new PatientAppointmentPresenter(this, appointmentsResultModel, paymentsModel);
    }

    @Override
    public void onBackPressed() {// sign-out from Cognito
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
                displayToolbar(true, null);
                toolbarHidden = false;
            }
        } else {
            // finish the app
            finishAffinity();
        }
    }

    @Override
    public AppointmentPresenter getAppointmentPresenter() {
        return presenter;
    }

    @Override
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(fragment, addToBackStack);
        displayToolbar(false, null);
        toolbarHidden = true;
    }

    @Override
    public void confirmAppointment(boolean showSuccess, boolean isAutoScheduled) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            fragmentManager.popBackStackImmediate();
        }
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_appointments);
        displayToolbar(true, menuItem.getTitle().toString());
        toolbarHidden = false;

        refreshAppointments();
        if (showSuccess) {
            showAppointmentConfirmation(isAutoScheduled);
        }
    }

    @Override
    public void refreshAppointments() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_main);
        if (fragment != null && fragment instanceof AppointmentsListFragment) {
            AppointmentsListFragment appointmentsListFragment = (AppointmentsListFragment) fragment;
            appointmentsListFragment.refreshAppointmentList();
        }

    }

    private void showAppointmentConfirmation(boolean isAutoScheduled) {
        String appointmentRequestSuccessMessage = Label.getLabel(isAutoScheduled ?
                "appointment_schedule_success_message_HTML" :
                "appointment_request_success_message_HTML");
        SystemUtil.showSuccessToast(getContext(), appointmentRequestSuccessMessage);
    }

    private void replaceFragment(Fragment fragment, boolean addToStack) {
        replaceFragment(R.id.container_main, fragment, addToStack);
    }

}
