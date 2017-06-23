package com.carecloud.carepaylibray.carepaycamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibray.qrcodescanner.DisplayUtils;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 11/10/2016.
 * Capture Image Camera view
 */

public class CarePayCameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    public enum CameraType implements Serializable {
        CAPTURE_PHOTO, SCAN_DOC
    }

    private SurfaceHolder cameraSurfaceHolder;
    private Camera camera;
    int borderWidth = 2;
    Context context;
    Rect shadowRect = null;
    boolean surfaceCreated;
    private int displayOrientation;
    private int currentCameraId;
    private Handler autoFocusHandler;
    public CameraType cameraType = CameraType.SCAN_DOC;

    /**
     * Constructor
     *
     * @param context caller context
     */
    public CarePayCameraPreview(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * Public constructor with context and Attribute.
     * All the custom styleable declare are apply here also.
     *
     * @param context sender context
     * @param attrs   styleable attributes
     */
    public CarePayCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * Public constructor with context, Attributes and default attributes.
     * All the custom styleable declare are apply here also.
     * Default attributes also apply here
     *
     * @param context      sender context
     * @param attrs        styleable attributes
     * @param defStyleAttr styleable default attributes
     */
    public CarePayCameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * Camera initialization
     */
    public void initialize() {
        camera = getCameraInstance();
        setBackgroundColor(Color.parseColor("#aa575555"));
        cameraSurfaceHolder = getHolder();
        cameraSurfaceHolder.addCallback(this);
        autoFocusHandler = new Handler();
        setFocusable(true);
        setFocusableInTouchMode(true);
        cameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    private Camera getCameraInstance() {
        if (!checkCameraHardware()) {
            return null;
        }

        try {
            currentCameraId = cameraType == CameraType.SCAN_DOC ? getBackFaceCamera() : getFrontFaceCamera();
            displayOrientation = DisplayUtils.getDisplayOrientation(context, currentCameraId);

            if (HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE)) {
                displayOrientation = 180;
            }

            Camera camera = Camera.open(currentCameraId);
            camera.setDisplayOrientation(displayOrientation);

            return camera;
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }

        return null;
    }

    /**
     * Get front camera if available otherwisw defaulft camera
     *
     * @return
     */
    private int getFrontFaceCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int defaultCameraId = -1;
        for (int i = 0; i < numberOfCameras; i++) {
            defaultCameraId = i;
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return i;
            }
        }
        return defaultCameraId;
    }

    /**
     * Get back camera if available otherwise default camera
     *
     * @return
     */
    private int getBackFaceCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int defaultCameraId = -1;
        for (int i = 0; i < numberOfCameras; i++) {
            defaultCameraId = i;
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }
        return defaultCameraId;
    }

    /**
     * On draw
     *
     * @param canvas canvas
     */

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (cameraType == CameraType.SCAN_DOC) {
            Paint transparentPaint = new Paint();
            transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
            transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            transparentPaint.setAntiAlias(true);
            Rect rect;

            Point size = DisplayUtils.getDisplaySize(getContext());
            if (DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
                int shadowWidth = (int) (size.y * .05);
                rect = new Rect(getLeft() + shadowWidth,
                        getTop() + shadowWidth * 2,
                        getRight() - shadowWidth,
                        getBottom() - shadowWidth * 2);
                shadowRect = rect;
            } else {
                int shadowWidth = (int) (size.x * .05);
                rect = new Rect(getLeft() + shadowWidth * 2,
                        getTop() + shadowWidth,
                        getRight() - shadowWidth * 2,
                        getBottom() - shadowWidth);
                shadowRect = rect;
            }
            float borderCornerRadius = 15;
            RectF shadowRectF = new RectF(shadowRect);
            canvas.drawRoundRect(shadowRectF, borderCornerRadius, borderCornerRadius, transparentPaint);
            Paint borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(borderWidth); // set stroke width
            borderPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawRoundRect(shadowRectF, borderCornerRadius, borderCornerRadius, borderPaint);
        }
    }

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            scheduleAutoFocus();
        }
    };

    /**
     * Camera autofous
     */
    public void safeAutoFocus() {
        try {
            camera.autoFocus(autoFocusCB);
        } catch (RuntimeException re) {
            // Horrible hack to deal with autofocus errors on Sony devices
            // See https://github.com/dm77/barcodescanner/issues/7 for example
            scheduleAutoFocus(); // wait 1 sec and then do check again
        }
    }

    /**
     * Schedule focus
     */
    private void scheduleAutoFocus() {
        autoFocusHandler.postDelayed(doAutoFocus, 1000);
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (camera != null && surfaceCreated) {
                safeAutoFocus();
            }
        }
    };

    /**
     * On serface created
     *
     * @param holder serface holder
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        surfaceCreated = true;
        try {
            surfaceCreated = true;
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            if (surfaceCreated) { // check if surface created before using autofocus
                safeAutoFocus();
            } else {
                scheduleAutoFocus(); // wait 1 sec and then do check again
            }
        } catch (IOException e) {
            Log.d("CameraRND", "Error setting camera preview: " + e.getMessage());
        } catch (Exception e) {
            Log.d("CameraRND", "Error setting camera : " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
        surfaceCreated = false;
    }

    /**
     * serfave change
     *
     * @param holder holder
     * @param format format
     * @param width  width
     * @param height height
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (cameraSurfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        try {
            // set Camera parameters
            Camera.Parameters params = camera.getParameters();
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                // Autofocus mode is supported
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            camera.setParameters(params);
            camera.setPreviewDisplay(cameraSurfaceHolder);
            camera.startPreview();

            if (surfaceCreated) { // check if surface created before using autofocus
                safeAutoFocus();
            } else {
                scheduleAutoFocus(); // wait 1 sec and then do check again
            }

        } catch (Exception e) {
            Log.d("CameraRND", "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * True if this device has a camera
     */
    private boolean checkCameraHardware() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    CarePayCameraCallback carePayCameraCallback;

    /**
     * Capture Picture with selected Camera
     *
     * @param callback callback for captured bitmap
     */
    public void takePicture(CarePayCameraCallback callback) {
        this.carePayCameraCallback = callback;
        if (camera != null) {
            camera.takePicture(null, null, pictureCallback);
        } else {
            callback.onCaptureFail();
        }
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.stopPreview();
            ImageCaptureHelper.setOrientation(displayOrientation);
            generateCroppedBitmap(data);
            releaseCamera();
        }
    };

    private void generateCroppedBitmap(byte[] data) {

        Bitmap capturedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        if (cameraType == CameraType.SCAN_DOC) {

            if (HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE)) {
                ImageCaptureHelper.setOrientation(270);
                capturedBitmap = rotateBitmap(capturedBitmap, 270);
            }
            Rect rectFrame = shadowRect;

            double scaleWidth = (float) capturedBitmap.getWidth() / getHeight();
            double scaleHeight = (float) capturedBitmap.getHeight() / getWidth();

            int cropedWidth = (int) (scaleWidth * rectFrame.height());
            int cropedHeight = (int) (scaleHeight * rectFrame.width());

            int left = (int) (rectFrame.top * scaleWidth) - 5;
            int top = (int) (rectFrame.left * scaleHeight) - 5;

            Log.d("Scales:", "scaleWidth: " + scaleWidth + "  scaleHeight: " + scaleHeight + "  cropedWidth: " + cropedWidth + "  cropedHeight: " + cropedHeight + "  left: " + left + "  top: " + top);

            capturedBitmap = Bitmap.createBitmap(capturedBitmap, left, top, cropedWidth, cropedHeight);

        } else {
        }
        int offset = 0;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                && currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            offset = 180;
        }
        capturedBitmap = rotateBitmap(capturedBitmap, DisplayUtils.getDisplayOrientation(context, currentCameraId) + offset);

        this.carePayCameraCallback.onCapturedSuccess(capturedBitmap);
    }

    /**
     * Rotale original bitmap by sending angle
     *
     * @param source source bitmap
     * @param angle  angle
     * @return rotated bitmap
     */
    public Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

    /**
     * change the camera back/front
     */
    public void changeCamera() {
        camera.stopPreview();
        camera.release();
        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        camera = Camera.open(currentCameraId);
        displayOrientation = DisplayUtils.getDisplayOrientation(context, currentCameraId);
        camera.setDisplayOrientation(displayOrientation);

        try {
            camera.setPreviewDisplay(getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();

        cameraSurfaceHolder = getHolder();
        cameraSurfaceHolder.addCallback(this);
    }

    /**
     * turns on the flash of the selected camera
     */
    public void turnOnFlash() {
        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
    }

    /**
     * turns off the flash of the current camera
     */
    public void turnOffFlash() {
        Camera.Parameters params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.startPreview();
    }

    /**
     * @return a boolean indicating if the selected camera has flash
     */
    public boolean hasFlash() {
        if (camera == null) {
            return false;
        }

        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getFlashMode() == null) {
            return false;
        }

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
            return false;
        }

        return true;
    }

    /**
     * @return a boolean indicating if device has more than one camera
     */
    public boolean canChangeCamera() {
        return Camera.getNumberOfCameras() > 1;
    }

    /**
     * @param cameraType the camera type
     */
    public void setCameraType(CameraType cameraType) {
        this.cameraType = cameraType;
    }

}