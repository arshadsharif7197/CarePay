package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public class AllergiesObject extends MedicationsAllergiesObject {

    @Expose
    @SerializedName("hL7ObjectIndetifier")
    private String hL7ObjectIndetifier;
    @Expose
    @SerializedName("hL7ObjectIndetifierDesc")
    private String hL7ObjectIndetifierDesc;
    @Expose
    @SerializedName("interoperableDesc")
    private String interoperableDesc;
    @Expose
    @SerializedName("interoperableID")
    private String interoperableID;
    @Expose
    @SerializedName("interoperableTypeClassification")
    private String interoperableTypeClassification;
    @Expose
    @SerializedName("interoperableTypeCode")
    private String interoperableTypeCode;
    @Expose
    @SerializedName("interoperableTypeCodeDesc")
    private String interoperableTypeCodeDesc;
    @Expose
    @SerializedName("isMedication")
    private boolean isMedication;
    @Expose
    @SerializedName("rxCui")
    private String rxCui;

    public String gethL7ObjectIndetifier() {
        return hL7ObjectIndetifier;
    }

    public void sethL7ObjectIndetifier(String hL7ObjectIndetifier) {
        this.hL7ObjectIndetifier = hL7ObjectIndetifier;
    }

    public String gethL7ObjectIndetifierDesc() {
        return hL7ObjectIndetifierDesc;
    }

    public void sethL7ObjectIndetifierDesc(String hL7ObjectIndetifierDesc) {
        this.hL7ObjectIndetifierDesc = hL7ObjectIndetifierDesc;
    }

    public String getInteroperableDesc() {
        return interoperableDesc;
    }

    public void setInteroperableDesc(String interoperableDesc) {
        this.interoperableDesc = interoperableDesc;
    }

    public String getInteroperableID() {
        return interoperableID;
    }

    public void setInteroperableID(String interoperableID) {
        this.interoperableID = interoperableID;
    }

    public String getInteroperableTypeClassification() {
        return interoperableTypeClassification;
    }

    public void setInteroperableTypeClassification(String interoperableTypeClassification) {
        this.interoperableTypeClassification = interoperableTypeClassification;
    }

    public String getInteroperableTypeCode() {
        return interoperableTypeCode;
    }

    public void setInteroperableTypeCode(String interoperableTypeCode) {
        this.interoperableTypeCode = interoperableTypeCode;
    }

    public String getInteroperableTypeCodeDesc() {
        return interoperableTypeCodeDesc;
    }

    public void setInteroperableTypeCodeDesc(String interoperableTypeCodeDesc) {
        this.interoperableTypeCodeDesc = interoperableTypeCodeDesc;
    }

    public boolean isMedication() {
        return isMedication;
    }

    public void setMedication(boolean medication) {
        isMedication = medication;
    }

    public String getRxCui() {
        return rxCui;
    }

    public void setRxCui(String rxCui) {
        this.rxCui = rxCui;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof AllergiesObject) {
            return interoperableDesc.equals(((AllergiesObject) object).getInteroperableDesc());
        }
        return super.equals(object);
    }
}
