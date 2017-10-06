package com.carecloud.carepaylibray.demographics;


import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 5/10/17.
 */

public interface EmployerInterface extends FragmentActivityInterface {

    void addEmployer(EmployerDto employer);

    void displayAddEmployerFragment();

    void displaySearchEmployer();

    void displayEmployerDetail(EmployerDto employer);
}
