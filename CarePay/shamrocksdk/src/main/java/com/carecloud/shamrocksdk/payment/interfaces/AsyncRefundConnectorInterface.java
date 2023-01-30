package com.carecloud.shamrocksdk.payment.interfaces;

import com.carecloud.shamrocksdk.payment.activities.CloverRefundActivity;

public interface AsyncRefundConnectorInterface {
    CloverRefundActivity getConnectorActivity();
}
