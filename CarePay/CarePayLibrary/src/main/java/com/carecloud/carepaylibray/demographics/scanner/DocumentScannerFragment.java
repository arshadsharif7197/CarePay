package com.carecloud.carepaylibray.demographics.scanner;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.PermissionsUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by lsoco_user on 9/13/2016.
 * Generic fragment that incorporates camera scanning functionality
 */
public abstract class DocumentScannerFragment extends Fragment {

    protected ImageCaptureHelper imageCaptureHelper;
    protected Bitmap bitmap;
    protected ImageCaptureHelper.CameraType cameraType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

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
        builder.setItems(ImageCaptureHelper.chooseActionDlOptions,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) { // "Take picture" chosen
                            imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[0].toString());
                            boolean result = PermissionsUtil.checkPermissionCamera(getActivity());
                            if (result) {
                                // uncomment when camera activity
                                startActivityForResult(imageCaptureHelper.getCameraIntent(cameraType), ImageCaptureHelper.REQUEST_CAMERA);
                            }
                        } /*else if (item == 1) {  // "Select from Gallery" chosen
                            imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[1].toString());
                            boolean result = PermissionsUtil.checkPermission(getActivity());
                            if (result) {
                                startActivityForResult(imageCaptureHelper.galleryIntent(), ImageCaptureHelper.SELECT_FILE);
                            }
                        }*/ else if (item == 1) { // "Cancel"
                            dialog.dismiss();
                        }
                    }
                });
        builder.show();
    }

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
            if (requestCode == ImageCaptureHelper.SELECT_FILE) {
                bitmap = imageCaptureHelper.onSelectFromGalleryResult(data, getImageShape());
            } else if (requestCode == ImageCaptureHelper.REQUEST_CAMERA) {
                if(cameraType == ImageCaptureHelper.CameraType.CUSTOM_CAMERA) {
                    bitmap = imageCaptureHelper.onCaptureImageResult(getImageShape());
                    Log.v(LOG_TAG,"Orientation camera to: " + imageCaptureHelper.getOrientation());
                } else {
                    bitmap = imageCaptureHelper.onCaptureImageResult(data, getImageShape());
                }
            }
            updateModelAndViewsAfterScan(imageCaptureHelper);
        }
    }

    /**
     * Gets the shape of the captured image
     *
     * @return The shape (ImageCaptureHelper.ROUND_IMAGE or RECTANGULAR_IMAGE)
     */
    public abstract int getImageShape();

    /**
     * Updates the number the button label and the number textview accoring to doc scanned (license or insurance)
     */
    protected abstract void updateModelAndViewsAfterScan(ImageCaptureHelper scanner);

    /**
     * Populate the views with the date from model
     */
    public abstract void populateViewsFromModel();

    /**
     * Set the typefaces
     */
    protected abstract void setTypefaces(View view);
}