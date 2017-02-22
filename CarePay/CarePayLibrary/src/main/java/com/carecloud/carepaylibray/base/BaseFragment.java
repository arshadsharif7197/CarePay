package com.carecloud.carepaylibray.base;

import android.support.v4.app.Fragment;
import android.view.View;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;

/**
 * Created by cocampo on 2/6/17.
 */

public abstract class BaseFragment extends Fragment implements ISession {
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
    public void showProgressDialog() {
        ((ISession) getActivity()).showProgressDialog();
    }

    @Override
    public void hideProgressDialog() {
        ((ISession) getActivity()).hideProgressDialog();
    }
}
