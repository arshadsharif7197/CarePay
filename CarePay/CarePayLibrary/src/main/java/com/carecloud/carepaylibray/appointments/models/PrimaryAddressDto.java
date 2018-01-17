package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.intake.models.AddressModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 21/07/17.
 */

public class PrimaryAddressDto {

    @Expose
    @SerializedName("address")
    private AddressModel address = new AddressModel();

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }
}
