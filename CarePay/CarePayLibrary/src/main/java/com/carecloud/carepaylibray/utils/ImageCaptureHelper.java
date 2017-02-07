package com.carecloud.carepaylibray.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraActivity;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;

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
    private ImageView            imageViewTarget;
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
     * @param targetImageView The target view where the captured image will be placed
     * @param demographicLabelsDTO The label from remote
     */
    public ImageCaptureHelper(Activity activity, ImageView targetImageView, DemographicLabelsDTO demographicLabelsDTO) {
        this.context = activity;
        this.imageViewTarget = targetImageView;
        // dialog options 1. Capture, 2.Library, 3. Cancel
        chooseActionDlOptions[0] = StringUtil.captialize(demographicLabelsDTO != null ? demographicLabelsDTO.getDemographicsTakePhotoOption() : CarePayConstants.NOT_DEFINED);
        chooseActionDlOptions[1] = StringUtil.captialize(demographicLabelsDTO != null ? demographicLabelsDTO.getDemographicsChooseFromLibraryOption() : CarePayConstants.NOT_DEFINED);
        chooseActionDlOptions[2] = StringUtil.captialize(demographicLabelsDTO != null ? demographicLabelsDTO.getDemographicsCancelLabel() : CarePayConstants.NOT_DEFINED);

        // dialog options 1. Capture, 2. Cancel
        chooseActionDocumentDlOptions[0] = StringUtil.captialize(demographicLabelsDTO != null ? demographicLabelsDTO.getDemographicsTakePhotoOption() : CarePayConstants.NOT_DEFINED);
        chooseActionDocumentDlOptions[1] = StringUtil.captialize(demographicLabelsDTO != null ? demographicLabelsDTO.getDemographicsCancelLabel() : CarePayConstants.NOT_DEFINED);

        chooseActionDlgTitle = StringUtil.captialize(demographicLabelsDTO != null ? demographicLabelsDTO.getDemographicsCaptureOptionsTitle() : CarePayConstants.NOT_DEFINED);

        imgWidth = (int) context.getResources().getDimension(R.dimen.demographics_docs_thumbnail_width);
        imgHeight = (int) context.getResources().getDimension(R.dimen.demographics_docs_thumbnail_height);
        resetTargetView();
    }

    /**
     * C-Tor
     * @param activity The activity using the helper
     * @param targetImageView The target view where the captured image will be placed
     */
    public ImageCaptureHelper(Activity activity, ImageView targetImageView, DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO) {
        this.context = activity;
        this.imageViewTarget = targetImageView;
        // dialog options 1. Capture, 2.Library, 3. Cancel
        chooseActionDlOptions[0] = demographicsSettingsLabelsDTO.getDemographicsTakePhotoOption();
        chooseActionDlOptions[1] = demographicsSettingsLabelsDTO.getDemographicsChooseFromLibraryOption();
        chooseActionDlOptions[2] = demographicsSettingsLabelsDTO.getDemographicsCancelLabel();

        // dialog options 1. Capture, 2. Cancel
        chooseActionDocumentDlOptions[0] = demographicsSettingsLabelsDTO.getDemographicsTakePhotoOption();
        chooseActionDocumentDlOptions[1] = demographicsSettingsLabelsDTO.getDemographicsCancelLabel();

        chooseActionDlgTitle = demographicsSettingsLabelsDTO.getDemographicsCaptureOptionsTitle();

        imgWidth = (int) context.getResources().getDimension(R.dimen.demographics_docs_thumbnail_width);
        imgHeight = (int) context.getResources().getDimension(R.dimen.demographics_docs_thumbnail_height);
        resetTargetView();
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public ImageView getImageViewTarget() {
        return imageViewTarget;
    }

    public String getUserChoosenTask() {
        return userChoosenTask;
    }

    public void setUserChoosenTask(String userChoosenTask) {
        this.userChoosenTask = userChoosenTask;
    }

    private static Bitmap imageBitmap;

    public static void setImageBitmap(Bitmap imageBitmap) {
        ImageCaptureHelper.imageBitmap = imageBitmap;
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
     * Estimates the value of the orientation to degree
     * @param orientation device orientation
     * @return estimated degree value
     */
    public int orientationToQuadrantDegrees(int orientation) {
        int degrees = 0;

        if (orientation < 140 & orientation > 30) {
            degrees = 180;
        }

        return degrees;
    }

    /**
     * Rotate a bitmap from center point
     *
     * @param bitmap picture to be rotated
     * @param degrees degrees to be rotated
     * @return rotated picture
     */
    public Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);

        return Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Callback method to be used upon returning from Camera activity
     *
     * @param data  The intent used to launch the Camera
     * @param shape The intended shape of the captured image
     * @return The bitmap
     */
    public Bitmap onCaptureImageResult(Intent data, ImageShape shape) {
        Bundle extras = data.getExtras();
        Bitmap thumbnail = (Bitmap) extras.get("data");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (thumbnail != null) {
            // compress
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        }

        return setCapturedImageToTargetView(thumbnail, shape);
    }

    /**
     * Callback method to be used upon returning from Camera activity
     *
     * @param shape The intended shape of the captured image
     * @return The bitmap
     */
    public Bitmap onCaptureImageResult(ImageShape shape) {
        if (imageBitmap == null) {
            return null;
        }
        int degrees = orientationToQuadrantDegrees(orientation);
        if (degrees > 0) {
            imageBitmap = rotateBitmap(imageBitmap, degrees);
        }
        Bitmap thumbnail = imageBitmap;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (thumbnail != null) {
            // compress
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        }

        return setCapturedImageToTargetView(thumbnail, shape);
    }

    /**
     * Callback method to be used upon returning from Gallery activity
     *
     * @param data  The intent used to launch the GAllery
     * @param shape The intended shape of the captured image
     * @return The bitmap
     */
    public Bitmap onSelectFromGalleryResult(Intent data, ImageShape shape) {
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            // compress
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            thumbnail = rotateImageIfRequired(context, thumbnail, data.getData());
            return setCapturedImageToTargetView(thumbnail, shape);
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
    private Bitmap getSquareCroppedBitmap(Bitmap finalBitmap) {
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
    private Bitmap getRoundedCroppedBitmap(Bitmap input, int outSize) {
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
    private Bitmap setCapturedImageToTargetView(Bitmap image, ImageShape shape) {
        switch (shape) {
            case CIRCULAR:
                image = getRoundedCroppedBitmap(image, imgWidth);
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
                        image = getSquareCroppedBitmap(Bitmap.createScaledBitmap(image, imgWidth, imgHeight, true));
                    }
                } else {
                    if (shape == RECTANGULAR) {
                        image = getSquareCroppedBitmap(Bitmap.createScaledBitmap(croppedBitmap, imgWidth, imgHeight, true));
                    }
                }

                break;
            default:
                return image;
        }

        imageViewTarget.setImageBitmap(image);

        return image;
    }

    public void resetTargetView() {
        imageViewTarget.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icn_camera));
    }

    public void setCameraType(CameraType cameraType){
        this.cameraType=cameraType;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        int orientation = getExifOrientation(context, selectedImage);

        if (orientation == 0) {
            return img;
        }

        return rotateImage(img, orientation);
    }

    private static int getExifOrientation(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT > 18) {
            String[] projection = new String[]{
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = null;
            try {
                String id = DocumentsContract.getDocumentId(uri);
                id = id.split(":")[1];
                cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, MediaStore.Images.Media._ID + " = ?", new String[]{id}, null);
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
        } else {
            return 0;
        }
    }

    private static Bitmap rotateImage(Bitmap bitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
