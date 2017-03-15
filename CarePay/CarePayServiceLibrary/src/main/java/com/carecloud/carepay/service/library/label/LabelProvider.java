package com.carecloud.carepay.service.library.label;

/**
 * Interface for a provider of language properties
 * Created by pjohnson on 15/03/17.
 */
public interface LabelProvider {

    /**
     * Returns the language resource for an specified key. If there is no resource, returns the key.
     * @param key the key related to the value
     */
    String getValue(String key);

    /**
     * Returns a boolean indicating if the language resource exists or not for the specified key.
     * @param key the key related to the value
     */
    boolean hasValue(String key);

    /**
     * Saves the label
     * @param key the key related to the value
     * @param value the value to store related to the key
     */
    void putValue(String key, String value);
}
