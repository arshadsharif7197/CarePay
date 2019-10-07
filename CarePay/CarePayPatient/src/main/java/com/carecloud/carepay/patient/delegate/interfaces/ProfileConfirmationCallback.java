package com.carecloud.carepay.patient.delegate.interfaces;

import com.carecloud.carepaylibray.profile.ProfileDto;

/**
 * @author pjohnson on 2019-07-12.
 */
public interface ProfileConfirmationCallback {
    void confirmAction(ProfileDto profile);

    void cancel();
}
