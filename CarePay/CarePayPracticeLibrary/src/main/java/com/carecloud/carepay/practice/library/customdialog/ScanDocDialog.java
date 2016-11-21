package com.carecloud.carepay.practice.library.customdialog;

/**
 * Created by prem_mourya on 11/16/2016.
 */

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class ScanDocDialog extends BasePracticeDialog implements  CarePayCameraCallback {


    private Button scanImageButton;
    private Button scanClearImageButton;
    private LinearLayout closeViewLayout;
    private ImageView cameraCaptureImage;
    private CarePayCameraPreview carePayCameraPreview;
    private byte[] cameraImageBitmap;
    private boolean isCapturing = true;
    private Context context;
    private  SaveScanDocListener saveScanDocListener;
    private CarePayTextView contentViewTitleLabel;
    private  View childActionView;

    public interface  SaveScanDocListener {
        public void onSaveScanDoc(byte[] bytes);
    }

    public ScanDocDialog(Context context, SaveScanDocListener saveScanDocListener){
        super(context);
        this.context = context;
        this.saveScanDocListener = saveScanDocListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAddChildLayout();
        onInitialization();
        onSetViewLabel();
    }

    private void onAddChildLayout(){
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        childActionView = inflater.inflate(R.layout.dialog_scan_camera_doc, null);
        ((LinearLayout)findViewById(R.id.base_dialog_content_layout)).addView(childActionView);
    }

    private void onInitialization() {
        carePayCameraPreview = (CarePayCameraPreview) childActionView.findViewById(R.id.camera_preview);

        scanImageButton = (Button) childActionView.findViewById(R.id.scan_button);
        scanImageButton.setOnClickListener(this);

        scanClearImageButton = (Button) childActionView.findViewById(R.id.clear_capture_image_button);
        scanClearImageButton.setOnClickListener(this);

        cameraCaptureImage = (ImageView) childActionView.findViewById(R.id.cameraImageView);
        cameraCaptureImage.setVisibility(View.INVISIBLE);

        contentViewTitleLabel = (CarePayTextView)childActionView.findViewById(R.id.content_view_title_view);
    }

    private void onSetViewLabel(){
        //labels will be get from DTO and Set
        setDialogTitle("Scan Insurance Card");
        contentViewTitleLabel.setText("Place your your health insurance card in front of the camera and press “Scan” to take a picture.");
        scanImageButton.setText("Scan");
        scanClearImageButton.setText("Clear");
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.scan_button) {

            if (isCapturing) {
                if (carePayCameraPreview != null) {
                    carePayCameraPreview.takePicturePractice(this);
                }
            } else {
                onSaveImage();
            }


        } else if (viewId == R.id.clear_capture_image_button) {
            setupImageCameraCapture();
        } else if (viewId == R.id.closeViewLayout) {
            dismiss();
        }

    }


    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        cameraImageBitmap = stream.toByteArray();
        setupImageDisplay();

    }

    private void setupImageCameraCapture() {
        cameraCaptureImage.setVisibility(View.INVISIBLE);
        carePayCameraPreview.setVisibility(View.VISIBLE);
        try {
            carePayCameraPreview.getCameraObject().startPreview();
        } catch (Exception excetion) {
            Log.e("CameraRND", "Error setting camera start: " + excetion.getMessage());
        }
        scanImageButton.setText("Scan");
        cameraImageBitmap = null;
        isCapturing = true;
        scanClearImageButton.setEnabled(false);
        onClearButtonTextColor();
    }

    private void setupImageDisplay() {

        Bitmap bitmap = BitmapFactory.decodeByteArray(cameraImageBitmap, 0, cameraImageBitmap.length);
        cameraCaptureImage.setImageBitmap(bitmap);
        try {
            carePayCameraPreview.getCameraObject().stopPreview();
        } catch (Exception excetion) {
            Log.e("CameraRND", "Error setting camera stop: " + excetion.getMessage());
        }
        carePayCameraPreview.setVisibility(View.INVISIBLE);
        cameraCaptureImage.setVisibility(View.VISIBLE);
        scanImageButton.setText("Save");
        isCapturing = false;
        scanClearImageButton.setEnabled(true);
        onClearButtonTextColor();
    }

    private void onSaveImage() {
        dismiss();
        this.saveScanDocListener.onSaveScanDoc(cameraImageBitmap);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        carePayCameraPreview.getCameraObject().release();
    }

    private void onClearButtonTextColor(){
        if(scanClearImageButton.isEnabled()){
            scanClearImageButton.setTextColor(ContextCompat.getColor(context, R.color.blue_cerulian));
        }else{
            scanClearImageButton.setTextColor(ContextCompat.getColor(context, R.color.light_gray));
        }
    }
}