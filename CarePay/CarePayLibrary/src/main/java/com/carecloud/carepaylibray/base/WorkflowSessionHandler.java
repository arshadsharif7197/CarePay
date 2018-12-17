package com.carecloud.carepaylibray.base;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;

/**
 * Created by lmenendez on 4/24/17.
 *
 */

public class WorkflowSessionHandler {
    private static final String WORKFLOW_SESSION = "workflow_session";
    private static final String SESSION_KEY = "session";

    /**
     * Create a new Session and cleanup any old Session records from the Workflow DB
     * @param context context
     */
    public static void createSession(Context context){
        SharedPreferences sessionPreferences = context.getSharedPreferences(WORKFLOW_SESSION, Context.MODE_PRIVATE);
        long workingSession = sessionPreferences.getLong(SESSION_KEY, -1);

        //delete all objects from previous Session
        deletePreviousSessionData(context);

        workingSession++;
        sessionPreferences.edit().putLong(SESSION_KEY, workingSession).apply();
        createNewSession(context);
    }

    /**
     * Get currently Active session for Workflow DB
     * @param context context
     * @return current workflow session of -1 if no current session
     */
    public static long getCurrentSession(Context context){
        SharedPreferences sessionPreferences = context.getSharedPreferences(WORKFLOW_SESSION, Context.MODE_PRIVATE);
        return sessionPreferences.getLong(SESSION_KEY, -1);
    }

    private static void createNewSession(Context context){
        File cacheDir = new File(context.getCacheDir(), WORKFLOW_SESSION);
        File session = new File(cacheDir, String.valueOf(getCurrentSession(context)));
        if(!session.exists() && !session.mkdirs()){
            throw new RuntimeException("Unable to create new workflow session");
        }
    }

    private static void deletePreviousSessionData(Context context){
        File cacheDir = new File(context.getCacheDir(), WORKFLOW_SESSION);
        try {
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                throw new IOException("Unable to create Cache Directory");
            }
            File[] sessions = cacheDir.listFiles();
            for(File session : sessions){
                if(session.isDirectory()){
                    flushDirectory(session);
                }
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
            throw new RuntimeException("Unable to delete Previous Session Data\n" + ioe.getMessage());
        }

    }

    private static void flushDirectory(File directory) throws IOException {
        if(!directory.isDirectory()){
            throw new IOException("Not a valid directory: "+directory);
        }
        File[] files = directory.listFiles();
        for(File file : files){
            if(file.isDirectory()){
                flushDirectory(file);
            }else if(!file.delete()){
                throw new IOException("Unable to delete file: "+file);
            }
        }
        files = directory.listFiles();//re-list files after delete
        if(files.length == 0 && !directory.delete()){
            throw new IOException("Unable to Delete Empty Directory");
        }
    }
}
