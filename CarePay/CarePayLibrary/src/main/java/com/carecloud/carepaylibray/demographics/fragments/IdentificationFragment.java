package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;

import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.BACK_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.FRONT_PIC;

import java.util.List;


public class IdentificationFragment extends CheckInDemographicsBaseFragment implements MediaViewInterface {
    private DemographicDTO demographicDTO;
    private MediaScannerPresenter mediaScannerPresenter;
    private DocumentScannerAdapter documentScannerAdapter;

    private boolean hasFrontImage = false;
    private boolean hasBackImage = false;
    private String base64FrontImage;
    private String base64BackImage;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        checkIfEnableButton(view);
        view.findViewById(R.id.toolbar_layout).setVisibility(View.INVISIBLE);

        setHeaderTitle(Label.getLabel("demographics_review_identification"),
                Label.getLabel("demographics_identity_heading"),
                Label.getLabel("demographics_identity_subheading"),
                view);

        initNextButton(view);

        initImageViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        stepProgressBar.setCurrentProgressDot(3);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 4);
        checkinFlowCallback.setCurrentStep(4);
    }

    @Override
    protected boolean passConstraints(View view) {
        return true;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review_demographic_identification;
    }

    @Override
    protected DemographicDTO updateDemographicDTO(View view) {
        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setIdDocument(getPostModel());

        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        return updatableDemographicDTO;
    }

    private DemographicIdDocPayloadDTO getPostModel(){
        setupImageBase64();
        DemographicIdDocPayloadDTO docPayloadDTO = new DemographicIdDocPayloadDTO();
        if(hasFrontImage && base64FrontImage != null){
            DemographicIdDocPhotoDTO docPhotoDTO = new DemographicIdDocPhotoDTO();
            docPhotoDTO.setPage(FRONT_PIC);
            docPhotoDTO.setIdDocPhoto(base64FrontImage);
            docPhotoDTO.setDelete(false);
            docPayloadDTO.getIdDocPhothos().add(docPhotoDTO);
        }
        if(hasBackImage && base64BackImage != null){
            DemographicIdDocPhotoDTO docPhotoDTO = new DemographicIdDocPhotoDTO();
            docPhotoDTO.setPage(BACK_PIC);
            docPhotoDTO.setIdDocPhoto(base64BackImage);
            docPhotoDTO.setDelete(false);
            docPayloadDTO.getIdDocPhothos().add(docPhotoDTO);
        }
        return docPayloadDTO;
    }

    private void initImageViews(View view){
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this);
        documentScannerAdapter = new DocumentScannerAdapter(getContext(), view, mediaScannerPresenter, getApplicationMode().getApplicationType());
        documentScannerAdapter.setIdDocumentsFromData(demographicDTO.getPayload().getDemographics().getPayload().getIdDocument());
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mediaScannerPresenter != null && mediaScannerPresenter.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(mediaScannerPresenter != null){
            mediaScannerPresenter.handleRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void setCapturedBitmap(String filePath, View view) {
        if (filePath != null) {
            documentScannerAdapter.setImageView(filePath, view, true);
            int page;
            if(view.getId() == documentScannerAdapter.getFrontImageId()){
                page = FRONT_PIC;
                hasFrontImage = true;
            }else{
                page = BACK_PIC;
                hasBackImage = true;
            }

            DemographicIdDocPayloadDTO docPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getIdDocument();
            for(DemographicIdDocPhotoDTO docPhotoDTO : docPayloadDTO.getIdDocPhothos()){
                if(docPhotoDTO.getPage() == page){
                    docPhotoDTO.setIdDocPhoto(filePath);
                    return;
                }
            }

            //did not find an item to update
            DemographicIdDocPhotoDTO docPhotoDTO = new DemographicIdDocPhotoDTO();
            docPhotoDTO.setPage(page);
            docPhotoDTO.setDelete(false);
            docPhotoDTO.setIdDocPhoto(filePath);
            docPayloadDTO.getIdDocPhothos().add(docPhotoDTO);
        }
    }

    @Override
    public void handleStartActivityForResult(Intent intent, int requestCode) {
        getActivity().startActivityForResult(intent, requestCode);
    }

    @Nullable
    @Override
    public Fragment getCallingFragment() {
        return this;
    }

    @Override
    public void setupImageBase64() {
        List<DemographicIdDocPhotoDTO> docPhotos = demographicDTO.getPayload().getDemographics().getPayload().getIdDocument().getIdDocPhothos();
        String filePath;
        for(DemographicIdDocPhotoDTO docPhotoDTO : docPhotos){
            if(docPhotoDTO.getPage() == FRONT_PIC && hasFrontImage){
                filePath = docPhotoDTO.getIdDocPhoto();
                base64FrontImage = documentScannerAdapter.getBase64(filePath);
            }

            if(docPhotoDTO.getPage() == BACK_PIC && hasBackImage){
                filePath = docPhotoDTO.getIdDocPhoto();
                base64BackImage = documentScannerAdapter.getBase64(filePath);
            }
        }
    }

}
