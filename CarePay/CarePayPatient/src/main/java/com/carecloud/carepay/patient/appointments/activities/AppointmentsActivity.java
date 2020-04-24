package com.carecloud.carepay.patient.appointments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.AppointmentViewModel;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentTabHostFragment;
import com.carecloud.carepay.patient.appointments.presenter.PatientAppointmentPresenter;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.rate.RateDialog;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
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
import com.carecloud.carepaylibray.utils.SystemUtil;

public class AppointmentsActivity extends MenuPatientActivity implements AppointmentViewHandler,
        FragmentActivityInterface {

    private AppointmentsResultModel appointmentsResultModel;
    private PaymentsModel paymentsModel;
    private PatientAppointmentPresenter presenter;

    private boolean toolbarHidden = false;
    private AppointmentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViewModel();
        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        boolean forceRefresh = false;
        boolean showSurvey = false;
        if (extra != null) {
            forceRefresh = extra.getBoolean(CarePayConstants.REFRESH, false);
            showSurvey = extra.getBoolean(CarePayConstants.SHOW_SURVEY, false);
        }
        if (appointmentsResultModel == null || forceRefresh) {
            viewModel.getAppointmentsDtoObservable().observe(this, appointmentsResultModel -> {
                this.appointmentsResultModel = appointmentsResultModel;
                paymentsModel = viewModel.getPaymentsModel();
                resumeOnCreate();
            });
            viewModel.getAppointments(getTransitionAppointments(), true);
        }

        if (showSurvey || forceRefresh) {
            showRateDialogFragment();
        }
    }

    protected void setUpViewModel() {
        viewModel = ViewModelProviders.of(this).get(AppointmentViewModel.class);
        setBasicObservers(viewModel);
        viewModel.getSkeleton().observe(this, showSkeleton -> {
            if (showSkeleton) {
                ShimmerFragment shimmerFragment = ShimmerFragment.newInstance(R.layout.shimmer_default_header,
                        R.layout.shimmer_default_item);
                shimmerFragment.setTabbed(true,
                        Label.getLabel("appointments.list.tab.title.current"),
                        Label.getLabel("appointments.list.tab.title.history"));
                replaceFragment(shimmerFragment, false);
            }
        });
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
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
            case PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT:
                presenter.forwardAndroidPayResult(requestCode, resultCode, data);
                break;
            case PatientAppointmentPresenter.CHECK_IN_FLOW_REQUEST_CODE:
                if (resultCode == RESULT_CANCELED) {
                    new Handler().postDelayed(() -> {
                        refreshAppointments();
                        showRateDialogFragment();
                    }, 100);
                } else if (resultCode == RESULT_OK) {
                    new Handler().postDelayed(() -> {
                        refreshAppointments();
                        showRateDialogFragment();
                    }, 100);
                }
                break;
            case PaymentConstants.REQUEST_CODE_CCLIVE:
//                onBackPressed();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void showRateDialogFragment() {
        if (ApplicationPreferences.getInstance().shouldShowRateDialog()) {
            displayDialogFragment(RateDialog.newInstance(), true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectMenuItem(R.id.appointmentMenuItem);
        if (!toolbarHidden && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            displayToolbar(true, getScreenTitle(Label.getLabel("navigation_link_appointments")));
        }
    }

    private void initPresenter() {
        presenter = new PatientAppointmentPresenter(this, appointmentsResultModel, paymentsModel);
    }

    @Override
    public void onBackPressed() {
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
        displayToolbar(true, getScreenTitle(Label.getLabel("navigation_link_appointments")));
        toolbarHidden = false;
        refreshAppointments();
        if (showSuccess) {
            showAppointmentConfirmation(isAutoScheduled);
        }
    }

    @Override
    public void refreshAppointments() {
        viewModel.getAppointments(getTransitionAppointments(), true);
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
        viewModel.getAppointments(getTransitionAppointments(), true);
    }

    @Override
    protected Profile getCurrentProfile() {
        return appointmentsResultModel.getPayload().getDelegate();
    }

}
