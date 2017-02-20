package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsPapiBankAccountDTO {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("account_id")
    @Expose
    private String accountId;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("metadata")
    @Expose
    private DemographicsSettingsPapiBankAccountMetadataDTO metadata = new DemographicsSettingsPapiBankAccountMetadataDTO();

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
    public String getAccountId() {
        return accountId;
    }


    /**
     * Sets account id.
     *
     * @param accountId the account id
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
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
