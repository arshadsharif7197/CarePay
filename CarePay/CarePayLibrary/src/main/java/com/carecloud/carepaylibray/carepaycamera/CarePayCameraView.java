package com.carecloud.carepaylibray.carepaycamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.carecloud.carepaylibrary.R;

/**
 * Created by Jahirul Bhuiyan on 11/10/2016.
 */

public class CarePayCameraView extends RelativeLayout {
    Context context;
    Button buttonCapture;
    private CarePayCameraPreview carePayCameraPreview;

    /**
     * Public constructor with context
     *
     * @param context sender context
     */
    public CarePayCameraView(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    /**
     * Public constructor with context and Attribute.
     * All the custom styleable declare are apply here also.
     *
     * @param context sender context
     * @param attrs   styleable attributes
     */
    public CarePayCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    /**
     * Public constructor with context, Attributes and default attributes.
     * All the custom styleable declare are apply here also.
     * Default attributes also apply here
     *
     * @param context      sender context
     * @param attrs        styleable attributes
     * @param defStyleAttr styleable default attributes
     */

    public CarePayCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    /**
     * initialize
     *
     * @param attrs styleable attributes
     */
    private void init(AttributeSet attrs) {
        inflate(context, R.layout.view_carepay_camera, this);
        buttonCapture = (Button) findViewById(R.id.button_capture);
        carePayCameraPreview = (CarePayCameraPreview) findViewById(R.id.camera_preview);
        buttonCapture.setOnClickListener(onCaptureClick);
    }

    OnClickListener onCaptureClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (carePayCameraPreview != null) {
                carePayCameraPreview.takePicture();
            }
        }
    };

    private Bitmap getCapturedBitmap() {
        return carePayCameraPreview == null ? null : carePayCameraPreview.getCapturedBitmap();
    }
}
