package com.carecloud.carepay.practice.library.payments.interfaces;

import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;
import com.carecloud.carepaylibray.retail.models.RetailItemDto;
import com.carecloud.carepaylibray.retail.models.RetailItemOptionChoiceDto;

import java.util.Map;

public interface AddPaymentItemCallback {
    void addChargeItem(SimpleChargeItem chargeItem);

    void addRetailItem(RetailItemDto retailItemDto);

    void addRetailItemWithOptions(RetailItemDto retailItemDto, int quantity, Map<Integer, RetailItemOptionChoiceDto> selectedOptions);

}
