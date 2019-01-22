package com.carecloud.carepaylibray.media;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.PermissionsUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 4/24/17.
 */
public class MediaScannerPresenter {

    private static final String LOG_TAG = "MediaScannerPresenter";
    public static final int REQUEST_CODE_CAMERA = 0x555;
    public static final int REQUEST_CODE_GALLERY = 0x666;
    public static final int REQUEST_CODE_CAPTURE = 0x777;
    public static final int REQUEST_CODE_FILE = 0x888;

    public static final String DATA_CAPTURED_IMAGE_KEY = "capturedImageKey";

    private static final String ACTION_PICTURE = "demographics_take_pic_option";
    private static final String ACTION_GALLERY = "demographics_select_gallery_option";
    private static final String ACTION_CANCEL = "demographics_cancel_label";
    private static final String ACTION_FILE = "demographics_select_file_option";

    private static final String[] SUPPORTED_MIME_TYPES = {
            "application/json",
            "application/pdf",
            "application/xml",
            "application/zip",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "text/plain",
            "text/html",
            "text/csv",
            "text/xml",
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/tiff"
    };

    public static int captureViewId;

    private Context context;
    private MediaViewInterface mediaViewInterface;
    private View captureView;

    private String pendingAction;
    private CarePayCameraPreview.CameraType cameraType;

    /**
     * Presenter for handling any views that require use of the Camera or Gallery
     *
     * @param context            context
     * @param mediaViewInterface view interface
     * @param cameraType         the camera type
     */
    public MediaScannerPresenter(Context context, MediaViewInterface mediaViewInterface,
                                 CarePayCameraPreview.CameraType cameraType) {
        this.context = context;
        this.mediaViewInterface = mediaViewInterface;
        this.cameraType = cameraType;
    }

    /**
     * Presenter for handling any views that require use of the Camera or Gallery
     *
     * @param context            context
     * @param mediaViewInterface view interface
     * @param captureView        view for setting captured image
     * @param cameraType         the camera type
     */
    public MediaScannerPresenter(Context context, MediaViewInterface mediaViewInterface, View captureView,
                                 CarePayCameraPreview.CameraType cameraType) {
        this(context, mediaViewInterface, cameraType);
        this.captureView = captureView;
        captureViewId = captureView.getId();
    }

    /**
     * Change the capture view for this presenter
     *
     * @param captureView new view for setting captured image
     */
    public void setCaptureView(View captureView) {
        this.captureView = captureView;
        captureViewId = captureView.getId();
    }

    /**
     * Start Image Selection by presenting a set of choices
     *
     * @param includeGalleryOption should include select from Gallery choice
     */
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

    public void selectFile() {
        handleFileAction();
    }

    /**
     * Forward permission result handling to presenter
     *
     * @param requestCode  requestCode
     * @param permissions  permissions
     * @param grantResults grantResults
     */
    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionsUtil.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (pendingAction.equals(ACTION_GALLERY)) {
                        handleGalleryAction();
                    } else if (pendingAction.equals(ACTION_FILE)) {
                        handleFileAction();
                    }
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
                break;

