package com.carecloud.carepaylibray.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Helper for scan with camera functionality
 */
public class CameraScannerHelper {
    public static final int REQUEST_CAMERA = 0;
    public static final int SELECT_FILE = 1;
    public static final String CHOOSER_NAME = "Select File";
    public static final CharSequence[] chooseActionDlOptions = {
            "Take Photo",
            "Choose from Library",
            "Cancel"};
    public static final String chooseActionDlgTitle = "Add Photo!";

    private String    userChoosenTask;
    private ImageView imageViewDetailsProfileImage;
    private int       imgWidth;
    private Activity  mContext;

    public CameraScannerHelper(Activity activity) {
        mContext = activity;
    }

    public CameraScannerHelper(Activity activity, ImageView targetImageView, int imSize) {
        mContext = activity;
        imageViewDetailsProfileImage = targetImageView;
        imgWidth = imSize;
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

    public void onCaptureImageResult(Intent data) {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("imgWidth", "" + imgWidth);
        Bitmap roundBitmap = Utility.getRoundedCroppedBitmap(Bitmap.createScaledBitmap(thumbnail, imgWidth, imgWidth, true), imgWidth);
        imageViewDetailsProfileImage.setImageBitmap(roundBitmap);
    }


    @SuppressWarnings("deprecation")
    public void onSelectFromGalleryResult(Intent data) {
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), data.getData());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

            File destination = new File(Environment.getExternalStorageDirectory(),
                                        System.currentTimeMillis() + ".jpg");

            FileOutputStream fo;

            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

            Log.e("imgWidth", "" + imgWidth);
            Bitmap roundBitmap = Utility.getRoundedCroppedBitmap(Bitmap.createScaledBitmap(thumbnail, imgWidth, imgWidth, true), imgWidth);
            imageViewDetailsProfileImage.setImageBitmap(roundBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
}