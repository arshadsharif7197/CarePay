package com.carecloud.carepaylibray.demographics;


import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepaylibray.interfaces.DTOInterface;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 5/10/17.
 */

public interface EmployerInterface extends DTOInterface {

    void addEmployer(EmployerDto employer);

    void displayAddEmployerFragment();

    void displaySearchEmployer();

    void displayEmployerDetail(EmployerDto employer);

    void addFragment(Fragment fragment, boolean addToBackStack);

    void setToolbar(Toolbar toolbar);

    void showErrorToast(String exceptionMessage);
}
