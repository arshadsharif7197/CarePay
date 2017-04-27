package com.carecloud.carepay.practice.library.patientmodecheckin;

import android.os.Bundle;

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

    private boolean shouldHandleHomeButton = false;
    private PatientModeCheckinDTO patientModeCheckinDTO;
    private ISession session;
    private DemographicsView demographicsView;

    /**
     * Default Constructor
     * @param demographicsView demographics View
     * @param savedInstanceState bundle
     * @param session application session
     */
    public PatientModeDemographicsPresenter(DemographicsView demographicsView, Bundle savedInstanceState, ISession session){
        super(demographicsView, savedInstanceState, true);
        this.session = session;
        this.demographicsView = demographicsView;

        if(session.getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE){
            //need to switch to PatientMode
            patientModeCheckinDTO = demographicsView.getConvertedDTO(PatientModeCheckinDTO.class);
            if(patientModeCheckinDTO != null) {
                session.getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
                String username = patientModeCheckinDTO.getPayload().getCheckinModeDTO().getMetadata().getUsername();
                session.getAppAuthorizationHelper().setUser(username);

                shouldHandleHomeButton = true;
            }
        }

    }


    /**
     * Callback if Presenter will handle the home button
     * @return true if Presenter will handle home button
     */
    public boolean handleHomeButtonClick(){
        if(shouldHandleHomeButton){
            exitToPatientHome();
            return true;
        }
        return false;
    }

    @Override
    protected boolean shouldPreventBackNav(){
        return getCurrentStep() == 1;
    }

    private void exitToPatientHome(){
        if(patientModeCheckinDTO != null){
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
