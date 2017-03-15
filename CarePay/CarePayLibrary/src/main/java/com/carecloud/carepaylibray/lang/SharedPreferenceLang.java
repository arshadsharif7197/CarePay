package com.carecloud.carepaylibray.lang;

import android.content.Context;
import android.content.SharedPreferences;

import com.carecloud.carepaylibray.base.AndroidPlatform;
import com.carecloud.carepaylibray.base.Platform;
import com.carecloud.carepaylibray.lang.LangProvider;

/**
 * Created by pjohnson on 15/03/17.
 */

public class SharedPreferenceLang implements LangProvider {

    public static final String LANG_PREF_FILE = "langsFile";

    private SharedPreferences sharedPreferences;

    private SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = ((AndroidPlatform) Platform.get()).getContext().getSharedPreferences(LANG_PREF_FILE, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    @Override
    public String getValue(String key) {
        return getSharedPreferences().getString(key, key);
    }

    @Override
    public boolean hasValue(String key) {
        return getSharedPreferences().getString(key, null) != null;
    }
}
