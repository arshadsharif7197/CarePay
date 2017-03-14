package com.carecloud.carepaylibray.demographics.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.DemographicMetadataPropertiesInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jorge on 07/02/17.
 */

public class HealthInsuranceFragment extends CheckInDemographicsBaseFragment {

    private List<DemographicInsurancePayloadDTO> insurancePayloadDTOs;
    private DemographicDTO demographicDTO;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO globalLabelsMetaDTO;
    private InsuranceDocumentScannerListener documentCallback;
    private boolean isPractice;

    private CarePayTextView selectedProvider;
    private CarePayTextView selectedPlan;
    private CarePayTextView selectedType;

    private TextInputLayout cardNumberInput;
    private TextInputLayout groupNumberInput;
    private EditText cardNumber;
    private EditText groupNumber;

    private boolean isCardNumberEmpty;
    private boolean isGroupNumberEmpty;

    private View.OnClickListener addNewElementListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DemographicInsurancePayloadDTO insurance = new DemographicInsurancePayloadDTO();
            insurance.getInsurancePhotos().add(new DemographicInsurancePhotoDTO());
            insurance.getInsurancePhotos().add(new DemographicInsurancePhotoDTO());
            documentCallback.navigateToInsuranceDocumentFragment(CarePayConstants.NO_INDEX, insurance);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        isPractice = getApplicationMode().getApplicationType().equals(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        initDTOs();
        initLabels(view);
//        initTitle(view);
//        initSwitch(view);
        initActiveSection(view);
        checkIfEnableButton(view);
        SystemUtil.hideSoftKeyboard(getActivity());
        stepProgressBar.setCurrentProgressDot(4);
        return view;
    }

    @Override
    protected boolean passConstraints(View view) {
        return true;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_health_insurance_main;
    }

    @Override
    protected DemographicDTO updateDemographicDTO(View view) {
        return demographicDTO;
    }

//    private void initTitle(View view){
//        TextView healthInsuranceTitleTextView = (TextView) view.findViewById(R.id.insurancesTitleLabel);
//        healthInsuranceTitleTextView.setText(globalLabelsMetaDTO.getDemographicsUpdateInsuranceToolbarTitle().toUpperCase());
//        SystemUtil.setProximaNovaSemiboldTypeface(getContext(), healthInsuranceTitleTextView);
//    }

    private void initLabels(View view) {
        // Set Labels
        ((TextView) view.findViewById(R.id.health_insurance_provider_label)).setText(globalLabelsMetaDTO.getDemographicsTitleSelectProvider());
        ((TextView) view.findViewById(R.id.health_insurance_plan_label)).setText(globalLabelsMetaDTO.getDemographicsTitleSelectPlan());
        ((TextView) view.findViewById(R.id.health_insurance_type_label)).setText(globalLabelsMetaDTO.getDemographicsInsuranceTypeLabel());

        selectedProvider = (CarePayTextView) view.findViewById(R.id.health_insurance_providers);
        selectedProvider.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());

        selectedPlan = (CarePayTextView) view.findViewById(R.id.health_insurance_plans);
        selectedPlan.setText(globalLabelsMetaDTO.getDemographicsDocumentsChoosePlanLabel());

        selectedType = (CarePayTextView) view.findViewById(R.id.health_insurance_types);
        selectedType.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());

        ((Button) view.findViewById(R.id.take_front_photo_button)).setText(globalLabelsMetaDTO.getDemographicsInsuranceTakeFrontPhotoLabel());
        ((Button) view.findViewById(R.id.take_back_photo_button)).setText(globalLabelsMetaDTO.getDemographicsInsuranceTakeBackPhotoLabel());
        ((Button) view.findViewById(R.id.health_insurance_dont_have_button)).setText(globalLabelsMetaDTO.getPracticeCheckinDemogrInsDontHaveOneButtonLabel());
