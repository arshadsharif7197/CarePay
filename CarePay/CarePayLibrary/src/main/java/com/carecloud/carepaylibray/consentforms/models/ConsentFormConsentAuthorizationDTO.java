package com.carecloud.carepaylibray.consentforms.models;

/**
 * Created by Rahul on 10/21/16.
 */


import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization.ConsentFormConsentAuthPropertiesDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormConsentAuthorizationDTO {

    @SerializedName("properties")
    @Expose
    private ConsentFormConsentAuthPropertiesDTO properties;

    /**
     * @return The properties
     */
    public ConsentFormConsentAuthPropertiesDTO getProperties() {
        return properties;
    }

    /**
     * @param properties The properties
     */
    public void setProperties(ConsentFormConsentAuthPropertiesDTO properties) {
        this.properties = properties;
    }

}