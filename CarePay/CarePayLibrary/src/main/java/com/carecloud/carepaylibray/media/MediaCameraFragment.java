package com.carecloud.carepaylibray.media;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraView;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;

import java.io.File;

/**
 * Created by lmenendez on 6/2/17
 */

public class MediaCameraFragment extends BaseDialogFragment implements CarePayCameraCallback {

    private CarePayCameraPreview.CameraType cameraType;
    private CarePayCameraView carePayCameraView;
    private int currentCameraId = CarePayCameraPreview.NO_DEFINED_CAMERA;

    public interface MediaCameraCallback {
        void onMediaFileCreated(File file);

        void displayCameraFragment(String tempFile);
    }

    public static final String BUNDLE_KEY_FILE = "bundleKeyFile";

    private String tempFile;
    private MediaCameraCallback callback;

    /**
     * Create a new instance of media fragment
     *
     * @param tempFile name of temporary file for storing captured image
     * @return new MediaCameraFragment
     */
    public static MediaCameraFragment newInstance(String tempFile) {
        return newInstance(tempFile, CarePayCameraPreview.CameraType.SCAN_DOC);
    }

    /**
     * Create a new instance of media fragment
     *
     * @param tempFile   name of temporary file for storing captured image
     * @param cameraType the camera type
     * @return new MediaCameraFragment
     */
    public static MediaCameraFragment newInstance(String tempFile, CarePayCameraPreview.CameraType cameraType) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_FILE, tempFile);
        args.putSerializable("cameraType", cameraType);
        MediaCameraFragment mediaCameraFragment = new MediaCameraFragment();
        mediaCameraFragment.setArguments(args);
        return mediaCameraFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (MediaCameraCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context does not implement MediaCameraCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        if (args != null) {
            tempFile = args.getString(BUNDLE_KEY_FILE);
            cameraType = (CarePayCameraPreview.CameraType) args.getSerializable("cameraType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setCancelable(true);
        return inflater.inflate(R.layout.fragment_media_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set content
        carePayCameraView = new CarePayCameraView(this, getContext(), cameraType);
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
        carePayCameraView.start(currentCameraId);

    }

    @Override
    public void onStop() {
        carePayCameraView.stop();
        super.onStop();
    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        if (tempFile != null) {
            File file = ImageCaptureHelper.getBitmapFileUrl(getContext(), bitmap, tempFile);
            callback.onMediaFileCreated(file);
        } else {
            showErrorNotification("no temp file specified");
        }
    }

    @Override
    public void onCaptureFail() {
        showErrorNotification("Failed to Capture image");
    }

    @Override
    public void onChangeCamera(int currentCameraId) {
        this.currentCameraId = currentCameraId;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentCameraId", currentCameraId);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            currentCameraId = savedInstanceState.getInt("currentCameraId");
        }
    }
}