//        ((Button) view.findViewById(R.id.health_insurance_add_new_button)).setText(globalLabelsMetaDTO.getPracticeCheckinDemogrInsAddNewButtonLabel());
    }

    private void showAlertDialogWithListView(final String[] dataArray, String title,
                                             String cancelLabel, final int index) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);

        dialog.setNegativeButton(cancelLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int listener) {
                dialogInterface.dismiss();
            }
        });

        View customView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_layout, (ViewGroup) getView(), false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter alertAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(dataArray));
        listView.setAdapter(alertAdapter);
        dialog.setView(customView);

        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long listener) {
                switch (index) {
                    case 0:
                        selectedProvider.setText(dataArray[position]);
                        break;

                    case 1:
                        selectedPlan.setText(dataArray[position]);
                        selectedPlan.setTextSize(getResources().getDimension(R.dimen.text_size_16));
                        selectedPlan.setTextColor(getResources().getColor(R.color.colorPrimary));
                        break;

                    case 2:
                        selectedType.setText(dataArray[position]);
                        break;
                }
                alert.dismiss();
            }
        });
    }

    private void setTextListeners() {
        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isCardNumberEmpty = StringUtil.isNullOrEmpty(cardNumber.getText().toString());
                if (!isCardNumberEmpty) { // clear the error
                    cardNumberInput.setError(null);
                    cardNumberInput.setErrorEnabled(false);
                }
            }
        });

        groupNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isGroupNumberEmpty = StringUtil.isNullOrEmpty(groupNumber.getText().toString());
                if (!isGroupNumberEmpty) { // clear the error
                    groupNumberInput.setError(null);
                    groupNumberInput.setErrorEnabled(false);
                }
            }
        });
    }

    private void setChangeFocusListeners() {
        cardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        groupNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
    }

    private void setActionListeners() {
        cardNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    groupNumber.requestFocus();
                    return true;
                }
                return false;
            }
        });

        groupNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    groupNumber.clearFocus();
                    SystemUtil.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * enable or disable sections
     * @param view main view
     */
    public void initActiveSection(View view) {
        setHeaderTitle(globalLabelsMetaDTO.getDemographicsInsuranceTitle(), view);
        initNextButton(globalLabelsMetaDTO.getDemographicsReviewNextButton(), null, view);

        cardNumberInput = (TextInputLayout) view.findViewById(R.id.health_insurance_card_number_layout);
        cardNumber = (EditText) view.findViewById(R.id.health_insurance_card_number);

        String cardNumberHint = globalLabelsMetaDTO.getDemographicsInsuranceCardNumber();
        cardNumberInput.setTag(cardNumberHint);
        cardNumber.setHint(cardNumberHint);
        cardNumber.setTag(cardNumberInput);

        groupNumberInput = (TextInputLayout) view.findViewById(R.id.health_insurance_group_number_layout);
        groupNumber = (EditText) view.findViewById(R.id.health_insurance_group_number);

        String groupNumberHint = globalLabelsMetaDTO.getDemographicsInsuranceGroupNumber();
        groupNumberInput.setTag(groupNumberHint);
        groupNumber.setHint(groupNumberHint);
        groupNumber.setTag(groupNumberInput);

        // Set Values
        if (insurancesMetaDTO != null) {
            DemographicMetadataPropertiesInsuranceDTO properties = insurancesMetaDTO.properties.items.insurance.properties;

            // Providers
            List<MetadataOptionDTO> providerList = properties.insuranceProvider.options;
            final String[] providers = new String[providerList.size()];
            for (int i = 0; i < providerList.size(); i++) {
                providers[i] = providerList.get(i).getLabel();
            }
            selectedProvider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View providerView) {
                    showAlertDialogWithListView(providers, "Choose Provider", "Cancel", 0);
                }
            });

            // Plans
            List<MetadataOptionDTO> planList = properties.insurancePlan.options;
            final String[] plans = new String[planList.size()];
            for (int i = 0; i < planList.size(); i++) {
                plans[i] = planList.get(i).getLabel();
            }
            selectedPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View providerView) {
                    showAlertDialogWithListView(plans, "Choose Plan", "Cancel", 1);
                }
            });

            // Types
            List<MetadataOptionDTO> typeList = properties.insuranceType.options;
            final String[] types = new String[typeList.size()];
            for (int i = 0; i < typeList.size(); i++) {
                types[i] = typeList.get(i).getLabel();
            }
            selectedType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View providerView) {
                    showAlertDialogWithListView(types, "Choose Type", "Cancel", 2);
                }
            });

//            Spinner providers = (Spinner) view.findViewById(R.id.health_insurance_providers);
//            ArrayAdapter<MetadataOptionDTO> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, providerList);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            providers.setAdapter(adapter);

//            Spinner plans = (Spinner) view.findViewById(R.id.health_insurance_plans);
//            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, planList);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            plans.setAdapter(adapter);

//            Spinner types = (Spinner) view.findViewById(R.id.health_insurance_types);
//            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, typeList);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            types.setAdapter(adapter);
        }

        setTextListeners();
        setChangeFocusListeners();
        setActionListeners();

        cardNumber.clearFocus();
        groupNumber.clearFocus();

        boolean loadResources = insurancePayloadDTOs.size() > 0;
