package com.carecloud.carepaylibray.signinsignup;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.common.BaseViewModel;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 2020-02-03.
 */
public class ResetPasswordViewModel extends BaseViewModel {

    private TransitionDTO resetPasswordTransition;
    private MutableLiveData<Void> resetPasswordDtoObservable = new MutableLiveData<>();
    private MutableLiveData<Void> resendPasswordDtoObservable = new MutableLiveData<>();

    public ResetPasswordViewModel(@NonNull Application application) {
        super(application);
    }

    public void setResetPasswordTransition(TransitionDTO resetPasswordTransition) {
        this.resetPasswordTransition = resetPasswordTransition;
    }

    public MutableLiveData<Void> getResetPasswordDtoObservable() {
        if (resetPasswordDtoObservable.getValue() != null) {
            resetPasswordDtoObservable = new MutableLiveData<>();
        }
        return resetPasswordDtoObservable;
    }

    public MutableLiveData<Void> getResendPasswordDtoObservable() {
        if (resendPasswordDtoObservable.getValue() != null) {
            resendPasswordDtoObservable = new MutableLiveData<>();
        }
        return resendPasswordDtoObservable;
    }

    public void resetPassword(String email) {
        callResetPasswordService(email, resetPasswordDtoObservable);
    }

    public void resendPassword(String email) {
        callResetPasswordService(email, resendPasswordDtoObservable);
    }

    public void callResetPasswordService(String email, MutableLiveData liveData) {
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("username", email);
        JsonObject userObject = new JsonObject();
        userObject.add("user", jsonObject2);
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        Map<String, String> queryParams = new HashMap<>();
        getWorkflowServiceHelper().execute(resetPasswordTransition, new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        setLoading(true);
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        setLoading(false);
                        liveData.postValue(null);
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        setLoading(false);
                        setErrorMessage(exceptionMessage);
                    }
                },
                userObject.toString(), queryParams, headers);
    }

}
