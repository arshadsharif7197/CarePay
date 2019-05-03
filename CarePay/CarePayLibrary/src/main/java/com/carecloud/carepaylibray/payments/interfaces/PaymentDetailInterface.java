package com.carecloud.carepaylibray.payments.interfaces;

import android.support.v4.app.Fragment;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * @author pjohnson on 29/05/17.
 */

public interface PaymentDetailInterface extends ResponsibilityPaymentInterface {

    void navigateToFragment(Fragment fragment, boolean addToBackStack);

}
