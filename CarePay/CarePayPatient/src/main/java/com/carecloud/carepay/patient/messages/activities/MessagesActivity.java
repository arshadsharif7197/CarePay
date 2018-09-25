package com.carecloud.carepay.patient.messages.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.fragments.MessagesConversationFragment;
import com.carecloud.carepay.patient.messages.fragments.MessagesListFragment;
import com.carecloud.carepay.patient.messages.fragments.MessagesProvidersFragment;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingDataModel;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.RestCallServiceCallback;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.RestDef;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagesActivity extends MenuPatientActivity implements MessageNavigationCallback {

    private RestCallServiceHelper restCallServiceHelper;
    private String userId;

    private List<ProviderContact> providerContacts = new ArrayList<>();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        restCallServiceHelper = new RestCallServiceHelper(getAppAuthorizationHelper(), getApplicationMode());
        replaceFragment(new MessagesListFragment(), false);
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
    public void getMessageThreads(long page, long size, boolean showShimmerEffect) {
        Map<String, String> queryMap = new HashMap<>();
        //first page is 1
        queryMap.put(getString(R.string.msg_query_page), String.valueOf(page < 1 ? 1 : page));
        //default size if 15, should not be less than that
        queryMap.put(getString(R.string.msg_query_size), String.valueOf(size < 15 ? 15 : size));

        String body = null;

        restCallServiceHelper.executeRequest(RestDef.GET,
                HttpConstants.getMessagingBaseUrl(),
                getMessageThreadsCallback(showShimmerEffect),
                true,
                getString(R.string.msg_auth_token_key),
                queryMap,
                null,
                body,
                getString(R.string.msg_path_start));
    }

    @Override
    public void getThreadMessages(Messages.Reply thread) {
        String body = null;

        restCallServiceHelper.executeRequest(RestDef.GET,
                HttpConstants.getMessagingBaseUrl(),
                getMessagesCallback,
                true,
                getString(R.string.msg_auth_token_key),
                null,
                null,
                body,
                getString(R.string.msg_path_message), thread.getId());
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void postMessage(Messages.Reply thread, String message) {
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put(getString(R.string.msg_field_message_text), message);
        fieldMap.put(getString(R.string.msg_field_participant_id), userId);
        fieldMap.put(getString(R.string.msg_field_participant_name), lookupName(thread, userId));

        restCallServiceHelper.executeRequest(RestDef.POST,
                HttpConstants.getMessagingBaseUrl(),
                postReplyCallback,
                true,
                getString(R.string.msg_auth_token_key),
                null,
                null,
                fieldMap,
                getString(R.string.msg_path_message), thread.getId());
    }

    @Override
    public void postNewMessage(ProviderContact provider, String subject, String message) {
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put(getString(R.string.msg_field_message_text), message);
        fieldMap.put(getString(R.string.msg_field_message_subject), subject);
        fieldMap.put(getString(R.string.msg_field_participant_id), provider.getId());
        fieldMap.put(getString(R.string.msg_field_participant_name), provider.getName());
        fieldMap.put(getString(R.string.msg_field_participant_type), getString(R.string.msg_field_type_value_provider));
        fieldMap.put(getString(R.string.msg_field_linked_id), provider.getPatientId());
        fieldMap.put(getString(R.string.msg_field_linked_type), getString(R.string.msg_field_type_value_patient));

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put(getString(R.string.msg_query_source), getString(R.string.msg_query_source_value));

        restCallServiceHelper.executeRequest(RestDef.POST,
                HttpConstants.getMessagingBaseUrl(),
                getPostNewMessageCallback(provider),
                true,
                getString(R.string.msg_auth_token_key),
                queryMap,
                null,
                fieldMap,
                getString(R.string.msg_path_message));

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
    public void deleteMessageThread(Messages.Reply thread) {
        String body = null;

        restCallServiceHelper.executeRequest(RestDef.PUT,
                HttpConstants.getMessagingBaseUrl(),
                deleteThreadCallback,
                true,
                getString(R.string.msg_auth_token_key),
                null,
                null,
                body,
                getString(R.string.msg_path_message), thread.getId(), getString(R.string.msg_path_action_delete));
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            setupToolbar();
        }
        super.onBackPressed();
    }

    private RestCallServiceCallback getMessageThreadsCallback(final boolean showShimmerEffect) {
        return new RestCallServiceCallback() {
            @Override
            public void onPreExecute() {
                if (showShimmerEffect) {
                    replaceFragment(ShimmerFragment.newInstance(R.layout.shimmer_default_item), true);
                } else {
                    showProgressDialog();
                }
            }

            @Override
            public void onPostExecute(JsonElement jsonElement) {
                hideProgressDialog();
                if (getSupportFragmentManager().findFragmentById(R.id.container_main) instanceof ShimmerFragment) {
                    if(isVisible){
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                }
                Gson gson = new Gson();
                MessagingDataModel messagingDataModel = gson.fromJson(jsonElement, MessagingDataModel.class);
                userId = messagingDataModel.getInbox().getUserId();
                providerContacts = messagingDataModel.getProviderContacts();
                try {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    MessagesListFragment messagesListFragment = (MessagesListFragment) fragmentManager
                            .findFragmentById(R.id.container_main);
                    messagesListFragment.updateDisplayDataModel(messagingDataModel);
                } catch (ClassCastException cce) {
                    cce.printStackTrace();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                hideProgressDialog();
                showErrorNotification(errorMessage);
            }
        };

    }

    private RestCallServiceCallback getMessagesCallback = new RestCallServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            hideProgressDialog();
            loadConversationsList(jsonElement);
        }

        @Override
        public void onFailure(String errorMessage) {
            hideProgressDialog();
            showErrorNotification(errorMessage);
        }
    };

    private RestCallServiceCallback postReplyCallback = new RestCallServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            hideProgressDialog();
            loadConversationsList(jsonElement);

            MixPanelUtil.logEvent(getString(R.string.event_message_reply));
        }

        @Override
        public void onFailure(String errorMessage) {
            hideProgressDialog();
            showErrorNotification(errorMessage);
        }
    };

    private RestCallServiceCallback getPostNewMessageCallback(final ProviderContact provider) {
        return new RestCallServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(JsonElement jsonElement) {
                hideProgressDialog();
                Gson gson = new Gson();
                Messages.Reply thread = gson.fromJson(jsonElement, Messages.Reply.class);
                getSupportFragmentManager().popBackStackImmediate(MessagesProvidersFragment.class.getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                replaceFragment(MessagesConversationFragment.newInstance(thread), true);

                String[] params = {getString(R.string.param_provider_id), getString(R.string.param_provider_name)};
                Object[] values = {provider.getId(), provider.getName()};
                MixPanelUtil.logEvent(getString(R.string.event_message_new), params, values);
            }

            @Override
            public void onFailure(String errorMessage) {
                hideProgressDialog();
                showErrorNotification(errorMessage);
            }
        };
    }

    private RestCallServiceCallback deleteThreadCallback = new RestCallServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            Log.d(MessagesActivity.class.getName(), "Thread Deleted Successfully");
        }

        @Override
        public void onFailure(String errorMessage) {
            Log.d(MessagesActivity.class.getName(), "Thread Delete Failed");
        }
    };

    private void loadConversationsList(JsonElement jsonElement) {
        Gson gson = new Gson();
        Messages.Reply thread = gson.fromJson(jsonElement, Messages.Reply.class);

        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            MessagesConversationFragment conversationFragment = (MessagesConversationFragment) fragmentManager
                    .findFragmentById(R.id.container_main);
            conversationFragment.updateThreadMessages(thread);
        } catch (ClassCastException cce) {
            cce.printStackTrace();
        }
    }

    private static String lookupName(Messages.Reply thread, String userId) {
        for (Messages.Participant participant : thread.getParticipants()) {
            if (participant.getUserId().equals(userId)) {
                return participant.getName();
            }
        }
        return null;
    }
}
