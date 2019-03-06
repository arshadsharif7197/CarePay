package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The type Demographics settings papi accounts dto.
 */
public class PapiAccountsDTO {
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
    @SerializedName("default_bank_account_mid")
    @Expose
    private String defaultBankAccountMid;
    @SerializedName("bank_account")
    @Expose
    private PapiBankAccountDTO bankAccount = new PapiBankAccountDTO();
    @SerializedName("metadata")
    @Expose
    private PapiMetadataDTO metadata = new PapiMetadataDTO();

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

    public String getDefaultBankAccountMid() {
        return defaultBankAccountMid;
    }

    public void setDefaultBankAccountMid(String defaultBankAccountMid) {
        this.defaultBankAccountMid = defaultBankAccountMid;
    }

    /**
     * Gets bank account.
     *
     * @return the bank account
     */
    public PapiBankAccountDTO getBankAccount() {
        return bankAccount;
    }

    /**
     * Sets bank account.
     *
     * @param bankAccount the bank account
     */
    public void setBankAccount(PapiBankAccountDTO bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    public PapiMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * Sets metadata.
     *
     * @param metadata the metadata
     */
    public void setMetadata(PapiMetadataDTO metadata) {
        this.metadata = metadata;
    }
}
