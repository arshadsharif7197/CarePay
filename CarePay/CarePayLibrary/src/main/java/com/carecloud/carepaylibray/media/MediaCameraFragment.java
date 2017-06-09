package com.carecloud.carepaylibray.media;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraView;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;

import java.io.File;

/**
 * Created by lmenendez on 6/2/17
 */

public class MediaCameraFragment extends BaseDialogFragment implements CarePayCameraCallback {

    public interface MediaCameraCallback{
        void onMediaFileCreated(File file);

        void displayCameraFragment(String tempFile);
    }

    public static final String BUNDLE_KEY_FILE = "bundleKeyFile";

    private String tempFile;
    private MediaCameraCallback callback;

    /**
     * Create a new instance of media fragment
     * @param tempFile name of temporary file for storing captured image
     * @return new MediaCameraFragment
     */
    public static MediaCameraFragment newInstance(String tempFile){
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_FILE, tempFile);
        MediaCameraFragment mediaCameraFragment = new MediaCameraFragment();
        mediaCameraFragment.setArguments(args);
        return mediaCameraFragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (MediaCameraCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context does not implement MediaCameraCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        if(args!=null){
            tempFile = args.getString(BUNDLE_KEY_FILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setCancelable(true);
        return inflater.inflate(R.layout.activity_care_pay_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set content
        CarePayCameraView carePayCameraView = new CarePayCameraView(this, getContext());
        ((FrameLayout) view.findViewById(R.id.camera_preview)).addView(carePayCameraView);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        Bitmap rotateBitmap = ImageCaptureHelper.rotateBitmap(bitmap, ImageCaptureHelper.getOrientation());
        if(tempFile != null) {
            File file = ImageCaptureHelper.getBitmapFileUrl(getContext(), rotateBitmap, tempFile);
            callback.onMediaFileCreated(file);
        }else{
            showErrorNotification("no temp file specified");
        }
    }

    @Override
    public void onCaptureFail() {
        showErrorNotification("Failed to Capture image");
    }
}