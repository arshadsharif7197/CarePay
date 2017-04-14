package com.carecloud.carepay.service.library.dtos;

import com.orm.SugarRecord;

public class WorkFlowRecord extends SugarRecord {
    private String metadata;
    private String payload;
    private String state;

    /**
     * Do not delete - SugarRecord needs it
     */
    public WorkFlowRecord() {

    }

    /**
     * @param dto Workflow DTO
     */
    public WorkFlowRecord(WorkflowDTO dto) {
        metadata = dto.getMetadata().toString();
        payload = dto.getPayload().toString();
        state = dto.getState();
    }

    public String getMetadata() {
        return metadata;
    }

    public String getPayload() {
        return payload;
    }

    public String getState() {
        return state;
    }
}