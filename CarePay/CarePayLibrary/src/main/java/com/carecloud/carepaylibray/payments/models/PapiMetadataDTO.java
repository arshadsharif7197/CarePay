package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PapiMetadataDTO {
    @SerializedName("form_sid")
    @Expose
    private String formSid;
    @SerializedName("account_sid")
    @Expose
    private String accountSid;
    @SerializedName("business_name")
    @Expose
    private String businessName;
    @SerializedName("form_finish_url")
    @Expose
    private String formFinishUrl;
    @SerializedName("merchant_service")
    @Expose
    private MerchantServiceMetadataDTO merchantService = new MerchantServiceMetadataDTO();

    /**
     * Gets form sid.
     *
     * @return the form sid
     */
    public String getFormSid() {
        return formSid;
    }

    /**
     * Sets form sid.
     *
     * @param formSid the form sid
     */
    public void setFormSid(String formSid) {
        this.formSid = formSid;
    }

    /**
     * Gets account sid.
     *
     * @return the account sid
     */
    public String getAccountSid() {
        return accountSid;
    }

    /**
     * Sets account sid.
     *
     * @param accountSid the account sid
     */
    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    /**
     * Gets business name.
     *
     * @return the business name
     */
    public String getBusinessName() {
        return businessName;
    }

    /**
     * Sets business name.
     *
     * @param businessName the business name
     */
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    /**
     * Gets form finish url.
     *
     * @return the form finish url
     */
    public String getFormFinishUrl() {
        return formFinishUrl;
    }

    /**
     * Sets form finish url.
     *
     * @param formFinishUrl the form finish url
     */
    public void setFormFinishUrl(String formFinishUrl) {
        this.formFinishUrl = formFinishUrl;
    }

    /**
     * Gets merchant service.
     *
     * @return the merchant service
     */
    public MerchantServiceMetadataDTO getMerchantService() {
        return merchantService;
    }

    /**
     * Sets merchant service.
     *
     * @param merchantService the merchant service
     */
    public void setMerchantService(MerchantServiceMetadataDTO merchantService) {
        this.merchantService = merchantService;
    }
}
