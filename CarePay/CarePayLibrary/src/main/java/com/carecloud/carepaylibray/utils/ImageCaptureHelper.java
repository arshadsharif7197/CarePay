package com.carecloud.carepaylibray.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraActivity;

import static com.carecloud.carepaylibray.utils.ImageCaptureHelper.ImageShape.RECTANGULAR;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Helper for scan with camera functionality
 */
public class ImageCaptureHelper {

    public static final int            REQUEST_CAMERA        = 0;
    public static final int            SELECT_FILE           = 1;
    public static final String         CHOOSER_NAME          = "Select File";
    public static final CharSequence[] chooseActionDlOptions = new CharSequence[3];
    public static final CharSequence[] chooseActionDocumentDlOptions = new CharSequence[2];
    public static String chooseActionDlgTitle;

    private static int           orientation                 = 0;
    private String               userChoosenTask;
    private int                  imgWidth;
    private int                  imgHeight;
    private Activity             context;
    private CameraType           cameraType;

    public enum CameraType {
        DEFAULT_CAMERA, CUSTOM_CAMERA;
    }

    public enum ImageShape {
        CIRCULAR, RECTANGULAR
    }

    /**
     * C-Tor
     * @param activity The activity using the helper
     */
    public ImageCaptureHelper(Activity activity) {
        this.context = activity;
        // dialog options 1. Capture, 2.Library, 3. Cancel
        chooseActionDlOptions[0] = StringUtil.captialize(Label.getLabel("demographics_take_pic_option"));
        chooseActionDlOptions[1] = StringUtil.captialize(Label.getLabel("demographics_select_gallery_option"));
        chooseActionDlOptions[2] = StringUtil.captialize(Label.getLabel("demographics_cancel_label"));

        // dialog options 1. Capture, 2. Cancel
        chooseActionDocumentDlOptions[0] = StringUtil.captialize(Label.getLabel("demographics_take_pic_option"));
        chooseActionDocumentDlOptions[1] = StringUtil.captialize(Label.getLabel("demographics_cancel_label"));

        chooseActionDlgTitle = StringUtil.captialize(Label.getLabel("demographics_select_capture_option_title"));

        imgWidth = (int) context.getResources().getDimension(R.dimen.demographics_docs_thumbnail_width);
        imgHeight = (int) context.getResources().getDimension(R.dimen.demographics_docs_thumbnail_height);
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public String getUserChoosenTask() {
        return userChoosenTask;
    }

    public void setUserChoosenTask(String userChoosenTask) {
        this.userChoosenTask = userChoosenTask;
    }

    /**
     * Getter of device orientation
     * @return orientation value
     */
    public static int getOrientation() {
        return orientation;
    }

    /**
     * Setter of device orientation
     * @param orientation orientation value
     */
    public static void setOrientation(int orientation) {
        ImageCaptureHelper.orientation = orientation;
    }

    /**
     * Rotate a bitmap from center point
     *
     * @param bitmap picture to be rotated
     * @param degrees degrees to be rotated
     * @return rotated picture
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Callback method to be used upon returning from Camera activity
     *
     * @param data  The intent used to launch the Camera
     * @param shape The intended shape of the captured image
     * @return The bitmap
     */
    public Bitmap onCaptureImageResult(ImageView imageViewTarget, Intent data, ImageShape shape) {
        Bundle extras = data.getExtras();
        Bitmap thumbnail = (Bitmap) extras.get("data");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (thumbnail != null) {
            // compress
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        }

        return setCapturedImageToTargetView(context, imageViewTarget, thumbnail, cameraType, shape);
    }

    /**
     * Callback method to be used upon returning from Gallery activity
     *
     * @param data  The intent used to launch the GAllery
     * @param shape The intended shape of the captured image
     * @return The bitmap
     */
    public Bitmap onSelectFromGalleryResult(ImageView imageViewTarget, Intent data, ImageShape shape) {
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            // compress
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            thumbnail = rotateImageIfRequired(context, thumbnail, data.getData());
            return setCapturedImageToTargetView(context, imageViewTarget, thumbnail, cameraType, shape);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates an intent to launch the camera
     *
     * @return The intent
     */
    public Intent galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    /**
     * Creates an intent to launch Gallery
     *
     * @return The intent
     */
    public Intent cameraIntent() {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    public Intent cameraIntent(Context context) {
        return new Intent(context, CarePayCameraActivity.class);
    }

    /**
     * Genrate an intent to launch a camera
     *
     * @param cameraType CAMERA_DEFAULT or CAMERA_CUSTOM
     * @return The intent
     */
    public Intent getCameraIntent(CameraType cameraType) {
        if (cameraType == CameraType.CUSTOM_CAMERA) {
            this.cameraType=CameraType.CUSTOM_CAMERA;
            return cameraIntent(context);
        }
        this.cameraType=CameraType.DEFAULT_CAMERA;
        return cameraIntent(); // launch default
    }

    /**
     * Builds a scaled square bitmap from another bitmap
     *
     * @param finalBitmap The original bitmap
     * @return The scaled bitmap
     */
    private static Bitmap getSquareCroppedBitmap(Context context, Bitmap finalBitmap) {
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                finalBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());
        final RectF rectf = new RectF(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(ContextCompat.getColor(context, R.color.paint_color_thumbnail));
        canvas.drawRoundRect(rectf, 3, 3, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }

    /**
     * Builds a scaled round bitmap from another bitmap
     *
     * @param input The original bitmap
     * @return The scaled bitmap
     */
    private static Bitmap getRoundedCroppedBitmap(Context context, Bitmap input, int outSize) {
        Bitmap output = Bitmap.createBitmap(outSize, outSize, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output); 

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(ContextCompat.getColor(context, R.color.paint_color_thumbnail)); 

        float halfSize = outSize / 2;
        canvas.drawCircle(halfSize + 0.7f, halfSize + 0.7f, halfSize + 0.1f, paint); 

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        int width = input.getWidth();
        int height = input.getHeight();
        int inSize = Math.min(width, height);
        int left = (width - inSize) / 2;
        int top = (height - inSize) / 2;
        Rect src = new Rect(left, top, left + inSize, top + inSize);
        Rect dest = new Rect(0, 0, outSize, outSize);
        canvas.drawBitmap(input, src, dest, paint); 

        return output;
    }

    /**
     * Set the a bitmap to the target image view
     *
     * @param image The bitmap
     * @param shape     The shape
     */
    public static Bitmap setCapturedImageToTargetView(Context context, ImageView imageViewTarget, Bitmap image, CameraType cameraType, ImageShape shape) {
        Resources resources = context.getResources();
        int imgWidth = (int) resources.getDimension(R.dimen.demographics_docs_thumbnail_width);
        int imgHeight = (int) resources.getDimension(R.dimen.demographics_docs_thumbnail_height);

        switch (shape) {
            case CIRCULAR:
                image = getRoundedCroppedBitmap(context, image, imgWidth);
                break;
            case RECTANGULAR:
                // calculate crop
                int origWidth = image.getWidth();
                int origHeigth = image.getHeight();
                int xxCoord = 0;
                int yyCoord = 0;
                int croppedWidth = origWidth;
                int croppedHeight = origHeigth;
                int cropSize;
                // calculate
                if (origWidth < origHeigth) {
                    cropSize = origHeigth - origWidth;
                    yyCoord = cropSize;
                    croppedHeight = origWidth;
                } else if (origWidth > origHeigth) {
                    cropSize = origWidth - origHeigth;
                    xxCoord = cropSize;
                    croppedWidth = origHeigth;
                }

                // crop to square
                Bitmap croppedBitmap = Bitmap.createBitmap(image,
                        xxCoord,
                        yyCoord,
                        croppedWidth,
                        croppedHeight);

                // compress
                if (cameraType == CameraType.CUSTOM_CAMERA && !SystemUtil.isTablet(context)) {
                    if (shape == RECTANGULAR) {
                        image = getSquareCroppedBitmap(context, Bitmap.createScaledBitmap(image, imgWidth, imgHeight, true));
                    }
                } else {
                    if (shape == RECTANGULAR) {
                        image = getSquareCroppedBitmap(context, Bitmap.createScaledBitmap(croppedBitmap, imgWidth, imgHeight, true));
                    }
                }

                break;
            default:
                return image;
        }

        imageViewTarget.setImageBitmap(image);

        return image;
    }

    public void setCameraType(CameraType cameraType){
        this.cameraType=cameraType;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        int orientation = getExifOrientation(context, selectedImage);

        if (0 == orientation) {
            return img;
        }

        return rotateBitmap(img, orientation);
    }

    private static int getExifOrientation(Context context, Uri uri) {
        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        String selection = null;
        String[] selectionArgs = null;

        try {

            if (Build.VERSION.SDK_INT > 18) {

                String id = DocumentsContract.getDocumentId(uri);
                id = id.split(":")[1];

                selectionArgs = new String[]{ id };
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                selection = MediaStore.Images.Media._ID + " = ?";

            } else if (!uri.getScheme().equals("content")) {

                return 0;

            }

            String[] projection = new String[]{
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

            if (cursor == null || !cursor.moveToFirst()) {
                return 0;
            }

            return cursor.getInt(0);
        } catch (RuntimeException ignored) {
            // If the orientation column doesn't exist, assume no rotation.
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
