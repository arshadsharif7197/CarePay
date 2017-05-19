package com.carecloud.carepay.patient.notifications.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.presenter.PatientAppointmentPresenter;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.notifications.fragments.NotificationFragment;
import com.carecloud.carepay.patient.notifications.models.NotificationItem;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 2/8/17
 */

public class NotificationActivity extends MenuPatientActivity implements NotificationFragment.NotificationCallback, AppointmentViewHandler {

    private PatientAppointmentPresenter appointmentPresenter;

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
        navigateToFragment(notificationFragment, false);

        initPresenter(null);

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
                if(appointmentPresenter !=null){
                    appointmentPresenter.displayAppointmentDetails(appointment);
                }else{
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
    }

    @Override
    public void confirmAppointment() {

    }

    @Override
    public void refreshAppointments() {

    }

    private void initPresenter(final AppointmentDTO appointmentDTO){
        TransitionDTO appointmentTransition = getTransitionAppointments();
        getWorkflowServiceHelper().execute(appointmentTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                if(appointmentDTO!=null){
                    showProgressDialog();
                }
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                AppointmentsResultModel appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
                if(appointmentsResultModel != null) {
                    appointmentPresenter = new PatientAppointmentPresenter(NotificationActivity.this, appointmentsResultModel);
                }
                if(appointmentDTO != null){
                    hideProgressDialog();
                    if(appointmentPresenter !=null){
                        appointmentPresenter.displayAppointmentDetails(appointmentDTO);
                    }
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                if(appointmentDTO!=null){
                    hideProgressDialog();
                    showErrorNotification(exceptionMessage);
                }
                appointmentPresenter = null;
            }
        });
    }

}
