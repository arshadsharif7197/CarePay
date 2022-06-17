package com.carecloud.carepay.patient.appointments.interfaces;

import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

public interface IntelligentSchedulerCallback {
    void onVisitTypeSelected(VisitTypeDTO visitTypeDTO);

    void onCancel();
}
