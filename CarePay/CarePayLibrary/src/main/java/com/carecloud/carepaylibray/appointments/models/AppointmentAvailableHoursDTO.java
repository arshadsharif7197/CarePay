package com.carecloud.carepaylibray.appointments.models;

public class AppointmentAvailableHoursDTO {
    private String appointmentDay;
    private String appointmentMonth;
    private String appointmentDate;
    private String appointmentTimeSlot;

    /**
     * Constructor.
     * @param appointmentDay appointment day
     * @param appointmentMonth appointment month
     * @param appointmentDate appointment date
     * @param appointmentTimeSlot appointment time
     */
    public AppointmentAvailableHoursDTO(String appointmentDay, String appointmentMonth,
                                        String appointmentDate, String appointmentTimeSlot) {
        this.appointmentDay = appointmentDay;
        this.appointmentMonth = appointmentMonth;
        this.appointmentDate = appointmentDate;
        this.appointmentTimeSlot = appointmentTimeSlot;
    }

    public String getAppointmentDay() {
        return appointmentDay;
    }

    public void setAppointmentDay(String appointmentDay) {
        this.appointmentDay = appointmentDay;
    }

    public String getAppointmentMonth() {
        return appointmentMonth;
    }

    public void setAppointmentMonth(String appointmentMonth) {
        this.appointmentMonth = appointmentMonth;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTimeSlot() {
        return appointmentTimeSlot;
    }

    public void setAppointmentTimeSlot(String appointmentTimeSlot) {
        this.appointmentTimeSlot = appointmentTimeSlot;
    }
}
