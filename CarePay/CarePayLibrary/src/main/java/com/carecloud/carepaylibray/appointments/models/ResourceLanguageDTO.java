
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResourceLanguageDTO {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("default")
    @Expose
    private Boolean defaultLanguage;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("name")
    @Expose
    private String name;

    /**
     *
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return defaultLanguage
     */
    public Boolean getDefault() {
        return defaultLanguage;
    }

    /**
     *
     * @param defaultLanguage defaultLanguage
     */
    public void setDefault(Boolean defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    /**
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     * @param label label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

}
