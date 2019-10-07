package com.carecloud.carepaylibray.payments.interfaces;

import androidx.fragment.app.Fragment;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentDetailInterface extends ResponsibilityPaymentInterface {

    void navigateToFragment(Fragment fragment, boolean addToBackStack);

}
