package com.carecloud.carepaylibray.base;

import android.support.v4.app.DialogFragment;
import android.view.View;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;

/**
 * Created by cocampo on 2/6/17.
 */

public abstract class BaseFragment extends DialogFragment implements ISession {
    public boolean enableViewById(int id) {
        return setEnabledViewById(id, true);
    }

    public boolean disableViewById(int id) {
        return setEnabledViewById(id, true);
    }

    private boolean setEnabledViewById(int id, boolean enabled) {
        View view = findViewById(id);
        if (null == view) {
            return false;
        }

        view.setEnabled(enabled);

        return true;
    }

    public boolean showViewById(int id) {
        return setVisibilityById(id, View.VISIBLE);
    }

    public boolean disappearViewById(int id) {
        return setVisibilityById(id, View.GONE);
    }

    private boolean setVisibilityById(int id, int visibility) {
        View view = findViewById(id);
        if (null == view) {
            return false;
        }

        view.setVisibility(visibility);

        return true;
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
        return ((ISession) getActivity()).getApplicationPreferences();
    }

    @Override
    public WorkflowServiceHelper getWorkflowServiceHelper() {
        return ((ISession) getActivity()).getWorkflowServiceHelper();
    }

    @Override
    public CognitoAppHelper getCognitoAppHelper() {
        return ((IApplicationSession) getActivity()).getCognitoAppHelper();
    }

    @Override
    public ApplicationMode getApplicationMode() {
        return ((IApplicationSession) getActivity()).getApplicationMode();
    }

    @Override
    public void showProgressDialog() {
        ISession session = (ISession) getActivity();
        if (null != session) {
            session.showProgressDialog();
        }
    }

    @Override
    public void hideProgressDialog() {
        ISession session = (ISession) getActivity();
        if (null != session) {
            session.hideProgressDialog();
        }
    }
}
