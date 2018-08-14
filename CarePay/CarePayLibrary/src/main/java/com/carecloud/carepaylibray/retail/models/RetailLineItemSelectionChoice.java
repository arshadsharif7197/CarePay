package com.carecloud.carepaylibray.retail.models;

import com.google.gson.annotations.SerializedName;

public class RetailLineItemSelectionChoice {

    @SerializedName("selection_title")
    private String title;

    @SerializedName("selection_modifier")
    private double modifier;

    @SerializedName("selection_modifier_type")
    @RetailItemOptionChoiceDto.ModifierType
    private String modifierType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getModifier() {
        return modifier;
    }

    public void setModifier(double modifier) {
        this.modifier = modifier;
    }

    @RetailItemOptionChoiceDto.ModifierType
    public String getModifierType() {
        return modifierType;
    }

    public void setModifierType(@RetailItemOptionChoiceDto.ModifierType String modifierType) {
        this.modifierType = modifierType;
    }
}
