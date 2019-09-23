package com.carecloud.carepaylibray.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 3/4/19.
 */
public class Permission {

    @SerializedName("enabled")
    @Expose
    private boolean enabled;
    @SerializedName("label")
    @Expose
    private String label;
    private String parent;
    private String key;
    private boolean checkBoxEnabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isCheckBoxEnabled() {
        return checkBoxEnabled;
    }

    public void setCheckBoxEnabled(boolean checkBoxEnabled) {
        this.checkBoxEnabled = checkBoxEnabled;
    }
}
