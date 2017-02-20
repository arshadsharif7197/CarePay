package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The type Demographics settings papi accounts dto.
 */
public class DemographicsSettingsPapiAccountsDTO {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("businessEntityId")
    @Expose
    private String businessEntityId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("merchant_service_id")
    @Expose
    private String merchantServiceId;
    @SerializedName("default_bank_account_token")
    @Expose
    private String defaultBankAccountToken;
    @SerializedName("bank_account")
    @Expose
    private DemographicsSettingsPapiBankAccountDTO bankAccount = new DemographicsSettingsPapiBankAccountDTO();
    @SerializedName("metadata")
    @Expose
    private DemographicsSettingsPapiMetadataDTO metadata = new DemographicsSettingsPapiMetadataDTO();

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
     * Gets business entity id.
     *
     * @return the business entity id
     */
    public String getBusinessEntityId() {
        return businessEntityId;
    }

    /**
     * Sets business entity id.
     *
     * @param businessEntityId the business entity id
     */
    public void setBusinessEntityId(String businessEntityId) {
        this.businessEntityId = businessEntityId;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets merchant service id.
     *
     * @return the merchant service id
     */
    public String getMerchantServiceId() {
        return merchantServiceId;
    }

    /**
     * Sets merchant service id.
     *
     * @param merchantServiceId the merchant service id
     */
    public void setMerchantServiceId(String merchantServiceId) {
        this.merchantServiceId = merchantServiceId;
    }

    /**
     * Gets default bank account token.
     *
     * @return the default bank account token
     */
    public String getDefaultBankAccountToken() {
        return defaultBankAccountToken;
    }

    /**
     * Sets default bank account token.
     *
     * @param defaultBankAccountToken the default bank account token
     */
    public void setDefaultBankAccountToken(String defaultBankAccountToken) {
        this.defaultBankAccountToken = defaultBankAccountToken;
    }

    /**
     * Gets bank account.
     *
     * @return the bank account
     */
    public DemographicsSettingsPapiBankAccountDTO getBankAccount() {
        return bankAccount;
    }

    /**
     * Sets bank account.
     *
     * @param bankAccount the bank account
     */
    public void setBankAccount(DemographicsSettingsPapiBankAccountDTO bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    public DemographicsSettingsPapiMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * Sets metadata.
     *
     * @param metadata the metadata
     */
    public void setMetadata(DemographicsSettingsPapiMetadataDTO metadata) {
        this.metadata = metadata;
    }
}
