package com.carecloud.carepay.patient.notifications.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.PatientAppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDetailDialog;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.notifications.fragments.NotificationFragment;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.AppointmentDisplayUtil;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

import java.util.Date;

/**
 * Created by lmenendez on 2/8/17
 */

public class NotificationActivity extends MenuPatientActivity implements NotificationFragment.NotificationCallback, PatientAppointmentNavigationCallback{


    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.activity_navigation);
        toolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.toolbar);
        drawer = (DrawerLayout) findViewById(com.carecloud.carepaylibrary.R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(com.carecloud.carepaylibrary.R.id.nav_view);
        appointmentsDrawerUserIdTextView = (TextView) navigationView.getHeaderView(0)
                .findViewById(com.carecloud.carepaylibrary.R.id.appointmentsDrawerIdTextView);

        NotificationsDTO notificationsDTO = getConvertedDTO(NotificationsDTO.class);
        NotificationFragment notificationFragment = NotificationFragment.newInstance(notificationsDTO);
        replaceFragment(R.id.container_main, notificationFragment, false);

        inflateDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_NOTIFICATION).setChecked(true);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle(navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_NOTIFICATION).getTitle());
        }
    }

    @Override
    public void displayNotification(NotificationItem notificationItem) {
        switch (notificationItem.getPayload().getNotificationType()){
            case appointment:
                AppointmentDTO appointment = notificationItem.getPayload().getAppointment();
                displayAppointmentDetails(appointment);
                break;
            default:
                //todo handle other notification types
                break;
        }
    }


    @Override
    public void displayAppointmentDetails(AppointmentDTO appointment) {
        if(appointment.getPayload().getDisplayStyle() == null){
            appointment.getPayload().setDisplayStyle(AppointmentDisplayUtil.determineDisplayStyle(appointment));
        }

        AppointmentDetailDialog detailDialog = AppointmentDetailDialog.newInstance(appointment);
        detailDialog.show(getSupportFragmentManager(), detailDialog.getClass().getName());

    }

    @Override
    public void onCancelAppointment(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void onCancelAppointment(AppointmentDTO appointmentDTO, int cancellationReason, String cancellationReasonComment) {

    }

    @Override
    public void onCheckInStarted(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void onCheckOutStarted(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void onCheckInOfficeStarted(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void onRescheduleAppointment(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void newAppointment() {

    }

    @Override
    public void rescheduleAppointment(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void selectVisitType(AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {

    }

    @Override
    public void selectTime(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {

    }

    @Override
    public void selectTime(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {

    }

    @Override
    public void selectDateRange(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {

    }

    @Override
    public void confirmAppointment(AppointmentsSlotsDTO appointmentsSlot, AppointmentAvailabilityDTO availabilityDTO) {

    }

    @Override
    public void requestAppointment(AppointmentsSlotsDTO appointmentSlot, String comments) {

    }

    @Override
    public void onAppointmentUnconfirmed() {

    }

    @Override
    public void onAppointmentRequestSuccess() {

    }
}
