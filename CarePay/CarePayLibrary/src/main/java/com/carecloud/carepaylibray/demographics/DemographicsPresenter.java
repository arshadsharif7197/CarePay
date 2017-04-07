package com.carecloud.carepaylibray.demographics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.InsuranceEditDialog;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;
import com.carecloud.carepaylibray.demographics.misc.CheckinDemographicsInterface;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;
import com.carecloud.carepaylibray.demographics.misc.DemographicsReviewLabelsHolder;
import com.carecloud.carepaylibray.medications.fragments.MedicationAllergySearchFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;

public interface DemographicsPresenter extends DemographicsReviewLabelsHolder,
        DemographicsLabelsHolder,
        HealthInsuranceFragment.InsuranceDocumentScannerListener,
        MedicationsAllergyFragment.MedicationAllergyCallback,
        CheckinDemographicsInterface,
        MedicationAllergySearchFragment.MedicationAllergySearchCallback,
        CheckInDemographicsBaseFragment.CheckInNavListener,
        PersonalInfoFragment.UpdateProfilePictureListener,
        CarePayCameraCallback,
        CarePayCameraReady,
        InsuranceEditDialog.InsuranceEditDialogListener {

    void onSaveInstanceState(Bundle icicle);

    void onStop();

    void onDestroy();

    FragmentManager getSupportFragmentManager();

    DemographicDTO getDemographicDTO();

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    void navigateToFragment(Fragment fragment, boolean addToBackStack) ;

    void setMedicationsAllergiesDto(MedicationsAllergiesResultsModel dto);
}
