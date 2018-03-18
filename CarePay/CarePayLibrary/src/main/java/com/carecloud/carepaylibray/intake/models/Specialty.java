
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Specialty {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("taxonomy")
    @Expose
    private String taxonomy;

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The taxonomy
     */
    public String getTaxonomy() {
        return taxonomy;
    }

    /**
     * 
     * @param taxonomy
     *     The taxonomy
     */
    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

}
