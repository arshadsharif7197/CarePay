package com.carecloud.carepay.patient.messages.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 7/6/17
 */

public class Inbox {

    @SerializedName("folder_label")
    private String folderLabel;

    @SerializedName("user_id")
    private String userId;

    public String getFolderLabel() {
        return folderLabel;
    }

    public void setFolderLabel(String folderLabel) {
        this.folderLabel = folderLabel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
