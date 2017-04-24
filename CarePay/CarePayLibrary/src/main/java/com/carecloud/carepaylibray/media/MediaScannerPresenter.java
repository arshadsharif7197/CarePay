package com.carecloud.carepaylibray.media;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.PermissionsUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 4/24/17.
 *
 */

public class MediaScannerPresenter {

    public static final String ACTION_PICTURE = "demographics_take_pic_option";
    public static final String ACTION_GALLERY = "demographics_select_gallery_option";
    public static final String ACTION_CANCEL = "demographics_cancel_label";

    private Context context;
    private MediaViewInterface mediaViewInterface;
    private CarePayCameraReady cameraReady;

    private String pendingAction;

    public MediaScannerPresenter(Context context, CarePayCameraReady cameraReady, MediaViewInterface mediaViewInterface){
        this.context = context;
        this.cameraReady = cameraReady;
        this.mediaViewInterface = mediaViewInterface;
    }


    public void selectImage(boolean includeGalleryOption) {
        // create the chooser dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String title = StringUtil.captialize(Label.getLabel("demographics_select_capture_option_title"));
        builder.setTitle(title);

        List<String> mediaOptions = getMediaOptions(includeGalleryOption);
        MediaActionAdapter mediaActionAdapter = new MediaActionAdapter(context, mediaOptions);
        builder.setAdapter(mediaActionAdapter, getActionItemClickListener(mediaOptions));

        builder.setCancelable(false);
        builder.show();
    }

    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case PermissionsUtil.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && pendingAction.equals(ACTION_GALLERY)) {
                    handleGalleryAction();
                } else {
                    //code for deny
                    Log.v(LOG_TAG, "read external denied");
                }
                break;

            case PermissionsUtil.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && pendingAction.equals(ACTION_PICTURE)) {
                    handlePictureAction();
                } else {
                    //code for deny
                    Log.v(LOG_TAG, "camera denied");
                }
        }

    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == ImageCaptureHelper.SELECT_FILE) {
               //TODO bitmap = ImageCaptureHelper.onSelectFromGalleryResult(context, targetImageView, data, cameraType, getImageShape());
            }

            if(bitmap != null) {
                mediaViewInterface.getCameraCallback().onCapturedSuccess(bitmap);
            }else{
                mediaViewInterface.getCameraCallback().onCaptureFail();
            }
        }
    }

    private DialogInterface.OnClickListener getActionItemClickListener(final List<String> mediaOptions) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String option = mediaOptions.get(which);
                switch(option){
                    case ACTION_PICTURE:
                        handlePictureAction();
                        break ;
                    case ACTION_GALLERY:
                        handleGalleryAction();
                        break;
                    default:
                        dialog.cancel();

                }
            }
        };
    }

    private void setPendingAction(String action){
        this.pendingAction = action;
    }

    private void handlePictureAction(){
        if(PermissionsUtil.checkPermissionCamera(context)){
            CarePayCameraCallback cameraCallback = mediaViewInterface.getCameraCallback();
            if(cameraCallback!=null) {
                cameraReady.captureImage(cameraCallback);
            }
        }else{
            setPendingAction(ACTION_PICTURE);
        }
    }

    private void handleGalleryAction(){
        if(PermissionsUtil.checkPermission(context)){
            mediaViewInterface.startActivityForResult(ImageCaptureHelper.galleryIntent(), ImageCaptureHelper.SELECT_FILE);
        }else{
            setPendingAction(ACTION_GALLERY);
        }
    }

    private List<String> getMediaOptions(boolean includeGallery){
        List<String> mediaOptions = new ArrayList<>();

        mediaOptions.add(ACTION_PICTURE);
        if(includeGallery){
            mediaOptions.add(ACTION_GALLERY);
        }
        mediaOptions.add(ACTION_CANCEL);

        return mediaOptions;
    }

    private class MediaActionAdapter extends BaseAdapter {
        private Context context;
        private List<String> items = new ArrayList<>();

        MediaActionAdapter(Context context, List<String> items){
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public String getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null){
                itemView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView textView = (TextView) itemView.findViewById(android.R.id.text1);
            textView.setText(Label.getLabel(items.get(position)));

            return itemView;
        }
    }



}
