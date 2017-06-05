package com.carecloud.carepaylibray.demographics.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by lmenendez on 6/5/17
 */

public class DocumentScannerAdapter {
    public static final int FRONT_PIC = 1;
    public static final int BACK_PIC = 2;

    private Context context;
    private MediaScannerPresenter mediaScannerPresenter;
    private ApplicationMode.ApplicationType applicationType;

    private Button scanFrontButton;
    private Button scanBackButton;
    private ImageView imageFront;
    private ImageView imageBack;


    public DocumentScannerAdapter(Context context, View view, MediaScannerPresenter mediaScannerPresenter, ApplicationMode.ApplicationType applicationType){
        initViews(view);
        this.context = context;
        this.mediaScannerPresenter = mediaScannerPresenter;
        this.applicationType = applicationType;
    }

    private void initViews(View view){
        imageFront = (ImageView) view.findViewById(R.id.demogrDocsFrontScanImage);
        imageBack = (ImageView) view.findViewById(R.id.demogrDocsBackScanImage);

        final boolean isPatientApp = applicationType == ApplicationMode.ApplicationType.PATIENT;
        scanFrontButton = (Button) view.findViewById(R.id.demogrDocsFrontScanButton);
        scanFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaScannerPresenter.setCaptureView(imageFront);
                mediaScannerPresenter.selectImage(isPatientApp);
            }
        });

        scanBackButton = (Button) view.findViewById(R.id.demogrDocsBackScanButton);
        scanBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaScannerPresenter.setCaptureView(imageBack);
                mediaScannerPresenter.selectImage(isPatientApp);
            }
        });

    }

    public void setDocumentsFromData(DemographicIdDocPayloadDTO docPayloadDTO){
        String frontPic = null;
        String backPic = null;
        if(docPayloadDTO !=null && !docPayloadDTO.getIdDocPhothos().isEmpty()){
            for(DemographicIdDocPhotoDTO docPhotoDTO : docPayloadDTO.getIdDocPhothos()){
                if(docPhotoDTO.getPage()==FRONT_PIC){
                    frontPic = docPhotoDTO.getIdDocPhoto();
                }
                if(docPhotoDTO.getPage()==BACK_PIC){
                    backPic = docPhotoDTO.getIdDocPhoto();
                }
            }
        }


        if (!StringUtil.isNullOrEmpty(frontPic)) {
            setImageView(frontPic, imageFront, false);
        }


        if (!StringUtil.isNullOrEmpty(backPic)) {
            setImageView(backPic, imageBack, false);
        }
    }

    public void setImageView(String filePath, final View view, final boolean updateButton){
        final ImageView imageView = (ImageView) view;

        imageView.measure(0,0);
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        final int width = Math.max(imageView.getMeasuredWidth(), lp.width);
        final int height = Math.max(imageView.getMeasuredHeight(), lp.height);

        File file = new File(filePath);
        Uri fileUri;
        if(file.exists()){
            fileUri = Uri.fromFile(file);
        }else{
            //check if we have a base64 image instead of an URI
            Bitmap bitmap = SystemUtil.convertStringToBitmap(filePath);
            if(bitmap!=null) {
                File temp = ImageCaptureHelper.getBitmapFileUrl(context, bitmap, "temp_"+System.currentTimeMillis());
                fileUri = Uri.fromFile(temp);
            }else {
                fileUri = Uri.parse(filePath);
            }
        }

        Picasso.with(context).load(fileUri)
                .placeholder(R.drawable.icn_camera)
                .resize(width, height)
                .centerInside()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .transform(new RoundedCornersTransformation(10, 0))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                        lp.width = width;
                        lp.height = height;
                        imageView.setLayoutParams(lp);

                        if(updateButton) {
                            if (view.getId() == getFrontImageId()) {
                                scanFrontButton.setText(Label.getLabel("demographics_documents_rescan_front"));
                            }

                            if (view.getId() == getBackImageId()) {
                                scanBackButton.setText(Label.getLabel("demographics_documents_rescan_back"));
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        imageView.setImageDrawable(ContextCompat.getDrawable(context,
                                R.drawable.icn_camera));
                    }
                });
    }

    public int getFrontImageId(){
        return imageFront.getId();
    }

    public int getBackImageId(){
        return imageBack.getId();
    }

}
