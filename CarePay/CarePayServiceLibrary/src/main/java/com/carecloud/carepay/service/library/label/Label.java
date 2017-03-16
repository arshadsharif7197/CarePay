package com.carecloud.carepay.service.library.label;

/**
 * This class permits an easily handling of the LangProvider
 * Created by pjohnson on 15/03/17.
 */
public class Label {

    private static LabelProvider labelProvider;

    private static LabelProvider getLabelProvider() {
        if (labelProvider == null) {
            labelProvider = new SharedPreferenceLabelProvider();
        }
        return labelProvider;
    }

    /**
     * static method to retrieve a label
     * @param key label key
     * @return a String containing the label value
     */
    public static String getLabel(String key) {
        return getLabelProvider().getValue(key);
    }

    /**
     * static method to retrieve a label
     * @param key label key
     * @return a String containing the label value
     */
    public static String getLabel(String key, String defaultValue) {
        return getLabelProvider().getValue(key, defaultValue);
    }

    /**
     * static method to know if a label exists
     * @param key label key
     * @return true if the label exists, false if not
     */
    public static boolean hasLabel(String key) {
        return getLabelProvider().hasValue(key);
    }

    /**
     * static method to save a label
     * @param key   label key
     * @param value label value
     */
    public static void putLabel(String key, String value) {
        getLabelProvider().putValue(key, value);
    }
}
