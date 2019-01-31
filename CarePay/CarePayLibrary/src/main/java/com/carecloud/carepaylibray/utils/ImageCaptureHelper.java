package com.carecloud.carepaylibray.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.util.Log;
import android.widget.ImageView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;

import static com.carecloud.carepaylibray.utils.ImageCaptureHelper.ImageShape.RECTANGULAR;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Helper for scan with camera functionality
 */
public class ImageCaptureHelper {

    public static final String JPF_EXT = ".jpg";

    public static final int REQUEST_CAMERA = 0;
    public static final int SELECT_FILE = 1;
    public static final String CHOOSER_NAME = "Select File";
    public static String chooseActionDlgTitle = StringUtil.captialize(Label.getLabel("demographics_select_capture_option_title"));

    private static Bitmap imageBitmap;
    private static int orientation = 0;
    private static String userChoosenTask;

    public enum CameraType {
        DEFAULT_CAMERA, CUSTOM_CAMERA;
    }

    public enum ImageShape {
        CIRCULAR, RECTANGULAR
    }

    /**
     * @param option dialog options 1. Capture, 2. Cancel
     * @return chose action
     */
    public static String getActionDlOptions(int option) {
        String key;
        switch (option) {
            case 0:
                key = "demographics_take_pic_option";
                break;
            case 1:
                key = "demographics_select_gallery_option";
                break;
            default:
                key = "demographics_cancel_label";
        }

        return StringUtil.captialize(Label.getLabel(key));
    }

    /**
     * @return actions
     */
    public static CharSequence[] getActionDlOptions() {
        return new CharSequence[]{
                getActionDlOptions(0),
                getActionDlOptions(1),
                getActionDlOptions(2)
        };
    }

    /**
     * @param option dialog options 1. Capture, 2. Cancel
     * @return actions
     */
    private static String getActionDocumentDlOptions(int option) {
        String key;
        switch (option) {
            case 0:
                key = "demographics_take_pic_option";
                break;
            default:
                key = "demographics_cancel_label";
        }

        return StringUtil.captialize(Label.getLabel(key));
    }

    /**
     * @return actions
     */
    public static CharSequence[] getActionDocumentDlOptions() {
        return new CharSequence[]{
                getActionDocumentDlOptions(0),
                getActionDocumentDlOptions(1)
        };
    }

    public static String getUserChoosenTask() {
        return userChoosenTask;
    }

    public static void setUserChoosenTask(String userChoosenTask) {
        ImageCaptureHelper.userChoosenTask = userChoosenTask;
    }

    /**
     * Getter of device orientation
     *
     * @return orientation value
     */
    public static int getOrientation() {
        return orientation;
    }

    /**
     * Setter of device orientation
     *
     * @param orientation orientation value
     */
    public static void setOrientation(int orientation) {
        ImageCaptureHelper.orientation = orientation;
    }

    /**
     * @return captured bitmap
     */
    public static Bitmap getImageBitmap() {
        Bitmap answer = imageBitmap;
        imageBitmap = null;
        return answer;
    }

    public static void setImageBitmap(Bitmap imageBitmap) {
        ImageCaptureHelper.imageBitmap = imageBitmap;
    }

    /**
     * Rotate a bitmap from center point
     *
     * @param bitmap  picture to be rotated
     * @param degrees degrees to be rotated
     * @return rotated picture
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
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
    public static Bitmap onCaptureImageResult(Context context, ImageView imageViewTarget, Intent data, CameraType cameraType, ImageShape shape) {
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
     * Creates an intent to launch the camera
     *
     * @return The intent
     */
    public static Intent galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
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
     * @param shape The shape
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

                selectionArgs = new String[]{id};
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

    /**
     * Convenience method for caching a bitmap to cache storage file
     *
     * @param context  context
     * @param bitmap   bitmap
     * @param fileName file
     * @return file of saved bitmap or null if i/o error
     */
    public static File getBitmapFileUrl(Context context, Bitmap bitmap, String fileName) {
        File fileDirectory = context.getCacheDir();
        File imageFile = new File(fileDirectory, fileName + JPF_EXT);

        OutputStream outputStream;
        try {
            if (imageFile.exists()) {
                imageFile.delete();
                imageFile.createNewFile();
            }

            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }


        return imageFile;
    }


    // Returns the degrees in clockwise. Values are 0, 90, 180, or 270.
    public static int getExifOrientation(byte[] jpeg) {
        String TAG = "Exif";

        if (jpeg == null) {
            return 0;
        }

        int offset = 0;
        int length = 0;

        // ISO/IEC 10918-1:1993(E)
        while (offset + 3 < jpeg.length && (jpeg[offset++] & 0xFF) == 0xFF) {
            int marker = jpeg[offset] & 0xFF;

            // Check if the marker is a padding.
            if (marker == 0xFF) {
                continue;
            }
            offset++;

            // Check if the marker is SOI or TEM.
            if (marker == 0xD8 || marker == 0x01) {
                continue;
            }
            // Check if the marker is EOI or SOS.
            if (marker == 0xD9 || marker == 0xDA) {
                break;
            }

            // Get the length and check if it is reasonable.
            length = pack(jpeg, offset, 2, false);
            if (length < 2 || offset + length > jpeg.length) {
                Log.e(TAG, "Invalid length");
                return 0;
            }

            // Break if the marker is EXIF in APP1.
            if (marker == 0xE1 && length >= 8 &&
                    pack(jpeg, offset + 2, 4, false) == 0x45786966 &&
                    pack(jpeg, offset + 6, 2, false) == 0) {
                offset += 8;
                length -= 8;
                break;
            }

            // Skip other markers.
            offset += length;
            length = 0;
        }

        // JEITA CP-3451 Exif Version 2.2
        if (length > 8) {
            // Identify the byte order.
            int tag = pack(jpeg, offset, 4, false);
            if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                Log.e(TAG, "Invalid byte order");
                return 0;
            }
            boolean littleEndian = (tag == 0x49492A00);

            // Get the offset and check if it is reasonable.
            int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
            if (count < 10 || count > length) {
                Log.e(TAG, "Invalid offset");
                return 0;
            }
            offset += count;
            length -= count;

            // Get the count and go through all the elements.
            count = pack(jpeg, offset - 2, 2, littleEndian);
            while (count-- > 0 && length >= 12) {
                // Get the tag and check if it is orientation.
                tag = pack(jpeg, offset, 2, littleEndian);
                if (tag == 0x0112) {
                    // We do not really care about type and count, do we?
                    int orientation = pack(jpeg, offset + 8, 2, littleEndian);
                    switch (orientation) {
                        case 1:
                            return 0;
                        case 3:
                            return 180;
                        case 6:
                            return 90;
                        case 8:
                            return 270;
                    }
                    Log.i(TAG, "Unsupported orientation");
                    return 0;
                }
                offset += 12;
                length -= 12;
            }
        }

        Log.i(TAG, "Orientation not found");
        return 0;
    }

    private static int pack(byte[] bytes, int offset, int length,
                            boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }

        int value = 0;
        while (length-- > 0) {
            value = (value << 8) | (bytes[offset] & 0xFF);
            offset += step;
        }
        return value;
    }
}
