package com.carecloud.carepaylibray.demographics.fragments.review;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoMetaDataDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographics.fragments.scanner.IdDocScannerFragment;
import com.carecloud.carepaylibray.demographics.fragments.scanner.InsuranceScannerFragment;
import com.carecloud.carepaylibray.demographics.services.DemographicService;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

public class HealthInsuranceReviewFragment extends InsuranceScannerFragment implements View.OnClickListener {


    private FragmentManager fm;
    private View view;
    private ScrollView detailsScrollView;
    private SwitchCompat doYouHaveInsuranceSwitch;
    private List<DemographicInsurancePayloadDTO> insuranceModelList;
    private DemographicPayloadInfoMetaDataDTO payload;

    private InsuranceScannerFragment insuranceFragment;
    private Button addInsuranceaInfoButton;
    private ProgressBar demographicProgressBar;
    private ImageCaptureHelper reviewInsuranceScanHelper;
    private ImageCaptureHelper reviewImageCaptureHelper;

    private DemographicPersDetailsPayloadDTO demPersDetailsPayloadDto;
    private DemographicAddressPayloadDTO demAddressPayloadDto;
    private DemographicIdDocPayloadDTO demographicPayloadDriversLicenseModel;
    private DemographicInsurancePayloadDTO insuranceModel1;
    private DemographicInsurancePayloadDTO insuranceModel2;
    private DemographicInsurancePayloadDTO insuranceModel3;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO globalLabelsMetaDTO;

    private TextView healthInsuranceTitleTextView;
    private TextView haveMultipleHealthInsuranceTextView;

    private boolean isSecondCardAdded;
    private boolean isThirdCardAdded;
    private FrameLayout insCardContainer1;
    private FrameLayout insCardContainer2;
    private FrameLayout insCardContainer3;


    public static HealthInsuranceReviewFragment newInstance() {
        return new HealthInsuranceReviewFragment();
    }

