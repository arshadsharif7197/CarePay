package com.carecloud.carepaylibray.demographics.interfaces;

import androidx.appcompat.widget.Toolbar;

import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.interfaces.DTOInterface;

/**
 * @author pjohnson on 30/10/17.
 */

public interface EmergencyContactInterface extends DTOInterface {

    void showAddEditEmergencyContactDialog();

    void updateEmergencyContact(PatientModel emergencyContact);

    void setToolbar(Toolbar toolbar);
}
