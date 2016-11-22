package com.carecloud.carepaylibray.carepaycamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.carecloud.carepaylibray.qrcodescanner.DisplayUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 11/10/2016.
 * Capture Image Camera view
 */

public class CarePayCameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    public enum CameraType {
        CAPTURE_PHOTO, SCAN_DOC
    }

    private SurfaceHolder cameraSurfaceHolder;
    private Camera camera;
    int borderWidth = 2;
    int shadowWidth = 100;
    float borderCornerRadius = 15;
    Context context;
    private Bitmap capturedBitmap;
    private boolean isPracticeCamera;
    boolean surfaceCreated;

    public CameraType cameraType = CameraType.SCAN_DOC;

    /**
     * Constructor
     *
     * @param context caller context
     */
    public CarePayCameraPreview(Context context) {
        super(context);
        this.context = context;
        initialize(context);
    }

    /**
     * Camera initialization
     *
     * @param context caller context
     */
    private void initialize(Context context) {
        this.context = context;
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
     * Get front camera if available otherwisw defaulft camera
     *
     * @return
     */
    public static int getFrontFaceCamera() {
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
     * Get back camera if available otherwisw defaulft camera
     *
     * @return
     */
    public static int getBackFaceCamera() {
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
     * Public constructor with context and Attribute.
     * All the custom styleable declare are apply here also.
     *
     * @param context sender context
     * @param attrs   styleable attributes
     */

    public CarePayCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
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
        initialize(context);
    }

    Rect shadowRect = null;

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

            if (DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
                rect = new Rect(getLeft() + shadowWidth,
                        getTop() + shadowWidth * 2,
                        getRight() - shadowWidth,
                        getBottom() - shadowWidth * 2);
                shadowRect = rect;
            } else {
                rect = new Rect(getLeft() + shadowWidth * 2,
                        getTop() + shadowWidth,
                        getRight() - shadowWidth * 2,
                        getBottom() - shadowWidth);
                shadowRect = rect;
            }
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

    private Handler autoFocusHandler;

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
     * On size change
     *
     * @param width     width
     * @param height    height
     * @param oldWidth  oldWidth
     * @param oldHeight oldHeight
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

    }

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
            if (!isPracticeCamera)
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

    private Rect calculateTapArea(float xPoint, float yPoint, float coefficient) {
        int areaSize = Float.valueOf(300 * coefficient).intValue();

        int left = clamp((int) xPoint - areaSize / 2, 0, this.getWidth() - areaSize);
        int top = clamp((int) yPoint - areaSize / 2, 0, this.getHeight() - areaSize);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        // matrix.mapRect(rectF);

        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int xPoint, int minimum, int maximum) {
        if (xPoint > maximum) {
            return maximum;
        }
        if (xPoint < minimum) {
            return minimum;
        }
        return xPoint;
    }


    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware() {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        if (checkCameraHardware()) {

            try {
                if (cameraType == CameraType.CAPTURE_PHOTO) {
                    camera = Camera.open(getFrontFaceCamera());
                    camera.setDisplayOrientation(90);
                } else {
                    camera = Camera.open(getBackFaceCamera());
                    camera.setDisplayOrientation(90);
                }
                // attempt to get a Camera instance
            } catch (Exception e) {
                // Camera is not available (in use or does not exist)
            }
        }
        return camera;
    }

    public void takePicture() {
        camera.takePicture(null, null, pictureCallback);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            genarateCropedBitmap(data);
        }
    };

    private void genarateCropedBitmap(byte[] data) {

        Bitmap capturedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (cameraType == CameraType.SCAN_DOC) {
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
            capturedBitmap = rotateBitmap(capturedBitmap, 90);
        }
        releaseCamera();
        ((CarePayCameraCallback) context).onCapturedSuccess(capturedBitmap);
    }


    private int dpToPx(int dp) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    /**
     * Rotale original bitmap by sending angle
     * @param source source bitmap
     * @param angle angle
     * @return
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

    // for practice app
    public void takePicturePractice() {
        isPracticeCamera = true;
        camera.takePicture(null, null, picturePracticeCallback);
    }

    //for practice app removed releaseCamera method call, camera will be  restart
    private Camera.PictureCallback picturePracticeCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            genarateCropedBitmap(data);
        }
    };

    /**
     * Rounded bitmap
     *
     * @param bitmap
     * @return
     */
    public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 70;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public Bitmap getCapturedBitmap() {
        return capturedBitmap;
    }

    // for restart camera needed camera object
    public Camera getCameraObject() {
        return camera;
    }
}