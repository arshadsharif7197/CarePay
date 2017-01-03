
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsPaymentPlansDTO {

    @SerializedName("default_payment")
    @Expose
    private String defaultPayment;
    @SerializedName("minimum_payment")
    @Expose
    private String minimumPayment;
    @SerializedName("maximum_payment")
    @Expose
    private String maximumPayment;
    @SerializedName("default_cycles_months")
    @Expose
    private String defaultCyclesMonths;
    @SerializedName("minimum_cycles_months")
    @Expose
    private String minimumCyclesMonths;
    @SerializedName("maximum_cycles_months")
    @Expose
    private String maximumCyclesMonths;

    public String getDefaultPayment() {
        return defaultPayment;
    }

    public void setDefaultPayment(String defaultPayment) {
        this.defaultPayment = defaultPayment;
    }

    public String getMinimumPayment() {
        return minimumPayment;
    }

    public void setMinimumPayment(String minimumPayment) {
        this.minimumPayment = minimumPayment;
    }

    public String getMaximumPayment() {
        return maximumPayment;
    }

    public void setMaximumPayment(String maximumPayment) {
        this.maximumPayment = maximumPayment;
    }

    public String getDefaultCyclesMonths() {
        return defaultCyclesMonths;
    }

    public void setDefaultCyclesMonths(String defaultCyclesMonths) {
        this.defaultCyclesMonths = defaultCyclesMonths;
    }

    public String getMinimumCyclesMonths() {
        return minimumCyclesMonths;
    }

    public void setMinimumCyclesMonths(String minimumCyclesMonths) {
        this.minimumCyclesMonths = minimumCyclesMonths;
    }

    public String getMaximumCyclesMonths() {
        return maximumCyclesMonths;
    }

    public void setMaximumCyclesMonths(String maximumCyclesMonths) {
        this.maximumCyclesMonths = maximumCyclesMonths;
    }

}
