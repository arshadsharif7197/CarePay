package com.carecloud.carepaylibray.carepaycamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.carecloud.carepaylibrary.R;

import java.io.ByteArrayOutputStream;

public class CarePayCameraActivity extends AppCompatActivity implements CarePayCameraCallback {
    FrameLayout preview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_pay_camera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        CarePayCameraView carePayCameraView = new CarePayCameraView(this);
        preview.addView(carePayCameraView);
    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] byteArray = stream.toByteArray();
        Intent intent = new Intent();
        intent.putExtra("data",byteArray);
        setResult(RESULT_OK, intent);
        finish();
    }

    public Bitmap onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (thumbnail != null) {
            // compress
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        }

        return thumbnail;

    }
}
