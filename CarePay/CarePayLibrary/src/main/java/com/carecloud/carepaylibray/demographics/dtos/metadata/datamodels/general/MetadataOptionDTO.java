package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * DTO for the metadata describing the possible choices of the values of a UI field
 * 'label' is private as its getter is not trivial
 */

public class MetadataOptionDTO {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("label")
    @Expose
    private String label;

    /**
     * Getter
     * @return The lable
     */
    public String getLabel() {
        return StringUtil.isNullOrEmpty(label) ? CarePayConstants.NOT_DEFINED : label;
    }

    /**
     * Setter
     * @param label The lsbel
     */
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
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
}