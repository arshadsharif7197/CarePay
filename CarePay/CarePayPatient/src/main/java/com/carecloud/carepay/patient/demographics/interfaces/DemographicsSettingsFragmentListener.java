package com.carecloud.carepay.patient.demographics.interfaces;

import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 2/05/17.
 */

public interface DemographicsSettingsFragmentListener extends FragmentActivityInterface {
    void initializeCreditCardListFragment();

    void showHelpFragment();

    void showSupportFragment();
}
