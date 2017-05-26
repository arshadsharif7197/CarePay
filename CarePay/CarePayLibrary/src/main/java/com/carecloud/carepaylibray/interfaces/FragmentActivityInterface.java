package com.carecloud.carepaylibray.interfaces;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
 * @author pjohnson on 26/04/17.
 */

public interface FragmentActivityInterface extends DTOInterface {
    void replaceFragment(Fragment fragment, boolean addToBackStack);

    void addFragment(Fragment fragment, boolean addToBackStack);

    void showErrorToast(String exceptionMessage);

    void setToolbar(Toolbar toolbar);

    void showSuccessToast(String successMessage);

    void setActionBarTitle(String title);

    void displayDialogFragment(DialogFragment fragment, boolean addToBackStack);
}
