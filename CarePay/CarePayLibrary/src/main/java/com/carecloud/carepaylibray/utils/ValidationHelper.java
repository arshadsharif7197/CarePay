package com.carecloud.carepaylibray.utils;

import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataValidationDTO;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lsoco_user on 11/4/2016.
 * Helper to apply validations coming from metadata
 */

public class ValidationHelper {

    public static String VALIDATION_TYPE_IS_OPTION = "is_in_options";
    public static String VALIDATION_TYPE_PATTERN = "pattern";

    public static final String EMAIL_PATTERN = "^([a-zA-Z0-9_\\-\\.+])+@([a-zA-Z0-9_\\-\\.])+\\.([a-zA-Z]{2,})$";
    //    public static final String EMAIL_PATTERN = "^[A-Z0-9a-z\\\\._%+-]+@([A-Za-z0-9-]+\\\\.)+[A-Za-z]{2,4}$";
    public static final String PHONE_NUMBER_PATTERN = "\\d{3}-\\d{3}-\\d{4}";
    public static final String SOCIAL_SECURITY_NUMBER_PATTERN = "\\d{3}-\\d{2}-\\d{4}";
    public static final String ZIP_CODE_PATTERN = "^[0-9]{5}(?:-[0-9]{4})?$";
    //    public static final String EMAIL_PATTERN = Patterns.EMAIL_ADDRESS.pattern();
    public static final String PASSWORD_REGEX_VALIDATION = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@!%*?&_-])[A-Za-z\\d$@!%*?&_-]{8,}";


    /**
     * Applies a pattern validate to an edit text wrapped into textinput layout
     *
     * @param editText                The edit
     * @param wrappingTextInputLayout The input text layout
     * @param metadataEntityDTO       The (meta) dto containing the validate
     * @return Whether matches
     */
    public static boolean applyPatternValidationToWrappedEdit(EditText editText, TextInputLayout wrappingTextInputLayout,
                                                              MetadataEntityDTO metadataEntityDTO,
                                                              @Nullable LocalValidation extraValidation) {

        // retrieve the 'pattern' validate
        MetadataValidationDTO patternValidation = getValidationUtility(VALIDATION_TYPE_PATTERN, metadataEntityDTO);

        // if no validate, valid by default
        if (patternValidation == null) {
            return true; // no 'pattern' validate; field valid by default
        }

        // if there is validate, but the string is null or empty, then invalid by default
        String string = editText.getText().toString();
        if (StringUtil.isNullOrEmpty(string)) {
            return false;
        }

        // there is a 'pattern' validate; match against
        final String phoneError = patternValidation.getErrorMessage();
        final String phoneValidationRegex = (String) patternValidation.getValue();
        if (!isValidString(string, phoneValidationRegex)) {
            wrappingTextInputLayout.setErrorEnabled(true);
            wrappingTextInputLayout.setError(phoneError);
            return false;
        }
        wrappingTextInputLayout.setError(null);
        wrappingTextInputLayout.setErrorEnabled(false);

        return extraValidation == null || extraValidation.validate(patternValidation);
    }

    /**
     * Applies validation
     *
     * @param editText          The edit text
     * @param wrapperLayout     The wrapping input text
     * @param metadataEntityDTO The metadta
     * @param extraValidation   The extra validation
     * @return Whether value in edit passes the validation
     */
    public static boolean applyIsInOptionsValidationToWrappedEdit(EditText editText,
                                                                  TextInputLayout wrapperLayout,
                                                                  MetadataEntityDTO metadataEntityDTO,
                                                                  @Nullable LocalValidation extraValidation) {
        if (metadataEntityDTO == null || metadataEntityDTO.getValidations() == null) {
            return true;
        }

        MetadataValidationDTO isInOptionsValidation = getValidationUtility(VALIDATION_TYPE_IS_OPTION, metadataEntityDTO);
        if (isInOptionsValidation == null) {
            return true; // if no 'is_in_options' validation, pass
        }

        String value = editText.getText().toString();
        if (StringUtil.isNullOrEmpty(value)) {
            return true; // if empty content, pass
        }

        List<MetadataOptionDTO> options = metadataEntityDTO.getOptions();
        if (options == null || options.size() == 0) {
            return false; // if no options, fail
        }

        boolean isInOptions = false; // check valid state
        for (MetadataOptionDTO option : options) {
            if (option.getLabel().equals(value)) {
                isInOptions = true;
                break;
            }
        }

        final String stateError = isInOptionsValidation.getErrorMessage();
        wrapperLayout.setErrorEnabled(isInOptions);
        if (!isInOptions) {
            wrapperLayout.setError(stateError);
            return false;
        }
        wrapperLayout.setError(null);
        return true;
    }

    private static MetadataValidationDTO getValidationUtility(String type, MetadataEntityDTO addressMetaDTO) {
        if (addressMetaDTO != null) {
            List<MetadataValidationDTO> validations = addressMetaDTO.getValidations();
            for (int i = 0; i < validations.size(); i++) {
                MetadataValidationDTO metadataValidationDTO = validations.get(i);
                if (metadataValidationDTO.getType().equals(type)) {
                    return metadataValidationDTO;
                }
            }
        }
        return null;
    }

    /**
     * Checks a string against a pattern
     *
     * @param string           The string
     * @param validationString The format string (as regex)
     * @return Whether correctly formatted
     */
    public static boolean isValidString(String string, String validationString) {
        Pattern pattern = Pattern.compile(validationString);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    /**
     * Adds an extra validate after (and related to) the 'pattern' validate that has been performed
     */
    public interface LocalValidation {

        boolean validate(MetadataValidationDTO validation);
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

    /**
     * Convinience method for validating emails
     *
     * @param email email
     * @return true if valid email
     */
    public static boolean isValidEmail(String email) {
        return isValidString(email, EMAIL_PATTERN);
    }

}
