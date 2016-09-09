package com.carecloud.carepaylibray.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.payment.ResponsibilityFragment;
import com.carecloud.carepaylibray.selectlanguage.SelectLanguageFragment;
import com.carecloud.carepaylibray.signinsignup.fragments.SigninFragment;
import com.carecloud.carepaylibray.signinsignup.fragments.SignupFragment;

public class LibraryMainActivity extends KeyboardHolderActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void placeInitContentFragment() {
        replaceFragment(SelectLanguageFragment.class);
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
    public void replaceFragment(Class fragClass) {
        Fragment fragment = fm.findFragmentByTag(fragClass.getSimpleName());
        if(fragment == null) {
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
        fm.beginTransaction().
                replace(getContentsHolderId(), fragment, fragClass.getSimpleName())
                .addToBackStack(null)
                .commit();
    }
}