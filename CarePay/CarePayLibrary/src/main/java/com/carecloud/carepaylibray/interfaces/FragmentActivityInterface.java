package com.carecloud.carepaylibray.interfaces;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
 * @author pjohnson on 26/04/17.
 */

public interface FragmentActivityInterface extends DTOInterface {
    void replaceFragment(Fragment fragment, boolean addToBackStack);

    void showErrorToast(String exceptionMessage);

    void setToolbar(Toolbar toolbar);

    void showSuccessToast(String forgot_password_confirmation_success_message);

    void setActionBarTitle(String forgot_password_confirmation_screen_title);
}
