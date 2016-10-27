package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/27/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeOptionDTO {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("default")
    @Expose
    private Boolean _default;

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return The code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code The code
     */
    public void setCode(String code) {
        this.code = code;
    }
    /**
     *
     * @return
     * The _default
     */
    public Boolean getDefault() {
        return _default;
    }

    /**
     *
     * @param _default
     * The default
     */
    public void setDefault(Boolean _default) {
        this._default = _default;
    }

}