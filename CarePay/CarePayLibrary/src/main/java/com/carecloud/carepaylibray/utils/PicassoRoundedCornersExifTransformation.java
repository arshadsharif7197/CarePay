package com.carecloud.carepaylibray.utils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PicassoRoundedCornersExifTransformation extends RoundedCornersTransformation {
    private String url;
    private Map<String, String> headers;

    public PicassoRoundedCornersExifTransformation(int radius,
                                                   int margin,
                                                   @NonNull String url,
                                                   @NonNull Map<String, String> headers) {
        this(radius, margin, CornerType.ALL, url, headers);
    }

    public PicassoRoundedCornersExifTransformation(int radius,
                                                   int margin,
                                                   CornerType cornerType,
                                                   @NonNull String url,
                                                   @NonNull Map<String, String> headers) {
        super(radius, margin, cornerType);
        this.url = url;
        this.headers = headers;
    }

    @Override
    public Bitmap transform(Bitmap source){
        byte[] bitmapBytes = getByteArrayImage(url);

        int degrees = ImageCaptureHelper.getExifOrientation(bitmapBytes);
        if(degrees != 0) {
            Bitmap rotated = ImageCaptureHelper.rotateBitmap(source, degrees);
            source.recycle();
            return super.transform(rotated);
        }
        return super.transform(source);
    }

    private byte[] getByteArrayImage(String url){
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            for(Map.Entry<String, String> entries : headers.entrySet()) {
                ucon.setRequestProperty(entries.getKey(), entries.getValue());
            }

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuilder bab = new ByteArrayBuilder(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                bab.append((byte) current);
            }

            return bab.toByteArray();
        } catch (Exception e) {
            Log.d(this.getClass().getName(), "Error: " + e.toString());
        }
        return null;
    }

}
