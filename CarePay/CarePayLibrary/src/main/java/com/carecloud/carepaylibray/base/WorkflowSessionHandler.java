package com.carecloud.carepaylibray.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.orm.util.NamingHelper;

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
        deletePreviousSessionData(workingSession);

        workingSession++;
        sessionPreferences.edit().putLong(SESSION_KEY, workingSession).apply();
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

    private static void deletePreviousSessionData(Long session){
        WorkFlowRecord.deleteAll(WorkFlowRecord.class, NamingHelper.toSQLNameDefault("sessionKey") + " <= ?", session.toString());
    }

}
