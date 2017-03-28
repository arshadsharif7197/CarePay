package com.carecloud.carepaylibray.demographics.scanner;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
public abstract class DocumentScannerFragment extends BaseCheckinFragment {

    protected boolean hasImageChanged;
    protected ImageCaptureHelper imageCaptureHelper;
    protected ImageCaptureHelper.CameraType cameraType;

    /**
     * Starts Camera or Gallery to capture/select an image
     *
     * @param imageCaptureHelper The camera helper used with a particular imageview
     */
    public void selectImage(final ImageCaptureHelper imageCaptureHelper, final ImageCaptureHelper.CameraType cameraType) {
        this.imageCaptureHelper = imageCaptureHelper;
        this.cameraType = cameraType;
        // create the chooser dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(ImageCaptureHelper.chooseActionDlgTitle);
        if (cameraType == ImageCaptureHelper.CameraType.DEFAULT_CAMERA) {
            builder.setItems(ImageCaptureHelper.chooseActionDlOptions, dialogOnClickListener);
        } else {
            builder.setItems(ImageCaptureHelper.chooseActionDocumentDlOptions, dialogDocumentScanOnClickListener);
        }

        builder.show();
    }

    private DialogInterface.OnClickListener dialogDocumentScanOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            if (item == 0) { // "Take picture" chosen
                imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[0].toString());
                boolean result = PermissionsUtil.checkPermissionCamera(getActivity());
                if (result) {
                    // uncomment when camera activity
                    startActivityForResult(imageCaptureHelper.getCameraIntent(cameraType), ImageCaptureHelper.REQUEST_CAMERA);
                }
            } else if (item == 1) { // "Cancel"
                dialog.dismiss();
            }
        }
    };

    private DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            if (item == 0) { // "Take picture" chosen
                imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[0].toString());
                boolean result = PermissionsUtil.checkPermissionCamera(getActivity());
                if (result) {
                    // uncomment when camera activity
                    startActivityForResult(imageCaptureHelper.getCameraIntent(cameraType), ImageCaptureHelper.REQUEST_CAMERA);
                }
            } else if (item == 1) {  // "Select from Gallery" chosen
                imageCaptureHelper.setCameraType(null);
                imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[1].toString());
                boolean result = PermissionsUtil.checkPermission(getActivity());
                if (result) {
                    startActivityForResult(imageCaptureHelper.galleryIntent(), ImageCaptureHelper.SELECT_FILE);
                }
            } else if (item == 2) { // "Cancel"
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
        String userChoosenTask = imageCaptureHelper.getUserChoosenTask();
        switch (requestCode) {
            case PermissionsUtil.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[1].toString())) {
                        startActivityForResult(Intent.createChooser(imageCaptureHelper.galleryIntent(),
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
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[0].toString())) {
                        startActivityForResult(imageCaptureHelper.cameraIntent(getContext()), ImageCaptureHelper.REQUEST_CAMERA);
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
                bitmap = imageCaptureHelper.onSelectFromGalleryResult(data, getImageShape());
            } else if (requestCode == ImageCaptureHelper.REQUEST_CAMERA) {
                if (cameraType == ImageCaptureHelper.CameraType.CUSTOM_CAMERA) {
//                    bitmap = imageCaptureHelper.onCaptureImageResult(getImageShape());
//                    Log.v(LOG_TAG, "Orientation camera to: " + imageCaptureHelper.getOrientation());
                } else {
                    bitmap = imageCaptureHelper.onCaptureImageResult(data, getImageShape());
                }
            }

            hasImageChanged = bitmap != null;
            updateModelAndViewsAfterScan(imageCaptureHelper, bitmap);
        }
    }

    /**
     * Gets the shape of the captured image
     *
     * @return The shape (ImageCaptureHelper.ImageShape)
     */
    public abstract ImageCaptureHelper.ImageShape getImageShape();

    /**
     * Updates the number the button label and the number textview accoring to doc scanned (license or insurance)
     */
    protected abstract void updateModelAndViewsAfterScan(ImageCaptureHelper scanner, Bitmap bitmap);

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
