package com.carecloud.carepay.patient.myhealth.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 19/07/17.
 */

public class MedicationDto {

    @Expose
    private Integer id;
    @Expose
    @SerializedName("drug_name")
    private String drugName;
    @Expose
    @SerializedName("frequency_description")
    private String frequencyDescription;
    @Expose
    @SerializedName("strength_description")
    private String strengthDescription;
    @Expose
    @SerializedName("route_description")
    private String routeDescription;
    @Expose
    @SerializedName("prescription_instructions")
    private String prescriptionInstructions;
    @Expose
    private String status;
    @Expose
    @SerializedName("rx_norm_code")
    private String rxNormCode;
    @Expose
    @SerializedName("loinc")
    private String loinc;
    @Expose
    @SerializedName("snomed")
    private String snomed;
    @Expose
    private Double quantity;
    @Expose
    @SerializedName("refill_count")
    private Integer refillCount;
    @Expose
    @SerializedName("refill_max_count")
    private Integer refillMaxCount;
    @Expose
    @SerializedName("effective_from")
    private String effectiveFrom;
    @Expose
    private MedicationProviderDto provider;
    @Expose
    private String practice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getFrequencyDescription() {
        return frequencyDescription;
    }

    public void setFrequencyDescription(String frequencyDescription) {
        this.frequencyDescription = frequencyDescription;
    }

    public String getStrengthDescription() {
        return strengthDescription;
    }

    public void setStrengthDescription(String strengthDescription) {
        this.strengthDescription = strengthDescription;
    }

    public String getRouteDescription() {
        return routeDescription;
    }

    public void setRouteDescription(String routeDescription) {
        this.routeDescription = routeDescription;
    }

    public String getPrescriptionInstructions() {
        return prescriptionInstructions;
    }

    public void setPrescriptionInstructions(String prescriptionInstructions) {
        this.prescriptionInstructions = prescriptionInstructions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getRefillCount() {
        return refillCount;
    }

    public void setRefillCount(Integer refillCount) {
        this.refillCount = refillCount;
    }

    public Integer getRefillMaxCount() {
        return refillMaxCount;
    }

    public void setRefillMaxCount(Integer refillMaxCount) {
        this.refillMaxCount = refillMaxCount;
    }

    public String getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(String effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public MedicationProviderDto getProvider() {
        return provider;
    }

    public void setProvider(MedicationProviderDto provider) {
        this.provider = provider;
    }

    public String getPractice() {
        return practice;
    }

    public void setPractice(String practice) {
        this.practice = practice;
    }

    public String getRxNormCode() {
        return rxNormCode;
    }

    public void setRxNormCode(String rxNormCode) {
        this.rxNormCode = rxNormCode;
    }

    public String getLoinc() {
        return loinc;
    }

    public void setLoinc(String loinc) {
        this.loinc = loinc;
    }

    public String getSnomed() {
        return snomed;
    }

    public void setSnomed(String snomed) {
        this.snomed = snomed;
    }
}
