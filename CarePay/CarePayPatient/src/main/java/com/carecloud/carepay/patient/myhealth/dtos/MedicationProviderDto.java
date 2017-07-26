package com.carecloud.carepay.patient.myhealth.dtos;

import com.google.gson.annotations.Expose;

/**
 * @author pjohnson on 17/07/17.
 */
public class MedicationProviderDto {

    @Expose
    private Integer id;
    @Expose
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
