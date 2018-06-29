package com.carecloud.carepaylibray.third_party.models;

import com.google.gson.annotations.SerializedName;

public class ThirdPartyProcess {

    @SerializedName("external_task")
    private ThirdPartyTask task = new ThirdPartyTask();

    public ThirdPartyTask getTask() {
        return task;
    }

    public void setTask(ThirdPartyTask task) {
        this.task = task;
    }

}
