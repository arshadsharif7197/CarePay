package com.carecloud.carepay.patient.messages;

import android.support.v4.app.Fragment;

import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.ProviderContact;

import java.util.List;

/**
 * Created by lmenendez on 6/30/17
 */

public interface MessageNavigationCallback {

    void replaceFragment(Fragment fragment, boolean addToBackStack);

    void displayThreadMessages(Messages.Reply thread);

    void getMessageThreads(long page, long size, boolean showShimmerEffect);

    void getThreadMessages(Messages.Reply thread);

    String getUserId();

    void postMessage(Messages.Reply thread, String message);

    void postNewMessage(ProviderContact providerContact, String subject, String message);

    void startNewThread();

    List<ProviderContact> getProvidersList();

    void deleteMessageThread(Messages.Reply thread);

    void displayToolbar(boolean display, String title);

}
