package com.carecloud.carepaylibray.demographics;

import android.support.v4.app.FragmentManager;

import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.InsuranceEditDialog;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;
import com.carecloud.carepaylibray.demographics.misc.CheckinDemographicsInterface;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;
import com.carecloud.carepaylibray.demographics.misc.DemographicsReviewLabelsHolder;
import com.carecloud.carepaylibray.medications.fragments.MedicationAllergySearchFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;

/**
 * Created by cocampo on 4/3/17.
 */

public interface DemographicsPresenter extends DemographicsReviewLabelsHolder,
        DemographicsLabelsHolder,
        HealthInsuranceFragment.InsuranceDocumentScannerListener,
        MedicationsAllergyFragment.MedicationAllergyCallback,
        CheckinDemographicsInterface,
        MedicationAllergySearchFragment.MedicationAllergySearchCallback,
        PaymentNavigationCallback,
        CheckInDemographicsBaseFragment.CheckInNavListener,
        PersonalInfoFragment.UpdateProfilePictureListener,
        CarePayCameraCallback,
        CarePayCameraReady,
        InsuranceEditDialog.InsuranceEditDialogListener {

    void onStop();

    void onDestroy();

    FragmentManager getSupportFragmentManager();
}
