package com.carecloud.carepaylibray.base;

/**
 * Created by cocampo on 2/17/17.
 */

public interface ISession extends IApplicationSession {
    void showProgressDialog();

    void hideProgressDialog();

    void showErrorNotification(String errorMessage);

    void hideErrorNotification();
}
