package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.adapters.InsuranceLineItemsListAdapter;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.BACK_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.FRONT_PIC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 5/24/17
 */

public class SettingsDocumentsFragment extends BaseFragment implements InsuranceLineItemsListAdapter.OnInsuranceEditClickListener, MediaViewInterface {
    private DemographicsSettingsDTO demographicsSettingsDTO;
    private DemographicDTO demographicDTO;

    private MediaScannerPresenter mediaScannerPresenter;
    private DocumentScannerAdapter documentScannerAdapter;

    private boolean hasFrontImage = false;
    private boolean hasBackImage = false;
    private String base64FrontImage;
    private String base64BackImage;

    private InsuranceLineItemsListAdapter adapter;

    private DemographicsSettingsFragmentListener callback;


    public static SettingsDocumentsFragment newInstance(){
        return new SettingsDocumentsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DemographicsSettingsFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicsSettingsDTO) callback.getDto();
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, DtoHelper.getStringDTO(demographicsSettingsDTO));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_settings_documents, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("demographics_label"));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        callback.setToolbar(toolbar);

        View nextButton = findViewById(R.id.buttonAddDemographicInfo);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDemographics();
            }
        });

        initDocumentViews(view);

        initHealthInsuranceList(view);
    }


    private void initDocumentViews(View view){
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this);
        documentScannerAdapter = new DocumentScannerAdapter(getContext(), view, mediaScannerPresenter, getApplicationMode().getApplicationType());
        documentScannerAdapter.setIdDocumentsFromData(demographicDTO.getPayload().getDemographics().getPayload().getIdDocument());
    }


    private void initHealthInsuranceList(View view){
        RecyclerView recyclerView = ((RecyclerView) view.findViewById(R.id.available_health_insurance_list));
        List<DemographicInsurancePayloadDTO> insuranceList = getInsurances(demographicDTO);
        if (adapter == null) {
            adapter = new InsuranceLineItemsListAdapter(getContext(), insuranceList, this, getApplicationMode().getApplicationType());
        }else{
            adapter.setInsurancesList(insuranceList);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        Button addAnotherButton = (Button) view.findViewById(R.id.health_insurance_add_another);
        addAnotherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View addAnotherButton) {
                editInsurance(-1);
            }
        });

        View noInsurance = view.findViewById(R.id.no_insurance_view);

        if(insuranceList.size()==0){
            noInsurance.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            addAnotherButton.setText(Label.getLabel("demographics_add_insurance_link"));
        }else{
            noInsurance.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            addAnotherButton.setText(Label.getLabel("demographics_add_another_insurance_link"));
        }

    }

    private List<DemographicInsurancePayloadDTO> getInsurances(DemographicDTO demographicDTO) {
        List<DemographicInsurancePayloadDTO> insuranceList = new ArrayList<>();
        for (DemographicInsurancePayloadDTO insurance : demographicDTO.getPayload().getDemographics().getPayload().getInsurances()) {
            if (!insurance.isDeleted()) {
                insuranceList.add(insurance);
            }
        }
        return insuranceList;
    }

    protected DemographicDTO getUpdateModel() {
        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setIdDocument(getPostModel());
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setInsurances(demographicDTO.getPayload().getDemographics().getPayload().getInsurances());
        return updatableDemographicDTO;
    }

    private void updateDemographics(){
        DemographicDTO updateModel = getUpdateModel();
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(updateModel.getPayload().getDemographics().getPayload());

        TransitionDTO updateDemographics = demographicsSettingsDTO.getMetadata().getTransitions().getUpdateDemographics();
        getWorkflowServiceHelper().execute(updateDemographics, updateDemographicsCallback, jsonPayload);
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


    private WorkflowServiceCallback updateDemographicsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

            DemographicsSettingsDTO updatedModel = DtoHelper.getConvertedDTO(DemographicsSettingsDTO.class, workflowDTO);
            demographicsSettingsDTO.getPayload().getDemographics().getPayload().setIdDocument(updatedModel.getPayload().getDemographics().getPayload().getIdDocument());
            demographicsSettingsDTO.getPayload().getDemographics().getPayload().setInsurances(updatedModel.getPayload().getDemographics().getPayload().getInsurances());

            getActivity().onBackPressed();
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onEditInsuranceClicked(DemographicInsurancePayloadDTO demographicInsurancePayloadDTO) {
        int position = demographicDTO.getPayload().getDemographics().getPayload().getInsurances().indexOf(demographicInsurancePayloadDTO);
        editInsurance(position);
    }

    private void editInsurance(int editedIndex) {
        if (callback != null) {
            callback.editInsurance(demographicDTO, editedIndex);
        }
    }

    /**
     * Refresh this fragments list of insurances
     * @param demographicDTO updated demographic dto payload
     */
    public void updateInsuranceList(DemographicDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
        if(getView()!=null) {
            initHealthInsuranceList(getView());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!handleActivityResult(requestCode, resultCode, data)){
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mediaScannerPresenter!=null && mediaScannerPresenter.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(mediaScannerPresenter!=null){
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
        for(DemographicIdDocPhotoDTO docPhotoDTO : docPhotos){
            if(docPhotoDTO.getPage() == FRONT_PIC && hasFrontImage){
                filePath = docPhotoDTO.getIdDocPhoto();
                base64FrontImage = getBase64(filePath);
            }

            if(docPhotoDTO.getPage() == BACK_PIC && hasBackImage){
                filePath = docPhotoDTO.getIdDocPhoto();
                base64BackImage = getBase64(filePath);
            }
        }
    }

    private String getBase64(String filePath){
        File file = new File(filePath);
        Bitmap bitmap = null;
        if(file.exists()) {
            bitmap = BitmapFactory.decodeFile(filePath);
        }else{
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(filePath));
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
