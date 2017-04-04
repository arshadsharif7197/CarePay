package com.carecloud.carepaylibray.demographics;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;

/**
 * Created by cocampo on 4/3/17.
 */

public interface DemographicsView {
    <S> S getConvertedDTO(Class<S> dtoClass);

    void onBackPressed();

    void navigateToWorkflow(WorkflowDTO workflowDTO);

    FragmentManager getSupportFragmentManager();

    Context getContext();

    void showErrorNotification(String message);

    void updateCheckInFlow(CheckinFlowState flowState, int totalPages, int currentPage);
}
