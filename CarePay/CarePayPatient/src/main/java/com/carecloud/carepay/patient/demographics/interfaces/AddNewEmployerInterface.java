package com.carecloud.carepay.patient.demographics.interfaces;


import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 5/10/17.
 */

public interface AddNewEmployerInterface extends FragmentActivityInterface {

    void addEmployer(EmployerDto employer);
}
