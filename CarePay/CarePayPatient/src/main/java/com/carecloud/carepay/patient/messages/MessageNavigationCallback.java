package com.carecloud.carepay.patient.messages;

import android.support.v4.app.Fragment;

import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;

/**
 * Created by lmenendez on 6/30/17
 */

public interface MessageNavigationCallback {

    void replaceFragment(Fragment fragment, boolean addToBackStack);

    void displayThreadMessages(Messages.Reply thread);

    void getMessageThreads(long page, long size);

    void getThreadMessages(Messages.Reply thread);

    String getUserId();

    void postMessage(Messages.Reply thread, String message);

    void postNewMessage(ProviderDTO providerDTO, String subject, String message);

    void startNewThread();

    void getProvidersList();

    void deleteMessageThread(Messages.Reply thread);

    void displayToolbar(boolean display, String title);

}
