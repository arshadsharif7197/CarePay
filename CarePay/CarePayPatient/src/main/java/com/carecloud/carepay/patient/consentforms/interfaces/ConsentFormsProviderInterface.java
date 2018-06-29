package com.carecloud.carepay.patient.consentforms.interfaces;

import com.carecloud.carepaylibray.consentforms.models.payload.FormDTO;

/**
 * @author pjohnson on 6/04/18.
 */
public interface ConsentFormsProviderInterface {

    void onProviderSelected(FormDTO practiceForm, int position);
}
