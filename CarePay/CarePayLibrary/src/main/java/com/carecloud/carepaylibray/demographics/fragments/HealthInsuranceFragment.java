package com.carecloud.carepaylibray.demographics.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.adapters.InsuranceLineItemsListAdapter;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.InsuranceModelProperties;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carecloud.carepaylibray.utils.StringUtil.checkEqualValues;

public class HealthInsuranceFragment extends CheckInDemographicsBaseFragment implements
        InsuranceLineItemsListAdapter.OnInsuranceEditClickListener {

    private TextView insurancePhotoAlert;
    private boolean showAlert = false;
    private boolean noPrimaryInsuranceFound;
    private boolean insuranceTypeRepeated = false;
    private boolean insuranceDataRepeated = false;
    private String insuranceTypeRepeatedErrorMessage;
    private boolean shouldContinue = false;

    private WeakReference<FragmentActivity> callingActivityReference;

    public interface InsuranceDocumentScannerListener {
        void editInsurance(DemographicDTO demographicDTO, Integer editedIndex, boolean showAsDialog);
    }

    private InsuranceLineItemsListAdapter adapter;
    private InsuranceDocumentScannerListener callback;

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (InsuranceDocumentScannerListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement InsuranceDocumentScannerListener");
        }
    }

    @Override
    protected void replaceTranslatedOptionsValues() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (demographicDTO == null) {
//            demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        }
        callingActivityReference = new WeakReference<>(getActivity());
        if (shouldContinue) {
            openNextFragment(demographicDTO);
            shouldContinue = false;
        }
    }

    @Override
    protected Activity getActivityProxy(){
        Activity activity = getActivity();
        if(activity == null && callingActivityReference != null){
            activity = callingActivityReference.get();
        }
        return activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initActiveSection(view);
        checkIfEnableButton(view);
        SystemUtil.hideSoftKeyboard(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = ((RecyclerView) findViewById(R.id.available_health_insurance_list));
        if (recyclerView != null) {
            final InsuranceModelProperties insuranceModelProperties = demographicDTO.getMetadata()
                    .getNewDataModel().getDemographic().getInsurances().getProperties().getItems()
                    .getInsuranceModel().getInsuranceModelProperties();
            List<DemographicInsurancePayloadDTO> insuranceList = getInsurances(demographicDTO);
            adapter = new InsuranceLineItemsListAdapter(getContext(), insuranceList, this,
                    getApplicationMode().getApplicationType(), insuranceModelProperties);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
        initializeViews();
    }

    private List<DemographicInsurancePayloadDTO> getInsurances(DemographicDTO demographicDTO) {
        List<DemographicInsurancePayloadDTO> insuranceList = new ArrayList<>();
        boolean hasOnePhoto = false;
        Map<String, Integer> insurancesTypeMap = new HashMap<>();
        if (demographicDTO != null) {
            boolean isThereAnyPrimaryInsurance = false;
            for (DemographicInsurancePayloadDTO insurance : demographicDTO.getPayload().getDemographics()
                    .getPayload().getInsurances()) {
                if (!insurance.isDeleted()) {
                    insuranceList.add(insurance);
                    if (insurance.getInsuranceType().toLowerCase().equals("primary")) {
                        isThereAnyPrimaryInsurance = true;
                    }
                    if (insurance.getInsurancePhotos().size() == 0) {
                        showAlert = true;
                    } else if (!hasOnePhoto) {
                        hasOnePhoto = true;
                    }
                    String insuranceType = StringUtil.captialize(insurance.getInsuranceType()).trim();
                    if (insurancesTypeMap.containsKey(insuranceType)) {
                        insurancesTypeMap.put(insuranceType, insurancesTypeMap.get(insuranceType) + 1);
                    } else {
                        insurancesTypeMap.put(insuranceType, 1);
                    }
                }
            }
            if (!demographicDTO.getPayload().getDemographics().getPayload().getInsurances().isEmpty()
                    && !isThereAnyPrimaryInsurance) {
                noPrimaryInsuranceFound = true;
                showAlert = true;
            }
            checkIfHasDuplicateInsuranceType(insurancesTypeMap);
            checkIfInsuranceDataMatches();
            checkIfEnableButton(getView());
        }

        MixPanelUtil.addCustomPeopleProperty(getString(R.string.people_has_identity_doc), hasOnePhoto);

        return insuranceList;
    }

    private boolean checkIfHasDuplicateInsuranceType(Map<String, Integer> insurancesTypeMap) {
        insuranceTypeRepeated = true;
        insuranceTypeRepeatedErrorMessage = Label
                .getLabel("insurance.insuranceList.alert.message.duplicatedInsuranceAlert");
        String insuranceType = "";
        if (insurancesTypeMap.containsKey("Primary") && insurancesTypeMap.get("Primary") > 1) {
            showAlert = true;
            insuranceType = "Primary";
            insuranceTypeRepeatedErrorMessage = String.format(insuranceTypeRepeatedErrorMessage,
                    insurancesTypeMap.get(insuranceType), insuranceType, insuranceType);
        } else if (insurancesTypeMap.containsKey("Secondary") && insurancesTypeMap.get("Secondary") > 1) {
            showAlert = true;
            insuranceType = "Secondary";
            insuranceTypeRepeatedErrorMessage = String.format(insuranceTypeRepeatedErrorMessage,
                    insurancesTypeMap.get(insuranceType), insuranceType, insuranceType);
        } else if (insurancesTypeMap.containsKey("Tertiary") && insurancesTypeMap.get("Tertiary") > 1) {
            showAlert = true;
            insuranceType = "Tertiary";
            insuranceTypeRepeatedErrorMessage = String.format(insuranceTypeRepeatedErrorMessage,
                    insurancesTypeMap.get(insuranceType), insuranceType, insuranceType);
        } else if (insurancesTypeMap.containsKey("Quaternary") && insurancesTypeMap.get("Quaternary") > 1) {
            showAlert = true;
            insuranceType = "Quaternary";
            insuranceTypeRepeatedErrorMessage = String.format(insuranceTypeRepeatedErrorMessage,
                    insurancesTypeMap.get(insuranceType), insuranceType, insuranceType);
        } else {
            insuranceTypeRepeated = false;
        }
        return insuranceTypeRepeated;
    }

    private boolean checkIfInsuranceDataMatches() {
        List<DemographicInsurancePayloadDTO> insuranceList = demographicDTO.getPayload().getDemographics().getPayload().getInsurances();
        if (insuranceList.size() > 1) {
            for (DemographicInsurancePayloadDTO insurance : insuranceList) {
                if (!insurance.isDeleted()) {
                    for (DemographicInsurancePayloadDTO insuranceVerify : insuranceList) {
                        if (!insuranceVerify.isDeleted() && !insurance.equals(insuranceVerify)){
                            boolean match = checkEqualValues(insurance.getInsuranceProvider(), insuranceVerify.getInsuranceProvider()) &&
                                    checkEqualValues(insurance.getInsurancePlan(), insuranceVerify.getInsurancePlan()) &&
                                    checkEqualValues(insurance.getInsuranceMemberId(), insuranceVerify.getInsuranceMemberId());
                            if (match) {
                                insuranceDataRepeated = showAlert = true;
                                return true;
                            }
                        }
                    }

                }
            }
        }
        insuranceDataRepeated = false;
        return false;
    }



    private void initializeViews() {
        if (demographicDTO != null) {
            if (hasInsurance()) {
                insurancePhotoAlert.setVisibility(View.GONE);
                adapter.setInsurancesList(getInsurances(demographicDTO));
                if (showAlert) {
                    showAlert();
                    showAlert = false;
                    noPrimaryInsuranceFound = false;
                }
            } else {
                //remove the health insurance fragment from the stack
                getFragmentManager().popBackStack(HealthInsuranceFragment.class.getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                editInsurance(null, false);
            }
        }
    }

    private void showAlert() {
        String alertMessage = Label.getLabel("demographics_insurance_no_photo_alert");
        int notificationType = CustomMessageToast.NOTIFICATION_TYPE_WARNING;

        if (insuranceTypeRepeated) {
            alertMessage = insuranceTypeRepeatedErrorMessage;
            notificationType = CustomMessageToast.NOTIFICATION_TYPE_ERROR;
        } else if (insuranceDataRepeated) {
            alertMessage = Label.getLabel("demographics_insurance_duplicate_insurance");
            notificationType = CustomMessageToast.NOTIFICATION_TYPE_ERROR;
        } else if (noPrimaryInsuranceFound) {
            alertMessage = Label.getLabel("demographics_insurance_no_primary_alert");
        }
        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT) {
            new CustomMessageToast(getActivity(), alertMessage, notificationType).show();
        } else {
            showPracticeAlert(alertMessage, notificationType);
        }
    }

    private void showPracticeAlert(String alertMessage, int notificationType) {
        if (notificationType == CustomMessageToast.NOTIFICATION_TYPE_WARNING) {
            insurancePhotoAlert.setTextColor(getContext().getResources().getColor(R.color.lightning_yellow));
            insurancePhotoAlert.setBackground(getContext().getResources()
                    .getDrawable(R.drawable.bg_round_border_lighting_yellow));
        } else {
            insurancePhotoAlert.setTextColor(getContext().getResources().getColor(R.color.redAlert));
            insurancePhotoAlert.setBackground(getContext().getResources()
                    .getDrawable(R.drawable.bg_round_border_red));
        }
        insurancePhotoAlert.setText(alertMessage);
        insurancePhotoAlert.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
        int total = checkinFlowCallback.getTotalSteps();
        int step = CheckinFlowCallback.INSURANCE > total ? total : CheckinFlowCallback.INSURANCE;
        stepProgressBar.setCurrentProgressDot(step - 1);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, total, step);
        checkinFlowCallback.setCurrentStep(step);
    }

    @Override
    protected boolean passConstraints(View view) {
        if (insuranceTypeRepeated || insuranceDataRepeated) {
            return false;
        }
        return true;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_health_insurance_main;
    }

    @Override
    protected DemographicDTO updateDemographicDTO(View view) {
        inflateNewImages();
        return demographicDTO;
    }

    /**
     * enable or disable sections
     *
     * @param view main view
     */
    public void initActiveSection(final View view) {
        insurancePhotoAlert = (TextView) view.findViewById(R.id.insurancePhotoAlert);
        Button addAnotherButton = (Button) view.findViewById(R.id.health_insurance_add_another);
        addAnotherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View addAnotherButton) {
                editInsurance(null, true);
            }
        });
        if (demographicDTO == null) {
            addAnotherButton.setVisibility(View.GONE);
        }

        setHeaderTitle(Label.getLabel("demographics_insurance_label"),
                Label.getLabel("demographics_health_insurance_heading"),
                Label.getLabel("demographics_health_insurance_subheading"),
                view);
        initNextButton(view);
    }

    @Override
    public void onEditInsuranceClicked(DemographicInsurancePayloadDTO demographicInsurancePayloadDTO) {
        int position = demographicDTO.getPayload().getDemographics().getPayload().getInsurances()
                .indexOf(demographicInsurancePayloadDTO);
        editInsurance(position, true);
    }

    private void editInsurance(Integer editedIndex, boolean showAsDialog) {
        if (callback != null) {
            callback.editInsurance(demographicDTO, editedIndex, showAsDialog);
        }
    }

    /**
     * @param demographicDTO Demographic DTO
     */
    public void updateInsuranceList(DemographicDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
        if (insurancePhotoAlert != null && isAdded()) {
            initializeViews();
        }
    }

    public void openNextFragment(DemographicDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
        openNextFragment(demographicDTO, true);
    }

    private boolean hasInsurance() {
        boolean hasInsurance = false;
        for (DemographicInsurancePayloadDTO insurance : demographicDTO.getPayload().getDemographics()
                .getPayload().getInsurances()) {
            if (!insurance.isDeleted()) {
                hasInsurance = true;
                break;
            }

        }
        return hasInsurance;
    }

    private void inflateNewImages() {
        for (DemographicInsurancePayloadDTO insurancePayloadDTO : demographicDTO.getPayload()
                .getDemographics().getPayload().getInsurances()) {
            for (DemographicInsurancePhotoDTO photoDTO : insurancePayloadDTO.getInsurancePhotos()) {
                if (!photoDTO.isDelete() && photoDTO.isNewPhoto()) {
                    photoDTO.setInsurancePhoto(DocumentScannerAdapter
                            .getBase64(getContext(), photoDTO.getInsurancePhoto()));
                }
            }
        }
    }

    public void setShouldContinue(boolean shouldContinue) {
        this.shouldContinue = shouldContinue;
    }
}
