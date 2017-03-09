package com.carecloud.carepay.patient.appointments.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.carecloud.carepay.patient.appointments.AppointmentNavigationCallback;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDateRangeFragment;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepay.patient.appointments.fragments.AvailableHoursFragment;
import com.carecloud.carepay.patient.appointments.fragments.ChooseProviderFragment;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.customdialogs.RequestAppointmentDialog;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.Date;

public class AppointmentsActivity extends MenuPatientActivity implements AppointmentNavigationCallback, RequestAppointmentDialog.RequestAppointmentCallback {

    private AppointmentsResultModel appointmentsDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_navigation);
        toolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.toolbar);
        displayToolbar(true, null);

        drawer = (DrawerLayout) findViewById(com.carecloud.carepaylibrary.R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(com.carecloud.carepaylibrary.R.id.nav_view);
        // get handler to navigation drawer's user id text view
        appointmentsDrawerUserIdTextView = (TextView) navigationView.getHeaderView(0)
                .findViewById(com.carecloud.carepaylibrary.R.id.appointmentsDrawerIdTextView);

        appointmentsDTO = getConvertedDTO(AppointmentsResultModel.class);

        if (appointmentsDTO.getPayload() != null ){
            try{
                practiceId = appointmentsDTO.getPayload().getPracticePatientIds().get(0).getPracticeId();
                practiceMgmt = appointmentsDTO.getPayload().getPracticePatientIds().get(0).getPracticeManagement();
                patientId = appointmentsDTO.getPayload().getPracticePatientIds().get(0).getPatientId();
                prefix = appointmentsDTO.getPayload().getPracticePatientIds().get(0).getPrefix();
                userId = appointmentsDTO.getPayload().getPracticePatientIds().get(0).getUserId();
                getApplicationPreferences().setPatientId(patientId);
                getApplicationPreferences().setPracticeManagement(practiceMgmt);
                getApplicationPreferences().setPracticeId(practiceId);
                getApplicationPreferences().setUserId(userId);
                getApplicationPreferences().setPrefix(prefix);
            }catch(Exception e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

        }

        setTransitionBalance(appointmentsDTO.getMetadata().getLinks().getPatientBalances());
        setTransitionLogout(appointmentsDTO.getMetadata().getTransitions().getLogout());
        setTransitionProfile(appointmentsDTO.getMetadata().getLinks().getProfileUpdate());
        setTransitionAppointments(appointmentsDTO.getMetadata().getLinks().getAppointments());

        inflateDrawer();
        navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_APPOINTMENTS).setChecked(true);
        gotoAppointmentFragment();
    }

    private void gotoAppointmentFragment() {
        AppointmentsListFragment appointmentsListFragment = new AppointmentsListFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(CarePayConstants.APPOINTMENT_INFO_BUNDLE, gson.toJson(appointmentsDTO));
        appointmentsListFragment.setArguments(bundle);

        navigateToFragment(appointmentsListFragment, false);
    }

    @Override
    public void onBackPressed() {// sign-out from Cognito
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStackImmediate();
            if(getSupportFragmentManager().getBackStackEntryCount()<1){
                displayToolbar(true, null);
            }
        }else {

            if (!HttpConstants.isUseUnifiedAuth() && getAppAuthoriztionHelper().getPool().getUser() != null) {
                getAppAuthoriztionHelper().getPool().getUser().signOut();
                getAppAuthoriztionHelper().setUser(null);
            }
            // finish the app
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }


    private void navigateToFragment(Fragment fragment, boolean addToBackStack){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_main, fragment, fragment.getClass().getSimpleName());
        if(addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }
        transaction.commit();
    }

    @Override
    public void newAppointment() {
        Bundle args = new Bundle();
        DtoHelper.bundleBaseDTO(args, getIntent(), CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, PatientNavigationHelper.class.getSimpleName());

        ChooseProviderFragment chooseProviderFragment = new ChooseProviderFragment();
        chooseProviderFragment.setArguments(args);

        navigateToFragment(chooseProviderFragment, true);
        displayToolbar(false, null);
    }

    @Override
    public void rescheduleAppointment(AppointmentDTO appointmentDTO) {
        AppointmentResourcesItemDTO resourcesItemDTO = new AppointmentResourcesItemDTO();
        resourcesItemDTO.setId(appointmentDTO.getPayload().getResourceId());
        resourcesItemDTO.setProvider(appointmentDTO.getPayload().getProvider());
        ResourcesToScheduleDTO resourcesToSchedule = new ResourcesToScheduleDTO();
        resourcesToSchedule.getPractice().setPracticeId(appointmentDTO.getMetadata().getPracticeId());
        resourcesToSchedule.getPractice().setPracticeMgmt(appointmentDTO.getMetadata().getPracticeMgmt());
        appointmentsDTO.getPayload().getResourcesToSchedule().add(resourcesToSchedule);

        String patientID = appointmentDTO.getPayload().getPatient().getPatientId();
        Gson gson = new Gson();
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PATIENT_ID, patientID);
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(resourcesItemDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(appointmentDTO.getPayload().getVisitType()));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(appointmentsDTO));

        AvailableHoursFragment availableHoursFragment  = new AvailableHoursFragment();
        availableHoursFragment.setArguments(bundle);

        navigateToFragment(availableHoursFragment, true);
        displayToolbar(false, null);
    }

    @Override
    public void availableTimes(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String patientID = appointmentsDTO.getPayload().getPracticePatientIds().get(0).getPatientId(); //TODO this should be updated for multi practice support
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(appointmentResourcesDTO.getResource()));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(visitTypeDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(appointmentsResultModel));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PATIENT_ID, patientID);

        AvailableHoursFragment availableHoursFragment = new AvailableHoursFragment();
        availableHoursFragment.setArguments(bundle);

        navigateToFragment(availableHoursFragment, true);
        displayToolbar(false, null);
    }

    @Override
    public void availableTimes(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String patientID = appointmentsDTO.getPayload().getPracticePatientIds().get(0).getPatientId(); //TODO this should be updated for multi practice support
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PATIENT_ID, patientID);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(appointmentResource));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(visitTypeDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(appointmentsResultModel));

        AvailableHoursFragment availableHoursFragment = new AvailableHoursFragment();
        availableHoursFragment.setArguments(bundle);

        navigateToFragment(availableHoursFragment, false);
        displayToolbar(false, null);
    }

    @Override
    public void selectDate(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(appointmentResource));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(visitTypeDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(appointmentsResultModel));

        AppointmentDateRangeFragment appointmentDateRangeFragment = new AppointmentDateRangeFragment();
        appointmentDateRangeFragment.setArguments(bundle);

        navigateToFragment(appointmentDateRangeFragment, true);
        displayToolbar(false, null);
    }


    @Override
    public void onAppointmentRequestSuccess() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        for(int i=0; i<backStackCount; i++){
            fragmentManager.popBackStackImmediate();
        }
        displayToolbar(true, null);
    }
}
