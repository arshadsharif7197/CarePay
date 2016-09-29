package com.carecloud.carepayclover.models;

/**
 * Created by Jahirul Bhuiyan on 9/28/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class ProviderModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("npi")
    @Expose
    private String npi;
    @SerializedName("name")
    @Expose
    private String name;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The npi
     */
    public String getNpi() {
        return npi;
    }

    /**
     *
     * @param npi
     * The npi
     */
    public void setNpi(String npi) {
        this.npi = npi;
    }

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
}
