package com.carecloud.carepay.mini.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;
import com.carecloud.carepay.mini.utils.Defs;
import com.carecloud.carepay.mini.utils.StringUtil;
import com.carecloud.shamrocksdk.connections.DeviceConnection;
import com.carecloud.shamrocksdk.registrations.interfaces.Callback;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lmenendez on 7/12/17
 */

public class WelcomeActivity extends AppCompatActivity {
    private static final int CONNECTION_RETRY_DELAY = 1000 * 5;

    private ApplicationHelper applicationHelper;
    private TextView message;
    private Handler handler;

    @Override
    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        applicationHelper = (ApplicationHelper) getApplication();
        handler = new Handler();

        setContentView(R.layout.activity_welcome);
        setPracticeDetails();

        message = (TextView) findViewById(R.id.welcome_message);
        connectDevice();

    }

    private void setPracticeDetails(){
        View practiceInitialsLayout = findViewById(R.id.practice_initials_layout);
        View carecloudLogoLayout = findViewById(R.id.carecloud_logo_layout);
        View practiceLogoLayout = findViewById(R.id.practice_logo_layout);
        TextView practiceInitials = (TextView) findViewById(R.id.practice_initials_name);
        ImageView practiceLogo = (ImageView) findViewById(R.id.practice_logo);

        practiceInitialsLayout.setVisibility(View.GONE);
        carecloudLogoLayout.setVisibility(View.GONE);
        practiceLogoLayout.setVisibility(View.GONE);

        UserPracticeDTO selectedPractice = applicationHelper.getApplicationPreferences().getUserPracticeDTO();
        @Defs.ImageStyles int selectedImageStyle = applicationHelper.getApplicationPreferences().getImageStyle();
        switch (selectedImageStyle) {
            case Defs.IMAGE_STYLE_PRACTICE_INITIALS: {
                practiceInitialsLayout.setVisibility(View.VISIBLE);

                if (selectedPractice.getPracticeInitials() != null) {
                    practiceInitials.setText(selectedPractice.getPracticeInitials());
                } else {
                    practiceInitials.setText(StringUtil.getShortName(selectedPractice.getPracticeName()));
                }
                break;
            }
            case Defs.IMAGE_STYLE_PRACTICE_LOGO: {
                practiceLogoLayout.setVisibility(View.VISIBLE);
                String imageUrl = selectedPractice.getPracticePhoto();
                Picasso.with(this)
                        .load(imageUrl)
                        .resize(400, 400)
                        .centerCrop()
                        .transform(new CropCircleTransformation())
                        .placeholder(R.drawable.practice_no_image_bkg)
                        .into(practiceLogo);
                break;
            }
            case Defs.IMAGE_STYLE_CARECLOUD_LOGO:
            default:{
                carecloudLogoLayout.setVisibility(View.VISIBLE);
                break;
            }
        }

    }

    private void connectDevice(){
        String token = applicationHelper.getApplicationPreferences().getDeviceToken();
        String id = applicationHelper.getApplicationPreferences().getDeviceId();
        DeviceConnection.connect(token, id, connectionCallback);
    }

    private Callback connectionCallback = new Callback() {
        @Override
        public void onPreExecute() {
            message.setText(getString(R.string.welcome_connecting));
        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            message.setText(getString(R.string.welcome_waiting));
        }

        @Override
        public void onFailure(String errorMessage) {
            message.setText(errorMessage);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectDevice();
                }
            }, CONNECTION_RETRY_DELAY);
        }
    };
}
