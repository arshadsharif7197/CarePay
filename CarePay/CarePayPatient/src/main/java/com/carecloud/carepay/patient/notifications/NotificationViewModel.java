package com.carecloud.carepay.patient.notifications;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepaylibray.common.BaseViewModel;
import com.carecloud.carepay.patient.notifications.activities.NotificationActivity;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationStatus;
import com.carecloud.carepay.patient.notifications.models.NotificationVM;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.utils.DtoHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 2019-12-02.
 */
public class NotificationViewModel extends BaseViewModel {

    private MutableLiveData<NotificationsDTO> notificationsDtoObservable;
    private MutableLiveData<NotificationVM> surveyObservable = new MutableLiveData<>();
    private MutableLiveData<NotificationVM> consentFormsObservable = new MutableLiveData<>();
    private MutableLiveData<Void> deleteNotificationObservable = new MutableLiveData<>();

    public NotificationViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<NotificationVM> getSurveyObservable() {
        return surveyObservable;
    }

    public MutableLiveData<NotificationVM> getConsentFormsObservable() {
        return consentFormsObservable;
    }

    public MutableLiveData<Void> getDeleteNotificationObservable() {
        return deleteNotificationObservable;
    }

    public MutableLiveData<NotificationsDTO> callNotificationService(TransitionDTO transitionDTO) {
        if (notificationsDtoObservable == null) {
            notificationsDtoObservable = new MutableLiveData<>();
        }
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                setSkeleton(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                setSkeleton(false);
                NotificationsDTO notificationsDTO = DtoHelper.getConvertedDTO(NotificationsDTO.class, workflowDTO);
                notificationsDtoObservable.setValue(notificationsDTO);
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                setSkeleton(false);
                setErrorMessage(serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        }, queryMap);
        return notificationsDtoObservable;
    }

    public void markNotificationRead(NotificationItem notificationItem) {
        TransitionDTO readNotifications = notificationsDtoObservable.getValue().getMetadata()
                .getTransitions().getReadNotifications();

        Map<String, String> properties = new HashMap<>();
        properties.put("notification_id", notificationItem.getPayload().getNotificationId());
        JSONObject payload = new JSONObject(properties);

        getWorkflowServiceHelper().execute(readNotifications, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                Log.d(NotificationActivity.class.getName(), "Notification marked as read");
                notificationItem.getPayload().setReadStatus(NotificationStatus.read);
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                Log.d(NotificationActivity.class.getName(), "Notification NOT marked as read");
                Log.e("Server Error", serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        }, payload.toString(), properties);

    }

    public void callSurveyService(final NotificationItem notificationItem) {
        NotificationVM notificationVM = new NotificationVM();
        notificationVM.setNotification(notificationItem);
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", notificationItem.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", notificationItem.getMetadata().getPracticeId());
        queryMap.put("appointment_id", notificationItem.getPayload().getPendingSurvey().getMetadata().getAppointmentId());
        queryMap.put("patient_id", notificationItem.getMetadata().getPatientId());
        getWorkflowServiceHelper().execute(notificationsDtoObservable.getValue()
                        .getMetadata().getLinks().getPendingSurvey(),
                new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        setLoading(true);
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        setLoading(false);
                        notificationVM.setWorkflowDTO(workflowDTO);
                        surveyObservable.setValue(notificationVM);
                    }

                    @Override
                    public void onFailure(ServerErrorDTO serverErrorDto) {
                        setLoading(false);
                        setErrorMessage(serverErrorDto.getMessage().getBody().getError().getMessage());
                        Log.e("Server Error", serverErrorDto.getMessage().getBody().getError().getMessage());
                    }
                }, queryMap);
    }

    public void callConsentFormsScreen(TransitionDTO transitionDTO, NotificationItem notificationItem) {
        NotificationVM notificationVM = new NotificationVM();
        notificationVM.setNotification(notificationItem);
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                setLoading(false);
                notificationVM.setWorkflowDTO(workflowDTO);
                consentFormsObservable.setValue(notificationVM);
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                setLoading(false);
                setErrorMessage(serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        });
    }

    public NotificationsDTO getDto() {
        return notificationsDtoObservable.getValue();
    }

    public MutableLiveData<NotificationsDTO> setDto(NotificationsDTO notificationsDTO) {
        if (notificationsDtoObservable == null) {
            notificationsDtoObservable = new MutableLiveData<>();
        }
        notificationsDtoObservable.setValue(notificationsDTO);
        return notificationsDtoObservable;
    }

    public Profile getDelegate() {
        return notificationsDtoObservable.getValue().getPayload().getDelegate();
    }

    public void deleteAllNotifications(TransitionDTO transitionDTO) {
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                setLoading(false);
                deleteNotificationObservable.setValue(null);
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                setLoading(false);
                Log.e("Server Error", serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        }, queryMap);
    }

    public void deleteNotification(TransitionDTO transitionDTO, NotificationItem notificationItem) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("notification_id", notificationItem.getPayload().getNotificationId());
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                Log.d("Server Error", "Delete notificaton successful");
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                Log.d("Server Error", serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        }, queryMap);
    }
}
