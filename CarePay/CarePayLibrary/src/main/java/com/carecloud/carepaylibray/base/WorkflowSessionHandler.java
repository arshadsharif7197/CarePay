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

    public static void createSession(Context context){
        SharedPreferences sessionPreferences = context.getSharedPreferences(WORKFLOW_SESSION, Context.MODE_PRIVATE);
        long workingSession = sessionPreferences.getLong(SESSION_KEY, -1);

        //delete all objects from previous Session
        deletePreviousSessionData(workingSession);

        workingSession++;
        sessionPreferences.edit().putLong(SESSION_KEY, workingSession).apply();
    }

    public static long getCurrentSession(Context context){
        SharedPreferences sessionPreferences = context.getSharedPreferences(WORKFLOW_SESSION, Context.MODE_PRIVATE);
        return sessionPreferences.getLong(SESSION_KEY, -1);
    }

    private static void deletePreviousSessionData(Long session){
        WorkFlowRecord.deleteAll(WorkFlowRecord.class, NamingHelper.toSQLNameDefault("sessionKey") + " <= ?", session.toString());
    }

}
