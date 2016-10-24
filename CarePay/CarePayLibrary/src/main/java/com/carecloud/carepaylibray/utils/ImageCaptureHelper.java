package com.carecloud.carepaylibray.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.carecloud.carepaylibrary.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Helper for scan with camera functionality
 */
public class ImageCaptureHelper {

    public static final  int            REQUEST_CAMERA        = 0;
    public static final  int            SELECT_FILE           = 1;
    public static final  int            ROUND_IMAGE           = 11;
    public static final  int            RECTANGULAR_IMAGE     = 22;
    public static final  String         CHOOSER_NAME          = "Select File";
    public static final  CharSequence[] chooseActionDlOptions = {
            "Take Photo",
            "Choose from Library",
            "Cancel"
    };
    public static final  String         chooseActionDlgTitle  = "Add Photo!";
    private static final String         LOG_TAG               = ImageCaptureHelper.class.getSimpleName();
    private String    userChoosenTask;
    private ImageView imageViewTarget;
    private int       imgWidth;
    private int       imgHeight;
    private Activity  context;

    public ImageCaptureHelper(Activity activity, ImageView targetImageView) {
        context = activity;
        imageViewTarget = targetImageView;
        imgWidth = (int) context.getResources().getDimension(R.dimen.demographics_docs_thumbnail_width);
        imgHeight = (int) context.getResources().getDimension(R.dimen.demographics_docs_thumbnail_height);
        Log.v(LOG_TAG, "ctor viewWidth=" + imageViewTarget.getWidth() + " viewHeight=" + imageViewTarget.getHeight());
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

    /**
     * Callback method to be used upon returning from Camera activity
     *
     * @param data  The intent used to launch the Camera
     * @param shape The intended shape of the captured image
     * @return The bitmap
     */
    public Bitmap onCaptureImageResult(Intent data, int shape) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (thumbnail != null) {
            // compress
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
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
        if(origWidth < origHeigth ) {
            cropSize = origHeigth - origWidth;
            yyCoord = cropSize;
            croppedHeight = origWidth;
        } else if(origWidth > origHeigth) {
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