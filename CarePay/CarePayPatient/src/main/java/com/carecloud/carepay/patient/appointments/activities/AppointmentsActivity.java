package com.carecloud.carepay.patient.appointments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.createappointment.CreateAppointmentFragment;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentTabHostFragment;
import com.carecloud.carepay.patient.appointments.presenter.PatientAppointmentPresenter;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.HashMap;
import java.util.Map;

public class AppointmentsActivity extends MenuPatientActivity implements AppointmentViewHandler,
        FragmentActivityInterface {

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
                ShimmerFragment shimmerFragment = ShimmerFragment.newInstance(R.layout.shimmer_default_header,
                        R.layout.shimmer_default_item);
                shimmerFragment.setTabbed(true,
                        Label.getLabel("appointments.list.tab.title.current"),
                        Label.getLabel("appointments.list.tab.title.history"));
                replaceFragment(shimmerFragment, false);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
                resumeOnCreate();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
                showErrorNotification(exceptionMessage);
            }
        }, queryMap);
    }

    private void resumeOnCreate() {
        if (paymentsModel == null) {
            paymentsModel = getConvertedDTO(PaymentsModel.class);
        }
        initPresenter();
        AppointmentTabHostFragment fragment = AppointmentTabHostFragment.newInstance(0);
        replaceFragment(R.id.container_main, fragment, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
            case PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT:
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
        selectMenuItem(R.id.appointmentMenuItem);
        if (!toolbarHidden) {
            displayToolbar(true, getScreenTitle(Label.getLabel("navigation_link_appointments")));
        }
    }

    private void initPresenter() {
        presenter = new PatientAppointmentPresenter(this, appointmentsResultModel, paymentsModel);
    }

    @Override
    public void onBackPressed() {// sign-out from Cognito
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
                displayToolbar(true, null);
                getSupportActionBar().setElevation(0);
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
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void confirmAppointment(boolean showSuccess, boolean isAutoScheduled) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            fragmentManager.popBackStackImmediate();
        }
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_appointments);
        displayToolbar(true, getScreenTitle(Label.getLabel("navigation_link_appointments")));
        toolbarHidden = false;
        refreshAppointments();
        if (showSuccess) {
            showAppointmentConfirmation(isAutoScheduled);
        }
    }

    @Override
    public void refreshAppointments() {
        callAppointmentService();
    }

    @Override
    public void newAppointment() {
        CreateAppointmentFragment fragment = CreateAppointmentFragment.newInstance();
        addFragment(fragment, true);
    }

    private void showAppointmentConfirmation(boolean isAutoScheduled) {
        String appointmentRequestSuccessMessage = Label.getLabel(isAutoScheduled ?
                "appointment_schedule_success_message_HTML" :
                "appointment_request_success_message_HTML");
        SystemUtil.showSuccessToast(getContext(), appointmentRequestSuccessMessage);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public DTO getDto() {
        return appointmentsResultModel;
    }

    @Override
    protected void onProfileChanged(ProfileDto profile) {
        displayToolbar(true, getScreenTitle(Label.getLabel("navigation_link_appointments")));
        callAppointmentService();
    }

    @Override
    protected Profile getCurrentProfile() {
        return appointmentsResultModel.getPayload().getDelegate();
    }
}
