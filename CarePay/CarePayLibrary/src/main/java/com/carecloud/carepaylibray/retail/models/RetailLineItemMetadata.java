package com.carecloud.carepaylibray.retail.models;

import com.google.gson.annotations.SerializedName;

public class RetailLineItemMetadata {
    private static final String STORE_VALUE = "ecwid";

    @SerializedName("store")
    private String store = STORE_VALUE;
    
}
