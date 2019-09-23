package com.carecloud.carepay.practice.library.adhocforms;

import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

import java.util.List;

/**
 * @author pjohnson on 6/07/17.
 */

public interface AdHocFormsInterface extends FragmentActivityInterface {
    List<PracticeForm> getFormsList();

    void highlightFormName(int displayedFormsIndex);

    void showAllDone(AdHocFormCompletedDialogFragment workflowDTO);
}
