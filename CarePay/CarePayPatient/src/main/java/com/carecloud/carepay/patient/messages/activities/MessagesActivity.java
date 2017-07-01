package com.carecloud.carepay.patient.messages.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.fragments.MessagesListFragment;
import com.carecloud.carepay.patient.messages.models.MessagingDataModel;
import com.carecloud.carepay.service.library.RestCallServiceCallback;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.RestDef;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagesActivity extends MenuPatientActivity implements MessageNavigationCallback {

    RestCallServiceHelper restCallServiceHelper;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        restCallServiceHelper = new RestCallServiceHelper(getAppAuthorizationHelper(), getApplicationMode());
        restCallServiceHelper.executeRequest(RestDef.GET,
                HttpConstants.getMessagingBaseUrl(),
                getMessagesCallback,
                true,
                getString(R.string.msg_auth_token_key),
                null,
                null,
                null,
                getString(R.string.msg_path_start));

        replaceFragment(new MessagesListFragment(), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_messages);
        menuItem.setChecked(true);
        displayToolbar(true, menuItem.getTitle().toString());
    }


    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    private RestCallServiceCallback getMessagesCallback = new RestCallServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            hideProgressDialog();
            Gson gson = new Gson();
            MessagingDataModel messagingDataModel = gson.fromJson(jsonElement, MessagingDataModel.class);

            FragmentManager fragmentManager = getSupportFragmentManager();
            MessagesListFragment messagesListFragment = (MessagesListFragment) fragmentManager.findFragmentById(R.id.container_main);
            messagesListFragment.setDisplayDataModel(messagingDataModel);
        }

        @Override
        public void onFailure(String errorMessage) {
            hideProgressDialog();
            showErrorNotification(errorMessage);
        }
    };
}
