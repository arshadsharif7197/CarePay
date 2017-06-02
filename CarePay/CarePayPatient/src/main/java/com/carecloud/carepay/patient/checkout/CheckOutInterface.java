package com.carecloud.carepay.patient.checkout;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 30/05/17.
 */

public interface CheckOutInterface extends FragmentActivityInterface {

    void showAllDoneFragment(WorkflowDTO workflowDTO);
}
