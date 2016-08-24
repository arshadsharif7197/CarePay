package com.carecloud.carepaylibray.services.interfaces;

import com.carecloud.carepaylibray.models.WorkflowModel;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Jahirul Bhuiyan on 8/23/2016.
 */
public interface WorkflowScreenService {
    @GET(value = "api/shamrock/json/carecloud")
    Call<WorkflowModel> getScreenWorkflow();
}
