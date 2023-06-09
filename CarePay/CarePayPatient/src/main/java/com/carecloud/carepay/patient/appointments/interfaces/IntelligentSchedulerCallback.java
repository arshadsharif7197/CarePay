package com.carecloud.carepay.patient.appointments.interfaces;

import com.carecloud.carepaylibray.appointments.models.VisitTypeQuestions;

public interface IntelligentSchedulerCallback {
    void onVisitTypeSelected(VisitTypeQuestions visitTypeDTO);

    void onOptionSelected(VisitTypeQuestions visitTypeQuestion);

    void onViewAnswerClicked();

    void onExit();

    void onBack();
}
