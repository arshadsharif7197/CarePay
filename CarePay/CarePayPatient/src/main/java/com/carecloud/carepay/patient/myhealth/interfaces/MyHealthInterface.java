package com.carecloud.carepay.patient.myhealth.interfaces;

import com.carecloud.carepay.patient.base.ToolbarInterface;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 17/07/17.
 */
public interface MyHealthInterface extends MyHealthDataInterface, FragmentActivityInterface, ToolbarInterface {
    void showListFragment(int type);
}
