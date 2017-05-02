package com.carecloud.carepay.practice.library.signin.dtos;

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
    private List<PracticeSelectionUserPractice> userPracticesList = new ArrayList<>();

    public List<PracticeSelectionUserPractice> getUserPracticesList() {
        return userPracticesList;
    }

    public void setUserPracticesList(List<PracticeSelectionUserPractice> userPracticesList) {
        this.userPracticesList = userPracticesList;
    }

}
