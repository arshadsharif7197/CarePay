package com.carecloud.carepay.service.library.label;

import android.content.SharedPreferences;

import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;


/**
 * An implementation of the LabelProvider that works with Shared Preferences
 * Created by pjohnson on 15/03/17.
 */
public class SharedPreferenceLabelProvider implements LabelProvider {


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = ((AndroidPlatform) Platform.get()).openSharedPreferences(AndroidPlatform.LABELS_FILE_NAME);
        }
        return sharedPreferences;
    }

    private SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = getSharedPreferences().edit();
        }
        return editor;
    }

    private void resetEditor() {
        editor = null;
    }

    /**
     * Returns the language resource for an specified key. If there is no resource, returns the key.
     *
     * @param key the key related to the value
     */
    @Override
    public String getValue(String key) {
        return getSharedPreferences().getString(key, null);
    }

    /**
     * Returns a boolean indicating if the language resource exists or not for the specified key.
     *
     * @param key the key related to the value
     */
    @Override
    public boolean hasValue(String key) {
        return getSharedPreferences().contains(key);
    }

    /**
     * Saves the label in the shared preferences
     *
     * @param key   the key related to the value
     * @param value the value to store related to the key
     */
    @Override
    public void putValue(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value).apply();
    }

    @Override
    public void putValueAsync(String key, String value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, value);
    }

    @Override
    public void applyAll() {
        SharedPreferences.Editor editor = getEditor();
        editor.apply();
        resetEditor();
    }

    @Override
    public void clearLabels() {
        getEditor().clear().apply();
    }
}
