package com.carecloud.carepaylibray.base;

import android.support.v7.app.AppCompatActivity;

public abstract class BaseVisibilityHintActivity extends AppCompatActivity {

    private boolean isVisible = false;

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    public boolean isVisible() {
        return isVisible;
    }
}
