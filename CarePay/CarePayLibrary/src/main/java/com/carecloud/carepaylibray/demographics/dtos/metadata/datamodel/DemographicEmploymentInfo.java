package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 3/11/17.
 */

public class DemographicEmploymentInfo {

    @Expose
    @SerializedName("display")
    private boolean display;

    @Expose
    @SerializedName("required")
    private boolean required;

    @Expose
    @SerializedName("label")
    private String label;

    @Expose
    @SerializedName("order")
    private int order;

    @Expose
    @SerializedName("properties")
    private DemographicEmploymentInfo.Properties properties = new Properties();

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public class Properties {

        @SerializedName("employment_status")
        @Expose
        private DemographicsField employmentStatus = new DemographicsField();

        @SerializedName("employer")
        @Expose
        private DemographicsField employer = new DemographicsField();

        public DemographicsField getEmploymentStatus() {
            return employmentStatus;
        }

        public void setEmploymentStatus(DemographicsField employmentStatus) {
            this.employmentStatus = employmentStatus;
        }

        public DemographicsField getEmployer() {
            return employer;
        }

        public void setEmployer(DemographicsField employer) {
            this.employer = employer;
        }
    }
}
