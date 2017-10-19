package com.carecloud.carepay.patient.notifications.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.presenter.PatientAppointmentPresenter;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.notifications.fragments.NotificationFragment;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationStatus;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodPrepaymentFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 2/8/17
 */

public class NotificationActivity extends MenuPatientActivity
        implements NotificationFragment.NotificationCallback, AppointmentViewHandler {

    private PatientAppointmentPresenter appointmentPresenter;
    private NotificationsDTO notificationsDTO;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        notificationsDTO = getConvertedDTO(NotificationsDTO.class);

        boolean isLandingPage = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO)
                .getBoolean(CarePayConstants.OPEN_NOTIFICATIONS, false);

        if (isLandingPage) {
            List<PracticePatientIdsDTO> practicePatientIds = notificationsDTO.getPayload()
                    .getPracticePatientIds();
            if (!practicePatientIds.isEmpty()) {
                getApplicationPreferences().writeObjectToSharedPreference(
                        CarePayConstants.KEY_PRACTICE_PATIENT_IDS, practicePatientIds);
            }
            setTransitionBalance(notificationsDTO.getMetadata().getLinks().getPatientBalances());
            setTransitionLogout(notificationsDTO.getMetadata().getTransitions().getLogout());
            setTransitionProfile(notificationsDTO.getMetadata().getLinks().getProfileUpdate());
            setTransitionAppointments(notificationsDTO.getMetadata().getLinks().getAppointments());
            setTransitionNotifications(notificationsDTO.getMetadata().getLinks().getNotifications());
            setTransitionMyHealth(notificationsDTO.getMetadata().getLinks().getMyHealth());

            String userImageUrl = notificationsDTO.getPayload().getDemographicDTO()
                    .getPersonalDetails().getProfilePhoto();
            if (userImageUrl != null) {
                getApplicationPreferences().setUserPhotoUrl(userImageUrl);
            }
        }

        if (icicle == null) {
            NotificationFragment notificationFragment = NotificationFragment.newInstance(notificationsDTO);
            navigateToFragment(notificationFragment, false);
        }

        initPresenter(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
                appointmentPresenter.forwardAndroidPayResult(requestCode, resultCode, data);
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_notification);
        menuItem.setChecked(true);
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            displayToolbar(true, menuItem.getTitle().toString());
        }
    }

    @Override
    public void displayNotification(NotificationItem notificationItem) {
        switch (notificationItem.getPayload().getNotificationType()) {
            case appointment:
                AppointmentDTO appointment = notificationItem.getPayload().getAppointment();
                if (appointmentPresenter != null) {
                    appointmentPresenter.displayAppointmentDetails(appointment);
                    if (notificationItem.getPayload().getReadStatus() == NotificationStatus.unread) {
                        markNotificationRead(notificationItem);
                    }
                } else {
                    initPresenter(appointment);
                }
                break;
            default:
                //todo handle other notification types
                break;
        }
    }

    @Override
    public AppointmentPresenter getAppointmentPresenter() {
        return appointmentPresenter;
    }

    @Override
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
        if (fragment instanceof PaymentMethodPrepaymentFragment){
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
    public void confirmAppointment() {
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(transitionNotifications, notificationsWorkflowCallback, queryMap);
    }

    @Override
    public void refreshAppointments() {

    }

    private void initPresenter(final AppointmentDTO appointmentDTO) {
        TransitionDTO appointmentTransition = getTransitionAppointments();
        getWorkflowServiceHelper().execute(appointmentTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                if (appointmentDTO != null) {
                    showProgressDialog();
                }
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                AppointmentsResultModel appointmentsResultModel = DtoHelper
                        .getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                if (appointmentsResultModel != null) {
                    PaymentsModel paymentModel = new PaymentsModel();
                    paymentModel.getPaymentPayload().setPatientCreditCards(appointmentsResultModel
                            .getPayload().getPatientCreditCards());
                    paymentModel.getPaymentPayload().setPaymentSettings(appointmentsResultModel
                            .getPayload().getPaymentSettings());
                    paymentModel.getPaymentPayload().setMerchantServices(appointmentsResultModel
                            .getPayload().getMerchantServices());
                    paymentModel.getPaymentPayload().setPatientBalances(appointmentsResultModel
                            .getPayload().getPatientBalances());
                    paymentModel.getPaymentPayload().setUserPractices(appointmentsResultModel
                            .getPayload().getUserPractices());
                    paymentModel.getPaymentsMetadata().getPaymentsTransitions().setMakePayment(
                            appointmentsResultModel.getMetadata().getTransitions().getMakePayment());
                    paymentModel.getPaymentsMetadata().getPaymentsTransitions().setAddCreditCard(
                            appointmentsResultModel.getMetadata().getTransitions().getAddCreditCard());

                    appointmentPresenter = new PatientAppointmentPresenter(NotificationActivity.this,
                            appointmentsResultModel, paymentModel);
                }
                if (appointmentDTO != null) {
                    hideProgressDialog();
                    if (appointmentPresenter != null) {
                        appointmentPresenter.displayAppointmentDetails(appointmentDTO);
                    }
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                if (appointmentDTO != null) {
                    hideProgressDialog();
                    showErrorNotification(exceptionMessage);
                }
                appointmentPresenter = null;
            }
        });
    }


    private void markNotificationRead(NotificationItem notificationItem) {
        TransitionDTO readNotifications = notificationsDTO.getMetadata().getTransitions().getReadNotifications();

        Map<String, String> properties = new HashMap<>();
        properties.put("notification_id", notificationItem.getPayload().getNotificationId());
        JSONObject payload = new JSONObject(properties);

        getWorkflowServiceHelper().execute(readNotifications, readNotificationsCallback, payload.toString());

    }

    private WorkflowServiceCallback readNotificationsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Log.d(NotificationActivity.class.getName(), "Notification marked as read");
        }

        @Override
        public void onFailure(String exceptionMessage) {
            Log.d(NotificationActivity.class.getName(), "Notification NOT marked as read");
        }
    };
}
