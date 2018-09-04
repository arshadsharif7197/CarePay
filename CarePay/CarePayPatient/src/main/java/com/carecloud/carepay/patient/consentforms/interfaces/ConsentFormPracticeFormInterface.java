package com.carecloud.carepay.patient.consentforms.interfaces;

import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

import java.util.List;

/**
 * @author pjohnson on 3/05/18.
 */
public interface ConsentFormPracticeFormInterface extends FragmentActivityInterface {
    void showForms(List<PracticeForm> selectedForms, List<ConsentFormUserResponseDTO> formsResponses, int selectedProviderIndex, boolean showSignButton);
}
