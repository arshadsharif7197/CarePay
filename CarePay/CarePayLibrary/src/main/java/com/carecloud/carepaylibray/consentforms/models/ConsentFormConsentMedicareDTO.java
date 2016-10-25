package com.carecloud.carepaylibray.consentforms.models;

/**
 * Created by Rahul on 10/21/16.
 */


        import com.carecloud.carepaylibray.consentforms.models.data_models.consent_for_medicare.ConsentFormsConsentMedicarePropertiesDTO;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class ConsentFormConsentMedicareDTO {

    @SerializedName("properties")
    @Expose
    private ConsentFormsConsentMedicarePropertiesDTO properties;

    /**
     *
     * @return
     * The properties
     */
    public ConsentFormsConsentMedicarePropertiesDTO getProperties() {
        return properties;
    }

    /**
     *
     * @param properties
     * The properties
     */
    public void setProperties(ConsentFormsConsentMedicarePropertiesDTO properties) {
        this.properties = properties;
    }

}