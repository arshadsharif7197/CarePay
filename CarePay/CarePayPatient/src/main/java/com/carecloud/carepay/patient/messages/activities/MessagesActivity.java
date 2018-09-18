package com.carecloud.carepay.patient.messages.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.fragments.MessagesConversationFragment;
import com.carecloud.carepay.patient.messages.fragments.MessagesListFragment;
import com.carecloud.carepay.patient.messages.fragments.MessagesProvidersFragment;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingModel;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagesActivity extends MenuPatientActivity implements MessageNavigationCallback {

    private MessagingModel messagingModel;
    private String userId;

    private List<ProviderContact> providerContacts = new ArrayList<>();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        messagingModel = getConvertedDTO(MessagingModel.class);
        if (messagingModel == null) {
            callMessagingService();
        } else {
            resumeOnCreate();
        }
    }

    private void resumeOnCreate() {
        userId = messagingModel.getPayload().getInbox().getUserId();
        providerContacts = messagingModel.getPayload().getProviderContacts();
        replaceFragment(new MessagesListFragment(), false);
    }

    private void callMessagingService() {
        getWorkflowServiceHelper().execute(getTransitionMessaging(), new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                replaceFragment(ShimmerFragment.newInstance(R.layout.shimmer_default_item), false);
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
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_messages);
        menuItem.setChecked(true);
        displayToolbar(true, menuItem.getTitle().toString());
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void displayThreadMessages(Messages.Reply thread) {
        if (!thread.isRead()) {
            ApplicationPreferences.getInstance()
                    .setMessagesBadgeCounter(ApplicationPreferences.getInstance().getMessagesBadgeCounter() - 1);
            updateBadgeCounterViews();
        }
        replaceFragment(MessagesConversationFragment.newInstance(thread), true);
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void startNewThread() {
        replaceFragment(new MessagesProvidersFragment(), true);
    }

    @Override
    public List<ProviderContact> getProvidersList() {
        return providerContacts;
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
    public String lookupName(Messages.Reply thread, String userId) {
        for (Messages.Participant participant : thread.getParticipants()) {
            if (participant.getUserId().equals(userId)) {
                return participant.getName();
            }
        }
        return null;
    }
}