//        boolean isSetup = !isPractice && !loadResources;
//        boolean isRatio = isPractice && !loadResources;
//        view.findViewById(R.id.setupContainer).setVisibility( isSetup ? View.VISIBLE : View.GONE );
//        view.findViewById(R.id.setupInsurancePracticeContainer).setVisibility( isRatio ? View.VISIBLE : View.GONE );
//        view.findViewById(R.id.existingContainer).setVisibility(loadResources? View.VISIBLE : View.GONE);
        if (loadResources) {
//            fillDetailAdapter(view);
//            initAddButton(view);
//        } else if (!isPractice) {
//            ((TextView)view.findViewById(R.id.setupInsuranceLabel)).setText(globalLabelsMetaDTO.getDemographicsSetupInsuranceTitle());
//            TextView setup = (TextView)view.findViewById(R.id.setupLabel);
//            setup.setText(globalLabelsMetaDTO.getDemographicsSetupInsuranceLabel());
//            setup.setOnClickListener(addNewElementListener);
        } else {
            initAddOtherButton(view);
//            RadioButton dontHaveInsurance = (RadioButton)view.findViewById(R.id.dontHaveInsurance);
//            dontHaveInsurance.setText(globalLabelsMetaDTO.getDemographicsDontHaveHealthInsuranceLabel());
//            RadioButton haveInsurance = (RadioButton)view.findViewById(R.id.haveInsurance);
//            haveInsurance.setText(globalLabelsMetaDTO.getDemographicsHaveHealthInsuranceLabel());
//            final Button addButton = (Button)view.findViewById(R.id.addNewButton);
//            haveInsurance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
//                    addButton.setEnabled(on);
//                    documentCallback.disableMainButton(on);
//                }
//            });
        }
    }

//    private void initAddButton(View view){
//        Button addButton = (Button)view.findViewById(R.id.addInsuranceButton);
//        addButton.setText(globalLabelsMetaDTO.getPracticeCheckinDemogrInsAddAnother());
//        addButton.setOnClickListener(addNewElementListener);
//        if(insurancePayloadDTOs.size() >= CarePayConstants.MAX_INSURANCE_DOC){
//            addButton.setEnabled(false);
//        }
//    }

    private void initAddOtherButton(View view) {
        Button addButton = (Button) view.findViewById(R.id.health_insurance_add_new_button);
        addButton.setText(globalLabelsMetaDTO.getPracticeCheckinDemogrInsAddNewButtonLabel());
        addButton.setOnClickListener(addNewElementListener);
    }

//    protected void fillDetailAdapter(View view){
//        RecyclerView detailsListRecyclerView = ((RecyclerView) view.findViewById(R.id.insurance_detail_list));
//        detailsListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
//
//        InsuranceLineItemsListAdapter adapter = new InsuranceLineItemsListAdapter(this.getContext(), demographicDTO, insurancePayloadDTOs, documentCallback);
//        detailsListRecyclerView.setAdapter(adapter);
//    }
//
//    private void initSwitch(View view) {
//        final LinearLayout setupInsuranceHolders = (LinearLayout) view.findViewById(R.id.setupInsuranceHolders);
//        final SwitchCompat doYouHaveInsuranceSwitch = (SwitchCompat) view.findViewById(R.id.demographicsInsuranceSwitch);
//        getChildFragmentManager().executePendingTransactions();
//
//        doYouHaveInsuranceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
//                setupInsuranceHolders.setVisibility(on ? View.VISIBLE : View.GONE);
//                documentCallback.disableMainButton(on);
//            }
//        });
//        String label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsSwitchLabel();
//        doYouHaveInsuranceSwitch.setText(label);
//    }

    private void initDTOs() {
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());

        insurancesMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.insurances;
        globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();

        insurancePayloadDTOs = new ArrayList<>(); // init in case no payload
        DemographicPayloadResponseDTO payloadResponseDTO = demographicDTO.getPayload();
        if (payloadResponseDTO != null) {
            DemographicPayloadInfoDTO demographicPayloadInfoDTO = payloadResponseDTO.getDemographics();
            if (demographicPayloadInfoDTO != null) {
                DemographicPayloadDTO payloadDTO = demographicPayloadInfoDTO.getPayload();
                if (payloadDTO != null) {
                    insurancePayloadDTOs = payloadDTO.getInsurances();
                }
            }
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            documentCallback = (InsuranceDocumentScannerListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement InsuranceDocumentScannerListener");
//        }
//    }

    public interface InsuranceDocumentScannerListener {
        void navigateToInsuranceDocumentFragment(int index, DemographicInsurancePayloadDTO model);

        void navigateToParentFragment();

        void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model);

        void disableMainButton(boolean isDisabled);
    }
}
