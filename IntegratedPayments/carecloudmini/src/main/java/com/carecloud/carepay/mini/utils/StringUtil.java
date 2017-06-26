package com.carecloud.carepay.mini.utils;

/**
 * Created by lmenendez on 6/23/17
 */

public class StringUtil {

    /**
     * Determines if the specified String object is null or equal to
     * an empty string.
     *
     * @param string the string to evaluate
     * @return true if object <code>string</code> is null or equal to
     * an empty string; false otherwise
     */
    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.trim().equals(""));
    }

    /**
     * Determine if entered string is valid email
     * @param email email string to validate
     * @return true if email matches pattern
     */
    public static boolean isValidEmail(String email) {
        return !isNullOrEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * @param fullName full name
     * @return short two letter name
     */
    public static String getShortName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "";
        }

        String[] splitArr = fullName.split(" ");
        if (splitArr.length == 1) {
            return getFirstChar(splitArr[0]);
        }

        // Check for doctor
        if (fullName.contains(".")) {
            if (splitArr.length > 2) {
                return getFirstChar(splitArr[1]) + getFirstChar(splitArr[splitArr.length - 1]);
            }

            return getFirstChar(splitArr[1]);
        }

        return getFirstChar(splitArr[0]) + getFirstChar(splitArr[splitArr.length - 1]);
    }

    private static String getFirstChar(String word) {
        if (word.isEmpty()) {
            return "";
        }

        return String.valueOf(word.charAt(0)).toUpperCase();
    }

}
