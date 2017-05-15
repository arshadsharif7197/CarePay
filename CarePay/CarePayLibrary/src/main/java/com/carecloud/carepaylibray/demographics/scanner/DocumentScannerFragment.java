package com.carecloud.carepaylibray.demographics.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.PermissionsUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by lsoco_user on 9/13/2016.
 * Generic fragment that incorporates camera scanning functionality
 */
public abstract class DocumentScannerFragment extends BaseCheckinFragment implements CarePayCameraCallback {

    protected boolean hasImageChanged;
    protected boolean isFrontScan;
    private ImageView targetImageView;
    protected ImageView imageFront;
    protected ImageView imageBack;
    private ImageCaptureHelper.CameraType cameraType;

    protected CarePayCameraReady carePayCameraReady;

    @Override
    public void attachCallback(Context context) {
        try {
            if (context instanceof DemographicsView) {
                carePayCameraReady = ((DemographicsView) context).getPresenter();
            } else {
                carePayCameraReady = (CarePayCameraReady) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CarePayCameraReady");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(carePayCameraReady == null){
            attachCallback(getContext());
        }
    }

    /**
     * Starts Camera or Gallery to capture/select an image
     *
     * @param isFrontScan The camera helper used with a particular imageview
     */
    public void selectImage(ImageView targetImageView, boolean isFrontScan, ImageCaptureHelper.CameraType cameraType) {
        this.targetImageView = targetImageView;
        this.isFrontScan = isFrontScan;
        this.cameraType = cameraType;
        // create the chooser dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(ImageCaptureHelper.chooseActionDlgTitle);
        if (cameraType == ImageCaptureHelper.CameraType.DEFAULT_CAMERA) {
            builder.setItems(ImageCaptureHelper.getActionDlOptions(), dialogOnClickListener);
        } else {
            builder.setItems(ImageCaptureHelper.getActionDocumentDlOptions(), dialogDocumentScanOnClickListener);
        }

        builder.show();
    }

    private DialogInterface.OnClickListener dialogDocumentScanOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            if (item == 0) { // "Take picture" chosen
                ImageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.getActionDlOptions(0));
                boolean result = PermissionsUtil.checkPermissionCamera(getActivity());
                if (result) {
                    carePayCameraReady.captureImage(DocumentScannerFragment.this);
                }
            } else if (item == 1) { // "Cancel"
                dialog.dismiss();
            }
        }
    };

    private DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            switch (item) {
                case 0:  // "Take picture" chosen
                    ImageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.getActionDlOptions(0));

                    if (PermissionsUtil.checkPermissionCamera(getActivity())) {
                        // uncomment when camera activity
                        carePayCameraReady.captureImage(DocumentScannerFragment.this);
                    }
                    break;

                case 1:   // "Select from Gallery" chosen
                    ImageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.getActionDlOptions(1));

                    if (PermissionsUtil.checkPermission(getActivity())) {
                        startActivityForResult(ImageCaptureHelper.galleryIntent(), ImageCaptureHelper.SELECT_FILE);
                    }
                    break;

                default:  // "Cancel"
                    dialog.dismiss();
            }
        }
    };

    /**
     * Creates a generic dialog that contains a list of choices
     *
     * @param options              The choices
     * @param title                The dlg title
     * @param selectionDestination The textview where the selected option will be displayed
     */
    protected void showChooseDialog(final String[] options, String title, String cancelLabel,
                                    final TextView selectionDestination) {
        SystemUtil.showChooseDialog(getActivity(),
                options, title, cancelLabel,
                selectionDestination,
                new SystemUtil.OnClickItemCallback() {
                    @Override
                    public void executeOnClick(TextView destination, String selectedOption) {
                        updateModel(selectionDestination);
                    }
                });
    }

    protected abstract void updateModel(TextView selectionDestination);

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.v(LOG_TAG, "onReauestPermissionsResult()");
        String userChoosenTask = ImageCaptureHelper.getUserChoosenTask();
        switch (requestCode) {
            case PermissionsUtil.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.getActionDlOptions(1))) {
                        startActivityForResult(Intent.createChooser(ImageCaptureHelper.galleryIntent(),
                                ImageCaptureHelper.CHOOSER_NAME),
                                ImageCaptureHelper.SELECT_FILE);
                    }
                } else {
                    //code for deny
                    Log.v(LOG_TAG, "read external denied");
                }
                break;

            case PermissionsUtil.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.getActionDlOptions(0))) {
                        ImageCaptureHelper.requestCamera(getActivity(), ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
                    }
                } else {
                    //code for deny
                    Log.v(LOG_TAG, "camera denied");
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == ImageCaptureHelper.SELECT_FILE) {
                bitmap = ImageCaptureHelper.onSelectFromGalleryResult(getContext(), targetImageView, data, cameraType, getImageShape());
            } else if (requestCode == ImageCaptureHelper.REQUEST_CAMERA) {
                if (cameraType == ImageCaptureHelper.CameraType.CUSTOM_CAMERA) {
                    bitmap = ImageCaptureHelper.getImageBitmap();
                    ImageCaptureHelper.setCapturedImageToTargetView(getContext(), targetImageView, bitmap, cameraType, getImageShape());
                } else {
                    bitmap = ImageCaptureHelper.onCaptureImageResult(getContext(), targetImageView, data, cameraType, getImageShape());
                }
            }

            hasImageChanged = bitmap != null;
            onCapturedSuccess(bitmap);
        }
    }

    protected void loadFrontPlaceHolder() {
        // if no image to load, simply load the placeholder
        Drawable drawable = null;
        if(getContext()!=null) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.icn_placeholder_user_profile_png);
        }

        if (imageFront != null && drawable!=null) {
            imageFront.setImageDrawable(drawable);
        }
    }

    /**
     * Gets the shape of the captured image
     *
     * @return The shape (ImageCaptureHelper.ImageShape)
     */
    public abstract ImageCaptureHelper.ImageShape getImageShape();

    /**
     * Populate the views with the date from model
     */
    public abstract void populateViewsFromModel(View view);

    /**
     * Set the typefaces
     */
    protected abstract void setTypefaces(View view);

    /**
     * Set insurance DTO
     *
     * @param insuranceDTO
     * @param placeholderBase64
     */
    protected abstract void setInsuranceDTO(DemographicInsurancePayloadDTO insuranceDTO, String placeholderBase64);

    /**
     * @param enabled
     */
    protected abstract void enablePlanClickable(boolean enabled);

    /**
     *set focus change listeners
     */
    protected abstract void setChangeFocusListeners();

}
