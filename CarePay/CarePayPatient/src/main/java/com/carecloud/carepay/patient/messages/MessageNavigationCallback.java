package com.carecloud.carepay.patient.messages;

import android.support.v4.app.Fragment;

/**
 * Created by lmenendez on 6/30/17
 */

public interface MessageNavigationCallback {

    void replaceFragment(Fragment fragment, boolean addToBackStack);
}
