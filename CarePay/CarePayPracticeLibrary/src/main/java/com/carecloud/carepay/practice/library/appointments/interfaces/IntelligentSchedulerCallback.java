package com.carecloud.carepay.practice.library.appointments.interfaces;

import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeQuestions;

public interface IntelligentSchedulerCallback {
    void onVisitTypeSelected(VisitTypeDTO visitTypeDTO);

    void onOptionSelected(VisitTypeQuestions visitTypeQuestion);

    void onExit();

    void onBack();
}
