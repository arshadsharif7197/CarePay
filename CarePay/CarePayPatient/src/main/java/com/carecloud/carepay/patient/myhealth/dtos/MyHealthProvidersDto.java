package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
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
    private List<ProviderDTO> providers = new ArrayList<>();

    public List<ProviderDTO> getProviders() {
        return providers;
    }

    public void setProviders(List<ProviderDTO> providers) {
        this.providers = providers;
    }
}
