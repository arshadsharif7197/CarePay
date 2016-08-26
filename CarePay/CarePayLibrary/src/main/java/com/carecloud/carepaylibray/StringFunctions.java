package com.carecloud.carepaylibray;


public class StringFunctions {

    /**
     * Determines if the specified String object is null or equal to
     * an empty string.
     * @param string the string to evaluate
     * @return true if object <code>string</code> is null or equal to
     * an empty string; false otherwise
     */
    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.equals(""));
    }
}
