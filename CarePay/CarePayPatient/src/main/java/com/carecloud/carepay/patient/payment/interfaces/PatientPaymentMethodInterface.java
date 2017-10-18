package com.carecloud.carepay.patient.payment.interfaces;

import android.content.Intent;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.google.android.gms.wallet.MaskedWallet;

/**
 * Created by lmenendez on 10/17/17
 */

public interface PatientPaymentMethodInterface extends PaymentMethodInterface {

    void createAndAddWalletFragment(MaskedWallet maskedWallet, Double amount);

    void forwardActivityResult(int requestCode, int resultCode, Intent data);

    UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel);

    void showPaymentConfirmation(WorkflowDTO workflowDTO);

}
