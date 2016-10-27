package com.carecloud.carepaylibray.consentforms.models;

/**
 * Created by Rahul on 10/21/16.
 */

import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforhipaa.ConsentFormConsentHippaPropertiesDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ConsentFormConsentHippaDTO {

    @SerializedName("properties")
    @Expose
    private ConsentFormConsentHippaPropertiesDTO properties;

    /**
     * @return The properties
     */
    public ConsentFormConsentHippaPropertiesDTO getProperties() {
        return properties;
    }

    /**
     * @param properties The properties
     */
    public void setProperties(ConsentFormConsentHippaPropertiesDTO properties) {
        this.properties = properties;
    }

}
