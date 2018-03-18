package com.carecloud.carepay.mini.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    /**
     * Get list of practices sorted alphabetically by practice name
     * @return practice list
     */
    public List<UserPracticeDTO> getSortedPractices(){
        Collections.sort(userPracticeDTOList, new Comparator<UserPracticeDTO>() {
            @Override
            public int compare(UserPracticeDTO practice1, UserPracticeDTO practice2) {
                if(practice1.getPracticeName() == null){
                    practice1.setPracticeName("");
                }
                if(practice2.getPracticeName() == null){
                    practice2.setPracticeName("");
                }
                return practice1.getPracticeName().compareTo(practice2.getPracticeName());
            }
        });
        return userPracticeDTOList;
    }
}
