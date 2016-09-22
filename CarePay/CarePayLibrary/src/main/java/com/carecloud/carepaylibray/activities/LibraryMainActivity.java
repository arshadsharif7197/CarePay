package com.carecloud.carepaylibray.activities;

import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.models.DemographicTransitionsDataObjectModel;
import com.carecloud.carepaylibray.demographics.services.DemographicService;
import com.carecloud.carepaylibray.payment.ResponsibilityFragment;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.selectlanguage.fragments.SelectLanguageFragment;
import com.carecloud.carepaylibray.signinsignup.fragments.SigninFragment;
import com.carecloud.carepaylibray.signinsignup.fragments.SignupFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryMainActivity extends KeyboardHolderActivity {

    DemographicModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DemographicService apptService = (new BaseServiceGenerator(this)).createService(DemographicService.class); //, String token, String searchString
        Call<DemographicModel> call = apptService.fetchDemographicInformation();
        call.enqueue(new Callback<DemographicModel>()
        {
            @Override
            public void onResponse(Call<DemographicModel> call, Response<DemographicModel> response) {
                model=response.body();
                DemographicTransitionsDataObjectModel dd= model.getMetadata().getTransitions().getConfirmDemographics().getTransitionsData();
                Log.d("sdadad","adasdasdasd");
            }

            @Override
            public void onFailure(Call<DemographicModel> call, Throwable t) {

            }
        });
    }

    @Override
    public void placeInitContentFragment() {
        replaceFragment(SelectLanguageFragment.class, false);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_keyboard_holder;
    }

    @Override
    public int getContentsHolderId() {
        return R.id.content_holder;
    }

    @Override
    public int getKeyboardHolderId() {
        return R.id.keyboard_holder;
    }

    /**
     * Replace the current content fragment with a new fragment specified by class
     *
     * @param fragClass The class
     */
    @Override
    public void replaceFragment(Class fragClass, boolean addToBackStack) {
        Fragment fragment = fm.findFragmentByTag(fragClass.getSimpleName());
        if (fragment == null) {
            if (fragClass.equals(SelectLanguageFragment.class)) {
                fragment = new SelectLanguageFragment();
            } else if (fragClass.equals(SigninFragment.class)) {
                fragment = new SigninFragment();
            } else if (fragClass.equals(SignupFragment.class)) {
                fragment = new SignupFragment();
            } else if (fragClass.equals(ResponsibilityFragment.class)) {
                fragment = new ResponsibilityFragment();
            }
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(getContentsHolderId(), fragment, fragClass.getSimpleName());
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();

    }
}