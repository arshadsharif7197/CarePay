package com.carecloud.carepaylibray.utils;


import android.text.Editable;

import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static final String PHONE_NUMBER_REGEX = "([\\+(]?(\\d){2,}[)]?[- \\.]?(\\d){2,}[- \\.]?(\\d){2,}[- \\.]?(\\d){2,}[- \\.]?(\\d){2,})|([\\+(]?(\\d){2,}[)]?[- \\.]?(\\d){2,}[- \\.]?(\\d){2,}[- \\.]?(\\d){2,})|([\\+(]?(\\d){2,}[)]?[- \\.]?(\\d){2,}[- \\.]?(\\d){2,})";
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    static private final String PASWWORD_REGEX_VALIDATION
            = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!%*?&_-])[A-Za-z\\d$@!%*?&_-]{8,}";

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

    /**
     * Utility to determine if an Editable is null or empty
     * @param editable The editable
     * @return Whether null or empty
     */
    public static boolean isNullOrEmpty(Editable editable) {
        return (editable == null || editable.length() == 0);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static boolean isValidmail(String email) {
        if (email != null) {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
        return false;
    }

    /**
     * Test if a password respect the standard pattern, id, at least 8 chars,
     * at least 1 number, 1 upper case, 1 lower case, 1 special character.
     * @param password The passwrod as a string
     * @return Whether the password matches the pattern.
     */
    public static boolean isValidPassword(String password) {
        if(password != null) {
            Pattern pattern = Pattern.compile(PASWWORD_REGEX_VALIDATION);
            Matcher matcher = pattern.matcher(password);
            return matcher.matches();
        }
        return false;
    }

    /**
     * Capitalize each first letter after a space
     * @param source The "sentence" (a string containing spaces)
     * @return The string with capitalized first letters
     */
    public static String captialize(String source) {

        source = source.toLowerCase();
        StringBuilder res = new StringBuilder();

        String[] strArr = source.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }

        return res.toString();
    }

    /**
     * Utility to remove any formatting from zip
     * @param formattedZipCode The zip code
     * @return The unformatted zip code as String
     */
    public static String revertZipToRawFormat(String formattedZipCode) {
        return formattedZipCode.replace("-", "");
    }

    /**
     * Utility to remove any formatting from phone
     * @param formattedPhoneNum The phone
     * @return The unformatted phone as String
     */
    public static String revertToRawPhoneFormat(String formattedPhoneNum) {
        return formattedPhoneNum.replace("-", "");
    }

    /**format phone number
     * @param phoneNumber phonenumber as a string
     * @return formated string
     */
    public static String formatPhoneNumber(String phoneNumber)
    {
        StringBuilder  phoneNumberString = new StringBuilder();
        phoneNumberString.append(phoneNumber);
        if (phoneNumberString.length() > 0)
        {
            if (phoneNumberString.length() == 3 || phoneNumberString.length() == 7)
                phoneNumberString.append("-");
            if (phoneNumberString.length() > 3)
            {
                if (Character.isDigit(phoneNumberString.charAt(3)))
                    phoneNumberString.insert(3, "-");
            }
            if (phoneNumberString.length() > 7) {
                if (Character.isDigit(phoneNumberString.charAt(7)))
                    phoneNumberString.insert(7, "-");
            }
        }
        return phoneNumberString.toString();
    }

    /** format zipcode
     * @param zipcode zipcode as a string
     * @return formated string
     */
    public static String formatZipCode(String zipcode)
    {
        StringBuilder zipCodeString = new StringBuilder();
        zipCodeString.append(zipcode);
            if (zipCodeString.length() > 0 && zipCodeString.length() > 5 && Character.isDigit(zipCodeString.charAt(5))) {
                zipCodeString.insert(5, "-");
            }

        return zipCodeString.toString();
    }

    /**
     * Util to auto-format a edit text holding a phone
     * @param editable The editable passed in the text watcher of that edit
     */
    public static void autoFormatPhone(Editable editable) {
        // password auto-complete functionality
        if (!StringUtil.isNullOrEmpty(editable)) {
            int len = editable.length();
            char lastChar = editable.charAt(len - 1);
            if ((len == 4 || len == 8) && lastChar != '-') {
                editable.replace(len - 1, len, "-"); // add '-'
            } else {
                if (len > 12) {
                    editable.replace(len - 1, len, ""); // discard
                }
            }
        }
    }

    /**
     * Util to auto-format a edit text holding a zip
     * @param editable The editable passed in the text watcher of that edit
     */
    public static void autoFormatZipcode(Editable editable) {
        if (!StringUtil.isNullOrEmpty(editable)) {
            int len = editable.length();
            char lastChar = editable.toString().charAt(len - 1);
            if (len == 6) {
                if (lastChar != '-') {
                    // remove
                    editable.replace(len - 1, len, "-");
                }
            } else {
                if (len > 10) {
                    editable.replace(len - 1, len, "");
                }
            }
        }
    }

    public static String onShortDrName(String fullName) {
        if (fullName != null && fullName.length() > 1) {
            String stringSplitArr[] = fullName.split(" ");
            if (stringSplitArr.length >= 3)
                return String.valueOf(stringSplitArr[1].charAt(0)).toUpperCase() + String.valueOf(stringSplitArr[stringSplitArr.length - 1].charAt(0)).toUpperCase();
            else if (stringSplitArr.length == 2)
                return String.valueOf(stringSplitArr[1].charAt(0)).toUpperCase();
            else
                return "";
        } else
            return "";
    }

    /**
     * Returns label for view.
     * @param label string received from endpoint
     * @return label for view
     */
    public static String getLabelForView(String label) {
        if (isNullOrEmpty(label)) {
            return "Not Defined";
        } else {
            return label;
        }
    }
}
