package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 5/10/17.
 */

public class EmployerDto {

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("address")
    private DemographicAddressPayloadDTO address = new DemographicAddressPayloadDTO();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DemographicAddressPayloadDTO getAddress() {
        return address;
    }

    public void setAddress(DemographicAddressPayloadDTO address) {
        this.address = address;
    }
}
