package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 5/03/18.
 */

public class TermsAndConditionsTranslationDTO {

    @Expose
    @SerializedName("es")
    private TermsAndConditionsDTO es;

    @Expose
    @SerializedName("en")
    private TermsAndConditionsDTO en;

    public TermsAndConditionsDTO getEs() {
        return es;
    }

    public void setEs(TermsAndConditionsDTO es) {
        this.es = es;
    }

    public TermsAndConditionsDTO getEn() {
        return en;
    }

    public void setEn(TermsAndConditionsDTO en) {
        this.en = en;
    }
}
