package com.carecloud.carepaylibray.carepaycamera;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;

/**
 * Created by cocampo on 3/28/17.
 */

public class CarePayCameraFragment extends BaseDialogFragment implements CarePayCameraCallback {

    private CarePayCameraCallback callback;

    public CarePayCameraFragment() {
    }

    public static CarePayCameraFragment newInstance(CarePayCameraPreview.CameraType cameraType) {
        Bundle args = new Bundle();
        args.putSerializable("cameraType", cameraType);
        CarePayCameraFragment fragment = new CarePayCameraFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setCancelable(true);
        return inflater.inflate(R.layout.activity_care_pay_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CarePayCameraPreview.CameraType cameraType = (CarePayCameraPreview.CameraType) getArguments()
                .getSerializable("cameraType");
        // Set content
        CarePayCameraView carePayCameraView = new CarePayCameraView(this, getContext(), cameraType);
        ((FrameLayout) view.findViewById(R.id.camera_preview)).addView(carePayCameraView);
    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        dismiss();
        if (callback != null) {
            callback.onCapturedSuccess(bitmap);
        }
    }

    @Override
    public void onCaptureFail() {
        dismiss();
    }


    @Override
    public void onStart() {
        super.onStart();
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    private void attachCallback(Context context) {
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (CarePayCameraCallback) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CarePayCameraCallback");
        }
    }

}
