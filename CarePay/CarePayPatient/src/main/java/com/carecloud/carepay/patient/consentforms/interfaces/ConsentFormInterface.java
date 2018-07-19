package com.carecloud.carepay.patient.consentforms.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

import java.util.List;

/**
 * @author pjohnson on 3/05/18.
 */
public interface ConsentFormInterface extends FragmentActivityInterface {

    List<PracticeForm> getAllFormsToShow();

    void showAllDone(WorkflowDTO workflowDTO);
}