    public HealthInsuranceReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_health_insurance, container, false);
        globalLabelsMetaDTO = ((DemographicReviewActivity) getActivity()).getLabelsDTO();
        insurancesMetaDTO = ((DemographicReviewActivity) getActivity()).getInsurancesMetaDTO();
        fm = getChildFragmentManager();
        DemographicReviewActivity.isFromReview = false;
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.healthinsurance_review_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.healthinsurance_review_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        demographicProgressBar = (ProgressBar) view.findViewById(R.id.demographichealthinsuranceReviewProgressBar);
        demographicProgressBar.setVisibility(View.GONE);
        detailsScrollView = (ScrollView) view.findViewById(R.id.updateInsuranceDocsScroll);

        initViewFromModels();
        setButtons();
        setCardContainers();
        setSwitch();
        setTypefaces(view);
        return view;
    }

    private void initViewFromModels() {
        demAddressPayloadDto = ((DemographicReviewActivity) getActivity())
                .getDemographicAddressPayloadDTO();

        demPersDetailsPayloadDto = ((DemographicReviewActivity) getActivity())
                .getDemographicPersDetailsPayloadDTO();
        demographicPayloadDriversLicenseModel = ((DemographicReviewActivity) getActivity())
                .getDemographicPayloadIdDocDTO();
        insuranceModelList = ((DemographicReviewActivity) getActivity()).getInsurances();

    }


    private void setButtons() {
        // next button
        healthInsuranceTitleTextView = (TextView) view.findViewById(R.id.demographicsDocsHeaderTitle);
        healthInsuranceTitleTextView.setText(globalLabelsMetaDTO.getDemographicsUpdateInsuranceScreenTitle());
        addInsuranceaInfoButton = (Button) view.findViewById(R.id.demographicsDocsNextButton);
        addInsuranceaInfoButton.setText(globalLabelsMetaDTO.getDemographicsInsuranceUpdateButton().toUpperCase());
        addInsuranceaInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postUpdates();
            }
        });
        haveMultipleHealthInsuranceTextView = (TextView) view.findViewById(R.id.multipleInsurancesClickable);
        haveMultipleHealthInsuranceTextView.setText(globalLabelsMetaDTO.getDemographicsDocumentsMultiInsLabel());
        haveMultipleHealthInsuranceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View listener) {
                if (!isSecondCardAdded) {
                    isSecondCardAdded = true;
                    showInsuranceCard(insCardContainer2, true);
                } else if (!isThirdCardAdded) {
                    showInsuranceCard(insCardContainer3, true);
                    showAddCardButton(false);
                }
                scrollToBottom();
            }
        });

    }

    private void postUpdates() {
        demographicProgressBar.setVisibility(View.VISIBLE);
        DemographicPayloadDTO postPayloadModel = new DemographicPayloadDTO();
        postPayloadModel.setAddress(demAddressPayloadDto);
        postPayloadModel.setPersonalDetails(demPersDetailsPayloadDto);
        // add the id docs
        List<DemographicIdDocPayloadDTO> idDocDTOs = new ArrayList<>();
        idDocDTOs.add(demographicPayloadDriversLicenseModel);
        postPayloadModel.setIdDocuments(idDocDTOs);
        // clear the list
        //   insuranceModelList.clear();
        // insuranceFragment.getBitmapsFromImageViews();
        // add non trivial insurance models
        if (isInsuaranceNonTrivial(insuranceModel1)) {
            insuranceModelList.add(insuranceModel1);
        }
        if (isInsuaranceNonTrivial(insuranceModel2)) {
            insuranceModelList.add(insuranceModel2);
        }

        if (isInsuaranceNonTrivial(insuranceModel3)) {
            insuranceModelList.add(insuranceModel3);
        }


        postPayloadModel.setInsurances(insuranceModelList);

        DemographicService backendService = (new BaseServiceGenerator(getActivity()))
                .createService(DemographicService.class);
        Call<ResponseBody> postInsurances = backendService.updateDemographicInformation(postPayloadModel);
        postInsurances.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v(LOG_TAG, "health insurance frag POST success\n" + response.code() + "\n"
                        + response.body());
                openNewFragment();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(LOG_TAG, "health insurance frag POST failed");
            }
        });

    }


    private boolean isInsuaranceNonTrivial(DemographicInsurancePayloadDTO insModel) {
        return insModel.getInsurancePlan() != null &&
                insModel.getInsuranceProvider() != null &&
                insModel.getInsuranceMemberId() != null;
    }


    private void openNewFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = ReviewFragment.newInstance();
        transaction.replace(R.id.root_layout, fragment, HealthInsuranceReviewFragment.class.getName());
        transaction.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageCaptureHelper.SELECT_FILE) {
                reviewImageCaptureHelper.onSelectFromGalleryResult(data, ImageCaptureHelper.RECTANGULAR_IMAGE);
            } else if (requestCode == ImageCaptureHelper.REQUEST_CAMERA) {
                reviewImageCaptureHelper.onCaptureImageResult(data, ImageCaptureHelper.RECTANGULAR_IMAGE);
            }
        }
    }

    @Override
    public int getImageShape() {
        return 0;
    }

    @Override
    protected void updateModelAndViewsAfterScan(ImageCaptureHelper scanner) {

    }

    @Override
    public void populateViewsFromModel() {

    }

    private void getTheModels() {
        List<DemographicInsurancePayloadDTO> payload = ((DemographicReviewActivity) getActivity()).getInsurances();
        if (payload != null) {
            insuranceModelList = payload;
        }
        insuranceModel1 = getInsuranceModelAtIndex(0);
        if (insuranceModel1 != null) {
            List<DemographicInsurancePhotoDTO> photoDtos = insuranceModel1.getInsurancePhotos();
            if (photoDtos != null) {
                if (photoDtos.size() > 0) {
                    DemographicInsurancePhotoDTO frontPhoto = photoDtos.get(0);
                    Log.v(LOG_TAG, "front: " + frontPhoto.getInsurancePhoto());
                }
                if (photoDtos.size() > 1) {
                    DemographicInsurancePhotoDTO backPhoto = photoDtos.get(1);
                    Log.v(LOG_TAG, "back: " + backPhoto.getInsurancePhoto());
                }
            }
        }
    }

    private void setCardContainers() {

        // fetch nested fragments containers
        insCardContainer1 = (FrameLayout) view.findViewById(R.id.insuranceDocument1);
        insCardContainer2 = (FrameLayout) view.findViewById(R.id.insuranceDocument2);
        insCardContainer3 = (FrameLayout) view.findViewById(R.id.insuranceDocument3);

        isSecondCardAdded = false;
        isThirdCardAdded = false;

        fm = getChildFragmentManager();

        // add insurance fragments
        insuranceModel1 = getInsuranceModelAtIndex(0);
        if (insuranceModel1 == null) {
            insuranceModel1 = new DemographicInsurancePayloadDTO();
        }
        InsuranceScannerFragment insuranceFragment = (InsuranceScannerFragment) fm.findFragmentByTag("insurance1");
        if (insuranceFragment == null) {
            insuranceFragment = new InsuranceScannerFragment();
            insuranceFragment.setButtonsStatusCallback(null);
            insuranceFragment.setInsuranceDTO(insuranceModel1); // set the model (if avail)
            insuranceFragment.setInsuranceMetadataDTO(insurancesMetaDTO == null ? null : insurancesMetaDTO.properties.items.insurance);
        }
        fm.beginTransaction()
                .replace(R.id.insuranceDocument1, insuranceFragment, "insurance1")
                .commit();

        insuranceModel2 = getInsuranceModelAtIndex(1);
        if (insuranceModel2 == null) {
            insuranceModel2 = new DemographicInsurancePayloadDTO();
        } else {
            isSecondCardAdded = true;
        }
        InsuranceScannerFragment extraInsuranceFrag1 = (InsuranceScannerFragment) fm.findFragmentByTag("insurance2");
        if (extraInsuranceFrag1 == null) {
            extraInsuranceFrag1 = new InsuranceScannerFragment();
            extraInsuranceFrag1.setButtonsStatusCallback(null);
            extraInsuranceFrag1.setInsuranceDTO(insuranceModel2); // set the model (if avail)
            extraInsuranceFrag1.setInsuranceMetadataDTO(insurancesMetaDTO == null ? null : insurancesMetaDTO.properties.items.insurance);
        }
        fm.beginTransaction()
                .replace(R.id.insuranceDocument2, extraInsuranceFrag1, "insurance2")
                .commit();

        insuranceModel3 = getInsuranceModelAtIndex(2);
        if (insuranceModel3 == null) {
            insuranceModel3 = new DemographicInsurancePayloadDTO();
        } else {
            isThirdCardAdded = true;
        }
        InsuranceScannerFragment extraInsuranceFrag2 = (InsuranceScannerFragment) fm.findFragmentByTag("insurance3");
        if (extraInsuranceFrag2 == null) {
            extraInsuranceFrag2 = new InsuranceScannerFragment();
            extraInsuranceFrag2.setButtonsStatusCallback(null);
            extraInsuranceFrag2.setInsuranceDTO(insuranceModel3); // set the model (if avail)
            extraInsuranceFrag2.setInsuranceMetadataDTO(insurancesMetaDTO == null ? null : insurancesMetaDTO.properties.items.insurance);
        }
        fm.beginTransaction()
                .replace(R.id.insuranceDocument3, extraInsuranceFrag2, "insurance3")
                .commit();
    }

    private DemographicInsurancePayloadDTO getInsuranceModelAtIndex(int i) {
        DemographicInsurancePayloadDTO model = null;
        if (insuranceModelList != null) {
            int numOfInsurances = insuranceModelList.size();
            if (numOfInsurances > i) { // check if the list has an item at index i
                model = insuranceModelList.get(i);
            }
        }
        return model;
    }

    /**
     * Toggles visible/invisible a container of an insurance card details
     *
     * @param cardContainer The container
     * @param isVisible     Whether visible
     */
    private void showInsuranceCard(FrameLayout cardContainer, boolean isVisible) {
        if (isVisible) {
            cardContainer.setVisibility(View.VISIBLE);
        } else {
            cardContainer.setVisibility(View.GONE);
        }
    }

    private void setSwitch() {
        // set the switch
        fm.executePendingTransactions();
        doYouHaveInsuranceSwitch = (SwitchCompat) view.findViewById(R.id.demographicsInsuranceSwitch);
        doYouHaveInsuranceSwitch.setText(globalLabelsMetaDTO.getDemographicsDocumentsSwitchLabel());

        doYouHaveInsuranceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                showInsuranceCard(insCardContainer1, on);
                if (isSecondCardAdded) {
                    showInsuranceCard(insCardContainer2, on);
                } else {
                    showInsuranceCard(insCardContainer2, false);
                }
                if (isThirdCardAdded) {
                    showInsuranceCard(insCardContainer3, on);
                } else {
                    showInsuranceCard(insCardContainer3, false);
                }
                showAddCardButton(on && !isThirdCardAdded);

            }
        });

    }

    /**
     * @param isVisible checking add card button is visible or not
     */
    public void showAddCardButton(boolean isVisible) {
        if (!isVisible) {
            haveMultipleHealthInsuranceTextView.setVisibility(View.GONE);
        } else {
            if (!isThirdCardAdded) { // show only if there is another add possibility
                haveMultipleHealthInsuranceTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void scrollToBottom() {
        View bottomView = view.findViewById(R.id.updateInsuranceDocsScroll);
        detailsScrollView.scrollTo(0, bottomView.getBottom());
    }

    protected void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), healthInsuranceTitleTextView);
        setProximaNovaRegularTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demographicsDocsHeaderSubtitle));
        setProximaNovaRegularTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demographicsInsuranceSwitch));
        setGothamRoundedMediumTypeface(getActivity(), addInsuranceaInfoButton);
    }


    @Override
    public void onClick(View view) {

    }
}
