package com.carecloud.carepay.patient.payment.interfaces;

import android.content.Intent;

import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodInterface;
import com.google.android.gms.wallet.MaskedWallet;

/**
 * Created by lmenendez on 10/17/17
 */

public interface PatientPaymentMethodInterface extends PaymentMethodInterface {

    void createAndAddWalletFragment(MaskedWallet maskedWallet);

    void forwardActivityResult(int requestCode, int resultCode, Intent data);
}
