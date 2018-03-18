package com.carecloud.carepaylibray.qrcodescanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.carecloud.carepaylibrary.R;

public abstract class BarcodeScannerView extends FrameLayout implements Camera.PreviewCallback  {
    private CameraWrapper cameraWrapper;
    private CameraPreview cameraPreview;
    private IViewFinder viewFinder;
    private Rect framingRectInPreview;
    private CameraHandlerThread cameraHandlerThread;
    private Boolean flashState;
    private boolean autofocusState = true;
    private boolean shouldScaleToFill = true;
    Context context;

    public BarcodeScannerView(Context context) {
        super(context);
        this.context=context;
    }

    public BarcodeScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context=context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.BarcodeScannerView,
                0, 0);

        try {
            setShouldScaleToFill(a.getBoolean(R.styleable.BarcodeScannerView_shouldScaleToFill, true));
        } finally {
            a.recycle();
        }
    }

    public final void setupLayout(CameraWrapper cameraWrapper) {
        removeAllViews();

        cameraPreview = new CameraPreview(getContext(), cameraWrapper, this);
        cameraPreview.setShouldScaleToFill(shouldScaleToFill);
        if (!shouldScaleToFill) {
            RelativeLayout relativeLayout = new RelativeLayout(getContext());
            relativeLayout.setGravity(Gravity.CENTER);
            relativeLayout.setBackgroundColor(Color.BLACK);
            relativeLayout.addView(cameraPreview);
            addView(relativeLayout);
        } else {
            addView(cameraPreview);
        }

        viewFinder = createViewFinderView(getContext());
        if (viewFinder instanceof View) {
            addView((View) viewFinder);
        } else {
            throw new IllegalArgumentException("IViewFinder object returned by " +
                    "'createViewFinderView()' should be instance of android.view.View");
        }
    }

    /**
     * <p>Method that creates view that represents visual appearance of a barcode scanner</p>
     * <p>Override it to provide your own view for visual appearance of a barcode scanner</p>
     *
     * @param context {@link Context}
     * @return {@link android.view.View} that implements {@link ViewFinderView}
     */
    protected IViewFinder createViewFinderView(Context context) {
        return new ViewFinderView(context);
    }

    public void startCamera(int cameraId) {
        if(cameraHandlerThread == null) {
            cameraHandlerThread = new CameraHandlerThread(this);
        }
        cameraHandlerThread.startCamera(cameraId);
    }

    public void setupCameraPreview(CameraWrapper cameraWrapper) {
        this.cameraWrapper = cameraWrapper;
        if(this.cameraWrapper != null) {
            setupLayout(this.cameraWrapper);
            viewFinder.setupViewFinder();
            if(flashState != null) {
                setFlash(flashState);
            }
            setAutoFocus(autofocusState);
        }
    }

    public void startCamera() {
        startCamera(CameraUtils.getDefaultCameraId());
    }

    public void stopCamera() {
        if(cameraWrapper != null) {
            cameraPreview.stopCameraPreview();
            cameraPreview.setCamera(null, null);
            cameraWrapper.camera.release();
            cameraWrapper = null;
        }
        if(cameraHandlerThread != null) {
            cameraHandlerThread.quit();
            cameraHandlerThread = null;
        }
    }

    public void stopCameraPreview() {
        if(cameraPreview != null) {
            cameraPreview.stopCameraPreview();
        }
    }

    protected void resumeCameraPreview() {
        if(cameraPreview != null) {
            cameraPreview.showCameraPreview();
        }
    }

    public synchronized Rect getFramingRectInPreview(int previewWidth, int previewHeight) {
        if (framingRectInPreview == null) {
            Rect framingRect = viewFinder.getFramingRect();
            int viewFinderViewWidth = viewFinder.getWidth();
            int viewFinderViewHeight = viewFinder.getHeight();
            if (framingRect == null || viewFinderViewWidth == 0 || viewFinderViewHeight == 0) {
                return null;
            }

            Rect rect = new Rect(framingRect);

            if(previewWidth < viewFinderViewWidth) {
                rect.left = rect.left * previewWidth / viewFinderViewWidth;
                rect.right = rect.right * previewWidth / viewFinderViewWidth;
            }

            if(previewHeight < viewFinderViewHeight) {
                rect.top = rect.top * previewHeight / viewFinderViewHeight;
                rect.bottom = rect.bottom * previewHeight / viewFinderViewHeight;
            }

            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }

    public void setFlash(boolean flag) {
        flashState = flag;
        if(cameraWrapper != null && CameraUtils.isFlashSupported(cameraWrapper.camera)) {

            Camera.Parameters parameters = cameraWrapper.camera.getParameters();
            if(flag) {
                if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                    return;
                }
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    return;
                }
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            cameraWrapper.camera.setParameters(parameters);
        }
    }

    public boolean getFlash() {
        if(cameraWrapper != null && CameraUtils.isFlashSupported(cameraWrapper.camera)) {
            Camera.Parameters parameters = cameraWrapper.camera.getParameters();
            if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void toggleFlash() {
        if(cameraWrapper != null && CameraUtils.isFlashSupported(cameraWrapper.camera)) {
            Camera.Parameters parameters = cameraWrapper.camera.getParameters();
            if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }
            cameraWrapper.camera.setParameters(parameters);
        }
    }

    public void setAutoFocus(boolean state) {
        autofocusState = state;
        if(cameraPreview != null) {
            cameraPreview.setAutoFocus(state);
        }
    }

    public void setShouldScaleToFill(boolean shouldScaleToFill) {
        this.shouldScaleToFill = shouldScaleToFill;
    }
}
