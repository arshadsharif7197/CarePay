package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 5/03/18.
 */

public class TermsAndConditionsDTO {

    @Expose
    @SerializedName("body")
    private String body;

    @Expose
    @SerializedName("translations")
    private TermsAndConditionsTranslationDTO translations = new TermsAndConditionsTranslationDTO();

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public TermsAndConditionsTranslationDTO getTranslations() {
        return translations;
    }

    public void setTranslations(TermsAndConditionsTranslationDTO translations) {
        this.translations = translations;
    }
}
