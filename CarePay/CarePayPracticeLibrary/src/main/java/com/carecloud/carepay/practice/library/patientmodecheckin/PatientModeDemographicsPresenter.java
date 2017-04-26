package com.carecloud.carepay.practice.library.patientmodecheckin;

import android.os.Bundle;

import com.carecloud.carepay.practice.library.patientmodecheckin.models.PatientModeCheckinDTO;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibray.base.IApplicationSession;
import com.carecloud.carepaylibray.demographics.DemographicsPresenterImpl;
import com.carecloud.carepaylibray.demographics.DemographicsView;

/**
 * Created by lmenendez on 4/25/17.
 */

public class PatientModeDemographicsPresenter extends DemographicsPresenterImpl {

    private boolean shouldHandleHomeButton = false;

    /**
     * Default Constructor
     * @param demographicsView demographics View
     * @param savedInstanceState bundle
     * @param applicationSession application session
     */
    public PatientModeDemographicsPresenter(DemographicsView demographicsView, Bundle savedInstanceState, IApplicationSession applicationSession){
        super(demographicsView, savedInstanceState, true);

        if(applicationSession.getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE){
            //need to switch to PatientMode
            PatientModeCheckinDTO patientModeCheckinDTO = demographicsView.getConvertedDTO(PatientModeCheckinDTO.class);
            if(patientModeCheckinDTO != null) {
                applicationSession.getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
                String username = patientModeCheckinDTO.getPayloadDTO().getCheckinModeDTO().getMetadata().getUsername();
                applicationSession.getAppAuthorizationHelper().setUser(username);

                shouldHandleHomeButton = true;
            }
        }

    }


    public boolean handleHomeButtonClick(){
        if(shouldHandleHomeButton){
            // TODO: handle home button
            return true;
        }
        return false;
    }

    @Override
    protected boolean shouldPreventBackNav(){
        return getCurrentStep() == 1;
    }

}
