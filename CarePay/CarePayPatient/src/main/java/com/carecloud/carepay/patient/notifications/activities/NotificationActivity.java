package com.carecloud.carepay.patient.notifications.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.AppointmentViewModel;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDetailDialog;
import com.carecloud.carepay.patient.appointments.presenter.PatientAppointmentPresenter;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.patient.notifications.NotificationViewModel;
import com.carecloud.carepay.patient.notifications.fragments.NotificationFragment;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationStatus;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.activities.ViewPaymentBalanceHistoryActivity;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodPrepaymentFragment;
import com.carecloud.carepay.patient.rate.RateDialog;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 2/8/17
 */

public class NotificationActivity extends MenuPatientActivity
        implements NotificationFragment.NotificationCallback, AppointmentViewHandler {

    private static final int SURVEY_FLOW = 100;

    private PatientAppointmentPresenter appointmentPresenter;
    private NotificationViewModel viewModel;
    private AppointmentViewModel appointmentViewModel;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        NotificationsDTO notificationsDTO = getConvertedDTO(NotificationsDTO.class);
        setUpViewModel();
        if (notificationsDTO == null) {
            viewModel.callNotificationService(getTransitionNotifications()).observe(this,
                    t -> showNotificationFragment());
        } else {
            viewModel.setDto(notificationsDTO).observe(this,
                    t -> showNotificationFragment());
        }
    }

    private void showNotificationFragment() {
        replaceFragment(NotificationFragment.newInstance(), false);
        setUpAppointmentViewModel();
    }

    protected void setUpViewModel() {
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        setBasicObservers(viewModel);
        viewModel.getSkeleton().observe(this, showSkeleton -> {
            if (showSkeleton) {
                replaceFragment(ShimmerFragment.newInstance(R.layout.shimmer_default_item), false);
            }
        });
        viewModel.getSurveyObservable().observe(this, notificationVM -> {
            Bundle bundle = new Bundle();
            bundle.putString(CarePayConstants.PATIENT_ID, notificationVM.getNotification().getMetadata().getPatientId());
            bundle.putBoolean(CarePayConstants.NOTIFICATIONS_FLOW, true);
            PatientNavigationHelper.navigateToWorkflow(getContext(), notificationVM.getWorkflowDTO(), true,
                    SURVEY_FLOW, bundle);
        });
        viewModel.getConsentFormsObservable().observe(this, notificationVM -> {
            Bundle bundle = new Bundle();
            bundle.putString("practiceId", notificationVM.getNotification().getMetadata().getPracticeId());
            navigateToWorkflow(notificationVM.getWorkflowDTO(), bundle);
        });

        setUpAppointmentViewModel();
        appointmentViewModel.getAppointments(getTransitionAppointments(), false);
    }

    private void setUpAppointmentViewModel() {
        appointmentViewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        appointmentViewModel.getAppointmentsDtoObservable().observe(this, appointmentsResultModel -> {
            PaymentsModel paymentsModel = appointmentViewModel.getPaymentsModel();
            appointmentPresenter = new PatientAppointmentPresenter(NotificationActivity.this,
                    appointmentsResultModel, paymentsModel);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
//                appointmentPresenter.forwardAndroidPayResult(requestCode, resultCode, data);
                break;
            case SURVEY_FLOW:
                if (resultCode == Activity.RESULT_OK
                        && data.getBooleanExtra(CarePayConstants.SHOW_SURVEY, false)) {
                    new Handler().postDelayed(() -> {
                        if (ApplicationPreferences.getInstance().shouldShowRateDialog()) {
                            displayDialogFragment(RateDialog.newInstance(), true);
                        }
                    }, 100);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        selectMenuItem(R.id.notificationsMenuItem);
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            displayToolbar(true, getScreenTitle(Label.getLabel("notifications_heading")));
        }
    }

    @Override
    public void displayNotification(NotificationItem notificationItem) {
        if (notificationItem.getPayload().getReadStatus() == NotificationStatus.unread) {
            viewModel.markNotificationRead(notificationItem);
        }
        switch (notificationItem.getMetadata().getNotificationType()) {
            case appointment:
                AppointmentDTO appointment = notificationItem.getPayload().getAppointment();
                if (appointmentPresenter != null) {
                    hideProgressDialog();
                    AppointmentDetailDialog detailDialog = AppointmentDetailDialog
                            .newInstance(appointment);
                    displayDialogFragment(detailDialog, false);
                } else {
                    showProgressDialog();
                    appointmentViewModel.getAppointments(getTransitionAppointments(), false);
                }
                break;
            case pending_forms:
                viewModel.callConsentFormsScreen(getTransitionForms(), notificationItem);
                break;
            case pending_survey:
                viewModel.callSurveyService(notificationItem);
                break;
            case payments:
                if ("patient_statement".equals(notificationItem.getMetadata().getNotificationSubtype())) {
                    Intent intent = new Intent(this, ViewPaymentBalanceHistoryActivity.class);
                    startActivity(intent);
                    break;
                }
                break;
            case secure_message:
                callMessagesScreen();
                break;
            default:
                //todo handle other notification types
                break;
        }
    }

    private void callMessagesScreen() {
        displayMessagesScreen();
    }

    @Override
    public AppointmentPresenter getAppointmentPresenter() {
        return appointmentPresenter;
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
        if (fragment instanceof PaymentMethodPrepaymentFragment) {
            displayToolbar(false, null);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            displayToolbar(true, null);
        }
    }

    @Override
    public void confirmAppointment(boolean showSuccess, boolean isAutoScheduled) {
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(getTransitionNotifications(), notificationsWorkflowCallback, queryMap);
    }

    @Override
    public void refreshAppointments() {
        displayToolbar(true, null);
    }

    @Override
    public DTO getDto() {
        return null;
    }

    @Override
    protected void onProfileChanged(ProfileDto profile) {
        displayToolbar(true, getScreenTitle(Label.getLabel("notifications_heading")));
        viewModel.callNotificationService(getTransitionNotifications());
    }

    @Override
    protected Profile getCurrentProfile() {
        return viewModel.getDelegate();
    }
}
