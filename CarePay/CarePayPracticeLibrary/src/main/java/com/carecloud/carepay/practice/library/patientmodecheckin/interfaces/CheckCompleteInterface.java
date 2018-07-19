package com.carecloud.carepay.practice.library.patientmodecheckin.interfaces;

import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 8/06/17.
 */

public interface CheckCompleteInterface extends FragmentActivityInterface {
    void logout();

    void showConfirmationPinDialog();

    void goToShop();
}
