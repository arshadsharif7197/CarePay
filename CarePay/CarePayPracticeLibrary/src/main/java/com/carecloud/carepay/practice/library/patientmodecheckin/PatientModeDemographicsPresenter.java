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

    public PatientModeDemographicsPresenter(DemographicsView demographicsView, Bundle savedInstanceState, IApplicationSession applicationSession){
        super(demographicsView, savedInstanceState, true);

        if(applicationSession.getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE){
            //need to switch to PatientMode
            applicationSession.getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
            PatientModeCheckinDTO patientModeCheckinDTO = demographicsView.getConvertedDTO(PatientModeCheckinDTO.class);
            String username = patientModeCheckinDTO.getPayloadDTO().getCheckinModeDTO().getMetadata().getUsername();
            applicationSession.getAppAuthorizationHelper().setUser(username);

        }

    }
}
