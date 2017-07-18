package com.carecloud.carepay.mini.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 7/18/17
 */

public class PreRegisterDataModel {

    @SerializedName("business_entities")
    private List<UserPracticeDTO> userPracticeDTOList = new ArrayList<>();

    public List<UserPracticeDTO> getUserPracticeDTOList() {
        return userPracticeDTOList;
    }

    public void setUserPracticeDTOList(List<UserPracticeDTO> userPracticeDTOList) {
        this.userPracticeDTOList = userPracticeDTOList;
    }

    /**
     * Find a practice by its Id
     * @param id practice id
     * @return practice if found or null
     */
    public UserPracticeDTO getPracticeById(String id){
        for (UserPracticeDTO userPracticeDTO : userPracticeDTOList){
            if(userPracticeDTO.getPracticeId().equals(id)){
                return userPracticeDTO;
            }
        }
        return null;
    }
}