            default:
                Log.d(MediaScannerPresenter.class.getName(), "Cannot handle this permission request");
        }

    }

    /**
     * Forward Activity Result handling
     *
     * @param requestCode request Code
     * @param resultCode  result Code
     * @param data        intent data
     * @return true if handled
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_GALLERY: {
                    String filePath = data.getDataString();
                    if (filePath != null) {
                        mediaViewInterface.setCapturedBitmap(filePath, captureView);
                    } else {
                        //// TODO: 6/2/17 show error message
                    }
                    return true;
                }
                case REQUEST_CODE_CAPTURE: {
                    String filePath = null;
                    if (data != null) {
                        filePath = data.getStringExtra(DATA_CAPTURED_IMAGE_KEY);
                    }
                    if (filePath != null) {
                        mediaViewInterface.setCapturedBitmap(filePath, captureView);
                    }
                    return true;
                }
                case REQUEST_CODE_FILE: {
                    String fileName = null;
                    File outFile = null;
                    Uri uri = data.getData();
                    if (uri != null && "content".equals(uri.getScheme())) {
                        Cursor cursor = context.getContentResolver().query(uri,
                                new String[]{MediaStore.Files.FileColumns.DISPLAY_NAME},
                                null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            fileName = cursor.getString(0);
                            cursor.close();
                        }


                        if (fileName != null) {
                            try {
                                outFile = new File(context.getCacheDir() + "/" + fileName);
                                InputStream input = context.getContentResolver().openInputStream(uri);
                                OutputStream output = new FileOutputStream(outFile);
                                byte[] bytes = new byte[16384];
                                int readBytes;
                                if(input != null) {
                                    while ((readBytes = input.read(bytes, 0, bytes.length)) != -1) {
                                        output.write(bytes, 0, readBytes);
                                    }
                                    input.close();
                                }
                                output.close();
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                                outFile = null;
                            }
                        }

                    }

                    if (fileName == null && uri != null) {
                        outFile = new File(URI.create(uri.getPath()));
                    }

                    if (outFile != null && outFile.exists()) {
                        mediaViewInterface.setCapturedBitmap(outFile.getAbsolutePath(), captureView);
                    }
                    return true;
                }
                default:
                    return false;
            }
        }
        return false;
    }

    private DialogInterface.OnClickListener getActionItemClickListener(final List<String> mediaOptions) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String option = mediaOptions.get(which);
                switch (option) {
                    case ACTION_PICTURE:
                        handlePictureAction();
                        break;
                    case ACTION_GALLERY:
                        handleGalleryAction();
                        break;
                    default:
                        dialog.cancel();

                }
            }
        };
    }

    private void setPendingAction(String action) {
        this.pendingAction = action;
    }

    private void handlePictureAction() {
        setPendingAction(ACTION_PICTURE);
        if (mediaViewInterface.getCallingFragment() != null) {
            if (!PermissionsUtil.checkPermissionCamera(mediaViewInterface.getCallingFragment())) {
                return;
            }
        } else if (!PermissionsUtil.checkPermissionCamera(context)) {
            return;
        }

        captureImage();
    }

    private void handleGalleryAction() {
        setPendingAction(ACTION_GALLERY);
        if (mediaViewInterface.getCallingFragment() != null) {
            if (!PermissionsUtil.checkPermissionStorage(mediaViewInterface.getCallingFragment())) {
                return;
            }
        } else if (!PermissionsUtil.checkPermissionStorage(context)) {
            return;
        }

        mediaViewInterface.handleStartActivityForResult(ImageCaptureHelper.galleryIntent(), REQUEST_CODE_GALLERY);

    }

    private void handleFileAction() {
        setPendingAction(ACTION_FILE);
        if (mediaViewInterface.getCallingFragment() != null) {
            if (!PermissionsUtil.checkPermissionStorage(mediaViewInterface.getCallingFragment())) {
                return;
            }
        } else if (!PermissionsUtil.checkPermissionStorage(context)) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, SUPPORTED_MIME_TYPES);

        mediaViewInterface.handleStartActivityForResult(intent, REQUEST_CODE_FILE);

    }

    private List<String> getMediaOptions(boolean includeGallery) {
        List<String> mediaOptions = new ArrayList<>();

        mediaOptions.add(ACTION_PICTURE);
        if (includeGallery) {
            mediaOptions.add(ACTION_GALLERY);
        }
        mediaOptions.add(ACTION_CANCEL);

        return mediaOptions;
    }

    private void captureImage() {
        Intent intent = new Intent(context, MediaCameraActivity.class);
        intent.putExtra("cameraType", cameraType);
        mediaViewInterface.handleStartActivityForResult(intent, REQUEST_CODE_CAPTURE);
    }

    private class MediaActionAdapter extends BaseAdapter {
        private Context context;
        private List<String> items = new ArrayList<>();

        MediaActionAdapter(Context context, List<String> items) {
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
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView textView = (TextView) itemView.findViewById(android.R.id.text1);
            textView.setText(Label.getLabel(items.get(position)));

            return itemView;
        }
    }

}
