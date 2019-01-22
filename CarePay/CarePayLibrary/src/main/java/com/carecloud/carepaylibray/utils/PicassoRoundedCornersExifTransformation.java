package com.carecloud.carepaylibray.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PicassoRoundedCornersExifTransformation extends RoundedCornersTransformation {
    public PicassoRoundedCornersExifTransformation(int radius, int margin) {
        super(radius, margin);
    }

    public PicassoRoundedCornersExifTransformation(int radius, int margin, CornerType cornerType) {
        super(radius, margin, cornerType);
    }

    @Override
    public Bitmap transform(Bitmap source){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        source.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapBytes = stream.toByteArray();

        int degrees = ImageCaptureHelper.getExifOrientation(bitmapBytes);
        Bitmap rotated = ImageCaptureHelper.rotateBitmap(source, degrees);

        return super.transform(rotated);
    }
}
