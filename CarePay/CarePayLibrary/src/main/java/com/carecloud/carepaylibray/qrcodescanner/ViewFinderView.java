package com.carecloud.carepaylibray.qrcodescanner;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.carecloud.carepaylibrary.R;

public class ViewFinderView extends View implements IViewFinder {
    private static final String TAG = "ViewFinderView";

    private Rect mFramingRect;

    private static final float PORTRAIT_WIDTH_RATIO = 6f/8;
    private static final float PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f;

    private static final float LANDSCAPE_HEIGHT_RATIO = 5f/8;
    private static final float LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f;
    private static final int MIN_DIMENSION_DIFF = 50;

    private static final float SQUARE_DIMENSION_RATIO = 5f/8;

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private int scannerAlpha;
    private static final int POINT_SIZE = 10;
    private static final long ANIMATION_DELAY = 80l;

    private final int defaultLaserColor = getResources().getColor(R.color.viewfinder_laser);
    private final int defaultMaskColor = getResources().getColor(R.color.viewfinder_mask);
    private final int defaultBorderColor = getResources().getColor(R.color.viewfinder_border);
    private final int defaultBorderStrokeWidth = getResources().getInteger(R.integer.viewfinder_border_width);
    private final int defaultBorderLineLength = getResources().getInteger(R.integer.viewfinder_border_length);

    protected Paint laserPaint;
    protected Paint finderMaskPaint;
    protected Paint borderPaint;
    protected int borderLineLength;
    protected boolean squareViewFinder;

    public ViewFinderView(Context context) {
        super(context);
        init();
    }

    public ViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //set up laser paint
        laserPaint = new Paint();
        laserPaint.setColor(defaultLaserColor);
        laserPaint.setStyle(Paint.Style.FILL);

        //finder mask paint
        finderMaskPaint = new Paint();
        finderMaskPaint.setColor(defaultMaskColor);

        //border paint
        borderPaint = new Paint();
        borderPaint.setColor(defaultBorderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(defaultBorderStrokeWidth);

        borderLineLength = defaultBorderLineLength;
    }

    public void setLaserColor(int laserColor) {
        laserPaint.setColor(laserColor);
    }
    public void setMaskColor(int maskColor) {
        finderMaskPaint.setColor(maskColor);
    }
    public void setBorderColor(int borderColor) {
        borderPaint.setColor(borderColor);
    }
    public void setBorderStrokeWidth(int borderStrokeWidth) {
        borderPaint.setStrokeWidth(borderStrokeWidth);
    }
    public void setBorderLineLength(int borderLineLength) {
        this.borderLineLength = borderLineLength;
    }

    // TODO: Need a better way to configure this. Revisit when working on 2.0
    public void setSquareViewFinder(boolean set) {
        squareViewFinder = set;
    }

    public void setupViewFinder() {
        updateFramingRect();
        invalidate();
    }

    public Rect getFramingRect() {
        return mFramingRect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(getFramingRect() == null) {
            return;
        }

        drawViewFinderMask(canvas);
        drawViewFinderBorder(canvas);
        //drawLaser(canvas);
    }

    public void drawViewFinderMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Rect framingRect = getFramingRect();
        
        canvas.drawRect(0, 0, width, framingRect.top, finderMaskPaint);
        canvas.drawRect(0, framingRect.top, framingRect.left, framingRect.bottom + 1, finderMaskPaint);
        canvas.drawRect(framingRect.right + 1, framingRect.top, width, framingRect.bottom + 1, finderMaskPaint);
        canvas.drawRect(0, framingRect.bottom + 1, width, height, finderMaskPaint);
    }

    public void drawViewFinderBorder(Canvas canvas) {
        Rect framingRect = getFramingRect();
        
        canvas.drawLine(framingRect.left - 1, framingRect.top - 1, framingRect.left - 1, framingRect.top - 1 + borderLineLength, borderPaint);
        canvas.drawLine(framingRect.left - 1, framingRect.top - 1, framingRect.left - 1 + borderLineLength, framingRect.top - 1, borderPaint);

        canvas.drawLine(framingRect.left - 1, framingRect.bottom + 1, framingRect.left - 1, framingRect.bottom + 1 - borderLineLength, borderPaint);
        canvas.drawLine(framingRect.left - 1, framingRect.bottom + 1, framingRect.left - 1 + borderLineLength, framingRect.bottom + 1, borderPaint);

        canvas.drawLine(framingRect.right + 1, framingRect.top - 1, framingRect.right + 1, framingRect.top - 1 + borderLineLength, borderPaint);
        canvas.drawLine(framingRect.right + 1, framingRect.top - 1, framingRect.right + 1 - borderLineLength, framingRect.top - 1, borderPaint);

        canvas.drawLine(framingRect.right + 1, framingRect.bottom + 1, framingRect.right + 1, framingRect.bottom + 1 - borderLineLength, borderPaint);
        canvas.drawLine(framingRect.right + 1, framingRect.bottom + 1, framingRect.right + 1 - borderLineLength, framingRect.bottom + 1, borderPaint);
    }

    public void drawLaser(Canvas canvas) {
        Rect framingRect = getFramingRect();
        
        // Draw a red "laser scanner" line through the middle to show decoding is active
        laserPaint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = framingRect.height() / 2 + framingRect.top;
        canvas.drawRect(framingRect.left + 2, middle - 1, framingRect.right - 1, middle + 2, laserPaint);

        postInvalidateDelayed(ANIMATION_DELAY,
                framingRect.left - POINT_SIZE,
                framingRect.top - POINT_SIZE,
                framingRect.right + POINT_SIZE,
                framingRect.bottom + POINT_SIZE);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        updateFramingRect();
    }

    public synchronized void updateFramingRect() {
        Point viewResolution = new Point(getWidth(), getHeight());
        int width;
        int height;
        int orientation = DisplayUtils.getScreenOrientation(getContext());

        if(squareViewFinder) {
            if(orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (int) (getHeight() * SQUARE_DIMENSION_RATIO);
                width = height;
            } else {
                width = (int) (getWidth() * SQUARE_DIMENSION_RATIO);
                height = width;
            }
        } else {
            if(orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (int) (getHeight() * LANDSCAPE_HEIGHT_RATIO);
                width = (int) (LANDSCAPE_WIDTH_HEIGHT_RATIO * height);
            } else {
                width = (int) (getWidth() * PORTRAIT_WIDTH_RATIO);
                height = (int) (PORTRAIT_WIDTH_HEIGHT_RATIO * width);
            }
        }

        if(width > getWidth()) {
            width = getWidth() - MIN_DIMENSION_DIFF;
        }

        if(height > getHeight()) {
            height = getHeight() - MIN_DIMENSION_DIFF;
        }

        int leftOffset = (viewResolution.x - width) / 2;
        int topOffset = (viewResolution.y - height) / 2;
        mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
    }
}
