package com.carecloud.carepaylibray.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.fragments.HomeFragment;
import com.carecloud.carepaylibray.fragments.ResponsibilityFragment;
import com.carecloud.carepaylibray.fragments.SelectLanguageFragment;
import com.carecloud.carepaylibray.fragments.SignInFragment;
import com.carecloud.carepaylibray.fragments.SignUpFragment;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;

public class MainActivityLibrary extends KeyboardHolderActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void placeInitContentFragment() {
        SelectLanguageFragment fragment = (SelectLanguageFragment) fm.findFragmentByTag("contents");
        if (fragment == null) {
            fragment = new SelectLanguageFragment();
        }
        fm.beginTransaction().replace(getContentsHolderId(), fragment, KB_CONTENT_TAG).commit();
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
        Fragment fragment = null;
        if (fragClass.equals(SelectLanguageFragment.class)) {
            fragment = new SelectLanguageFragment();
        } else if (fragClass.equals(SignInFragment.class)) {
            fragment = new SignInFragment();
        } else if (fragClass.equals(SignUpFragment.class)) {
            fragment = new SignUpFragment();
        } else if (fragClass.equals(HomeFragment.class)) {
            fragment = new HomeFragment();
        } else if (fragClass.equals(ResponsibilityFragment.class)) {
            fragment = new ResponsibilityFragment();
        } else {
            // TODO: 9/2/2016 register more fragments here if needed
        }
        fm.beginTransaction().replace(getContentsHolderId(), fragment, fragClass.getSimpleName()).addToBackStack(null).commit();
    }
}
