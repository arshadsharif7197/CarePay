package com.carecloud.carepaylibray.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraActivity;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Helper for scan with camera functionality
 */
public class ImageCaptureHelper {

    public static final int            REQUEST_CAMERA        = 0;
    public static final int            SELECT_FILE           = 1;
    public static final int            ROUND_IMAGE           = 11;
    public static final int            RECTANGULAR_IMAGE     = 22;
    public static final String         CHOOSER_NAME          = "Select File";
    public static final CharSequence[] chooseActionDlOptions = new CharSequence[3];
    public static String chooseActionDlgTitle;

    private static int           orientation                 = 0;
    private static final String LOG_TAG = ImageCaptureHelper.class.getSimpleName();
    private String               userChoosenTask;
    private ImageView            imageViewTarget;
    private int                  imgWidth;
    private int                  imgHeight;
    private Activity             context;

    public enum CameraType {
        DEFAULT_CAMERA, CUSTOM_CAMERA;
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

        chooseActionDlOptions[0] = StringUtil.captialize(demographicLabelsDTO != null ? demographicLabelsDTO.getDemographicsTakePhotoOption() : CarePayConstants.NOT_DEFINED);
        chooseActionDlOptions[1] = StringUtil.captialize(demographicLabelsDTO != null ? demographicLabelsDTO.getDemographicsChooseFromLibraryOption() : CarePayConstants.NOT_DEFINED);
        chooseActionDlOptions[2] = StringUtil.captialize(demographicLabelsDTO != null ? demographicLabelsDTO.getDemographicsCancelLabel() : CarePayConstants.NOT_DEFINED);

        chooseActionDlgTitle = StringUtil.captialize(demographicLabelsDTO != null ? demographicLabelsDTO.getDemographicsCaptureOptionsTitle() : CarePayConstants.NOT_DEFINED);

        imgWidth = (int) context.getResources().getDimension(R.dimen.demographics_docs_thumbnail_width);
        imgHeight = (int) context.getResources().getDimension(R.dimen.demographics_docs_thumbnail_height);
        resetTargetView();
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public ImageView getImageViewTarget() {
        return imageViewTarget;
    }

    public void setImageViewTarget(ImageView imageViewTarget) {
        this.imageViewTarget = imageViewTarget;
    }

    public String getUserChoosenTask() {
        return userChoosenTask;
    }

    public void setUserChoosenTask(String userChoosenTask) {
        this.userChoosenTask = userChoosenTask;
    }

    private static Bitmap imageBitmap;

    public static Bitmap getImageBitmap() {
        return imageBitmap;
    }

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

        if (orientation < 140 & orientation > 40) {
            degrees = 180;
        }

        return degrees;
    }
    /**
     * Rotate a bitmap from center point
     * @param originalBitmap picture to be rotated
     * @param degrees degrees to be rotated
     * @return rotated picture
     */
    public Bitmap rotateBitmap(Bitmap originalBitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);
        Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    /**
     * Callback method to be used upon returning from Camera activity
     *
     * @param data  The intent used to launch the Camera
     * @param shape The intended shape of the captured image
     * @return The bitmap
     */
    public Bitmap onCaptureImageResult(Intent data, int shape) {
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
    public Bitmap onCaptureImageResult(int shape) {
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
    public Bitmap onSelectFromGalleryResult(Intent data, int shape) {
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            // compress
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
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
            return cameraIntent(context);
        }
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
     * @param finalBitmap The original bitmap
     * @return The scaled bitmap
     */
    private Bitmap getRoundedCroppedBitmap(Bitmap finalBitmap) {
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                                            finalBitmap.getHeight(),
                                            Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(ContextCompat.getColor(context, R.color.paint_color_thumbnail));

        canvas.drawCircle(finalBitmap.getWidth() / 2 + 0.7f,
                          finalBitmap.getHeight() / 2 + 0.7f,
                          finalBitmap.getWidth() / 2 + 0.1f,
                          paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }

    /**
     * Set the a bitmap to the target image view
     *
     * @param thumbnail The bitmap
     * @param shape     The shape (ImageCaptureHelper.ROUNDED or ImageCaptureHelper.RECTANGULAR)
     */
    private Bitmap setCapturedImageToTargetView(Bitmap thumbnail, int shape) {
        // calculate crop
        int origWidth = thumbnail.getWidth();
        int origHeigth = thumbnail.getHeight();
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
        Bitmap croppedBitmap = Bitmap.createBitmap(thumbnail,
                                                   xxCoord,
                                                   yyCoord,
                                                   croppedWidth,
                                                   croppedHeight);

        // compress
        Bitmap bitmap = null;
        if (shape == ROUND_IMAGE) {
            bitmap = getRoundedCroppedBitmap(Bitmap.createScaledBitmap(croppedBitmap, imgWidth, imgWidth, false));
        } else if (shape == RECTANGULAR_IMAGE) {
            bitmap = getSquareCroppedBitmap(Bitmap.createScaledBitmap(croppedBitmap, imgWidth, imgHeight, true));
        }
        imageViewTarget.setImageBitmap(bitmap);
        return bitmap;
    }

    public void resetTargetView() {
        imageViewTarget.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icn_camera));
    }
}
