package com.carecloud.carepaylibray.payments.utils;

/**
 * Created by sudhir_pingale on 12/13/2016.
 */

public class CreditCardUtil {

    private CreditCardUtil(){

    }

    // VISA
    public static final String VISA = "4[0-9]{12}(?:[0-9]{3})?";
    public static final String VISA_VALID = "^4[0-9]{12}(?:[0-9]{3})?$";

    // MasterCard
    public static final String MASTERCARD = "^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$";
    public static final String MASTERCARD_SHORT = "^(?:222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)";
    public static final String MASTERCARD_SHORTER = "^(?:5[1-5])";
    public static final String MASTERCARD_VALID = "^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$";

    // American Express
    public static final String AMERICAN_EXPRESS = "^3[47][0-9]{0,13}";
    public static final String AMERICAN_EXPRESS_VALID = "^3[47][0-9]{13}$";

    // DISCOVER
    public static final String DISCOVER = "^6(?:011|5[0-9]{1,2})[0-9]{0,12}";
    public static final String DISCOVER_SHORT = "^6(?:011|5)";
    public static final String DISCOVER_VALID = "^6(?:011|5[0-9]{2})[0-9]{12}$";

    // JCB
    public static final String JCB = "^(?:2131|1800|35\\d{0,3})\\d{0,11}$";
    public static final String JCB_SHORT = "^2131|1800";
    public static final String JCB_VALID = "^(?:2131|1800|35\\d{3})\\d{11}$";

    // Discover
    public static final String DINERS_CLUB = "^3(?:0[0-5]|[68][0-9])[0-9]{11}$";
    public static final String DINERS_CLUB_SHORT = "^30[0-5]";
    public static final String DINERS_CLUB_VALID = "^3(?:0[0-5]|[68][0-9])[0-9]{11}$";



    public static String getCreditCardType(String cardNumber) {
        String type;
        if (cardNumber.startsWith("4") || cardNumber.matches(CreditCardUtil.VISA)) {
            type = "Visa";
        } else if (cardNumber.matches(CreditCardUtil.MASTERCARD_SHORTER) || cardNumber.matches(CreditCardUtil
                .MASTERCARD_SHORT) || cardNumber.matches(CreditCardUtil.MASTERCARD)) {
            type = "Mastercard";
        } else if (cardNumber.matches(CreditCardUtil.AMERICAN_EXPRESS)) {
            type = "American Express";
        } else if (cardNumber.matches(CreditCardUtil.DISCOVER_SHORT)
                || cardNumber.matches(CreditCardUtil.DISCOVER)) {
            type = "Discover";
        } else if (cardNumber.matches(CreditCardUtil.JCB_SHORT) || cardNumber.matches(CreditCardUtil.JCB)) {
            type = "JCB";
        } else if (cardNumber.matches(CreditCardUtil.DINERS_CLUB_SHORT)
                || cardNumber.matches(CreditCardUtil.DINERS_CLUB)) {
            type = "Diners Club";
        } else {
            type = null;
        }
        return type;
    }



    /**
     * Is valid boolean.
     *
     * @return the boolean
     */
    public static boolean isCreditCardNumberValid(String cardNumber) {
        if (cardNumber.matches(CreditCardUtil.VISA_VALID)) {
            return true;
        }
        if (cardNumber.matches(CreditCardUtil.MASTERCARD_VALID)) {
            return true;
        }
        if (cardNumber.matches(CreditCardUtil.AMERICAN_EXPRESS_VALID)) {
            return true;
        }
        if (cardNumber.matches(CreditCardUtil.DISCOVER_VALID)) {
            return true;
        }
        if (cardNumber.matches(CreditCardUtil.DINERS_CLUB_VALID)) {
            return true;
        }
        if (cardNumber.matches(CreditCardUtil.JCB_VALID)) {
            return true;
        }
        return false;
    }
}
