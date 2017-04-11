package com.carecloud.carepay.patient.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * Common Activity class for any practice.
 * Use for holding the common DTO which will be converted to the desire DTO using getConvertedDTO
 */

public abstract class BasePatientActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    /**
     * Common WorkflowDTO which will converted to the desire DTO with dtoClass params
     *
     * @param dtoClass class to convert
     * @param <S>      Dynamic class to convert
     * @return Dynamic converted class object
     */
    public <S> S getConvertedDTO(Class<S> dtoClass, String jsonString) {

        if (!StringUtil.isNullOrEmpty(jsonString)) {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, dtoClass);
        }
        return null;
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        PatientNavigationHelper.getInstance(this).navigateToWorkflow(workflowDTO);
    }
}
