
package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenderPropertiesDTO {


    @SerializedName("gender")
    @Expose
    private GenderDTO gender = new GenderDTO();


    public GenderDTO getGender() {
        return gender;
    }

    public void setGender(GenderDTO gender) {
        this.gender = gender;
    }

}
