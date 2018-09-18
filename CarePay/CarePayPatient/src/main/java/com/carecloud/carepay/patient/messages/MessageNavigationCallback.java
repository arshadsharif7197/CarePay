package com.carecloud.carepay.patient.messages;

import android.support.v4.app.Fragment;

import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingModel;
import com.carecloud.carepay.patient.messages.models.ProviderContact;

import java.util.List;

/**
 * Created by lmenendez on 6/30/17
 */

public interface MessageNavigationCallback {

    void replaceFragment(Fragment fragment, boolean addToBackStack);

    void displayThreadMessages(Messages.Reply thread);

    String getUserId();

    void startNewThread();

    List<ProviderContact> getProvidersList();

    void displayToolbar(boolean display, String title);

    MessagingModel getDto();

    String lookupName(Messages.Reply thread, String userId);

}
