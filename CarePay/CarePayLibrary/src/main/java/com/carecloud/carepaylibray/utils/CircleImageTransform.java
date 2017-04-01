package com.carecloud.carepaylibray.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.squareup.picasso.Transformation;

public class CircleImageTransform implements Transformation {

    private static final int MAX_SIZE = 100;

    private int maxSize = MAX_SIZE;

    public CircleImageTransform() {
    }

    public CircleImageTransform(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        return circle(square(source));
    }

    private Bitmap circle(Bitmap input) {
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(input, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);

        int size = input.getWidth();
        float r = size / 2f;

        Bitmap output = Bitmap.createBitmap(size, size, input.getConfig());

        Canvas canvas = new Canvas(output);
        canvas.drawCircle(r, r, r, paint);

        input.recycle();
        return output;
    }

    private Bitmap square(Bitmap input) {
        int inSize = Math.min(input.getWidth(), input.getHeight());
        int outSize = Math.min(inSize, maxSize);

        int left = (input.getWidth() - inSize) / 2;
        int top = (input.getHeight() - inSize) / 2;

        final Rect inRect = new Rect(left, top, inSize, inSize);
        final Rect outRect = new Rect(0, 0, outSize, outSize);

        Bitmap output = Bitmap.createBitmap(outSize, outSize, input.getConfig());

        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(input, inRect, outRect, null);

        input.recycle();
        return output;
    }

    @Override
    public String key() {
        return "circle";
    }
}