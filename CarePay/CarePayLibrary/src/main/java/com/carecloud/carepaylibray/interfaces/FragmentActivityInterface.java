package com.carecloud.carepaylibray.interfaces;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

/**
 * @author pjohnson on 26/04/17.
 */

public interface FragmentActivityInterface extends DTOInterface {
    void addFragment(Fragment fragment, boolean addToBackStack);

    void replaceFragment(Fragment fragment, boolean addToBackStack);

    void displayDialogFragment(DialogFragment fragment, boolean addToBackStack);

    void setToolbar(Toolbar toolbar);

    void setActionBarTitle(String title);

    @Deprecated
    void showSuccessToast(String successMessage);

    @Deprecated
    void showErrorToast(String exceptionMessage);
}
