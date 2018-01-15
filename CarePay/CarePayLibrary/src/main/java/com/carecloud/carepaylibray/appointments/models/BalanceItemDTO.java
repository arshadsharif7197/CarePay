package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jorge on 30/12/16
 */

public class BalanceItemDTO {
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("balance")
    @Expose
    private double balance;
    @SerializedName("unapplied_credit")
    @Expose
    private double unappliedCredit;
    @SerializedName("posting_date")
    @Expose
    private String postingDate;
    @SerializedName("effective_date")
    @Expose
    private String effectiveDate;
    @SerializedName("transaction_type")
    @Expose
    private String transactionType;
    @SerializedName("transaction_status")
    @Expose
    private String transactionStatus;
    @SerializedName("location_id")
    @Expose
    private Integer locationId;
    @SerializedName("location")
    @Expose
    private LocationDTO location = new LocationDTO();
    @SerializedName("attending_provider_id")
    @Expose
    private Integer attendingProviderId;
    @SerializedName("provider_id")
    @Expose
    private Integer providerId;
    @SerializedName("provider")
    @Expose
    private ProviderDTO provider = new ProviderDTO();
    @SerializedName("units")
    @Expose
    private String units;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("responsibility_type")
    @Expose
    private
    @IntegratedPaymentLineItem.LineItemType
    String responsibilityType;
    @SerializedName("new_charge")
    @Expose
    private boolean newCharge = false;
    @SerializedName("max_amount")
    @Expose(serialize = false)
    private double maxAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getUnappliedCredit() {
        return unappliedCredit;
    }

    public void setUnappliedCredit(double unappliedCredit) {
        this.unappliedCredit = unappliedCredit;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public Integer getAttendingProviderId() {
        return attendingProviderId;
    }

    public void setAttendingProviderId(Integer attendingProviderId) {
        this.attendingProviderId = attendingProviderId;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public ProviderDTO getProvider() {
        return provider;
    }

    public void setProvider(ProviderDTO provider) {
        this.provider = provider;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public
    @IntegratedPaymentLineItem.LineItemType
    String getResponsibilityType() {
        return responsibilityType;
    }

    public void setResponsibilityType(@IntegratedPaymentLineItem.LineItemType String responsibilityType) {
        this.responsibilityType = responsibilityType;
    }

    public boolean isNewCharge() {
        return newCharge;
    }

    public void setNewCharge(boolean newCharge) {
        this.newCharge = newCharge;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }
}
