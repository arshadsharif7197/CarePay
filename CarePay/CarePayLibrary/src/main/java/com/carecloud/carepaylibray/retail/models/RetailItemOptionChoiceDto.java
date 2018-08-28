package com.carecloud.carepaylibray.retail.models;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RetailItemOptionChoiceDto {

    public static final String MODIFIER_TYPE_AMOUNT = "ABSOLUTE";
    public static final String MODIFIER_TYPE_PERCENT = "PERCENT";

    @StringDef({MODIFIER_TYPE_AMOUNT, MODIFIER_TYPE_PERCENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ModifierType{}

    @SerializedName("text")
    private String name;

    @SerializedName("price_modifier")
    private double priceModify;

    @SerializedName("price_modifier_type")
    @ModifierType
    private String priceModifyType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPriceModify() {
        return priceModify;
    }

    public void setPriceModify(double priceModify) {
        this.priceModify = priceModify;
    }

    public @ModifierType String getPriceModifyType() {
        return priceModifyType;
    }

    public void setPriceModifyType(@ModifierType String priceModifyType) {
        this.priceModifyType = priceModifyType;
    }
}
