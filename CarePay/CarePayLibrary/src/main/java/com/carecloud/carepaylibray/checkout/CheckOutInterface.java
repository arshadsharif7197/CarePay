package com.carecloud.carepaylibray.checkout;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

import java.util.Date;

/**
 * @author pjohnson on 30/05/17.
 */

public interface CheckOutInterface extends AppointmentPrepaymentCallback, FragmentActivityInterface {

    void showAvailableHoursFragment(Date startDate, Date endDate,
                                    AppointmentsResultModel appointmentsResultModel,
                                    AppointmentResourcesItemDTO resourcesItemDTO,
                                    VisitTypeDTO visitTypeDTO);



    void showAllDone(WorkflowDTO workflowDTO);

    void navigateToWorkflow(WorkflowDTO workflowDTO);

    boolean shouldAllowNavigateBack();

    void showChooseProviderFragment();

    void completeCheckout(boolean paymentMade, double paymentAmount, boolean surveyAvailable, boolean paymentPlanCreated);

    void startSurveyFlow(WorkflowDTO workflowDTO);
}
