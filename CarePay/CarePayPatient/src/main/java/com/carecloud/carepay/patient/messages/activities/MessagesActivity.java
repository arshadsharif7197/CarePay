package com.carecloud.carepay.patient.messages.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.fragments.MessagesConversationFragment;
import com.carecloud.carepay.patient.messages.fragments.MessagesListFragment;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingModel;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagesActivity extends MenuPatientActivity implements MessageNavigationCallback {

    private MessagingModel messagingModel;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        messagingModel = getConvertedDTO(MessagingModel.class);
        if (messagingModel == null) {
            callMessagingService(true);
        } else {
            resumeOnCreate();
        }
    }

    private void resumeOnCreate() {
        List<ProviderContact> providerContacts = messagingModel.getPayload().getProviderContacts();
        Collections.sort(providerContacts, new Comparator<ProviderContact>() {
            @Override
            public int compare(ProviderContact o1, ProviderContact o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        replaceFragment(new MessagesListFragment(), false);
    }

    private void callMessagingService(final boolean showShimmerEffect) {
        getWorkflowServiceHelper().execute(getTransitionMessaging(), new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                if (showShimmerEffect) {
                    replaceFragment(ShimmerFragment.newInstance(R.layout.shimmer_default_item), false);
                }
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                messagingModel = DtoHelper.getConvertedDTO(MessagingModel.class, workflowDTO);
                resumeOnCreate();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupToolbar();
    }

    private void setupToolbar() {
        selectMenuItem(R.id.messagesMenuItem);
        displayToolbar(true, getScreenTitle(Label.getLabel("navigation_link_messages")));
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void displayThreadMessages(Messages.Reply thread, boolean dismissAndRefresh) {
        if (dismissAndRefresh) {
            getSupportFragmentManager().popBackStackImmediate();
            getSupportFragmentManager().popBackStackImmediate();
            if (getSupportFragmentManager().findFragmentById(R.id.container_main) instanceof MessagesListFragment) {
                ((MessagesListFragment) getSupportFragmentManager().findFragmentById(R.id.container_main))
                        .refreshListMessages();
            }
        }
        if (!thread.isRead()) {
            ApplicationPreferences.getInstance()
                    .setMessagesBadgeCounter(ApplicationPreferences.getInstance().getMessagesBadgeCounter() - 1);
            updateBadgeCounterViews();
        }
        addFragment(MessagesConversationFragment.newInstance(thread), true);
    }

    @Override
    public MessagingModel getDto() {
        return messagingModel;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            setupToolbar();
        }
        super.onBackPressed();
    }

    @Override
    protected void onProfileChanged(ProfileDto profile) {
        displayToolbar(true, getScreenTitle(Label.getLabel("navigation_link_messages")));
        callMessagingService(true);
    }

    @Override
    protected Profile getCurrentProfile() {
        return messagingModel.getPayload().getDelegate();
    }

    @Override
    public boolean canSendProvidersMessages() {
        for (UserPracticeDTO practiceDTO : messagingModel.getPayload().getUserPractices()) {
            if (messagingModel.getPayload().canMessageProviders(practiceDTO.getPracticeId())) {
                return true;
            }
        }
        return false;
    }

}
