package com.carecloud.carepay.patient.messages.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.fragments.MessagesConversationFragment;
import com.carecloud.carepay.patient.messages.fragments.MessagesListFragment;
import com.carecloud.carepay.patient.messages.models.Messages;
import com.carecloud.carepay.patient.messages.models.MessagingDataModel;
import com.carecloud.carepay.service.library.RestCallServiceCallback;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.RestDef;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagesActivity extends MenuPatientActivity implements MessageNavigationCallback {

    RestCallServiceHelper restCallServiceHelper;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        restCallServiceHelper = new RestCallServiceHelper(getAppAuthorizationHelper(), getApplicationMode());
        replaceFragment(new MessagesListFragment(), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupToolbar();
    }

    private void setupToolbar(){
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
        displayToolbar(false, null);
        replaceFragment(MessagesConversationFragment.newInstance(thread), true);
    }

    @Override
    public void getMessageThreads(int page, int size) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put(getString(R.string.msg_query_page), String.valueOf(page < 1 ? 1 : page));//first page is 1
        queryMap.put(getString(R.string.msg_query_size), String.valueOf(size < 15 ? 15 : size));//default size if 15, should not be less than that

        restCallServiceHelper.executeRequest(RestDef.GET,
                HttpConstants.getMessagingBaseUrl(),
                getMessageThreadsCallback,
                true,
                getString(R.string.msg_auth_token_key),
                queryMap,
                null,
                null,
                getString(R.string.msg_path_start));
    }

    @Override
    public void getThreadMessages(Messages.Reply thread) {
        restCallServiceHelper.executeRequest(RestDef.GET,
                HttpConstants.getMessagingBaseUrl(),
                getMessagesCallback,
                true,
                getString(R.string.msg_auth_token_key),
                null,
                null,
                null,
                getString(R.string.msg_path_message), thread.getId());
    }

    @Override
    public void onBackPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            setupToolbar();
        }
        super.onBackPressed();
    }

    private RestCallServiceCallback getMessageThreadsCallback = new RestCallServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            hideProgressDialog();
            Gson gson = new Gson();
            MessagingDataModel messagingDataModel = gson.fromJson(jsonElement, MessagingDataModel.class);

            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                MessagesListFragment messagesListFragment = (MessagesListFragment) fragmentManager.findFragmentById(R.id.container_main);
                messagesListFragment.updateDisplayDataModel(messagingDataModel);
            }catch (ClassCastException cce){
                cce.printStackTrace();
            }
        }

        @Override
        public void onFailure(String errorMessage) {
            hideProgressDialog();
            showErrorNotification(errorMessage);
        }
    };

    private RestCallServiceCallback getMessagesCallback = new RestCallServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            hideProgressDialog();

            Gson gson = new Gson();
            Messages.Reply thread = gson.fromJson(jsonElement, Messages.Reply.class);

            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                MessagesConversationFragment conversationFragment = (MessagesConversationFragment) fragmentManager.findFragmentById(R.id.container_main);
                conversationFragment.updateThreadMessages(thread);
            }catch (ClassCastException cce){
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
