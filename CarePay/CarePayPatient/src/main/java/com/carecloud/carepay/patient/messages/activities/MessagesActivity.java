package com.carecloud.carepay.patient.messages.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.MessagesViewModel;
import com.carecloud.carepay.patient.messages.fragments.MessagesConversationFragment;
import com.carecloud.carepay.patient.messages.fragments.MessagesListFragment;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingModelDto;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagesActivity extends MenuPatientActivity implements MessageNavigationCallback {

    private MessagingModelDto messagingDto;
    private MessagesViewModel viewModel;
    public static final String KEY_MESSAGE_ID = "messageId";
    public static final String KEY_MESSAGE_DEEPLINK_ID = "message_id";
    private String practiceName = "";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setUpViewModel();
        callMessagingService();
    }

    protected void setUpViewModel() {
        viewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        setBasicObservers(viewModel);
        viewModel.getSkeleton().observe(this, showSkeleton -> {
            if (showSkeleton) {
                replaceFragment(ShimmerFragment.newInstance(R.layout.shimmer_default_item), false);
            }
        });
    }

    private Messages.Reply getThread(String messageId) {
        for (Messages.Reply message : messagingDto.getPayload().getMessages().getData()) {
            if (message.getId().equals(messageId)) {
                return message;
            }
        }
        return null;
    }

    private void callMessagingService() {
        MutableLiveData<MessagingModelDto> observable = viewModel.getMessagesDto(getTransitionMessaging(), true);
        observable.observe(this, dto -> {
            messagingDto = dto;
            resumeOnCreate();
            observable.removeObservers(this);
        });
    }

    private void resumeOnCreate() {
        List<ProviderContact> providerContacts = messagingDto.getPayload().getProviderContacts();
        Collections.sort(providerContacts, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        replaceFragment(new MessagesListFragment(), false);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MessagesActivity.KEY_MESSAGE_ID)) {
            String messageId = intent.getStringExtra(KEY_MESSAGE_ID);
            if (!StringUtil.isNullOrEmpty(messageId)) {
                Messages.Reply thread = getThread(messageId);
                if (thread != null) {
                    displayThreadMessages(thread, practiceName, false);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupToolbar();
    }

    private void setupToolbar() {
        selectMenuItem(R.id.messagesMenuItem);
        displayToolbar(true, Label.getLabel("navigation_link_messages"));
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
    public void displayThreadMessages(Messages.Reply thread, String practiceName, boolean dismissAndRefresh) {
        this.practiceName = practiceName;
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
        addFragment(MessagesConversationFragment.newInstance(thread, practiceName), true);
    }

    @Override
    public MessagingModelDto getDto() {
        return messagingDto;
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
        callMessagingService();
    }

    @Override
    protected Profile getCurrentProfile() {
        return messagingDto.getPayload().getDelegate();
    }

    @Override
    public boolean canSendProvidersMessages() {
        return messagingDto.getPayload().canMessageProviders(messagingDto.getPayload().getPracticePatientIds().get(0).getPracticeId());
    }


}
