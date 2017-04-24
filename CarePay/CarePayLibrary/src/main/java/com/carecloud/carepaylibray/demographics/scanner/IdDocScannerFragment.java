package com.carecloud.carepaylibray.demographics.scanner;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by lsoco_user on 9/13/2016.
 * Fragment for with scanning driver's license functionality
 */
public class IdDocScannerFragment extends DocumentScannerFragment {
    private static final int FRONT_PIC = 1;
    private static final int BACK_PIC = 2;

    private static final String LOG_TAG = IdDocScannerFragment.class.getSimpleName();
    private View view;
    private Button scanFrontButton;
    private Button scanBackButton;

    private DemographicIdDocPayloadDTO currentModel;

    private DemographicIdDocPayloadDTO postModel;

    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // create the view
        view = inflater.inflate(getLayoutRes(), container, false);

        initializeUIFields();
        handler = new Handler();

        return view;
    }

    protected int getLayoutRes() {
        return R.layout.fragment_demographics_scan_license;
    }

    private void initializeUIFields() {
        currentModel = DtoHelper.getConvertedDTO(DemographicIdDocPayloadDTO.class, getArguments());

//        initializePhotos();

        imageFront = (ImageView) view.findViewById(R.id.demogrDocsFrontScanImage);
        imageBack = (ImageView) view.findViewById(R.id.demogrDocsBackScanImage);

        // add click listener
        scanFrontButton = (Button) view.findViewById(R.id.demogrDocsFrontScanButton);
        scanFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageFront, true, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
            }
        });

        scanBackButton = (Button) view.findViewById(R.id.demogrDocsBackScanButton);
        scanBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageBack, false, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
            }
        });

        setTypefaces(view);

        populateViewsFromModel(view);
    }

    @Override
    protected void updateModel(TextView selectionDestination) {
    }

    @Override
    public void onCapturedSuccess(Bitmap bitmap) { // license has been scanned
        if (bitmap != null) {
            final Bitmap rotateBitmap = ImageCaptureHelper.rotateBitmap(bitmap, ImageCaptureHelper.getOrientation());


            final ImageView imageView;
            String tempFile;
            final int page;

            if(isFrontScan){
                scanFrontButton.setText(Label.getLabel("demographics_documents_rescan_front"));
                imageView = imageFront;
                tempFile = "idFront";
                page = FRONT_PIC;
            }else{
                scanBackButton.setText(Label.getLabel("demographics_documents_rescan_back"));
                imageView = imageBack;
                tempFile = "idBack";
                page = BACK_PIC;
            }

                int width = imageView.getWidth();
                int height = imageView.getHeight();
                imageView.getLayoutParams().width = width;
                imageView.getLayoutParams().height = height;

                File file = ImageCaptureHelper.getBitmapFileUrl(getContext(), rotateBitmap, tempFile);
                Picasso.with(getContext()).load(file)
//                        .rotate(ImageCaptureHelper.getOrientation())
                        .resize(width, height)
                        .centerInside()
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .transform(new RoundedCornersTransformation(10, 0))
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            }

                            @Override
                            public void onError() {

                            }
                        });


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // save from image
                        String imageAsBase64 = SystemUtil.convertBitmapToString(rotateBitmap, Bitmap.CompressFormat.JPEG, 90);
                        DemographicIdDocPhotoDTO photoDTO = new DemographicIdDocPhotoDTO();
                        photoDTO.setIdDocPhoto(imageAsBase64); // create the image dto
                        photoDTO.setPage(page);
                        photoDTO.setDelete(false);
                        addPostModelPhoto(photoDTO);
                    }
                });


        }
    }

    @Override
    public void onCaptureFail() {

    }

    @Override
    public void populateViewsFromModel(View view) {
        String frontPic = null;
        String backPic = null;
        if(currentModel !=null && !currentModel.getIdDocPhothos().isEmpty()){
            for(DemographicIdDocPhotoDTO docPhotoDTO : currentModel.getIdDocPhothos()){
                if(docPhotoDTO.getPage()==FRONT_PIC){
                    frontPic = docPhotoDTO.getIdDocPhoto();
                }
                if(docPhotoDTO.getPage()==BACK_PIC){
                    backPic = docPhotoDTO.getIdDocPhoto();
                }
            }
        }


        if (!StringUtil.isNullOrEmpty(frontPic)) {
            setPhotoView(imageFront, frontPic);
        }


        if (!StringUtil.isNullOrEmpty(backPic)) {
            setPhotoView(imageBack, backPic);
        }

    }

    @Override
    protected void setTypefaces(View view) {
    }

    @Override
    public ImageCaptureHelper.ImageShape getImageShape() {
        return ImageCaptureHelper.ImageShape.RECTANGULAR;
    }

    public DemographicIdDocPayloadDTO getPostModel() {
        return postModel!=null?postModel:new DemographicIdDocPayloadDTO();
    }

    private void addPostModelPhoto(DemographicIdDocPhotoDTO docPhotoDTO){
        if(postModel==null){
            postModel = new DemographicIdDocPayloadDTO();
        }

        if(!postModel.getIdDocPhothos().isEmpty()){
            for(DemographicIdDocPhotoDTO savedPhotoDTO : postModel.getIdDocPhothos()){
                if(savedPhotoDTO.getPage() == docPhotoDTO.getPage()){
                    //overwrite
                    savedPhotoDTO = docPhotoDTO;
                    return;
                }
            }
        }

        //if it wasnt found already in the model then add it
        postModel.getIdDocPhothos().add(docPhotoDTO);
    }

    private void setPhotoView(final ImageView imageView, String photoUrl){
        imageView.measure(0,0);
        final int width = imageView.getMeasuredWidth();
        final int height = imageView.getMeasuredHeight();

        Picasso.with(getContext()).load(photoUrl)
                .resize(width, height)
                .centerInside()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .transform(new RoundedCornersTransformation(10, 0))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    }

                    @Override
                    public void onError() {
                        imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                R.drawable.icn_placeholder_document));
                    }
                });
    }



    @Override
    public void setInsuranceDTO(DemographicInsurancePayloadDTO insuranceDTO, String placeholderBase64) {

    }

    @Override
    public void enablePlanClickable(boolean enabled) {

    }

    @Override
    protected void setChangeFocusListeners() {

    }
}