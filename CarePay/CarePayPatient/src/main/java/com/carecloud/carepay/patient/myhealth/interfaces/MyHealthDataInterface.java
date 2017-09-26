package com.carecloud.carepay.patient.myhealth.interfaces;

import com.carecloud.carepay.patient.myhealth.dtos.AllergyDto;
import com.carecloud.carepay.patient.myhealth.dtos.LabDto;
import com.carecloud.carepay.patient.myhealth.dtos.MedicationDto;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;

/**
 * @author pjohnson on 18/07/17.
 */

public interface MyHealthDataInterface {

    void onSeeAllFullMedicalRecordClicked(ProviderDTO provider);

    void onProviderClicked(ProviderDTO provider);

    void onAllergyClicked(AllergyDto allergy);

    void addAllergy();

    void onMedicationClicked(MedicationDto medication);

    void addMedication();

    void onLabClicked(LabDto lab);
}
