package com.carecloud.carepay.patient.appointments.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;

import com.carecloud.carepay.patient.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepay.patient.appointments.presenter.PatientAppointmentPresenter;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.List;

public class AppointmentsActivity extends MenuPatientActivity implements AppointmentViewHandler {

    private AppointmentsResultModel appointmentsResultModel;
    private PatientAppointmentPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setContentView(com.carecloud.carepaylibrary.R.layout.activity_navigation);
//        toolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.toolbar);
//
//        drawer = (DrawerLayout) findViewById(com.carecloud.carepaylibrary.R.id.drawer_layout);
//        navigationView = (NavigationView) findViewById(com.carecloud.carepaylibrary.R.id.nav_view);
//        // get handler to navigation drawer's user id text view
//        appointmentsDrawerUserIdTextView = (TextView) navigationView.getHeaderView(0)
//                .findViewById(com.carecloud.carepaylibrary.R.id.appointmentsDrawerIdTextView);

        appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
        if (appointmentsResultModel.getPayload() != null) {
            List<PracticePatientIdsDTO> practicePatientIds = appointmentsResultModel.getPayload().getPracticePatientIds();
            if (!practicePatientIds.isEmpty()) {
                getApplicationPreferences().writeObjectToSharedPreference(CarePayConstants.KEY_PRACTICE_PATIENT_IDS, appointmentsResultModel.getPayload().getPracticePatientIds());
            }
        }

        setTransitionBalance(appointmentsResultModel.getMetadata().getLinks().getPatientBalances());
        setTransitionLogout(appointmentsResultModel.getMetadata().getTransitions().getLogout());
        setTransitionProfile(appointmentsResultModel.getMetadata().getLinks().getProfileUpdate());
        setTransitionAppointments(appointmentsResultModel.getMetadata().getLinks().getAppointments());
        setTransitionNotifications(appointmentsResultModel.getMetadata().getLinks().getNotifications());

        String userImageUrl = appointmentsResultModel.getPayload().getDemographicDTO().getPayload()
                .getPersonalDetails().getProfilePhoto();
        if (userImageUrl != null) {
            getApplicationPreferences().setUserPhotoUrl(userImageUrl);
        }

        super.onCreate(savedInstanceState);//hold off on calling super until imageURL can be stored to shared pref

//        inflateDrawer();
        initPresenter();
        gotoAppointmentFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_APPOINTMENTS).setChecked(true);
        displayToolbar(true, navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_APPOINTMENTS).getTitle().toString());
    }

    private void gotoAppointmentFragment() {
        AppointmentsListFragment appointmentsListFragment = new AppointmentsListFragment();
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putString(CarePayConstants.APPOINTMENT_INFO_BUNDLE, gson.toJson(appointmentsResultModel));
        appointmentsListFragment.setArguments(bundle);

        navigateToFragment(appointmentsListFragment, false);
    }

    private void initPresenter(){
        this.presenter = new PatientAppointmentPresenter(this, appointmentsResultModel);
    }

    @Override
    public void onBackPressed() {// sign-out from Cognito
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
                displayToolbar(true, null);
            }
        } else {

            if (!HttpConstants.isUseUnifiedAuth() && getAppAuthorizationHelper().getPool().getUser() != null) {
                getAppAuthorizationHelper().getPool().getUser().signOut();
                getAppAuthorizationHelper().setUser(null);
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

    @Override
    public AppointmentPresenter getAppointmentPresenter() {
        return presenter;
    }

    @Override
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
        displayToolbar(false, null);
    }

    @Override
    public void confirmAppointment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            fragmentManager.popBackStackImmediate();
        }
        displayToolbar(true, null);

        refreshAppointments();
        showAppointmentConfirmation();
    }

    @Override
    public void refreshAppointments(){
        AppointmentsListFragment fragment = (AppointmentsListFragment)
                getSupportFragmentManager().findFragmentById(R.id.container_main);

        if (fragment != null) {
            fragment.refreshAppointmentList();
        }

    }

    private void showAppointmentConfirmation() {
        String appointmentRequestSuccessMessage = Label.getLabel("appointment_request_success_message_HTML");
        SystemUtil.showSuccessToast(getContext(), appointmentRequestSuccessMessage);
    }

}
