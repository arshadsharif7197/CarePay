package com.carecloud.carepaylibray.demographics;

import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 30/10/17.
 */

public interface EmergencyContactInterface extends FragmentActivityInterface {

    void showAddEditEmergencyContactDialog(PatientModel emergencyContact);
}
