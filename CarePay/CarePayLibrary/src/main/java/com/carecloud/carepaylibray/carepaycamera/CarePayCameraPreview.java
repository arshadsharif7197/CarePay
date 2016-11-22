package com.carecloud.carepaylibray.carepaycamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 11/10/2016.
 */

public class CarePayCameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder cameraSurfaceHolder;
    private Camera camera;
    int borderWidth=2;
    int shadowWidth=100;
    float borderCornerRadius=15;
    Context context;
    private Bitmap capturedBitmap;
    private boolean isPracticeCamera;
    private CarePayCameraCallback carePayCameraCallback;

    public CarePayCameraPreview(Context context) {
        super(context);
        initialize(context);
    }

    private void initialize(Context context) {
        this.context=context;
        camera = getCameraInstance();
        setBackgroundColor(Color.parseColor("#aa575555"));
        cameraSurfaceHolder = getHolder();
        cameraSurfaceHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        cameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
        initialize(context);
    }

    /**
     * On draw
     * @param canvas canvas
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint transparentPaint = new Paint();
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        transparentPaint.setAntiAlias(true);

        final Rect rect = new Rect(getLeft()+shadowWidth,
                getTop()+shadowWidth,
                getRight()-shadowWidth,
                getBottom()-shadowWidth);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, borderCornerRadius, borderCornerRadius, transparentPaint);
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth); // set stroke width
        borderPaint.setColor(Color.parseColor("#ffffff"));
        canvas.drawRoundRect(rectF, borderCornerRadius, borderCornerRadius, borderPaint);
    }

    /**
     * On size change
     * @param width  width
     * @param height height
     * @param oldWidth oldWidth
     * @param oldHeight oldHeight
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

    }

    /**
     * On serface created
     * @param holder serface holder
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            Log.d("CameraRND", "Error setting camera preview: " + e.getMessage());
        }catch (Exception e) {
            Log.d("CameraRND", "Error setting camera : " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    /**
     * serfave change
     * @param holder holder
     * @param format format
     * @param width width
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
            if(!isPracticeCamera)
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
    private  Camera getCameraInstance() {
        Camera camera = null;
        if(checkCameraHardware()) {

            try {
                camera = Camera.open(); // attempt to get a Camera instance
            } catch (Exception e) {
                // Camera is not available (in use or does not exist)
            }
        }
        return camera;
    }

    public void takePicture(){
        camera.takePicture(null, null, pictureCallback);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            capturedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            capturedBitmap = scaleCenterCrop(capturedBitmap, getWidth()-borderWidth, getHeight()-borderWidth);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            releaseCamera();
            ((CarePayCameraCallback)context).onCapturedSuccess(capturedBitmap);
        }
    };

    private void releaseCamera() {
        if (camera != null) {
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

    // for practice app
    /**
     * @param carePayCameraCallback listener
     */
    public void takePicturePractice(CarePayCameraCallback carePayCameraCallback){
        this.carePayCameraCallback =carePayCameraCallback;
        isPracticeCamera= true;
        try {
            camera.takePicture(null, null, picturePracticeCallback);
        }catch (Exception excepetion){
            Log.d("CameraRND", "Error starting camera preview: " + excepetion.getMessage());
        }
    }

    //for practice app removed releaseCamera method call, camera will be  restart
    private Camera.PictureCallback picturePracticeCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            capturedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            capturedBitmap = scaleCenterCrop(capturedBitmap,  getWidth()-borderWidth, getHeight()-borderWidth);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            carePayCameraCallback.onCapturedSuccess(capturedBitmap);
        }
    };
    /**
     * Crop captured image in center
     * @param source source
     * @param newHeight newHeight
     * @param newWidth newWidth
     * @return bitmap
     */
    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);
        return getRoundedCornerBitmap(dest);
    }

    /**
     * Rounded bitmap
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
    public Camera getCameraObject(){
        return  camera;
    }
}
