package com.carecloud.carepay.mini.activities;

import android.os.Bundle;

import com.carecloud.carepay.mini.fragments.UnlockFragment;

/**
 * Created by lmenendez on 10/16/17
 */

public class SettingsActivity extends RegistrationActivity {

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        replaceFragment(new UnlockFragment(), false);
    }

}