package com.carecloud.carepaylibray.signinsignup;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepaylibray.common.BaseViewModel;
import com.carecloud.carepaylibray.unifiedauth.UnifiedSignInResponse;

public class UnifiedSignInResponseViewModel extends BaseViewModel {
    //for 2F authentication
    private MutableLiveData<UnifiedSignInResponse> unifiedSignInResponse=new MutableLiveData<>();



    public void setUnifiedSignInResponse(UnifiedSignInResponse unifiedSignInResponse) {
        this.unifiedSignInResponse.setValue(unifiedSignInResponse);
    }

  public UnifiedSignInResponseViewModel(Application application){
        super(application);
    }
    public MutableLiveData<UnifiedSignInResponse> getUnifiedSignInResponse(){
        return unifiedSignInResponse;
    }

}
