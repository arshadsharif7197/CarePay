package com.carecloud.carepay.patient.payment;

import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.google.android.gms.wallet.WalletConstants;

/**
 * Created by kkannan on 12/13/16.
 */

public class PaymentConstants {
    // Environment to use when creating an instance of Wallet.WalletOptions
    public static final int WALLET_ENVIRONMENT = HttpConstants.getEnvironment().equals("Production") ? WalletConstants.ENVIRONMENT_PRODUCTION : WalletConstants.ENVIRONMENT_TEST;
    public static final String ANDROID_PAY = "Android Pay";
    public static final String ANDROID_PAY_PAYMENT_TYPE = "android_pay";
    public static final String MERCHANT_NAME = "First data Corporation";
    public static final String MERCHANT_GATEWAY = "firstdata";

    public static final String ANDROID_PAY_MERCHANT_SERVICE = "PZY";
    public static final String ANDROID_PAY_PAPI_ACCOUNT_TYPE = "payeezy";

    // Intent extra keys
    public static final String EXTRA_ITEM_ID = "com.firstdata.firstapi.androidpay.EXTRA_ITEM_ID";
    public static final String EXTRA_MASKED_WALLET = "com.firstdata.firstapi.androidpay.EXTRA_MASKED_WALLET";

    public static final String EXTRA_AMOUNT = "com.firstdata.firstapi.androidpay.EXTRA_AMOUNT";
    public static final String EXTRA_ENV = "com.firstdata.firstapi.androidpay.EXTRA_ENV";

    public static final String EXTRA_RESULT_STATUS = "com.firstdata.firstapi.androidpay.EXTRA_RESULT_STATUS";
    public static final String EXTRA_RESULT_MESSAGE = "com.firstdata.firstapi.androidpay.EXTRA_RESULT_MESSAGE";

    public static final String CURRENCY_CODE_USD = "USD";

    // values to use with KEY_DESCRIPTION
    public static final String DESCRIPTION_LINE_ITEM_SHIPPING = "Shipping";
    public static final String DESCRIPTION_LINE_ITEM_TAX = "Tax";
    public static final String ANDROID_PAY_SPENDING_LIMIT_EXCEEDED = "Spending Limit Exceeded";

    //  Request Codes
    public static final int REQUEST_CODE_MASKED_WALLET = 1001;
    public static final int REQUEST_CODE_CHANGE_MASKED_WALLET = 1002;
    public static final int REQUEST_CODE_FULL_WALLET = 1003;
    public static final int REQUEST_CODE_GOOGLE_PAYMENT = 1004;

}
