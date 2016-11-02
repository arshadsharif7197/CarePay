package com.carecloud.carepay.practice.library.checkin.filters;

/**
 * Created by sudhir_pingale on 10/19/2016.
 */

public class FilterableDataDTO {
    private int id = -1;
    private String name = "";
    private String imageURL = "";
    private boolean isHeader = false;
    private boolean checked = false;

    public FilterableDataDTO() {
        this.id = -1;
        this.name = null;
        this.imageURL = null;
        this.isHeader = false;
        this.checked = false;
    }

    public FilterableDataDTO(String name) {
        this.name = name;
        this.id = -1;
        this.imageURL = null;
        this.isHeader = false;
        this.checked = false;
    }

    public FilterableDataDTO(int id, String name) {
        this.id = id;
        this.name = name;
        this.imageURL = null;
        this.isHeader = false;
        this.checked = false;
    }

    public FilterableDataDTO(String name, boolean isHeader) {
        this.name = name;
        this.isHeader = isHeader;
        this.id = -1;
        this.imageURL = null;
        this.checked = false;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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