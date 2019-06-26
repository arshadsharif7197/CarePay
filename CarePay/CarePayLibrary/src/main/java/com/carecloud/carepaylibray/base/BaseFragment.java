package com.carecloud.carepaylibray.base;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by cocampo on 2/6/17
 */

public abstract class BaseFragment extends Fragment implements ISession {
    private static final int FULLSCREEN_VALUE = 0x10000000;
    private boolean isPracticeAppPatientMode;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        isPracticeAppPatientMode = getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE;
        setNewRelicInteraction(getClass().getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        setLastInteraction(System.currentTimeMillis());
    }

    protected void hideKeyboardOnViewTouch(View view) {
        if (isPracticeAppPatientMode && view != null) {
            view.setSoundEffectsEnabled(false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SystemUtil.hideSoftKeyboard(getContext(), view);
                    view.requestFocus();
                }
            });
        }

    }

    /**
     * @param id of the view to be found
     * @return the view or null if not found
     */
    public View findViewById(int id) {
        View rootView = getView();
        if (null == rootView) {
            return null;
        }

        return rootView.findViewById(id);
    }

    @Override
    public ApplicationPreferences getApplicationPreferences() {
        return ((ISession) getActivityProxy()).getApplicationPreferences();
    }

    @Override
    public WorkflowServiceHelper getWorkflowServiceHelper() {
        return ((ISession) getActivityProxy()).getWorkflowServiceHelper();
    }

    @Override
    public AppAuthorizationHelper getAppAuthorizationHelper() {
        return ((IApplicationSession) getActivityProxy()).getAppAuthorizationHelper();
    }

    @Override
    public ApplicationMode getApplicationMode() {
        return ((IApplicationSession) getActivityProxy()).getApplicationMode();
    }

    @Override
    public void onAtomicRestart() {
        ((IApplicationSession) getActivityProxy()).onAtomicRestart();
    }

    @Override
    public void setLastInteraction(long systemTime) {
        ((IApplicationSession) getActivityProxy()).setLastInteraction(systemTime);
    }

    @Override
    public long getLastInteraction() {
        return ((IApplicationSession) getActivityProxy()).getLastInteraction();
    }

    @Override
    public void setNewRelicInteraction(String interactionName) {
        ((IApplicationSession) getActivityProxy()).setNewRelicInteraction(interactionName);
    }

    @Override
    public void showProgressDialog() {
        ISession session = (ISession) getActivityProxy();
        if (null != session) {
            session.showProgressDialog();
        }
    }

    @Override
    public void hideProgressDialog() {
        ISession session = (ISession) getActivityProxy();
        if (null != session) {
            session.hideProgressDialog();
        }
    }

    @Override
    public void showErrorNotification(String errorMessage) {
        ISession session = (ISession) getActivityProxy();
        if (null != session) {
            session.showErrorNotification(errorMessage);
        }
    }

    @Override
    public void hideErrorNotification() {
        ISession session = (ISession) getActivityProxy();
        if (null != session) {
            session.hideErrorNotification();
        }
    }

    protected void showDefaultActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivityProxy()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    public void setNavigationBarVisibility() {
        if (getView() != null) {
            View decorView = getView().getRootView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | FULLSCREEN_VALUE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    protected Activity getActivityProxy() {
        return getActivity();
    }

}
