package com.carecloud.carepaylibray.carepaycamera;

public interface CarePayCameraReady {
    void captureImage(CarePayCameraCallback callback, CarePayCameraPreview.CameraType cameraType);
}
