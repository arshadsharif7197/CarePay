package com.carecloud.carepay.patient.demographics;

import android.os.Bundle;

import com.carecloud.carepay.patient.demographics.fragments.DemographicsAllSetFragment;
import com.carecloud.carepaylibray.demographics.DemographicsPresenterImpl;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.fragments.AddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.IdentificationFragment;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;

public class NewDemographicsPresenterImpl extends DemographicsPresenterImpl {
    /**
     * @param demographicsView   Demographics View
     * @param savedInstanceState Bundle
     * @param isPatientMode      true if Practice App - Patient Mode
     */
    public NewDemographicsPresenterImpl(DemographicsView demographicsView, Bundle savedInstanceState, boolean isPatientMode) {
        super(demographicsView, savedInstanceState, isPatientMode);
    }

    @Override
    protected String getHealthInsuranceFragmentTag() {
        return NewHealthInsuranceFragment.class.getName();
    }

    @Override
    protected CheckInDemographicsBaseFragment getDemographicFragment(int step) {
        switch (step){
            case 1:
                return new NewPersonalInfoFragment();
            case 2:
                return new NewAddressFragment();
            case 3:
                return new NewDemographicsFragment();
            case 4:
                return new NewIdentificationFragment();
            case 5:
                return new NewHealthInsuranceFragment();
            default:
                return new DemographicsAllSetFragment();
        }
    }

    public static class NewPersonalInfoFragment extends PersonalInfoFragment {
        @Override
        protected void openNextFragment(DemographicDTO demographicDTO, boolean transition) {
            checkinFlowCallback.applyChangesAndNavTo(demographicDTO, 2);
        }
    }

    public static class NewAddressFragment extends AddressFragment {
        @Override
        protected void openNextFragment(DemographicDTO demographicDTO, boolean transition) {
            checkinFlowCallback.applyChangesAndNavTo(demographicDTO, 3);
        }
    }

    public static class NewDemographicsFragment extends DemographicsFragment {
        @Override
        protected void openNextFragment(DemographicDTO demographicDTO, boolean transition) {
            checkinFlowCallback.applyChangesAndNavTo(demographicDTO, 4);
        }
    }

    public static class NewIdentificationFragment extends IdentificationFragment {
        @Override
        protected void openNextFragment(DemographicDTO demographicDTO, boolean transition) {
            checkinFlowCallback.applyChangesAndNavTo(demographicDTO, 5);
        }
    }

    public static class NewHealthInsuranceFragment extends HealthInsuranceFragment {
        @Override
        protected void openNextFragment(DemographicDTO demographicDTO, boolean transition) {
            checkinFlowCallback.applyChangesAndNavTo(demographicDTO, 6);
        }
    }
}
