package com.carecloud.carepay.service.library.constants;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lmenendez on 6/12/17
 */

public class Defs {

    public static final int NAVIGATE_CHECKIN = 0x111;
    public static final int NAVIGATE_APPOINTMENT = 0x222;
    public static final int NAVIGATE_CHECKOUT = 0x333;
    public static final int CHOOSE_PARTNERS_REQUEST = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NAVIGATE_CHECKIN, NAVIGATE_APPOINTMENT, NAVIGATE_CHECKOUT})
    public @interface AppointmentNavigationTypeDef {
    }


    public static final String START_PM_CARECLOUD = "carecloud";
    public static final String START_PM_EAGLE = "eagle";
    public static final String START_PM_TALKEHR = "talkehr";

    public static final String partnersInfo = "partnersList";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({START_PM_CARECLOUD, START_PM_EAGLE, START_PM_TALKEHR})
    public @interface PracticeManagementDef {
    }
}
