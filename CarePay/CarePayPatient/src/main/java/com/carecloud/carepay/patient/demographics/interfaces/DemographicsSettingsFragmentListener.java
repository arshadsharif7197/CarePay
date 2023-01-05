package com.carecloud.carepay.patient.demographics.interfaces;

import com.carecloud.carepay.patient.demographics.fragments.settings.TwoFactorAuthFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 2/05/17.
 */

public interface DemographicsSettingsFragmentListener extends FragmentActivityInterface {

    void displayUpdateEmailFragment();

    void displayUpdatePasswordFragment();

    void displayUpdateNameFragment();

    void displayTwoFactorAuthFragment(TwoFactorAuthFragment twoFactorAuthFragment, boolean b);

    void editInsurance(DemographicDTO demographicDTO, int position);

    void displayAddCreditCardFragment();

    void displayCreditCardDetailsFragment(DemographicsSettingsCreditCardsPayloadDTO creditCardsPayloadDTO);

    void displayHelpFragment();

    void showSupportFragment();

    void onCreditCardOperation(DemographicDTO demographicsSettingsDTO);

    void logOut();
}
