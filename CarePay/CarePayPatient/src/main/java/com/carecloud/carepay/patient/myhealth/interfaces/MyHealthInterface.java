package com.carecloud.carepay.patient.myhealth.interfaces;

import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 17/07/17.
 */
public interface MyHealthInterface extends MyHealthDataInterface, FragmentActivityInterface {
    void showListFragment(int type);

    void displayToolbar(boolean display, String title);
}
