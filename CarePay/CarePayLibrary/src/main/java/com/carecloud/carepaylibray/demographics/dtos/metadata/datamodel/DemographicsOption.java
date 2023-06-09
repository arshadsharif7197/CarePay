package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.carecloud.carepay.service.library.base.OptionNameInterface;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 5/16/17
 */

public class DemographicsOption implements OptionNameInterface {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("label")
    @Expose
    protected String label;

    public DemographicsOption() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label != null && label.length() > 2 ? StringUtil.captialize(label) : label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDisplayName() {
        return getLabel() != null ? getLabel() : name;
    }
}
