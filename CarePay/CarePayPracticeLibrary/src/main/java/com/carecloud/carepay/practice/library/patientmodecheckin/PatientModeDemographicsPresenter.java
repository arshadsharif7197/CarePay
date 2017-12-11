package com.carecloud.carepay.practice.library.patientmodecheckin;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.carecloud.carepaylibray.demographics.fragments.HomeAlertDialogFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.models.PatientModeCheckinDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.demographics.DemographicsPresenterImpl;
import com.carecloud.carepaylibray.demographics.DemographicsView;

/**
 * Created by lmenendez on 4/25/17.
 */

public class PatientModeDemographicsPresenter extends DemographicsPresenterImpl {
    public static final String KEY_HANDLE_HOME = "handle_home_button";

    private boolean shouldHandleHomeButton = false;
    private PatientModeCheckinDTO patientModeCheckinDTO;
    private ISession session;
    private DemographicsView demographicsView;

    /**
     * Default Constructor
     *
     * @param demographicsView   demographics View
     * @param savedInstanceState bundle
     * @param session            application session
     */
    public PatientModeDemographicsPresenter(DemographicsView demographicsView, Bundle savedInstanceState, ISession session) {
        super(demographicsView, savedInstanceState, true);
        this.session = session;
        this.demographicsView = demographicsView;

        if (savedInstanceState != null) {
            shouldHandleHomeButton = savedInstanceState.getBoolean(KEY_HANDLE_HOME, false);
        }

        String username = null;
        if (session.getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
            //need to switch to PatientMode
            session.getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
            shouldHandleHomeButton = true;
        } else {
            username = session.getAppAuthorizationHelper().getPatientUser();
        }

        patientModeCheckinDTO = demographicsView.getConvertedDTO(PatientModeCheckinDTO.class);
        if (patientModeCheckinDTO != null) {
            if (patientModeCheckinDTO.getPayload().getCheckinModeDTO().getMetadata().getUsername() != null) {
                username = patientModeCheckinDTO.getPayload().getCheckinModeDTO().getMetadata().getUsername();
                session.getAppAuthorizationHelper().setUser(username);
            }
        }

        if (username == null) {
            //bail out of this
            demographicsView.showErrorNotification("Error creating patient log-in");
            exitToPatientHome();
        }


    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        icicle.putBoolean(KEY_HANDLE_HOME, shouldHandleHomeButton);
    }


    /**
     * Callback if Presenter will handle the home button
     *
     * @return true if Presenter will handle home button
     */
    public boolean handleHomeButtonClick() {
        if (shouldHandleHomeButton) {
            showHomeAlertDialog();
            return true;
        }
        return false;
    }

    private void showHomeAlertDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HomeAlertDialogFragment homeAlertDialogFragment = HomeAlertDialogFragment.newInstance(null, null);
        homeAlertDialogFragment.setCallback(new HomeAlertDialogFragment.HomeAlertInterface() {
            @Override
            public void onAcceptExit() {
                exitToPatientHome();
            }
        });
        String tag = homeAlertDialogFragment.getClass().getName();
        homeAlertDialogFragment.show(ft, tag);
    }

    @Override
    protected boolean shouldPreventBackNav() {
        return getCurrentStep() == 1;
    }

    private void exitToPatientHome() {
        if (patientModeCheckinDTO != null) {
            TransitionDTO transitionDTO = patientModeCheckinDTO.getMetaData().getTransitions().getPatientHome();
            session.getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
                @Override
                public void onPreExecute() {
                    session.showProgressDialog();
                }

                @Override
                public void onPostExecute(WorkflowDTO workflowDTO) {
                    session.hideProgressDialog();
                    demographicsView.navigateToWorkflow(workflowDTO);
                }

                @Override
                public void onFailure(String exceptionMessage) {
                    session.hideProgressDialog();
                    session.showErrorNotification(exceptionMessage);
                }
            });
        }
    }

}
