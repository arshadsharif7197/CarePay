package com.carecloud.carepay.practice.library.signin.interfaces;

import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionUserPractice;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.interfaces.DTOInterface;

/**
 * @author pjohnson on 1/05/17.
 */
public interface SelectPracticeCallback extends DTOInterface {
    void onSelectPractice(PracticeSelectionUserPractice userPractice);

    void onSelectPracticeLocation(PracticeSelectionUserPractice selectedPractice, LocationDTO locationDTO);

    void onSelectPracticeLocation(WorkflowDTO workflowDTO, PracticeSelectionUserPractice selectedPractice, LocationDTO locationDTO);

    void onSelectPracticeCanceled();

    void onSelectPracticeLocationCanceled(PracticeSelectionUserPractice selectedPractice);
}