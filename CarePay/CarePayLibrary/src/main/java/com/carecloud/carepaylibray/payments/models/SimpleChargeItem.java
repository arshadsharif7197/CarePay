package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.payments.models.postmodel.ResponsibilityType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/20/17.
 */

public class SimpleChargeItem {
    @SerializedName("amount")
    @Expose
    private double amount;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("responsibility_type")
    @Expose
    private ResponsibilityType responsibilityType;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResponsibilityType getResponsibilityType() {
        return responsibilityType;
    }

    public void setResponsibilityType(ResponsibilityType responsibilityType) {
        this.responsibilityType = responsibilityType;
    }
}
