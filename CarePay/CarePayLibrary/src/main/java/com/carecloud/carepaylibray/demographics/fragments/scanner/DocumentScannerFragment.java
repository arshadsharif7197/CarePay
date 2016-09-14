package com.carecloud.carepaylibray.demographics.fragments.scanner;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.Utility;

import java.util.Arrays;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by lsoco_user on 9/13/2016.
 * Generic fragment that incorporates camera scanning functionality
 */
public abstract class DocumentScannerFragment extends Fragment{

    private   ImageCaptureHelper          imageCaptureHelper;
    protected NextAddRemoveStatusModifier buttonsStatusCallback;

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
    public void selectImage(final ImageCaptureHelper imageCaptureHelper) {
        this.imageCaptureHelper = imageCaptureHelper;
        // create the chooser dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(ImageCaptureHelper.chooseActionDlgTitle);
        builder.setItems(ImageCaptureHelper.chooseActionDlOptions,
                         new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int item) {
                                 if (item == 0) { // "Take picture" chosen
                                     imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[0].toString());
                                     boolean result = Utility.checkPermissionCamera(getActivity());
                                     if (result) {
                                         startActivityForResult(imageCaptureHelper.cameraIntent(), ImageCaptureHelper.REQUEST_CAMERA);
                                     }
                                 } else if (item == 1) {  // "Select from Gallery" chosen
                                     imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[1].toString());
                                     boolean result = Utility.checkPermission(getActivity());
                                     if (result) {
                                         startActivityForResult(Intent.createChooser(imageCaptureHelper.galleryIntent(),
                                                                                     ImageCaptureHelper.CHOOSER_NAME),
                                                                ImageCaptureHelper.SELECT_FILE);
                                     }
                                 } else if (item == 3) { // "Cancel"
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
    protected void showChooseDialog(final String[] options, String title, final TextView selectionDestination) {
        final String cancelLabel = "Cancel";
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        // add cancel button
        dialog.setNegativeButton(cancelLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // create dialog layout
        View customView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        // create the adapter
        CustomAlertAdapter mAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(options));
        listView.setAdapter(mAdapter);
        // show the dialog
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();
        // set item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedOption = options[position];
                selectionDestination.setText(selectedOption);
                alert.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.v(LOG_TAG, "onReauestPermissionsResult()");
        String userChoosenTask = imageCaptureHelper.getUserChoosenTask();
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[1].toString()))
                        startActivityForResult(Intent.createChooser(imageCaptureHelper.galleryIntent(),
                                                                    ImageCaptureHelper.CHOOSER_NAME),
                                               ImageCaptureHelper.SELECT_FILE);
                } else {
                    //code for deny
                    Log.v(LOG_TAG, "read external denied");
                }
                break;

            case Utility.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[0].toString()))
                        startActivityForResult(imageCaptureHelper.cameraIntent(), ImageCaptureHelper.REQUEST_CAMERA);
                } else {
                    //code for deny
                    Log.v(LOG_TAG, "camera denied");
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageCaptureHelper.SELECT_FILE) {
                imageCaptureHelper.onSelectFromGalleryResult(data, ImageCaptureHelper.RECTANGULAR_IMAGE);
            } else if (requestCode == ImageCaptureHelper.REQUEST_CAMERA) {
                imageCaptureHelper.onCaptureImageResult(data, ImageCaptureHelper.RECTANGULAR_IMAGE);
            }
            updateDetailViewsAfterScan();
        }
    }

    /**
     * Set the callback that will enable this fragment to change the status of butons in another fragment
     * @param buttonsStatusCallback The callback
     */
    public void setButtonsStatusCallback(NextAddRemoveStatusModifier buttonsStatusCallback) {
        this.buttonsStatusCallback = buttonsStatusCallback;
    }

    /**
     * Updates the number the button label and the number textview accoring to doc scanned (license or insurance)
     */
    protected abstract void updateDetailViewsAfterScan();


    /**
     * Set the typefaces
     */
    protected abstract void setTypefaces(View view);

    /**
     * Callback interface to change the status of buttons of another fragment
     */
    public interface NextAddRemoveStatusModifier {
        void showAddCardButton(boolean isVisible);
        void enableNextButton(boolean isEnabled);
        void scrollToBottom();
    }
}