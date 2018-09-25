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
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

import org.json.JSONObject;

import java.util.HashMap;
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
                break;
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
        switch (notificationItem.getMetadata().getNotificationType()) {
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
            case pending_forms:
                if (notificationItem.getPayload().getReadStatus() == NotificationStatus.unread) {
                    markNotificationRead(notificationItem);
                }
                callConsentFormsScreen(notificationItem);
                break;
            case pending_survey:
                if (notificationItem.getPayload().getReadStatus() == NotificationStatus.unread) {
                    markNotificationRead(notificationItem);
                }
                callSurveyService(notificationItem);
                break;
            case payments:

                break;
            case secure_message:
                if (notificationItem.getPayload().getReadStatus() == NotificationStatus.unread) {
                    markNotificationRead(notificationItem);
                }
                callMessagesScreen(notificationItem);
                break;
            default:
                //todo handle other notification types
                break;
        }
    }

    private void callSurveyService(final NotificationItem notificationItem) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", notificationItem.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", notificationItem.getMetadata().getPracticeId());
        queryMap.put("appointment_id", notificationItem.getPayload().getPendingSurvey().getMetadata().getAppointmentId());
        queryMap.put("patient_id", notificationItem.getMetadata().getPatientId());
        getWorkflowServiceHelper().execute(notificationsDTO.getMetadata().getLinks().getPendingSurvey(),
                new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        showProgressDialog();
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        hideProgressDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString(CarePayConstants.PATIENT_ID, notificationItem.getMetadata().getPatientId());
                        navigateToWorkflow(workflowDTO, bundle);
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        hideProgressDialog();
                        showErrorNotification(exceptionMessage);
                        Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
                    }
                }, queryMap);
    }

    private void callConsentFormsScreen(final NotificationItem notificationItem) {
        getWorkflowServiceHelper().execute(getTransitionForms(), new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                Bundle bundle = new Bundle();
                bundle.putString("practiceId", notificationItem.getMetadata().getPracticeId());
                navigateToWorkflow(workflowDTO, bundle);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }
        });
    }

    private void callMessagesScreen(NotificationItem notificationItem) {
        displayMessagesScreen();
    }

    @Override
    public AppointmentPresenter getAppointmentPresenter() {
        return appointmentPresenter;
    }

    @Override
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
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

        getWorkflowServiceHelper().execute(readNotifications, readNotificationsCallback, payload.toString(), properties);

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
