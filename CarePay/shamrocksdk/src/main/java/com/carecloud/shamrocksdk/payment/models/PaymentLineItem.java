package com.carecloud.shamrocksdk.payment.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for handling line items in {@link PaymentRequest Payment Requests}
 */

public class PaymentLineItem {

    @SerializedName("amount")
    private double amount;

    @SerializedName("description")
    private String description;

    @SerializedName("provider_id")
    private String providerId;

    @SerializedName("location_id")
    private String locationId;

    @SerializedName("type")
    private String type;

    @SerializedName("type_id")
    private Integer typeId;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
