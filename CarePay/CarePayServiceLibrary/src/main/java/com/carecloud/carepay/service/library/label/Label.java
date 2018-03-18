package com.carecloud.carepay.service.library.label;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;

/**
 * This class permits an easily handling of the LangProvider
 * Created by pjohnson on 15/03/17.
 */
public class Label {

    private static LabelProvider labelProvider;
    private static ApplicationMode.ApplicationType applicationType;

    private static LabelProvider getLabelProvider() {
        if (labelProvider == null) {
            labelProvider = new SharedPreferenceLabelProvider();
        }
        return labelProvider;
    }

    /**
     * static method to retrieve a label
     *
     * @param key label key
     * @return a String containing the label value
     */
    public static String getLabel(String key) {
        if (applicationType == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            key = CarePayConstants.PATIENT_MODE_LABELS_PREFIX + key;
        }
        return getLabelForView(getLabelProvider().getValue(key));
    }

    /**
     * static method to know if a label exists
     *
     * @param key label key
     * @return true if the label exists, false if not
     */
    public static boolean hasLabel(String key) {
        return getLabelProvider().hasValue(key);
    }

    /**
     * static method to save a label
     *
     * @param key   label key
     * @param value label value
     */
    public static void putLabel(String key, String value) {
        getLabelProvider().putValue(key, value);
    }

    /**
     * static method to save a label
     *
     * @param key   label key
     * @param value label value
     */
    public static void putLabelAsync(String key, String value) {
        getLabelProvider().putValueAsync(key, value);
    }

    /**
     * Apply pending labels
     */
    public static void applyAsyncLabels() {
        getLabelProvider().applyAll();
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
        }

        return label;
    }

    /**
     * Determines if the specified String object is null or equal to
     * an empty string.
     *
     * @param string the string to evaluate
     * @return true if object <code>string</code> is null or equal to an empty string
     */
    public static boolean isNullOrEmpty(String string) {
        return (string == null || string.equals(""));
    }

    public static void setApplicationType(ApplicationMode.ApplicationType applicationType) {
        Label.applicationType = applicationType;
    }
}
