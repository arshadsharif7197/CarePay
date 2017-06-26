package com.carecloud.carepay.mini.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 6/24/17
 */

public class RegistrationPayloadDTO {

    @SerializedName("user_practices")
    private List<UserPracticeDTO> userPractices = new ArrayList<>();

    @SerializedName("locations")
    private List<LocationsDTO> locations = new ArrayList<>();


    public List<UserPracticeDTO> getUserPractices() {
        return userPractices;
    }

    public void setUserPractices(List<UserPracticeDTO> userPractices) {
        this.userPractices = userPractices;
    }

    public List<LocationsDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationsDTO> locations) {
        this.locations = locations;
    }
}
