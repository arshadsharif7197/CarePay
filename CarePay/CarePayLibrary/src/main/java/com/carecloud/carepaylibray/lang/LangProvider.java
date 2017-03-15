package com.carecloud.carepaylibray.lang;

/**
 * Interface for a provider of language properties
 * Created by pjohnson on 15/03/17.
 */
public interface LangProvider {

    /**
     * Returns the language resource for an specified key. If there is no resource, returns the key.
     */
    String getValue(String key);

    /**
     * Returns a boolean indicating if the language resource exists or not for the specified key.
     */
    boolean hasValue(String key);
}
