package com.carecloud.carepaylibray.demographics.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

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


    /**
     * Adapter for managing Document Scanner Views
     * @param context context
     * @param view base view
     * @param mediaScannerPresenter media scanner presenter
     * @param applicationType application mode
     */
    public DocumentScannerAdapter(Context context, View view, MediaScannerPresenter mediaScannerPresenter, ApplicationMode.ApplicationType applicationType){
        this.context = context;
        this.mediaScannerPresenter = mediaScannerPresenter;
        this.applicationType = applicationType;
        initViews(view);
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

    /**
     * Setup Documents from Dto Data
     * @param docPayloadDTO DemographicIdDocPayloadDTO
     */
    public void setIdDocumentsFromData(DemographicIdDocPayloadDTO docPayloadDTO){
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

    /**
     * Setup Insurance Photos from Dto Data
     * @param insurancePayloadDTO DemographicInsurancePayloadDTO
     */
    public void setInsuranceDocumentsFromData(DemographicInsurancePayloadDTO insurancePayloadDTO){
        String frontPic = null;
        String backPic = null;

        if(insurancePayloadDTO != null && !insurancePayloadDTO.getInsurancePhotos().isEmpty()){
            for(DemographicInsurancePhotoDTO photoDTO : insurancePayloadDTO.getInsurancePhotos()){
                if(photoDTO.getPage() == FRONT_PIC){
                    frontPic = photoDTO.getInsurancePhoto();
                }
                if(photoDTO.getPage() == BACK_PIC){
                    backPic = photoDTO.getInsurancePhoto();
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

    /**
     * Show image from path
     * @param filePath can be a file path, other URI, or Base64 string
     * @param view view to add image
     * @param updateButton should update the corresponding button text
     */
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

    /**
     * Get Base64 encoded string from File or URI
     * @param filePath file path or Uri
     * @return Base64 string if filepath is valid
     */
    public String getBase64(String filePath){
        File file = new File(filePath);
        Bitmap bitmap = null;
        if(file.exists()) {
            bitmap = BitmapFactory.decodeFile(filePath);
        }else{
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(filePath));
            }catch (IOException ioe){
                //do nothing
            }
        }

        if(bitmap != null){
            return SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
        }
        return null;
    }

}
