package com.carecloud.carepay.patient.notifications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 5/11/17.
 */

public class NotificationItemMetadata {

    @SerializedName("practice_mgmt")
    @Expose
    private String practiceMgmt;
    @SerializedName("practice_id")
    @Expose
    private String practiceId;
    @SerializedName("appointment_id")
    @Expose
    private String appointmentId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("created_dt")
    @Expose
    private String createdDt;
    @SerializedName("updated_dt")
    @Expose
    private String updatedDt;

    @SerializedName("notification_type")
    @Expose
    private NotificationType notificationType;

    @SerializedName("event")
    @Expose
    private Event event;

    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(String createdDt) {
        this.createdDt = createdDt;
    }

    public String getUpdatedDt() {
        return updatedDt;
    }

    public void setUpdatedDt(String updatedDt) {
        this.updatedDt = updatedDt;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public class Event {

        @Expose
        @SerializedName("payload")
        private EventPayload payload;

        public EventPayload getPayload() {
            return payload;
        }

        public void setPayload(EventPayload payload) {
            this.payload = payload;
        }
    }

    public class EventPayload {

        public static final String EXECUTION_TYPE_ONE_TIME = "one_time_payment";
        public static final String EXECUTION_TYPE_PAYMENT_PLAN = "payment_plan";

        @Expose
        @SerializedName("is_payment_successful")
        private boolean isPaymentSuccessful;

        @Expose
        @SerializedName("execution_type")
        private String executionType;

        @Expose
        @SerializedName("scheduled_payment_execution")
        private ScheduledPaymentExecution scheduledPaymentExecution;

        public boolean isPaymentSuccessful() {
            return isPaymentSuccessful;
        }

        public void setPaymentSuccessful(boolean paymentSuccessful) {
            isPaymentSuccessful = paymentSuccessful;
        }

        public String getExecutionType() {
            return executionType;
        }

        public void setExecutionType(String executionType) {
            this.executionType = executionType;
        }

        public ScheduledPaymentExecution getScheduledPaymentExecution() {
            return scheduledPaymentExecution;
        }

        public void setScheduledPaymentExecution(ScheduledPaymentExecution scheduledPaymentExecution) {
            this.scheduledPaymentExecution = scheduledPaymentExecution;
        }
    }

    public class ScheduledPaymentExecution {

        @Expose
        @SerializedName("payment_date")
        private String paymentDate;
        @Expose
        @SerializedName("plan_name")
        private String paymentPlanName;
        @Expose
        @SerializedName("paid")
        private double paid;
        @Expose
        @SerializedName("balance")
        private double balance;

        public String getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
        }

        public String getPaymentPlanName() {
            return paymentPlanName;
        }

        public void setPaymentPlanName(String paymentPlanName) {
            this.paymentPlanName = paymentPlanName;
        }

        public double getPaid() {
            return paid;
        }

        public void setPaid(double paid) {
            this.paid = paid;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }
    }
}
