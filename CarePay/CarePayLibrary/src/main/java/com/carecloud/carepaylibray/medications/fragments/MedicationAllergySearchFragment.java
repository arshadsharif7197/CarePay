package com.carecloud.carepaylibray.medications.fragments;

import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationAllergySearchFragment extends BaseDialogFragment {

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;

    public interface MedicationAllergySearchCallback {
        void addMedication(MedicationsAllergiesObject item);
        void addAllergy(MedicationsAllergiesObject item);
    }



}
