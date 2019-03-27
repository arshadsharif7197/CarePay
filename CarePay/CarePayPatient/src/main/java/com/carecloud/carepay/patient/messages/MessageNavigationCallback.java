package com.carecloud.carepay.patient.messages;

import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * Created by lmenendez on 6/30/17
 */

public interface MessageNavigationCallback extends FragmentActivityInterface {

    void displayThreadMessages(Messages.Reply thread, boolean dismissDialogs);

    void displayToolbar(boolean display, String title);

}
