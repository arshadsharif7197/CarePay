package com.carecloud.carepaylibray.utils;

import android.content.Context;
import android.text.Editable;
import android.util.Log;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;

import static com.carecloud.carepaylibray.utils.SystemUtil.isNotEmptyString;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PASSWORD_REGEX_VALIDATION
            = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!%*?&_-])[A-Za-z\\d$@!%*?&_-]{8,}";

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

    public static boolean isValidmail(String email) {
        return !isNullOrEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Test if a password respect the standard pattern, id, at least 8 chars,
     * at least 1 number, 1 upper case, 1 lower case, 1 special character.
     *
     * @param password The passwrod as a string
     * @return Whether the password matches the pattern.
     */
    public static boolean isValidPassword(String password) {
        if (password != null) {
            Pattern pattern = Pattern.compile(PASSWORD_REGEX_VALIDATION);
            Matcher matcher = pattern.matcher(password);
            return matcher.matches();
        }
        return false;
    }


    public static String getFormatedLabal(Context context, String label) {
        return isNullOrEmpty(label) ? context.getString(R.string.not_defined) : label;
    }

    /**
     * Capitalize each first letter after a space
     *
     * @param source The "sentence" (a string containing spaces)
     * @return The string with capitalized first letters
     */
    public static String captialize(String source) {
        if(source == null){
            return "";
        }
        source = source.replaceAll("( ){2,}", " ").toLowerCase();
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
     *
     * @param formattedZipCode The zip code
     * @return The unformatted zip code as String
     */
    public static String revertZipToRawFormat(String formattedZipCode) {
        return formattedZipCode.replace("-", "");
    }

    /**
     * Utility to remove any formatting from phone
     *
     * @param formattedPhoneNum The phone
     * @return The unformatted phone as String
     */
    public static String revertToRawPhoneFormat(String formattedPhoneNum) {
        return formattedPhoneNum.replace("-", "");
    }

    /**
     * format phone number
     *
     * @param phoneNumber phonenumber as a string
     * @return formated string
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if(phoneNumber == null){
            return null;
        }
        StringBuilder phoneNumberString = new StringBuilder();
        phoneNumberString.append(phoneNumber);
        if (phoneNumberString.length() > 0) {
            if (phoneNumberString.length() == 3 || phoneNumberString.length() == 7)
                phoneNumberString.append("-");
            if (phoneNumberString.length() > 3) {
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

    /**
     * format zipcode
     *
     * @param zipcode zipcode as a string
     * @return formated string
     */
    public static String formatZipCode(String zipcode) {
        StringBuilder zipCodeString = new StringBuilder();
        zipCodeString.append(zipcode);
        if (zipCodeString.length() > 0 && zipCodeString.length() > 5 && Character.isDigit(zipCodeString.charAt(5))) {
            zipCodeString.insert(5, "-");
        }

        return zipCodeString.toString();
    }

    /**
     * Util to auto-format a edit text holding a phone
     *
     * @param phoneNumber The editable passed in the text watcher of that edit
     */
    public static void autoFormatPhone(Editable phoneNumber, int lengthBefore) {
        // password auto-complete functionality
        int currentLength = phoneNumber.length();
        if (lengthBefore < currentLength) {
            char lastChar = phoneNumber.charAt(currentLength - 1);
            if (currentLength == 3 && lastChar != '-') {
                phoneNumber.append("-");
            }

            if (currentLength == 4 && lastChar != '-') { // re-insert / after its deletion
                phoneNumber.replace(currentLength - 1, currentLength, "-" + lastChar);
            }

            if (currentLength == 7 && lastChar != '-') {
                phoneNumber.append("-");
            }

            if (currentLength == 8 && lastChar != '-') { // re-insert / after its deletion
                phoneNumber.replace(currentLength - 1, currentLength, "-" + lastChar);
            }

            if (lengthBefore != 3 && lengthBefore != 7 && lastChar == '-') {
                phoneNumber.replace(currentLength - 1, currentLength, "");
            }
        }
    }

    /**
     * Util to auto-format a edit text holding a zip
     *
     * @param zipcode The editable passed in the text watcher of that edit
     */
    public static void autoFormatZipcode(Editable zipcode, int lengthBefore) {
        // zipcode auto-complete functionality
        int currentLength = zipcode.length();
        if (lengthBefore < currentLength) {
            char lastChar = zipcode.charAt(currentLength - 1);
            if (currentLength == 6 && lastChar != '-') { // insert separator
                zipcode.insert(currentLength - 1, "-");
            }

            if (lengthBefore != 5 && lastChar == '-') { // discard separator except for position 5
                zipcode.replace(currentLength - 1, currentLength, "");
            }

            if (currentLength == 9 && !zipcode.toString().contains("-")){
                zipcode.insert(5, "-");
            }
        }
    }

    /**
     * Util to auto-format a edit text holding a date as (mm/dd/yyyy)
     *
     * @param dob The editable passed in the text watcher of that edit
     */
    public static void autoFormatDateOfBirth(Editable dob, int lengthBefore) {
        // date auto-complete functionality
        int currentLength = dob.length();
        if (lengthBefore < currentLength) { // on adding chars
            char lastChar = dob.charAt(currentLength - 1);
            Log.v("StringUtil", "autoFormatDateOfBirth(" + dob + ", " + lengthBefore + ")"
                    + " lastChar: " + lastChar);
            if (currentLength == 2 && lastChar != '/') {
                dob.append("/");
            }

            if (currentLength == 3 && lastChar != '/') { // re-insert / after its deletion
                dob.replace(currentLength - 1, currentLength, "/" + lastChar);
            }

            if (currentLength == 5 && lastChar != '/') {
                dob.append("/");
            }

            if (currentLength == 6 && lastChar != '/') { // re-insert / after its deletion
                dob.replace(currentLength - 1, currentLength, "/" + lastChar);
            }

            if (lengthBefore != 2 && lengthBefore != 5 && lastChar == '/') {
                dob.replace(currentLength - 1, currentLength, "");
            }
        }
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

    /**
     * Returns label for view.
     *
     * @param label string received from endpoint
     * @return label for view
     */
    public static String getLabelForView(String label) {
        if (isNullOrEmpty(label)) {
            return CarePayConstants.NOT_DEFINED;
        } else {
            return label;
        }
    }

    /**
     * Convinience method for validating json String
     *
     * @param jsonLabel - String to vaildate
     * @return - Either original string if valid or "Not Defined"
     */
    public static String validateJsonLabel(String jsonLabel) {
        if (isNotEmptyString(jsonLabel)) {
            return jsonLabel;
        }
        return CarePayConstants.NOT_DEFINED;
    }

    /**
     * Returns formatted balance amount.
     *
     * @param amount string received from endpoint
     * @return formatted balance amount
     */
    public static String getFormattedBalanceAmount(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);
        return CarePayConstants.DOLLAR + decimalFormat.format(amount);
    }

    /**
     * Returns encoded credit card number
     *
     * @param cardType   Card Type
     * @param cardNumber Card Number
     * @return encoded credit card number
     */
    public static String getEncodedCardNumber(String cardType, String cardNumber) {
        if (!isNullOrEmpty(cardNumber)) {
            return cardType + " **** " + cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
        }
        return "";
    }

    /**
     * Returns formatted credit card number
     *
     * @param cardType   Card Type
     * @param cardNumber Card Number
     * @return formatted credit card number
     */
    public static String getFormattedCardNumber(String cardType, String cardNumber) {
        if (!isNullOrEmpty(cardNumber)) {
            return cardType + " " + cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
        }
        return "";
    }

    /**
     * @param capString The capString
     * @return modified String
     */
    public static String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    /**
     * Convenience method for exporting collection contents to comma delimited string
     * @param collection collection to export
     * @return comma delimited string
     */
    public static String getListAsCommaDelimitedString(Collection<?> collection){
        return getListAsDelimitedString(collection, ',');
    }

    /**
     * Method for exporting contents of a collection to a delimited string
     * @param collection collection to export
     * @param delimiter character to use as a delimiter
     * @return delimited string
     */
    public static String getListAsDelimitedString(Collection<?> collection, char delimiter){
        StringBuilder builder = new StringBuilder();
        for(Object obj : collection){
            builder.append(obj.toString());
            builder.append(delimiter);
        }
        return builder.length() > 0 ? builder.substring(0, builder.length()-1) : "";
    }
}
