package com.carecloud.carepaylibray.payments.models.postmodel;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lmenendez on 8/29/17
 */

public class IntegratedPaymentLineItem {
    public static final String TYPE_COPAY = "co_pay";
    public static final String TYPE_COINSURANCE = "co_insurance";
    public static final String TYPE_DEDUCTABLE = "deductable";
    public static final String TYPE_PREPAYMENT = "prepayment";
    public static final String TYPE_CANCELLATION = "cancel_fee";
    public static final String TYPE_NEWCHARGE = "new_debit";
    public static final String TYPE_APPLICATION = "debit_application";
    public static final String TYPE_UNAPPLIED = "unapplied";
    public static final String TYPE_OTHER = "other";
    public static final String TYPE_RETAIL = "retail";

    @StringDef({TYPE_COPAY, TYPE_COINSURANCE, TYPE_DEDUCTABLE, TYPE_PREPAYMENT, TYPE_CANCELLATION, TYPE_NEWCHARGE, TYPE_APPLICATION, TYPE_UNAPPLIED, TYPE_OTHER, TYPE_RETAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LineItemType{}

    @SerializedName("amount")
    private double amount;

    @SerializedName("provider_id")
    private String providerID;

    @SerializedName("location_id")
    private String locationID;

    @SerializedName("type")
    private @LineItemType String itemType;

    @SerializedName("type_id")
    private String id;

    @SerializedName("description")
    private String description;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public @LineItemType String getItemType() {
        return itemType;
    }

    public void setItemType(@LineItemType String itemType) {
        this.itemType = itemType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
