package com.carecloud.carepay.practice.tablet.payments;

import android.os.Bundle;

import com.carecloud.carepay.practice.tablet.R;
import com.carecloud.carepaylibray.base.BaseActivity;

/**
 * Payment activity class for tablet mode
 */

public class TabletPaymentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_payment);
    }
}
