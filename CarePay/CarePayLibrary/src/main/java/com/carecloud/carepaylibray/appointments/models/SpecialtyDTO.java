package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model for appointment providers speciality.
 */
public class SpecialtyDTO {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("taxonomy")
    @Expose
    private String taxonomy;

    /**
     * Get speciality name.
     *
     * @return name
     */
    public String getName() {
        return StringUtil.getLabelForView(name);
    }

    /**
     * Set speciality name.
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Taxonomy
     *
     * @return taxonomy
     */
    public String getTaxonomy() {
        return StringUtil.getLabelForView(taxonomy);
    }

    /**
     * Set Taxonomy.
     *
     * @param taxonomy taxonomy
     */
    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }
}
