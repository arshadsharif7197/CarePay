package com.carecloud.carepaylibray.lang;

/**
 * Created by pjohnson on 15/03/17.
 */

public class Lang {

    private static LangProvider langProvider;

    private static LangProvider getLangProvider() {
        if (langProvider == null) {
            langProvider = new SharedPreferenceLang();
        }
        return langProvider;
    }

    public static String getLang(String key) {
        return getLangProvider().getValue(key);
    }

    public static boolean hasLang(String key) {
        return getLangProvider().hasValue(key);
    }
}
