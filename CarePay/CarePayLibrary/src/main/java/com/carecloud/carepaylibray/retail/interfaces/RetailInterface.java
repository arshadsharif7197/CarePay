package com.carecloud.carepaylibray.retail.interfaces;

import android.os.Bundle;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.interfaces.ChooseCreditCardInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.retail.models.RetailModel;
import com.carecloud.carepaylibray.retail.models.RetailPracticeDTO;

/**
 * Created by lmenendez on 11/20/17
 */

public interface RetailInterface extends PaymentInterface, ChooseCreditCardInterface,
        FragmentActivityInterface, PaymentMethodInterface {

    void displayRetailStore(RetailModel retailModel, RetailPracticeDTO retailPractice, UserPracticeDTO practiceDTO);

    void displayToolbar(boolean visibility);

    PaymentsModel getPaymentModel();

    Bundle getWebViewBundle();
}
