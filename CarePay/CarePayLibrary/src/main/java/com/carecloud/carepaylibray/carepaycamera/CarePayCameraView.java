package com.carecloud.carepaylibray.carepaycamera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
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
    private int currentCameraId = CarePayCameraPreview.NO_DEFINED_CAMERA;
    private Button flashButton;
    private View closeButton;
    private ApplicationMode.ApplicationType applicationType;

    /**
     * Public constructor with context
     *
     * @param callback after photo taken
     * @param context  sender context
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
                             CarePayCameraPreview.CameraType cameraType, ApplicationMode.ApplicationType applicationType) {
        super(context);
        this.callback = callback;
        this.context = context;
        this.cameraType = cameraType;
        this.applicationType = applicationType;
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
        buttonCapture = findViewById(R.id.button_capture);
        carePayCameraPreview = findViewById(R.id.camera_preview);
        carePayCameraPreview.setCameraType(cameraType);
        buttonCapture.setOnClickListener(onCaptureClick);

        if (applicationType != ApplicationMode.ApplicationType.PATIENT) {
            closeButton = findViewById(R.id.closeViewLayout);
            closeButton.setOnClickListener(view -> carePayCameraPreview.onClose());
        }

        flashButton = findViewById(R.id.button_flash);
        flashButton.setOnClickListener(view -> {
            if (view.isSelected()) {
                carePayCameraPreview.turnOffFlash();
            } else {
                carePayCameraPreview.turnOnFlash();
            }
            view.setSelected(!view.isSelected());

        });

        Button changeCameraButton = findViewById(R.id.button_change_camera);
        changeCameraButton.setEnabled(carePayCameraPreview.canChangeCamera());
        changeCameraButton.setOnClickListener(view -> {
            currentCameraId = carePayCameraPreview.changeCamera();
            callback.onChangeCamera(currentCameraId);
            flashButton.setSelected(false);
            flashButton.setEnabled(carePayCameraPreview.hasFlash());
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

    /**
     * @param currentCameraId current camera Id
     */
    public void start(int currentCameraId) {
        carePayCameraPreview.start(currentCameraId);
        carePayCameraPreview.setVisibility(View.VISIBLE);
        flashButton.setEnabled(carePayCameraPreview.hasFlash());
        buttonCapture.setEnabled(true);
    }

    /**
     * stop camera preview
     */
    public void stop() {
        buttonCapture.setEnabled(false);
        carePayCameraPreview.setVisibility(View.GONE);
        carePayCameraPreview.stop();
    }
}