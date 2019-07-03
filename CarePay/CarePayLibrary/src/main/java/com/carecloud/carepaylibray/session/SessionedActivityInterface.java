package com.carecloud.carepaylibray.session;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;

/**
 * @author pjohnson on 2019-07-02.
 */
public interface SessionedActivityInterface {

    boolean manageSession();

    TransitionDTO getLogoutTransition();
}
