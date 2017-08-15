package com.carecloud.carepay.mini.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.services.carepay.RestCallServiceHelper;

/**
 * Created by lmenendez on 7/20/17
 */

public abstract class FullScreenActivity extends AppCompatActivity {
    private static final int FULLSCREEN_VALUE = 0x10000000;

    @Override
    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setSystemUiVisibility();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setSystemUiVisibility();
    }

    private void setSystemUiVisibility() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.KEEP_SCREEN_ON
                | FULLSCREEN_VALUE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    protected ApplicationHelper getApplicationHelper(){
        return (ApplicationHelper) getApplication();
    }

    protected RestCallServiceHelper getRestHelper(){
        return getApplicationHelper().getRestHelper();
    }


}
