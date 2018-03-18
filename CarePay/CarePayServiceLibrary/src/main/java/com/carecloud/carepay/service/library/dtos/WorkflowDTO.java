package com.carecloud.carepay.service.library.dtos;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * This DTO belongs to common data model for any endpoint call
 * Need conversion mechanism between JsonObject to desire DTO based on the returned state
 * e.g. State return appointments then metadata and payload object need to convert to appointment
 * Gson conversion can be use for JsonObject to desire DTO conversion
 */

public class WorkflowDTO{
    @SerializedName("metadata")
    @Expose
    private JsonObject metadata;
    @SerializedName("payload")
    @Expose
    private JsonObject payload;
    @SerializedName("state")
    @Expose
    private String state;

    @Deprecated
    public WorkflowDTO(){

    }

    /**
     * @param workFlowRecord WorkFlow Record
     */
    public WorkflowDTO(WorkFlowRecord workFlowRecord) {
        Gson gson = new Gson();
        metadata = gson.fromJson(workFlowRecord.getMetadata(), JsonObject.class);
        payload = gson.fromJson(workFlowRecord.getPayload(), JsonObject.class);
        state = workFlowRecord.getState();
    }

    /**
     * @return The metadata
     */
    public JsonObject getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata
     */
    public void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    /**
     * @return The payload
     */
    public JsonObject getPayload() {
        return payload;
    }

    public <S> S getPayload(Class<S> serviceClass) {
        Gson gson=new Gson();
        return gson.fromJson(payload,serviceClass);
    }

    /**
     * @param payload The payload
     */
    public void setPayload(JsonObject payload) {
        this.payload = payload;
    }

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return  gson.toJson(this);
    }
}
