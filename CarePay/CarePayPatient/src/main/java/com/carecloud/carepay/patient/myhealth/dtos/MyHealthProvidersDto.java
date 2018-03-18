package com.carecloud.carepay.patient.myhealth.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthProvidersDto {

    @Expose
    @SerializedName("providers")
    private List<MyHealthProviderDto> providers = new ArrayList<>();

    public List<MyHealthProviderDto> getProviders() {
        return providers;
    }

    public void setProviders(List<MyHealthProviderDto> providers) {
        this.providers = providers;
    }
}
