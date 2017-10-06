package com.carecloud.carepaylibray.demographics.dtos.payload;

/**
 * @author pjohnson on 5/10/17.
 */

public class EmployerDto {

    private String name;
    private AddressDto address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }
}
