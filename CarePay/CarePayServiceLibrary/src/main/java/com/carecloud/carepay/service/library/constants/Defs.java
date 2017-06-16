package com.carecloud.carepay.service.library.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lmenendez on 6/12/17
 */

public class Defs {

    public static final int NAVIGATE_CHECKIN = 0x111;
    public static final int NAVIGATE_APPOINTMENT = 0x222;
    public static final int NAVIGATE_CHECKOUT = 0x333;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NAVIGATE_CHECKIN, NAVIGATE_APPOINTMENT, NAVIGATE_CHECKOUT})
    public @interface AppointmentNavigationTypeDef{}


}
