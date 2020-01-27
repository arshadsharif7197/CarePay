package com.carecloud.carepay.practice.library.signin.dtos;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/9/17.
 */

public class PracticeSelectionPayloadDTO {

    @SerializedName("user_practices")
    @Expose
    private List<UserPracticeDTO> userPracticesList = new ArrayList<>();

    public List<UserPracticeDTO> getUserPracticesList() {
        return userPracticesList;
    }

    public void setUserPracticesList(List<UserPracticeDTO> userPracticesList) {
        this.userPracticesList = userPracticesList;
    }

}
