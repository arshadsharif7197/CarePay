package com.carecloud.carepaylibray.demographics;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;

public interface DemographicsView {
    <S> S getConvertedDTO(Class<S> dtoClass);

    void onBackPressed();

    void navigateToWorkflow(WorkflowDTO workflowDTO);

    /**
     * Consent form navigation
     *
     * @param workflowDTO consent DTO
     */
    void navigateToConsentForms(WorkflowDTO workflowDTO);

    void navigateToIntakeForms(WorkflowDTO workflowDTO);

    void navigateToMedicationsAllergy(WorkflowDTO workflowDTO);

    FragmentManager getSupportFragmentManager();

    Context getContext();

    void showErrorNotification(String message);

    void updateCheckInFlow(CheckinFlowState flowState, int totalPages, int currentPage);

    DemographicsPresenter getPresenter();

    void finish();
}
