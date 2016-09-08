package com.carecloud.carepaylibray.activities;

import android.support.v4.app.Fragment;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.fragments.HomeFragment;
import com.carecloud.carepaylibray.fragments.ResponsibilityFragment;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;

public class HomeActivity extends KeyboardHolderActivity {

    @Override
    public void replaceFragment(Class fragClass) {
        Fragment fragment = fm.findFragmentByTag(fragClass.getSimpleName());
        if(fragment == null) {
            if (fragClass.getSimpleName().equals(HomeFragment.class.getSimpleName())) {
                fragment = new HomeFragment();
            } else if(fragClass.getSimpleName().equals(ResponsibilityFragment.class.getSimpleName())) {
                fragment = new ResponsibilityFragment();
            }
        }

        if(fragment != null) {
            fm.beginTransaction().replace(getContentsHolderId(), fragment, fragClass.getSimpleName()).commit();
        }
    }

    @Override
    public void placeInitContentFragment() {
        replaceFragment(HomeFragment.class);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_home;
    }

    @Override
    public int getContentsHolderId() {
        return R.id.home_frag_holder;
    }

    @Override
    public int getKeyboardHolderId() {
        return 0;
    }
}