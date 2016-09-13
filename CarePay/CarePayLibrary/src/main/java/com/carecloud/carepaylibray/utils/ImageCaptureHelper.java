package com.carecloud.carepaylibray.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import com.carecloud.carepaylibrary.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

    public ImageCaptureHelper(Activity activity) {
        context = activity;
    }

    public ImageCaptureHelper(Activity activity, ImageView targetImageView) {
        context = activity;
        imageViewTarget = targetImageView;
        imgWidth = (int) context.getResources().getDimension(R.dimen.demogr_docs_thumbnail_width);
        imgHeight = (int) context.getResources().getDimension(R.dimen.demogr_docs_thumbnail_height);
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
     * @param data The intent used to launch the Camera
     * @param shape The intended shape of the captured image
     */
    public void onCaptureImageResult(Intent data, int shape) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        setCapturedImageToTargetView(thumbnail, shape);
    }

    /**
     * Callback method to be used upon returning from Gallery activity
     * @param data The intent used to launch the GAllery
     * @param shape The intended shape of the captured image
     */
    public void onSelectFromGalleryResult(Intent data, int shape) {
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            setCapturedImageToTargetView(thumbnail, shape);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an intent to launch the camera
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
     * @return The intent
     */
    public Intent cameraIntent() {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    /**
     * Builds a scaled square bitmap from another bitmap
     * @param bitmap The original bitmap
     * @param width The destination bitmap width
     * @param height The destination heght
     * @return The scaled bitmap
     */
    public Bitmap getSquareCroppedBitmap(Bitmap bitmap, int width, int height) {
        Bitmap finalBitmap;
        if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
            finalBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        } else {
            finalBitmap = bitmap;
        }

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
        canvas.drawRect(rect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }

    /**
     * Builds a scaled round bitmap from another bitmap
     * @param bitmap The original bitmap
     * @param radius The destination bitmap radius
     * @return The scaled bitmap
     */
    public Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap;

        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius) {
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        } else {
            finalBitmap = bitmap;
        }

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
     * @param thumbnail The bitmap
     * @param shape The shape (ImageCaptureHelper.ROUNDED or ImageCaptureHelper.RECTANGULAR)
     */
    private void setCapturedImageToTargetView(Bitmap thumbnail, int shape) {
        Bitmap bitmap = null;
        if (shape == ROUND_IMAGE) {
            bitmap = getRoundedCroppedBitmap(Bitmap.createScaledBitmap(thumbnail, imgWidth, imgWidth, true),
                                             imgWidth);
        } else if (shape == RECTANGULAR_IMAGE) {
            bitmap = getSquareCroppedBitmap(Bitmap.createScaledBitmap(thumbnail, imgWidth, imgHeight, true),
                                            imgWidth,
                                            imgHeight);
        }
        imageViewTarget.setImageBitmap(bitmap);
    }
}