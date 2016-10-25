package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageOptionDTO {
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
    private Boolean isDefault;

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The label
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     * @param label
     * The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     *
     * @return
     * The code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return
     * The isDefault
     */
    public Boolean getDefault() {
        return isDefault;
    }

    /**
     *
     * @param _default
     * The default
     */
    public void setDefault(Boolean _default) {
        this.isDefault = _default;
    }
}
