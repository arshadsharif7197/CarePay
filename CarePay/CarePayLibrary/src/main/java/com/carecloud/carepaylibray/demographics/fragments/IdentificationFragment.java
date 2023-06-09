package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.BACK_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.FRONT_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_DTO;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_HAS_BACK;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_HAS_FRONT;

import java.util.List;


public class IdentificationFragment extends CheckInDemographicsBaseFragment implements MediaViewInterface {

    private MediaScannerPresenter mediaScannerPresenter;
    private DocumentScannerAdapter documentScannerAdapter;

    boolean hasFrontImage = false;
    boolean hasBackImage = false;
    private String base64FrontImage;
    private String base64BackImage;

    private Button scanFrontButton;
    private Button scanBackButton;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (icicle != null) {
            String demographicDtoString = icicle.getString(KEY_DTO);
            if (demographicDtoString != null) {
                demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, demographicDtoString);
            }
            hasFrontImage = icicle.getBoolean(KEY_HAS_FRONT, false);
            hasBackImage = icicle.getBoolean(KEY_HAS_BACK, false);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        icicle.putString(KEY_DTO, DtoHelper.getStringDTO(demographicDTO));
        icicle.putBoolean(KEY_HAS_FRONT, hasFrontImage);
        icicle.putBoolean(KEY_HAS_BACK, hasBackImage);
        super.onSaveInstanceState(icicle);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        checkIfEnableButton(view);
        view.findViewById(R.id.toolbar_layout).setVisibility(View.INVISIBLE);

        setHeaderTitle(Label.getLabel("demographics_review_identification"),
                Label.getLabel("demographics_identity_heading"),
                Label.getLabel("demographics_identity_subheading"),
                view);

        initNextButton(view);

        initImageViews(view);
    }


    @Override
    public void onResume() {
        super.onResume();
        stepProgressBar.setCurrentProgressDot(CheckinFlowCallback.IDENTITY - 1);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, checkinFlowCallback.getTotalSteps(), CheckinFlowCallback.IDENTITY);
        checkinFlowCallback.setCurrentStep(CheckinFlowCallback.IDENTITY);
    }

    @Override
    protected boolean getCDRFieldsStatus() {
        // empty implementation
        return false;
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

        updatableDemographicDTO.getPayload()
                .setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        return updatableDemographicDTO;
    }

    @Override
    protected void replaceTranslatedOptionsValues() {

    }

    private DemographicIdDocPayloadDTO getPostModel() {
        setupImageBase64();
        DemographicIdDocPayloadDTO docPayloadDTO = new DemographicIdDocPayloadDTO();
        if ((hasFrontImage && base64FrontImage != null) ||
                (hasBackImage && base64BackImage != null)) {
            //Log new Identity Doc
            String[] params = {getString(R.string.param_is_checkin),
                    getString(R.string.param_practice_id),
                    getString(R.string.param_provider_id),
                    getString(R.string.param_location_id),
                    getString(R.string.param_appointment_id)
            };
            Object[] values = {true,
                    checkinFlowCallback.getAppointment().getMetadata().getPracticeId(),
                    checkinFlowCallback.getAppointment().getPayload().getProvider().getGuid(),
                    checkinFlowCallback.getAppointment().getPayload().getLocation().getGuid(),
                    checkinFlowCallback.getAppointment().getMetadata().getAppointmentId()
            };
            MixPanelUtil.logEvent(getString(R.string.event_add_identity_doc), params, values);
        }

        if (hasFrontImage && base64FrontImage != null) {
            DemographicIdDocPhotoDTO docPhotoDTO = new DemographicIdDocPhotoDTO();
            docPhotoDTO.setPage(FRONT_PIC);
            docPhotoDTO.setIdDocPhoto(base64FrontImage);
            docPhotoDTO.setDelete(false);
            docPayloadDTO.getIdDocPhothos().add(docPhotoDTO);
        }
        if (hasBackImage && base64BackImage != null) {
            DemographicIdDocPhotoDTO docPhotoDTO = new DemographicIdDocPhotoDTO();
            docPhotoDTO.setPage(BACK_PIC);
            docPhotoDTO.setIdDocPhoto(base64BackImage);
            docPhotoDTO.setDelete(false);
            docPayloadDTO.getIdDocPhothos().add(docPhotoDTO);
        }
        return docPayloadDTO;
    }

    private void initImageViews(View view) {
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this,
                CarePayCameraPreview.CameraType.SCAN_DOC);
        documentScannerAdapter = new DocumentScannerAdapter(getContext(), view, mediaScannerPresenter, getApplicationMode().getApplicationType());
        documentScannerAdapter.setIdDocumentsFromData(demographicDTO.getPayload().getDemographics().getPayload().getIdDocument());

        scanFrontButton = view.findViewById(com.carecloud.carepaylibrary.R.id.demogrDocsFrontScanButton);
        scanBackButton = view.findViewById(com.carecloud.carepaylibrary.R.id.demogrDocsBackScanButton);

        scanFrontButton.setOnClickListener(view1 -> {
            documentScannerAdapter.setFrontCaptureImage();
            mediaScannerPresenter.handlePictureAction();
        });
        scanBackButton.setOnClickListener(view12 -> {
            documentScannerAdapter.setBackCaptureImage();
            mediaScannerPresenter.handlePictureAction();
        });

        View lastCaptureView = view.findViewById(MediaScannerPresenter.captureViewId);
        if (lastCaptureView != null) {
            mediaScannerPresenter.setCaptureView(lastCaptureView);
        }

        if (hasFrontImage) {
            documentScannerAdapter.setFrontRescan();
        }

        if (hasBackImage) {
            documentScannerAdapter.setBackRescan();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        if (mediaScannerPresenter != null) {
            mediaScannerPresenter.handleRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void setCapturedBitmap(String filePath, View view) {
        if (filePath != null) {
            documentScannerAdapter.setImageView(filePath, view, true);
            int page;
            if (view.getId() == documentScannerAdapter.getFrontImageId()) {
                page = FRONT_PIC;
                hasFrontImage = true;
            } else {
                page = BACK_PIC;
                hasBackImage = true;
            }

            DemographicIdDocPayloadDTO docPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getIdDocument();
            for (DemographicIdDocPhotoDTO docPhotoDTO : docPayloadDTO.getIdDocPhothos()) {
                if (docPhotoDTO.getPage() == page) {
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
        startActivityForResult(intent, requestCode);
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
        for (DemographicIdDocPhotoDTO docPhotoDTO : docPhotos) {
            if (docPhotoDTO.getPage() == FRONT_PIC && hasFrontImage) {
                filePath = docPhotoDTO.getIdDocPhoto();
                base64FrontImage = DocumentScannerAdapter.getBase64(getContext(), filePath);
            }

            if (docPhotoDTO.getPage() == BACK_PIC && hasBackImage) {
                filePath = docPhotoDTO.getIdDocPhoto();
                base64BackImage = DocumentScannerAdapter.getBase64(getContext(), filePath);
            }
        }
    }

}
