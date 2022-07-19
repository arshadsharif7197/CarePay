package com.carecloud.carepay.patient.appointments.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.AppointmentViewModel;
import com.carecloud.carepay.patient.appointments.createappointment.AvailabilityHourFragment;
import com.carecloud.carepay.patient.appointments.createappointment.CreateAppointmentFragment;
import com.carecloud.carepay.patient.appointments.createappointment.RequestAppointmentDialogFragment;
import com.carecloud.carepay.patient.appointments.dialog.CancelAppointmentFeeDialog;
import com.carecloud.carepay.patient.appointments.dialog.CancelReasonAppointmentDialog;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDetailDialog;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentTabHostFragment;
import com.carecloud.carepay.patient.appointments.presenter.PatientAppointmentPresenter;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.patient.messages.activities.MessagesActivity;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodPrepaymentFragment;
import com.carecloud.carepay.patient.rate.RateDialog;
import com.carecloud.carepay.patient.utils.payments.Constants;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentConnectivityHandler;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.viewModel.PatientResponsibilityViewModel;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

public class AppointmentsActivity extends MenuPatientActivity implements AppointmentConnectivityHandler,
        FragmentActivityInterface {

    private AppointmentsResultModel appointmentsResultModel;
    private PaymentsModel paymentsModel;
    private PatientAppointmentPresenter presenter;

    private boolean toolbarHidden = false;
    private AppointmentViewModel viewModel;
    public static boolean isFromTelehealthScreen;
    private PatientResponsibilityViewModel patientResponsibilityViewModel;

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
                patientResponsibilityViewModel.setPaymentsModel(paymentsModel);
                resumeOnCreate();
            });
            viewModel.getAppointments(getTransitionAppointments(), true);
        }

        if (showSurvey || forceRefresh) {
            showRateDialogFragment();
        }
    }

    protected void setUpViewModel() {
        patientResponsibilityViewModel = new ViewModelProvider(this).get(PatientResponsibilityViewModel.class);
        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
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
            patientResponsibilityViewModel.setPaymentsModel(paymentsModel);
        }
        initPresenter();
        AppointmentTabHostFragment fragment = AppointmentTabHostFragment.newInstance(0);
        replaceFragment(R.id.container_main, fragment, false);

        String messageId = ApplicationPreferences.getInstance().getMessageId();
        if (!messageId.isEmpty()) {
            ApplicationPreferences.getInstance().setMessageId("");
            Intent intent = new Intent(this, MessagesActivity.class);
            intent.putExtra(MessagesActivity.KEY_MESSAGE_ID, messageId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void refreshUI() {
        new Handler().postDelayed(() -> {
            refreshAppointments();
            showRateDialogFragment();
        }, 100);
    }

    private AppointmentDTO getAppointmentInfo(String appointmentId) {
        for (AppointmentDTO appointmentDTO : appointmentsResultModel.getPayload().getAppointments()) {
            if (appointmentDTO.getPayload().getId().equals(appointmentId)) {
                return appointmentDTO;
            }
        }
        return null;
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
            if (!isFragmentVisible()) {
                getSupportFragmentManager().popBackStackImmediate();
                if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
                    displayToolbar(true, null);
                    getSupportActionBar().setElevation(0);
                    toolbarHidden = false;
                }
            }
        } else {
            // finish the app
            finishAffinity();
        }
    }


    private boolean isFragmentVisible() {
        Fragment fragment = getTopFragment();
        if (fragment != null) {
            if (fragment instanceof PaymentMethodPrepaymentFragment) {
                // skip backpress
                return true;
            } else if (fragment instanceof CancelReasonAppointmentDialog) {
                // skip backpress
                return true;
            } else if (fragment instanceof AvailabilityHourFragment) {
                // skip backpress
                return true;
            }
        }
        return false;
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
        clearDialogs();
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            try {
                fragmentManager.popBackStackImmediate();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        clearDialogs();
        viewModel.getAppointments(getTransitionAppointments(), true);
        displayToolbar(true, getScreenTitle(Label.getLabel("navigation_link_appointments")));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
            case PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT:
                presenter.forwardAndroidPayResult(requestCode, resultCode, data);
                break;
            case PatientAppointmentPresenter.CHECK_IN_FLOW_REQUEST_CODE: {
                refreshAppointments();
                if (isFromTelehealthScreen) {
                    isFromTelehealthScreen = false;
                    new Handler().postDelayed(() -> {
                        presenter.startVideoVisit(((CarePayApplication) getApplicationContext()).getAppointmentDTO());
                    }, 1000);
                }
                break;
            }
            case CarePayConstants.TELEHEALTH_APPOINTMENT_REQUEST:
                if (resultCode == RESULT_OK) {
                    refreshAppointments();
                    new Handler().postDelayed(() -> {
                        presenter.startVideoVisit(((CarePayApplication) getApplicationContext()).getAppointmentDTO());
                    }, 1000);
                } else {
                    refreshUI();
                }
                break;
            case CarePayConstants.INTELLIGENT_SCHEDULER_REQUEST:
                if (resultCode == RESULT_OK) {
                    if (data != null && data.hasExtra(CarePayConstants.INTELLIGENT_SCHEDULER_VISIT_TYPE_KEY)) {
                        VisitTypeDTO visitTypeDTO = (VisitTypeDTO) data.getSerializableExtra(CarePayConstants.INTELLIGENT_SCHEDULER_VISIT_TYPE_KEY);
                        viewModel.setAutoScheduleVisitTypeObservable(visitTypeDTO);
                    } else {
                        viewModel.setAutoScheduleVisitTypeObservable(null);
                    }
                } else {
                    viewModel.setAutoScheduleVisitTypeObservable(null);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void onAppointmentScheduleFlowFailure() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            try {
                fragmentManager.popBackStackImmediate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        displayToolbar(true, getScreenTitle(Label.getLabel("navigation_link_appointments")));
    }

    private void clearDialogs() {
        if (CancelReasonAppointmentDialog.getInstance() != null) {
            CancelReasonAppointmentDialog.getInstance().dismiss();
        }
        if (CancelAppointmentFeeDialog.getInstance() != null) {
            CancelAppointmentFeeDialog.getInstance().dismiss();
        }
        if (AppointmentDetailDialog.getInstance() != null) {
            AppointmentDetailDialog.getInstance().dismiss();
        }
    }
}
