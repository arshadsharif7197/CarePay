package com.carecloud.carepay.patient.messages.models;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.base.dtos.DelegatePermissionBasePayloadDto;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagingDataModel extends DelegatePermissionBasePayloadDto {

    @SerializedName("messages")
    private Messages messages = new Messages();

    @SerializedName("inbox")
    private Inbox inbox = new Inbox();

    @SerializedName("contacts")
    private List<ProviderContact> providerContacts = new ArrayList<>();

    @SerializedName(value = "practice_information", alternate = "user_practices")
    private List<UserPracticeDTO> userPractices = new ArrayList<>();

    @SerializedName("practice_patient_ids")
    private List<PracticePatientIdsDTO> practicePatientIds = new ArrayList<>();

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public Inbox getInbox() {
        return inbox;
    }

    public void setInbox(Inbox inbox) {
        this.inbox = inbox;
    }

    public List<ProviderContact> getProviderContacts() {
        return providerContacts;
    }

    public void setProviderContacts(List<ProviderContact> providerContacts) {
        this.providerContacts = providerContacts;
    }

    public List<UserPracticeDTO> getUserPractices() {
        return userPractices;
    }

    public void setUserPractices(List<UserPracticeDTO> userPractices) {
        this.userPractices = userPractices;
    }

    public String lookupName(Messages.Reply thread, String userId) {
        for (Messages.Participant participant : thread.getParticipants()) {
            if (participant.getUserId().equals(userId)) {
                return participant.getName();
            }
        }
        return null;
    }

    public List<PracticePatientIdsDTO> getPracticePatientIds() {
        return practicePatientIds;
    }
}
