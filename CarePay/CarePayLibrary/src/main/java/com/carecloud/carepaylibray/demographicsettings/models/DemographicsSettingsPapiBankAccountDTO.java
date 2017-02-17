package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsPapiBankAccountDTO {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("account_id")
    @Expose
    private String account_id;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("metadata")
    @Expose
    private DemographicsSettingsPapiBankAccountMetadataDTO metadata;

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets account id.
     *
     * @return the account id
     */
    public String getAccount_id() {
        return account_id;
    }

    /**
     * Sets account id.
     *
     * @param account_id the account id
     */
    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets token.
     *
     * @param token the token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    public DemographicsSettingsPapiBankAccountMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * Sets metadata.
     *
     * @param metadata the metadata
     */
    public void setMetadata(DemographicsSettingsPapiBankAccountMetadataDTO metadata) {
        this.metadata = metadata;
    }
}
