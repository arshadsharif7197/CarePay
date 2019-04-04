package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.mini.HttpConstants;
import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.activities.FullScreenActivity;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;
import com.carecloud.carepay.mini.views.CustomErrorToast;

import java.util.List;

/**
 * Created by lmenendez on 10/16/17
 */

public class UnlockFragment extends LoginFragment {
    public static final int LOGIN_TIMEOUT = 1000 * 60;
    private Handler handler;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_unlock, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();
        handler.postDelayed(exitRunnable, LOGIN_TIMEOUT);
    }

    @Override
    public void onPause(){
        handler.removeCallbacks(exitRunnable);
        super.onPause();
    }

    @Override
    protected void initProgressToolbar(View view, String titleString, int progress) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.registration_toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(getString(R.string.settings_title));

        View exitButton = view.findViewById(R.id.button_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onBackPressed();
            }
        });

        View signOutButton = view.findViewById(R.id.button_signout);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FullScreenActivity) getActivity()).toggleCustomerMode();
                getActivity().finishAffinity();
            }
        });

    }

    @Override
    protected void displayNextStep(){
        String currentPracticeId = getApplicationHelper().getApplicationPreferences().getPracticeId();
        if(hasAccess(currentPracticeId, callback.getPreRegisterDataModel().getUserPracticeDTOList())){
            callback.replaceFragment(new SettingsFragment(), false);
        }else if(HttpConstants.getEnvironment().equals("Support")){
            super.displayNextStep();
        }
        else{
            CustomErrorToast.showWithMessage(getContext(), getString(R.string.error_user_access_practice));
        }
    }

    private Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            getActivity().finish();
        }
    };

    private static boolean hasAccess(String currentPracticeId, List<UserPracticeDTO> userPracticeDTOs){
        for(UserPracticeDTO userPracticeDTO : userPracticeDTOs){
            if(userPracticeDTO.getPracticeId().equals(currentPracticeId)){
                return true;
            }
        }
        return false;
    }
}
