package com.carecloud.carepay.patient.consentforms.interfaces;

import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;

/**
 * @author pjohnson on 6/04/18.
 */
public interface ConsentFormsFormsInterface {

    void onPendingFormSelected(PracticeForm form, boolean isChecked);

    void onFilledFormSelected(PracticeForm form);
}
