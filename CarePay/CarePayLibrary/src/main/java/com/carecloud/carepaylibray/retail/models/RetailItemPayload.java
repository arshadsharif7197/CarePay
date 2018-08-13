package com.carecloud.carepaylibray.retail.models;

import java.util.Map;

public class RetailItemPayload {

    private RetailItemDto retailItemDto;

    private int quantity;

    private Map<Integer, RetailItemOptionChoiceDto> selectedOptions;

    public RetailItemDto getRetailItemDto() {
        return retailItemDto;
    }

    public void setRetailItemDto(RetailItemDto retailItemDto) {
        this.retailItemDto = retailItemDto;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Map<Integer, RetailItemOptionChoiceDto> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(Map<Integer, RetailItemOptionChoiceDto> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }
}
