package com.carecloud.carepaylibray.carepaycamera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.carecloud.carepaylibrary.R;


/**
 * Created by Jahirul Bhuiyan on 11/10/2016.
 */

public class CarePayCameraView extends RelativeLayout {

    private CarePayCameraPreview.CameraType cameraType = CarePayCameraPreview.CameraType.SCAN_DOC;
    private CarePayCameraCallback callback;
    private Context context;
    private Button buttonCapture;

    private CarePayCameraPreview carePayCameraPreview;

    /**
     * Public constructor with context
     *
     * @param callback   after photo taken
     * @param context    sender context
     */
    public CarePayCameraView(CarePayCameraCallback callback, Context context) {
        super(context);
        this.callback = callback;
        this.context = context;
        init(null);
    }

    /**
     * Public constructor with context
     *
     * @param callback   after photo taken
     * @param context    sender context
     * @param cameraType the camera type
     */
    public CarePayCameraView(CarePayCameraCallback callback, Context context,
                             CarePayCameraPreview.CameraType cameraType) {
        super(context);
        this.callback = callback;
        this.context = context;
        this.cameraType = cameraType;
        init(null);
    }

    /**
     * Public constructor with context
     *
     * @param context sender context
     * @param cameraType the camera type
     */
    public CarePayCameraView(Context context, CarePayCameraPreview.CameraType cameraType) {
        this((CarePayCameraCallback) context, context, cameraType);
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
        carePayCameraPreview.setCameraType(cameraType);
        carePayCameraPreview.initialize();
        buttonCapture.setOnClickListener(onCaptureClick);

        final Button flashButton = (Button) findViewById(R.id.button_flash);
        flashButton.setEnabled(carePayCameraPreview.hasFlash());
        flashButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isSelected()) {
                    carePayCameraPreview.turnOffFlash();
                } else {
                    carePayCameraPreview.turnOnFlash();
                }
                view.setSelected(!view.isSelected());

            }
        });

        Button changeCameraButton = (Button) findViewById(R.id.button_change_camera);
        changeCameraButton.setEnabled(carePayCameraPreview.canChangeCamera());
        changeCameraButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                carePayCameraPreview.changeCamera();
                flashButton.setSelected(false);
                flashButton.setEnabled(carePayCameraPreview.hasFlash());
            }
        });

    }

    OnClickListener onCaptureClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            buttonCapture.setClickable(false);
            if (carePayCameraPreview != null) {
                carePayCameraPreview.takePicture(callback);
            }
        }
    };

    public CarePayCameraPreview getCarePayCameraPreview() {
        return carePayCameraPreview;
    }


}