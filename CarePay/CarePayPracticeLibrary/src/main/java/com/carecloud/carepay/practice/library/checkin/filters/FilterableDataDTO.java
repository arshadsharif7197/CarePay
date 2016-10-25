package com.carecloud.carepay.practice.library.checkin.filters;

/**
 * Created by sudhir_pingale on 10/19/2016.
 */

public class FilterableDataDTO {
    private int id;
    private String name = "";
    private String patientImageURL = "";
    private boolean checked = false;

    public FilterableDataDTO() {
    }

    public FilterableDataDTO(String name) {
        this.name = name;
    }

    public FilterableDataDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatientImageURL() {
        return patientImageURL;
    }

    public void setPatientImageURL(String patientImageURL) {
        this.patientImageURL = patientImageURL;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String toString() {
        return name;
    }

    public void toggleChecked() {
        checked = !checked;
    }
}  