package com.carecloud.carepaylibray.checkout;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.appointments.interfaces.ScheduleAppointmentInterface;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 30/05/17.
 */

public interface CheckOutInterface extends AppointmentPrepaymentCallback, FragmentActivityInterface,
        ScheduleAppointmentInterface {

    void showAllDone(WorkflowDTO workflowDTO);

    void navigateToWorkflow(WorkflowDTO workflowDTO);

    boolean shouldAllowNavigateBack();

    void completeCheckout(boolean paymentMade,
                          double paymentAmount,
                          boolean surveyAvailable,
                          boolean paymentPlanCreated);

    void startSurveyFlow(WorkflowDTO workflowDTO);
}
