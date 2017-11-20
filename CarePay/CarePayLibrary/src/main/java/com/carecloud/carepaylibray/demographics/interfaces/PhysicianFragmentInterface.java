package com.carecloud.carepaylibray.demographics.interfaces;

import com.carecloud.carepaylibray.demographics.dtos.payload.PhysicianDto;

/**
 * @author pjohnson on 15/11/17.
 */

public interface PhysicianFragmentInterface {
    void setPhysician(PhysicianDto physician, int physicianType);
}
