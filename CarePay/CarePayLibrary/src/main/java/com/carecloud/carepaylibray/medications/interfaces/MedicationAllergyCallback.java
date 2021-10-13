package com.carecloud.carepaylibray.medications.interfaces;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;

public interface MedicationAllergyCallback {
    void showMedicationAllergySearchFragment(int searchType);
    void showMedicationAllergySearchFragment(int searchType, MedicationsAllergiesResultsModel medicationsAllergiesDTO);

    void addMedicationAllergyItem(MedicationsAllergiesObject item);

    void promptAddUnlisted(MedicationsAllergiesObject item, int mode);

}
