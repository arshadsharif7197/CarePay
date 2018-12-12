package com.carecloud.carepay.service.library.dtos;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

public class WorkFlowRecord {
    private static final String WORKFLOW_SESSION = "workflow_session";

    private static long lastfindId;
    private static WorkFlowRecord lastFindRecord;

    private String metadata;
    private String payload;
    private String state;

    private Long sessionKey;

    /**
     * @param dto Workflow DTO
     */
    public WorkFlowRecord(WorkflowDTO dto) {
        metadata = dto.getMetadata().toString();
        payload = dto.getPayload().toString();
        state = dto.getState();
    }

    public WorkFlowRecord(WorkflowDTO dto, Long sessionKey) {
        this(dto);
        this.sessionKey = sessionKey;
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

    public void setSessionKey(Long sessionKey) {
        this.sessionKey = sessionKey;
    }

    public long save(Context context) {
        File cacheDir = new File(context.getCacheDir(), WORKFLOW_SESSION);
        File session = new File(cacheDir, String.valueOf(sessionKey));

        long id = (long) (System.currentTimeMillis() * Math.random());
        File save = new File(session, String.valueOf(id));
        try {
            if (!save.exists() && !save.createNewFile()) {
                throw new RuntimeException("Unable to create workflow record: " + id);
            }
            String fileData = new Gson().toJson(this);
            FileWriter writer = new FileWriter(save);
            writer.write(fileData);
            writer.flush();
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return id;
    }

    public static WorkFlowRecord findById(Context context, long id) {
        if(id == lastfindId && lastFindRecord != null){
            return lastFindRecord;
        }

        File cacheDir = new File(context.getCacheDir(), WORKFLOW_SESSION);
        File findFile = findFileByName(String.valueOf(id), cacheDir);
        if (findFile != null) {
            WorkFlowRecord findRecord = getWorkflowRecordFromFile(findFile);
            lastfindId = id;
            lastFindRecord = findRecord;
            return findRecord;
        }
        return null;
    }

    private static File findFileByName(String name, File directory) {
        if (name == null) {
            throw new IllegalArgumentException("File Search name cannot be null");
        }
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                File sub = findFileByName(name, file);
                if (sub != null) {
                    return sub;
                }
            } else if (name.equalsIgnoreCase(file.getName())) {
                return file;
            }
        }
        return null;
    }

    private static WorkFlowRecord getWorkflowRecordFromFile(File file) {
        if (!file.exists()) {
            return null;
        }
        try {
            Reader reader = new FileReader(file);
            return new Gson().fromJson(reader, WorkFlowRecord.class);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

}