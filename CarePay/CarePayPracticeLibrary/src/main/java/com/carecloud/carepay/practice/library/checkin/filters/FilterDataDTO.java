package com.carecloud.carepay.practice.library.checkin.filters;

/**
 * Created by sudhir_pingale on 10/19/2016.
 * This class use for filtering the checkin screen
 */

public class FilterDataDTO {

    public enum FilterDataType {
        PATIENT, PROVIDER, LOCATION, HEADER
    }

    private String id;
    private String displayText;
    private String imageURL = "";
    private FilterDataType filterDataType;
    private boolean checked = false;

    /**
     * Constructor with text and type
     *
     * @param id             of the DTO
     * @param displayText    text
     * @param filterDataType type PREVIOUS_BALANCE, PROVIDER,LOCATION or HEADER
     */
    public FilterDataDTO(String id, String displayText, FilterDataType filterDataType) {
        this.id = id;
        this.filterDataType = filterDataType;
        this.displayText = displayText;
    }

    /**
     * Constructor with text and type
     *
     * @param id             of the DTO
     * @param displayText    text
     * @param filterDataType type PREVIOUS_BALANCE, PROVIDER,LOCATION or HEADER
     */
    public FilterDataDTO(int id, String displayText, FilterDataType filterDataType) {
        this(String.valueOf(id), displayText, filterDataType);
    }

    /**
     * Constructor with text and type
     *
     * @param displayText text
     */
    public FilterDataDTO(String displayText) {
        this(null, displayText, FilterDataDTO.FilterDataType.HEADER);
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

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object filterDataDTO) {
        if (this == filterDataDTO) {
            return true;
        }
        if (!(filterDataDTO instanceof FilterDataDTO)) {
            return false;
        }

        FilterDataDTO that = (FilterDataDTO) filterDataDTO;

        if (!getDisplayText().equals(that.getDisplayText())) {
            return false;
        }
        return getFilterDataType() == that.getFilterDataType();

    }

    @Override
    public int hashCode() {
        int result = getDisplayText().hashCode();
        result = 31 * result + getFilterDataType().hashCode();
        return result;
    }
}