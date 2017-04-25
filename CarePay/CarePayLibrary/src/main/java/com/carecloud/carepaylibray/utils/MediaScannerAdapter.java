package com.carecloud.carepaylibray.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 4/24/17.
 *
 */

public class MediaScannerAdapter {

    public static final String ACTION_PICTURE = "demographics_take_pic_option";
    public static final String ACTION_GALLERY = "demographics_select_gallery_option";
    public static final String ACTION_CANCEL = "demographics_cancel_label";

    private Context context;
    private CarePayCameraReady cameraReady;

    public MediaScannerAdapter(Context context, CarePayCameraReady cameraReady){
        this.context = context;
        this.cameraReady = cameraReady;
    }


    /**
     * Starts Camera or Gallery to capture/select an image
     *
     * @param isFrontScan The camera helper used with a particular imageview
     */
    public void selectImage(ImageView targetImageView, boolean isFrontScan, boolean includeGalleryOption) {
//        this.targetImageView = targetImageView;
//        this.isFrontScan = isFrontScan;

        // create the chooser dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(ImageCaptureHelper.chooseActionDlgTitle);

        List<String> mediaOptions = getMediaOptions(includeGalleryOption);
        MediaActionAdapter mediaActionAdapter = new MediaActionAdapter(context, mediaOptions);
        builder.setAdapter(mediaActionAdapter, getActionItemClickListener(mediaOptions));
        builder.show();
    }


    private DialogInterface.OnClickListener getActionItemClickListener(final List<String> mediaOptions) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String option = mediaOptions.get(which);
                switch(option){
                    case ACTION_PICTURE:

                        break ;
                    case ACTION_GALLERY:

                        break;
                    default:


                }
            }
        };
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
