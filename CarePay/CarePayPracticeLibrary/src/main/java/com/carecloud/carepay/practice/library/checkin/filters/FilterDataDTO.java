package com.carecloud.carepay.practice.library.checkin.filters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudhir_pingale on 10/19/2016.
 * This class use for filtering the checkin screen
 */

public class FilterDataDTO {

    public enum FilterDataType{
        PATIENT,PROVIDER,LOCATION,HEADER
    }

    //private int filterId = -1;
    private String displayText = "";
    private String imageURL = "";
    private FilterDataType filterDataType=FilterDataType.HEADER;
    private boolean checked = false;

    private List<String> appointmentList;

    private FilterDataDTO(){}

    /**
     * Constructor with text and type
     * @param displayText text
     * @param filterDataType type PATIENT, PROVIDER,LOCATION or HEADER
     */
    public FilterDataDTO(String displayText,FilterDataType filterDataType) {
        this.filterDataType=filterDataType;
        this.displayText=displayText;
        appointmentList=new ArrayList<>();

    }


    public String getDisplayText() {
        return displayText;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public FilterDataType getFilterDataType() {
        return filterDataType;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<String> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(List<String> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @Override
    public boolean equals(Object filterDataDTO) {
        if (this == filterDataDTO) {return true;}
        if (!(filterDataDTO instanceof FilterDataDTO)) {return false;}

        FilterDataDTO that = (FilterDataDTO) filterDataDTO;

        if (!getDisplayText().equals(that.getDisplayText())) {return false;}
        return getFilterDataType() == that.getFilterDataType();

    }

    @Override
    public int hashCode() {
        int result = getDisplayText().hashCode();
        result = 31 * result + getFilterDataType().hashCode();
        return result;
    }
}