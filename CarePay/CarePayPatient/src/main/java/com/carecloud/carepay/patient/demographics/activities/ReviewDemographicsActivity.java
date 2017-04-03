package com.carecloud.carepay.patient.demographics.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsPresenterImpl;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.fragments.AddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckinDemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsCheckInDocumentsFragment;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.IdentificationFragment;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;
import com.carecloud.carepaylibray.demographics.misc.CheckinDemographicsInterface;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;
import com.carecloud.carepaylibray.demographics.scanner.IdDocScannerFragment;
import com.carecloud.carepaylibray.demographics.scanner.ProfilePictureFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lsoco_user on 11/28/2016.
 */

public class ReviewDemographicsActivity extends BasePatientActivity
        implements DemographicsLabelsHolder,
        CheckinDemographicsFragment.CheckinDemographicsFragmentListener,
        DemographicsCheckInDocumentsFragment.DemographicsCheckInDocumentsFragmentListener,
        HealthInsuranceFragment.InsuranceDocumentScannerListener,
        CheckinDemographicsInterface,
        CheckInDemographicsBaseFragment.CheckInNavListener,
        PersonalInfoFragment.UpdateProfilePictureListener,
        CarePayCameraReady,
        DemographicsView {

    //demographics nav
    private Map<Integer, CheckInDemographicsBaseFragment> demographicFragMap = new HashMap<>();
    private View[] checkinFlowViews;
    private int currentDemographicStep = 1;
    private View checkinDemographics;
    private View checkinConsent;
    private View checkinMedications;
    private View checkinIntake;
    private View checkinPayment;
    //
    private DemographicDTO demographicDTO;
    private CarePayCameraCallback carePayCameraCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_demographic_review);

        // demographics DTO
        demographicDTO = getConvertedDTO(DemographicDTO.class);
        demographicFragMap.put(1, new PersonalInfoFragment());
        demographicFragMap.put(2, new AddressFragment());
        demographicFragMap.put(3, new DemographicsFragment());
        demographicFragMap.put(4, new IdentificationFragment());
        demographicFragMap.put(5, new HealthInsuranceFragment());

        navigateToDemographicFragment(1);
        checkinDemographics = findViewById(R.id.checkinDemographicsHeaderLabel);
        checkinFlowViews = new View[]{checkinDemographics, checkinConsent, checkinMedications, checkinIntake, checkinPayment};
    }

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.root_layout, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commit();
    }

    @Override
    public DemographicLabelsDTO getLabelsDTO() {
        if(demographicDTO != null && demographicDTO.getMetadata() != null && demographicDTO.getMetadata().getLabels() != null) {
            return demographicDTO.getMetadata().getLabels();
        }
        return null;
    }

    /**
     * Changes the global DTO
     *
     * @param demographicDTO The new DTO
     */
    @Override
    public void onDemographicDtoChanged(DemographicDTO demographicDTO) {
        this.demographicDTO = demographicDTO;

        IdDocScannerFragment idDocScannerFragment = (IdDocScannerFragment)
                getSupportFragmentManager().findFragmentById(R.id.demographicsDocsLicense);

        if (idDocScannerFragment != null) {
            demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().clear();
            demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments()
                    .add(idDocScannerFragment.getModel());
        }
    }

    @Override
    public void initializeDocumentFragment(){

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO.getMetadata().getDataModels().getDemographic().getIdentityDocuments());
        DtoHelper.bundleDto(args, demographicDTO.getMetadata().getLabels());
        DtoHelper.bundleDto(args, getDemographicIdDocPayloadDTO());

        DemographicsCheckInDocumentsFragment fragment = new DemographicsCheckInDocumentsFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.documentCapturer, fragment, DemographicsCheckInDocumentsFragment.class.getSimpleName());
        transaction.commit();

    }



    @Override
    public void initializeInsurancesFragment(){
        String tag = HealthInsuranceFragment.class.getSimpleName();

        HealthInsuranceFragment fragment = new HealthInsuranceFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.insuranceCapturer, fragment, tag);
        transaction.commit();
    }


    @Override
    public void editInsurance(DemographicDTO demographicDTO, Integer editedIndex, boolean showAsDialog) {


    }

    @Override
    public void navigateToParentFragment() {
        CheckinDemographicsFragment fragment = new CheckinDemographicsFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO);
        fragment.setArguments(args);

        navigateToFragment(fragment, false);
        Log.v(ReviewDemographicsActivity.class.getSimpleName(), "NewReviewDemographicsActivity");
    }

    @Override
    public void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model) {
        List<DemographicInsurancePayloadDTO> insurances = demographicDTO.getPayload().getDemographics().getPayload()
                .getInsurances();
        if (index>=0){
            insurances.set(index, model);
        } else {
            insurances.add(model);
        }
    }




    @Override
    public void initializeIdDocScannerFragment() {

        // add license fragment
        IdDocScannerFragment fragment = new IdDocScannerFragment();

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, getDemographicIdDocPayloadDTO());

        DemographicMetadataEntityIdDocsDTO idDocsMetaDTO =
                demographicDTO.getMetadata().getDataModels().getDemographic().getIdentityDocuments();

        if (null != idDocsMetaDTO) {
            DtoHelper.bundleDto(args, idDocsMetaDTO.properties.items.identityDocument);
        }
        String tag = "license";
        FragmentManager fm = getSupportFragmentManager();
        fragment.setArguments(args);
        fm.beginTransaction().replace(R.id.demographicsDocsLicense, fragment, tag).commit();
    }

    private DemographicIdDocPayloadDTO getDemographicIdDocPayloadDTO() {
        DemographicIdDocPayloadDTO demographicIdDocPayloadDTO = new DemographicIdDocPayloadDTO();

        if (demographicDTO.getPayload().getDemographics() != null) {
            int size = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().size();
            for (int i = 0; i < size; i++) {
                demographicIdDocPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().get(i);
            }
        }

        return demographicIdDocPayloadDTO;
    }


    public void navigateToConsentFlow(WorkflowDTO workflowDTO){
        PatientNavigationHelper.getInstance(this).navigateToWorkflow(workflowDTO);
    }

    /**
     * Entry point for navigating to medication fragment
     *
     * @param globalLabelDTO global dto
     * @param persDetailsDTO personal details dto
     */
    public void initializeProfilePictureFragment(DemographicLabelsDTO globalLabelDTO,
                                                 PatientModel persDetailsDTO) {

        ProfilePictureFragment fragment = new ProfilePictureFragment();

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, persDetailsDTO);
        args.putBoolean(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE, true);
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        String tag = ProfilePictureFragment.class.getSimpleName();
        fm.beginTransaction().replace(R.id.revdemographicsAddressPicCapturer, fragment, tag)
                .commit();

    }

    @Override
    public String getProfilePicture() {
        ProfilePictureFragment fragment = (ProfilePictureFragment)
                getSupportFragmentManager().findFragmentById(R.id.revdemographicsAddressPicCapturer);

        if (fragment != null) {
            PatientModel demographicPersDetailsPayloadDTO = fragment.getDemographicPersDetailsPayloadDTO();
            if (demographicPersDetailsPayloadDTO != null) {
                return demographicPersDetailsPayloadDTO.getProfilePhoto();
            }
        }
        return null;
    }

    @Override
    public void applyChangesAndNavTo(DemographicDTO demographicDTO, Integer step) {
        currentDemographicStep = step;
        this.demographicDTO = demographicDTO;
        navigateToDemographicFragment(step);
    }

    @Override
    public Integer getCurrentStep() {
        return currentDemographicStep;
    }

    @Override
    public void setCurrentStep(Integer step) {
        if (step > 0) {
            currentDemographicStep = step;
        }
    }

    @Override
    public void loadPictureFragment() {
        initializeProfilePictureFragment(demographicDTO.getMetadata().getLabels(),
                demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails());
    }


    /**
     * Navigate to fragment
     *
     * @param step fragment
     */
    public void navigateToDemographicFragment(Integer step) {
        CheckInDemographicsBaseFragment fragment = demographicFragMap.get(step);
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO);
        fragment.setArguments(args);

        navigateToFragment(fragment, currentDemographicStep == 1 ? false : true);
    }

    @Override
    public void setCheckinFlow(CheckinFlowState flowState, int totalPages, int currentPage) {
        View view = null;
        switch (flowState) {
            case DEMOGRAPHICS:
                view = checkinDemographics;
                break;
            case CONSENT:
                view = checkinConsent;
                break;
            case MEDICATIONS_AND_ALLERGIES:
                view = checkinMedications;
                break;
            case INTAKE:
                view = checkinIntake;
                break;
            case PAYMENT:
                view = checkinPayment;
                break;
            default:
                return;
        }

        updateCheckinFlow(view, totalPages, currentPage);
    }

    private void updateCheckinFlow(View highlightView, int totalPages, int currentPage) {
        if (highlightView == null) {
            return;
        }

        for (View flowView : checkinFlowViews) {
            TextView progress = (TextView) flowView.findViewById(R.id.checkinDemographicsHeaderLabel);

            if (flowView == highlightView && totalPages > 1) {

                    progress.setText(String.format("%d %s %d", currentPage, "of", totalPages));//todo label for "of"

            }
        }
    }

    @Override
    public void captureImage(CarePayCameraCallback callback) {
        this.carePayCameraCallback = callback;

        String tag = CarePayCameraFragment.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }

        CarePayCameraFragment dialog = new CarePayCameraFragment();
        dialog.show(ft, tag);
    }
}
