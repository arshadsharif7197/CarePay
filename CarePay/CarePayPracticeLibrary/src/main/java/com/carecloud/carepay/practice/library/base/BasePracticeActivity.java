package com.carecloud.carepay.practice.library.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.carecloud.carepay.practice.library.customdialog.IConfirmPracticeAppPin;
import com.carecloud.carepay.practice.library.practicesetting.models.PracticeSettingDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * Common Activity class for any practice.
 * Use for holding the common DTO which will be converted to the desire DTO using getConvertedDTO
 */

public abstract class BasePracticeActivity extends AppCompatActivity implements IConfirmPracticeAppPin{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setSystemUiVisibility();
    }

    /**
     * Common WorkflowDTO which will converted to the desire DTO with dtoClass params
     *
     * @param dtoClass class to convert
     * @param <S>      Dynamic class to convert
     * @return Dynamic converted class object
     */
    public <S> S getConvertedDTO(Class<S> dtoClass) {
        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null) {
            Gson gson = new Gson();
            return gson.fromJson(bundle.getString(getApplicationContext().getClass().getSimpleName()), dtoClass);
        }
        return null;
    }

    /**
     * Common WorkflowDTO which will converted to the desire DTO with dtoClass params
     *
     * @param dtoClass class to convert
     * @param <S>      Dynamic class to convert
     * @return Dynamic converted class object
     */
    public static  <S> S getConvertedDTO(Class<S> dtoClass, String jsonString) {

        if (!StringUtil.isNullOrEmpty(jsonString)) {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, dtoClass);
        }
        return null;
    }

    /**
     * Show/Hide system ui like status bar or navigation bar.
     */
    public void setSystemUiVisibility() {
        getWindow().getDecorView().setSystemUiVisibility(
                          View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onPinConfirmationCheck(boolean isCorrectPin, String pin) {

    }


    /**
     * Updates layout so in clover and devices with navigation bar is on screen don't hide content
     * */
    public void setNavigationBarVisibility(){

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }


}
