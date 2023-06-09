package com.carecloud.carepaylibray.demographics.interfaces;

import androidx.appcompat.widget.Toolbar;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.PhysicianDto;
import com.carecloud.carepaylibray.interfaces.DTOInterface;

/**
 * @author pjohnson on 13/11/17.
 */

public interface PhysicianInterface extends DTOInterface {

    void setToolbar(Toolbar toolbar);

    void showSearchPhysicianFragmentDialog(PhysicianDto physicianDto, int physicianType);

    void onPhysicianSelected(PhysicianDto physician, int physicianType);

    AppointmentDTO getAppointment();
}
