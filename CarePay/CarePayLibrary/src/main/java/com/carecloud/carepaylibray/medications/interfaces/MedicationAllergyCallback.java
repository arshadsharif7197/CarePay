package com.carecloud.carepaylibray.medications.interfaces;

import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;

public interface MedicationAllergyCallback {
    void showMedicationAllergySearchFragment(int searchType);

    void addMedicationAllergyItem(MedicationsAllergiesObject item);

    void promptAddUnlisted(MedicationsAllergiesObject item, int mode);

}
