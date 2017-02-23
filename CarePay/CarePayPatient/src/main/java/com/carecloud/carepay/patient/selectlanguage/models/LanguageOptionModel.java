
package com.carecloud.carepay.patient.selectlanguage.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for language list
 */
public class LanguageOptionModel {

    @SerializedName("code")
    @Expose
    private String languageId;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("iconId")
    @Expose
    private String iconId;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("child")
    @Expose
    private List<Object> child = new ArrayList<>();
    @SerializedName("isDefault")
    @Expose
    private Boolean isDefault;
    @SerializedName("skip")
    @Expose
    private List<Object> skip = new ArrayList<Object>();

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private boolean isChecked;

    /**
     *
     * @return
     *     The languageId
     */
    public String getLanguageId() {
        return languageId;
    }

    /**
     *
     * @param languageId
     *     The languageId
     */
    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    /**
     *
     * @return
     *     The label
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     * @param label
     *     The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     *
     * @return
     *     The iconId
     */
    public String getIconId() {
        return iconId;
    }

    /**
     *
     * @param iconId
     *     The iconId
     */
    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    /**
     *
     * @return
     *     The value
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @param value
     *     The value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     *
     * @return
     *     The child
     */
    public List<Object> getChild() {
        return child;
    }

    /**
     *
     * @param child
     *     The child
     */
    public void setChild(List<Object> child) {
        this.child = child;
    }

    /**
     *
     * @return
     *     The isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     *
     * @param isDefault
     *     The isDefault
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     *
     * @return
     *     The skip
     */
    public List<Object> getSkip() {
        return skip;
    }

    /**
     *
     * @param skip
     *     The skip
     */
    public void setSkip(List<Object> skip) {
        this.skip = skip;
    }

}
