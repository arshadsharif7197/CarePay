package com.carecloud.carepay.service.library.label;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * An implementation of the LabelProvider that works with Shared Preferences
 * Created by pjohnson on 15/03/17.
 */
public class SharedPreferenceLabelProvider implements LabelProvider {

    private static final String LABELS_FILE_NAME = "labelsFileName";
    private static final String DEFAULT_FILE = "defaultName";

    private SharedPreferences sharedPreferences;

    private final Context context;

    public SharedPreferenceLabelProvider(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = openSharedPreferences(LABELS_FILE_NAME);
        }
        return sharedPreferences;
    }

    /**
     * Saves the label in the shared preferences
     * @param key the key related to the value
     * @param value the value to store related to the key
     */
    public void putValue(String key, String value) {

    }

    /**
     * @return the Application Context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @param fileName name of the Shared Preferences file.
     * @param mode     file opening mode
     * @return an specific Shared Preferences file
     */
    public SharedPreferences openSharedPreferences(String fileName, int mode) {
        return context.getSharedPreferences(fileName, mode);
    }

    /**
     * @param fileName name of the Shared Preferences file
     * @return an specific Shared Preferences file opened in private mode
     */
    public SharedPreferences openSharedPreferences(String fileName) {
        return openSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * static method to retrieve a label
     * @param key label key
     * @return a String containing the label value
     */
    public String getLabel(String key) {
        return getSharedPreferences().getString(key, key);
    }

    /**
     * static method to know if a label exists
     * @param key label key
     * @return true if the label exists, false if not
     */
    public boolean hasLabel(String key) {
        return getSharedPreferences().contains(key);
    }

    /**
     * static method to save a label
     * @param key   label key
     * @param value label value
     */
    public void putLabel(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value).apply();
    }
}
