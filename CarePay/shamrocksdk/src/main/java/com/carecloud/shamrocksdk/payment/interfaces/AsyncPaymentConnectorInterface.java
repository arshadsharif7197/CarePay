package com.carecloud.shamrocksdk.payment.interfaces;

import com.carecloud.shamrocksdk.payment.activities.CloverPaymentConnectorActivity;

public interface AsyncPaymentConnectorInterface {
    CloverPaymentConnectorActivity getConnectorActivity();
}
