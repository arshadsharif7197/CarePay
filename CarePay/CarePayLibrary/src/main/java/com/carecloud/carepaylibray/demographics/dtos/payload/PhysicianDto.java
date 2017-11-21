package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 13/11/17.
 */

public class PhysicianDto {

    @Expose
    @SerializedName("first_name")
    private String firstName;
    @Expose
    @SerializedName("last_name")
    private String lastName;
    @Expose
    @SerializedName("npi")
    private String npi;
    @Expose
    @SerializedName("specialty")
    private String speciality;
    @Expose
    @SerializedName("address")
    private DemographicAddressPayloadDTO address;

    private String fullName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public DemographicAddressPayloadDTO getAddress() {
        return address;
    }

    public void setAddress(DemographicAddressPayloadDTO address) {
        this.address = address;
    }

    public String getFullName() {
        if (fullName == null) {
            StringBuffer sb = new StringBuffer();
            if (firstName != null) {
                sb.append(firstName);
            }
            if (lastName != null) {
                sb.append(" ").append(lastName);
            }
            fullName = sb.toString();
        }
        return fullName;
    }

    /**
     * @return the firstname and the first letter of the lastname
     */
    public String getFormattedName() {
        StringBuffer sb = new StringBuffer();
        sb.append(firstName);
        if (lastName != null) {
            sb.append(" ").append(lastName.substring(0, 1)).append(".");
        }
        return sb.toString();
    }
}
