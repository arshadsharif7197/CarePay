package com.carecloud.carepay.patient.notifications.models;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

/**
 * @author pjohnson on 2019-12-03.
 */
public class NotificationVM {

    private NotificationItem notification;
    private WorkflowDTO workflowDTO;

    public NotificationItem getNotification() {
        return notification;
    }

    public void setNotification(NotificationItem notificationModel) {
        this.notification = notificationModel;
    }

    public WorkflowDTO getWorkflowDTO() {
        return workflowDTO;
    }

    public void setWorkflowDTO(WorkflowDTO workflowDTO) {
        this.workflowDTO = workflowDTO;
    }
}
