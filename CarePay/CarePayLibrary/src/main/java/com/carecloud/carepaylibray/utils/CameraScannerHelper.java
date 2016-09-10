package com.carecloud.carepaylibray.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Helper for scan with camera functionality
 */
public class CameraScannerHelper {

    public static final int            REQUEST_CAMERA        = 0;
    public static final int            SELECT_FILE           = 1;
    public static final int            ROUND_IMAGE           = 11;
    public static final int            RECTANGULAR_IMAGE     = 22;
    public static final String         CHOOSER_NAME          = "Select File";
    public static final CharSequence[] chooseActionDlOptions = {
            "Take Photo",
            "Choose from Library",
            "Cancel"
    };
    public static final String         chooseActionDlgTitle  = "Add Photo!";

    private String    userChoosenTask;
    private ImageView imageViewDetailsProfileImage;
    private int       imgWidth;
    private int       imgHeight;
    private Activity  context;

    public CameraScannerHelper(Activity activity) {
        context = activity;
    }

    public CameraScannerHelper(Activity activity, ImageView targetImageView, int width, int height) {
        context = activity;
        imageViewDetailsProfileImage = targetImageView;
        imgWidth = width;
        imgHeight = height;
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

    public ImageView getImageViewDetailsProfileImage() {
        return imageViewDetailsProfileImage;
    }

    public void setImageViewDetailsProfileImage(ImageView imageViewDetailsProfileImage) {
        this.imageViewDetailsProfileImage = imageViewDetailsProfileImage;
    }

    public String getUserChoosenTask() {
        return userChoosenTask;
    }

    public void setUserChoosenTask(String userChoosenTask) {
        this.userChoosenTask = userChoosenTask;
    }

    public void onCaptureImageResult(Intent data, int shape) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                                    System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setCapturedImageToTargetView(thumbnail, shape);
    }

    public void onSelectFromGalleryResult(Intent data, int shape) {
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

            File destination = new File(Environment.getExternalStorageDirectory(),
                                        System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;

            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

            setCapturedImageToTargetView(thumbnail, shape);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Intent galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    public Intent cameraIntent() {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    public static Bitmap getSquareCroppedBitmap(Bitmap bitmap, int width, int height) {
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
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawRect(rect, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
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
        paint.setColor(Color.parseColor("#BAB399"));

        canvas.drawCircle(finalBitmap.getWidth() / 2 + 0.7f,
                          finalBitmap.getHeight() / 2 + 0.7f,
                          finalBitmap.getWidth() / 2 + 0.1f,
                          paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        return output;
    }

    private void setCapturedImageToTargetView(Bitmap thumbnail, int shape) {

        Bitmap bitmap = null;
        if (shape == ROUND_IMAGE) {
            bitmap = getRoundedCroppedBitmap(Bitmap.createScaledBitmap(thumbnail, imgWidth, imgWidth, true),
                                             imgWidth);
        } else if (shape == RECTANGULAR_IMAGE) {
            bitmap = getSquareCroppedBitmap(Bitmap.createScaledBitmap(thumbnail, imgWidth, imgHeight, true),
                                            imgWidth, imgHeight);
        }
        imageViewDetailsProfileImage.setImageBitmap(bitmap);
    }
}