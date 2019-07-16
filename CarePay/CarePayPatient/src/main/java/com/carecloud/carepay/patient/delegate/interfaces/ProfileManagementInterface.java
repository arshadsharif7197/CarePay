package com.carecloud.carepay.patient.delegate.interfaces;

import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.profile.UserLinks;

/**
 * @author pjohnson on 2019-06-27.
 */
public interface ProfileManagementInterface extends FragmentActivityInterface {

    void updateProfiles(UserLinks userLinks);
}
