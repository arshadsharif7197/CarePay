
package com.carecloud.carepaylibray.payments.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationIndexDTO {

    @SerializedName("SOUTH MEDICAL CENTER")
    @Expose
    private List<String> sOUTHMEDICALCENTER = null;
    @SerializedName("MAIN MEDICAL CENTER")
    @Expose
    private List<String> mAINMEDICALCENTER = null;

    public List<String> getSOUTHMEDICALCENTER() {
        return sOUTHMEDICALCENTER;
    }

    public void setSOUTHMEDICALCENTER(List<String> sOUTHMEDICALCENTER) {
        this.sOUTHMEDICALCENTER = sOUTHMEDICALCENTER;
    }

    public List<String> getMAINMEDICALCENTER() {
        return mAINMEDICALCENTER;
    }

    public void setMAINMEDICALCENTER(List<String> mAINMEDICALCENTER) {
        this.mAINMEDICALCENTER = mAINMEDICALCENTER;
    }

}
