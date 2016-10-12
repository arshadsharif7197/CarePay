package com.carecloud.carepaylibray.utils;


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

    /**format phone number
     * @param phoneNumber
     * @return
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
     * @param zipcode zipcode
     * @return
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
}
