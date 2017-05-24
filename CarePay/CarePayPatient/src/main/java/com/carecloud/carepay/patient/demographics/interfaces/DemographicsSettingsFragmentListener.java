package com.carecloud.carepay.patient.demographics.interfaces;

import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 2/05/17.
 */

public interface DemographicsSettingsFragmentListener extends FragmentActivityInterface {
    void displayEditProfileFragment();

    void displayUpdateEmailFragment();

    void displayUpdatePasswordFragment();

    void displayUpdateNameFragment();

    void displayDemographicsFragment();

    void displayExpandedDemographicsFragment();

    void displayDocumentsFragment();

    void displayCreditCardListFragment();

    void displayAddCreditCardFragment();

    void displayCreditCardDetailsFragment(DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO);

    void displayHelpFragment();

    void showSupportFragment();

    void onCreditCardOperation(DemographicsSettingsDTO demographicsSettingsDTO);
}
