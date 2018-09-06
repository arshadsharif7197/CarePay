package com.carecloud.carepay.patient.consentforms.interfaces;

import com.carecloud.carepaylibray.consentforms.models.UserFormDTO;

/**
 * @author pjohnson on 6/04/18.
 */
public interface ConsentFormsProviderInterface {

    void onProviderSelected(UserFormDTO practiceForm, int position);
}
