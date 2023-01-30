package com.carecloud.shamrocksdk.payment.models.defs;



import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Execution types for Shamrock Payments
 */

public final class ExecDef {
    /**
     * Clover Execution Constant
     */
    public static final String EXECUTION_CLOVER = "clover";

    @StringDef({EXECUTION_CLOVER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExecutionType{}


}
