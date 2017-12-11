package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 10/11/17.
 */

public class DemographicPhysicianSection {

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
    @SerializedName("properties")
    private Properties properties = new Properties();

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

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public class Properties {

        @SerializedName("first_name")
        @Expose
        private DemographicsField firstName = new DemographicsField();

        @SerializedName("last_name")
        @Expose
        private DemographicsField lastName = new DemographicsField();

        @SerializedName("title")
        @Expose
        private DemographicsField title = new DemographicsField();

        @SerializedName("npi")
        @Expose
        private DemographicsField npi = new DemographicsField();

        public DemographicsField getFirstName() {
            return firstName;
        }

        public void setFirstName(DemographicsField firstName) {
            this.firstName = firstName;
        }

        public DemographicsField getLastName() {
            return lastName;
        }

        public void setLastName(DemographicsField lastName) {
            this.lastName = lastName;
        }

        public DemographicsField getTitle() {
            return title;
        }

        public void setTitle(DemographicsField title) {
            this.title = title;
        }

        public DemographicsField getNpi() {
            return npi;
        }

        public void setNpi(DemographicsField npi) {
            this.npi = npi;
        }
    }
}
