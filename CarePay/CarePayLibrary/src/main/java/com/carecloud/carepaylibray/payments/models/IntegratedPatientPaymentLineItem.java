package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 8/31/17
 */

public class IntegratedPatientPaymentLineItem extends IntegratedPaymentLineItem {

    @SerializedName("papi_processed")
    private boolean processed = false;

    @SerializedName("papi_processing_error_ids")
    private List<String> processingErrors = new ArrayList<>();


    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public List<String> getProcessingErrors() {
        return processingErrors;
    }

    public void setProcessingErrors(List<String> processingErrors) {
        this.processingErrors = processingErrors;
    }
}
