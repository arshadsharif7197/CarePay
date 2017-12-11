package com.carecloud.carepay.patient.retail.models.sso;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 10/30/17
 */

public class Profile {

    @SerializedName("email")
    private String email;

    @SerializedName("billingPerson")
    private Person billingPerson;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Person getBillingPerson() {
        return billingPerson;
    }

    public void setBillingPerson(Person billingPerson) {
        this.billingPerson = billingPerson;
    }
}
