package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomOptionsAdapter;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsInsuranceOption;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.InsuranceModelProperties;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.marcok.stepprogressbar.StepProgressBar;

import java.util.Iterator;
import java.util.List;

import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.BACK_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.FRONT_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_BACK_DTO;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_FRONT_DTO;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_HAS_BACK;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_HAS_FRONT;

public class InsuranceEditDialog extends BaseDialogFragment implements MediaViewInterface {

    public static final String EDITED_INDEX = "EditedIndex";
    public static final String IS_PATIENT_MODE = "IsPatientMode";
    public static final String IS_CHECK_IN = "IsCheckIn";
    public static final int NEW_INSURANCE = -1;

    private DemographicDTO demographicDTO;
    private MediaScannerPresenter mediaScannerPresenter;
    private DocumentScannerAdapter documentScannerAdapter;

    private boolean hasFrontImage = false;
    private boolean hasBackImage = false;

    private TextInputLayout cardNumberInput;
    private TextInputLayout groupNumberInput;
    private TextInputLayout policyFirstNameHolderInput;
    private TextInputLayout policyLastNameHolderInput;
    private TextInputLayout policyBirthDateHolderInput;
    private EditText policyFirstNameHolder;
    private EditText policyLastNameHolder;
    private EditText policyBirthDateHolder;
    private EditText cardNumber;
    private EditText groupNumber;
    private Button saveInsuranceButton;

    private TextView selectedProviderTextView;
    private TextView selectedPlanTextView;
    private TextView selectedTypeTextView;
    private TextView selectedRelationshipTextView;
    private TextView selectedGenderTextView;
    private TextView genderRequiredTextView;
    private EditText otherProviderEditText;

    private View dateOfBirthContainer;
    private View genderContainer;
    private boolean hadInsurance;
    private boolean isPatientMode;
    private boolean isDataHolderSelf;
    private boolean isCheckin;

    private DemographicInsurancePhotoDTO frontInsurancePhotoDTO;
    private DemographicInsurancePhotoDTO backInsurancePhotoDTO;

    private InsuranceEditDialogListener callback;
    private int editedIndex;


    private String defaultType;

    private DemographicsInsuranceOption selectedProviderOption = new DemographicsInsuranceOption();
    private DemographicsOption selectedPlanOption = new DemographicsOption();
    private DemographicsOption selectedTypeOption = new DemographicsOption();
    private DemographicsOption selectedRelationshipOption = new DemographicsOption();
    private DemographicsOption selectedGenderOption = new DemographicsOption();


    public interface InsuranceEditDialogListener {
        void onInsuranceEdited(DemographicDTO demographicDTO, boolean proceed);

        void goOneStepBack();

        void showRemovePrimaryInsuranceDialog(HomeAlertDialogFragment.HomeAlertInterface callback);
    }

    /**
     * Creates a InsuranceEditDialog fragment
     *
     * @param demographicDTO Demographic DTO
     * @param editedIndex    index of the insurance being modified
     */
    public static InsuranceEditDialog newInstance(DemographicDTO demographicDTO,
                                                  Integer editedIndex,
                                                  boolean isPatientMode,
                                                  boolean isCheckin) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        args.putInt(EDITED_INDEX, editedIndex == null ? NEW_INSURANCE : editedIndex);
        args.putBoolean(IS_PATIENT_MODE, isPatientMode);
        args.putBoolean(IS_CHECK_IN, isCheckin);
        DtoHelper.bundleDto(args, demographicDTO);

        InsuranceEditDialog dialog = new InsuranceEditDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    private void attachCallback(Context context) {
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (InsuranceEditDialogListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement InsuranceEditDialogListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        editedIndex = arguments.getInt(EDITED_INDEX);
        isPatientMode = arguments.getBoolean(IS_PATIENT_MODE);
        isCheckin = arguments.getBoolean(IS_CHECK_IN);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, arguments);
        if (savedInstanceState != null) {
            String frontString = savedInstanceState.getString(KEY_FRONT_DTO);
            frontInsurancePhotoDTO = DtoHelper.getConvertedDTO(DemographicInsurancePhotoDTO.class, frontString);
            String backString = savedInstanceState.getString(KEY_BACK_DTO);
            backInsurancePhotoDTO = DtoHelper.getConvertedDTO(DemographicInsurancePhotoDTO.class, backString);

            hasFrontImage = savedInstanceState.getBoolean(KEY_HAS_FRONT, false);
            hasBackImage = savedInstanceState.getBoolean(KEY_HAS_BACK, false);
        }

        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        if (frontInsurancePhotoDTO != null) {
            icicle.putString(KEY_FRONT_DTO, DtoHelper.getStringDTO(frontInsurancePhotoDTO));
        }
        if (backInsurancePhotoDTO != null) {
            icicle.putString(KEY_BACK_DTO, DtoHelper.getStringDTO(backInsurancePhotoDTO));
        }
        icicle.putBoolean(KEY_HAS_FRONT, hasFrontImage);
        icicle.putBoolean(KEY_HAS_BACK, hasBackImage);
        super.onSaveInstanceState(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        hadInsurance = hasInsurance();
        if (getDialog() != null || (hadInsurance && !isPatientMode) || !isCheckin) {
            View view = inflater.inflate(R.layout.dialog_add_edit_insurance, container, false);

            hideKeyboardOnViewTouch(view.findViewById(R.id.dialog_content_layout));
            hideKeyboardOnViewTouch(view.findViewById(R.id.container_main));

            if (!isPatientMode) {
                inflateToolbarViews(view);
            }

            return view;
        }

        View view = inflater.inflate(R.layout.fragment_review_demographic_base, container, false);

        StepProgressBar stepProgressBar = (StepProgressBar) view.findViewById(R.id.stepProgressBarCheckin);
        stepProgressBar.setNumDots(5);
        stepProgressBar.setCurrentProgressDot(4);

        ViewGroup contentLayout = (ViewGroup) view.findViewById(R.id.checkinDemographicsContentLayout);
        inflater.inflate(R.layout.add_edit_insurance_view, contentLayout, true);
        float padding = getResources().getDimension(R.dimen.demographics_add_padding);
        contentLayout.setPadding((int) padding, 0, 0, 0);

        View heading = view.findViewById(R.id.demographicsHeading);
        if (heading != null) {
            heading.setVisibility(View.GONE);
        }

        inflateToolbarViews(view);

        hideKeyboardOnViewTouch(view);
        hideKeyboardOnViewTouch(view.findViewById(R.id.dialog_content_layout));
        hideKeyboardOnViewTouch(view.findViewById(R.id.container_main));

        return view;
    }

    private void inflateToolbarViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                if (callback != null) {
                    callback.goOneStepBack();
                }
            }
        });

        if (!isCheckin) {
            View container = view.findViewById(R.id.insurance_toolbar);
            container.setPadding(0, 0, 0, 0);
            toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
        }

        TextView textView = (TextView) view.findViewById(R.id.toolbar_title);
        textView.setText(Label.getLabel("demographics_insurance_label"));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (hasInsurance()) {
            View closeButton = findViewById(R.id.edit_insurance_close_button);
            if (closeButton != null) {
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View closeButton) {
                        closeDialog();
                    }
                });
            }
        }
        View container = view.findViewById(R.id.container_main);
        hideKeyboardOnViewTouch(container);

        selectedProviderTextView = (TextView) findViewById(R.id.health_insurance_providers);
        selectedPlanTextView = (TextView) findViewById(R.id.health_insurance_choose_plans);
        selectedTypeTextView = (TextView) findViewById(R.id.health_insurance_types);
        selectedRelationshipTextView = (TextView) findViewById(R.id.health_insurance_relationship);
        selectedGenderTextView = (TextView) findViewById(R.id.chooseGenderTextView);
        genderRequiredTextView = (TextView) findViewById(R.id.genderOptional);

        cardNumberInput = (TextInputLayout) findViewById(R.id.health_insurance_card_number_layout);
        cardNumber = (EditText) findViewById(R.id.health_insurance_card_number);
        otherProviderEditText = (EditText) findViewById(R.id.otherProviderEditText);

        groupNumberInput = (TextInputLayout) findViewById(R.id.health_insurance_group_number_layout);
        groupNumber = (EditText) findViewById(R.id.health_insurance_group_number);

        policyFirstNameHolderInput = (TextInputLayout)
                findViewById(R.id.health_insurance_policy_first_name_holder_layout);
        policyFirstNameHolder = (EditText) findViewById(R.id.health_insurance_policy_first_name_holder);
        policyLastNameHolderInput = (TextInputLayout)
                findViewById(R.id.health_insurance_policy_last_name_holder_layout);
        policyLastNameHolder = (EditText) findViewById(R.id.health_insurance_policy_last_name_holder);
        policyBirthDateHolderInput = (TextInputLayout)
                findViewById(R.id.health_insurance_policy_birth_date_holder_layout);
        policyBirthDateHolder = (EditText) findViewById(R.id.health_insurance_policy_birth_date_holder);
        dateOfBirthContainer = findViewById(R.id.dateOfBirthContainer);
        genderContainer = findViewById(R.id.genderContainer);

        setTextListeners();
        setChangeFocusListeners();
        setActionListeners();

        if (getDialog() != null || (hadInsurance && !isPatientMode) || !isCheckin) {
            saveInsuranceButton = (Button) findViewById(R.id.save_insurance_changes);
        } else {
            saveInsuranceButton = (Button) findViewById(R.id.checkinDemographicsNextButton);
        }

        getInsuranceDropdownLists();
        defaultType = demographicDTO.getMetadata().getNewDataModel().getDemographic()
                .getInsurances().getProperties().getItems()
                .getInsuranceModel().getInsuranceModelProperties().getInsuranceType()
                .getOptions().get(0).getName();

        if (editedIndex == NEW_INSURANCE) {
            ((Button) findViewById(R.id.demogrDocsFrontScanButton)).setText(
                    Label.getLabel("demographics_insurance_take_front_photo"));
            ((Button) findViewById(R.id.demogrDocsBackScanButton)).setText(
                    Label.getLabel("demographics_insurance_take_back_photo"));

            selectedTypeTextView.setText(Label.getLabel("demographics_choose"));

            if (hasInsurance() && (getDialog() != null || !isPatientMode) || !isCheckin) {
                disappearViewById(R.id.remove_insurance_entry);
                ((CarePayTextView) findViewById(R.id.toolbar_title)).setText(
                        Label.getLabel("practice_checkin_demogr_ins_add_new_button_label"));
            } else {
                showViewById(R.id.check_in_demographics_left_button);
                findViewById(R.id.check_in_demographics_left_button).setOnClickListener(getNoInsuranceListener());
                saveInsuranceButton.setText(Label.getLabel("practice_checkin_demogr_ins_add_new_button_label"));
            }
            selectedRelationshipOption = demographicDTO.getMetadata().getNewDataModel().getDemographic()
                    .getInsurances().getProperties().getItems()
                    .getInsuranceModel().getInsuranceModelProperties().getRelationship()
                    .getOptions().get(0);
            selectedRelationshipTextView.setText(selectedRelationshipOption.getLabel());
            isDataHolderSelf = true;
        } else {
            DemographicInsurancePayloadDTO demographicInsurancePayload = demographicDTO.getPayload()
                    .getDemographics().getPayload().getInsurances().get(editedIndex);
            initInsuranceData(demographicInsurancePayload);

            findViewById(R.id.remove_insurance_entry).setOnClickListener(removeButtonListener);
            findViewById(R.id.providerRequired).setVisibility(View.GONE);
        }

        initializeScanArea(view);

        saveInsuranceButton.setOnClickListener(saveButtonListener);

        saveInsuranceButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View buttonView, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !buttonView.isSelected()) {
                    if (validateForm(true)) {
                        saveInsurance();
                    }
                    return true;
                }
                return false;
            }
        });
        checkIfEnableButton();
    }


    private void initInsuranceData(DemographicInsurancePayloadDTO demographicInsurancePayload) {
        String selectedProvider = demographicInsurancePayload.getInsuranceProvider();
        selectedProviderTextView.setText(selectedProvider);
        selectedProviderOption.setName(selectedProvider);
        selectedProviderOption.setLabel(selectedProvider);
        setProviderOptionsPlans();

        String selectedPlan = demographicInsurancePayload.getInsurancePlan();
        selectedPlanTextView.setText(selectedPlan);
        selectedPlanTextView.setVisibility(View.VISIBLE);
        findViewById(R.id.health_insurance_plans).setVisibility(View.GONE);
        selectedPlanOption.setName(selectedPlan);
        selectedPlanOption.setLabel(selectedPlan);

        String selectedType = demographicInsurancePayload.getInsuranceType();
        selectedTypeTextView.setText(selectedType);
        selectedTypeOption.setName(selectedType);
        selectedTypeOption.setLabel(selectedType);

        String selectedRelationship = demographicInsurancePayload.getRelationship();
        selectedRelationshipTextView.setText(selectedRelationship);
        selectedRelationshipOption.setName(selectedRelationship);
        selectedRelationshipOption.setLabel(selectedRelationship);
        isDataHolderSelf = selectedRelationshipOption.getLabel().toLowerCase().equals("self");
        manageBoDAndGenderVisibility(isDataHolderSelf);

        String selectedGender = demographicInsurancePayload.getGender();
        if (selectedGender == null) {
            selectedGenderTextView.setText(Label.getLabel("demographics_choose"));
        } else {
            selectedGenderTextView.setText(selectedGender);
            selectedGenderOption.setName(selectedGender);
            selectedGenderOption.setLabel(selectedGender);
            genderRequiredTextView.setVisibility(View.GONE);
        }

        cardNumber.setText(demographicInsurancePayload.getInsuranceMemberId());
        if (!StringUtil.isNullOrEmpty(demographicInsurancePayload.getInsuranceMemberId())
                && cardNumber.getOnFocusChangeListener() != null) {
            cardNumber.getOnFocusChangeListener().onFocusChange(cardNumber, false);
        }
        groupNumber.setText(demographicInsurancePayload.getInsuranceGroupId());
        if (!StringUtil.isNullOrEmpty(demographicInsurancePayload.getInsuranceGroupId())
                && groupNumber.getOnFocusChangeListener() != null) {
            groupNumber.getOnFocusChangeListener().onFocusChange(groupNumber, false);
        }

        policyFirstNameHolder.setText(demographicInsurancePayload.getPolicyFirstNameHolder());
        if (!StringUtil.isNullOrEmpty(demographicInsurancePayload.getPolicyFirstNameHolder())
                && policyFirstNameHolder.getOnFocusChangeListener() != null) {
            policyFirstNameHolder.getOnFocusChangeListener().onFocusChange(policyFirstNameHolder, false);
        }

        policyLastNameHolder.setText(demographicInsurancePayload.getPolicyLastNameHolder());
        if (!StringUtil.isNullOrEmpty(demographicInsurancePayload.getPolicyLastNameHolder())
                && policyLastNameHolder.getOnFocusChangeListener() != null) {
            policyLastNameHolder.getOnFocusChangeListener().onFocusChange(policyLastNameHolder, false);
        }

        policyBirthDateHolder.setText(demographicInsurancePayload.getPolicyDateOfBirthHolder());
        if (!StringUtil.isNullOrEmpty(demographicInsurancePayload.getPolicyDateOfBirthHolder())
                && policyBirthDateHolder.getOnFocusChangeListener() != null) {
            policyBirthDateHolder.getOnFocusChangeListener().onFocusChange(policyBirthDateHolder, false);
        }

        String title = selectedProvider + (selectedPlan != null ? " " + selectedPlan : "");
        ((TextView) findViewById(R.id.toolbar_title)).setText(title);

    }

    private void setProviderOptionsPlans() {
        InsuranceModelProperties insuranceModelProperties = demographicDTO.getMetadata()
                .getNewDataModel().getDemographic().getInsurances().getProperties().getItems()
                .getInsuranceModel().getInsuranceModelProperties();
        for (DemographicsInsuranceOption insuranceOption : insuranceModelProperties
                .getInsuranceProvider().getOptions()) {
            if (insuranceOption.getName().equals(selectedProviderOption.getName())) {
                selectedProviderOption.setPayerPlans(insuranceOption.getPayerPlans());
            }
        }
    }

    private View.OnClickListener removeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View saveChanges) {
            DemographicInsurancePayloadDTO insurance = demographicDTO.getPayload().getDemographics()
                    .getPayload().getInsurances().get(editedIndex);
            if (insurance.getInsuranceType().toLowerCase().equals("primary")) {
                callback.showRemovePrimaryInsuranceDialog(new HomeAlertDialogFragment.HomeAlertInterface() {
                    @Override
                    public void onAcceptExit() {
                        removeInsurance();
                    }
                });
            } else {
                removeInsurance();
            }
        }
    };

    private void removeInsurance() {
        if (editedIndex != NEW_INSURANCE) {
            demographicDTO.getPayload().getDemographics().getPayload().getInsurances()
                    .get(editedIndex).setDeleted(true);
        }
        closeDialog();
    }

    private View.OnClickListener getNoInsuranceListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View saveChanges) {
                if (callback != null) {
                    callback.onInsuranceEdited(null, true);
                }
            }
        };
    }


    View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View saveChanges) {
            if (validateForm(true)) {
                saveInsurance();
            }
        }
    };

    private void saveInsurance() {
        DemographicInsurancePayloadDTO demographicInsurancePayloadDTO;
        if (editedIndex == NEW_INSURANCE) {
            demographicInsurancePayloadDTO = new DemographicInsurancePayloadDTO();
            demographicDTO.getPayload().getDemographics().getPayload().getInsurances()
                    .add(demographicInsurancePayloadDTO);
        } else {
            demographicInsurancePayloadDTO = demographicDTO.getPayload().getDemographics()
                    .getPayload().getInsurances().get(editedIndex);
        }

        demographicInsurancePayloadDTO.setInsuranceProvider(selectedProviderOption.getName());
        demographicInsurancePayloadDTO.setInsurancePlan(selectedPlanOption.getName());
        demographicInsurancePayloadDTO.setInsuranceType(selectedTypeOption.getName() != null
                ? selectedTypeOption.getName() : defaultType);

        demographicInsurancePayloadDTO.setInsuranceMemberId(cardNumber.getText().toString().trim());
        demographicInsurancePayloadDTO.setInsuranceGroupId(groupNumber.getText().toString().trim());

        demographicInsurancePayloadDTO.setRelationship(selectedRelationshipOption.getName());
        demographicInsurancePayloadDTO.setPolicyFirstNameHolder(policyFirstNameHolder.getText()
                .toString().trim());
        demographicInsurancePayloadDTO.setPolicyLastNameHolder(policyLastNameHolder.getText()
                .toString().trim());
        demographicInsurancePayloadDTO.setPolicyDateOfBirthHolder(policyBirthDateHolder.getText()
                .toString().trim());
        demographicInsurancePayloadDTO.setGender(selectedGenderTextView.getText()
                .toString().trim());

        setupImageBase64();

        List<DemographicInsurancePhotoDTO> photos = demographicInsurancePayloadDTO.getInsurancePhotos();
        if (frontInsurancePhotoDTO != null) {
            removeOldPhoto(photos, FRONT_PIC);
            photos.add(frontInsurancePhotoDTO);
        }

        if (backInsurancePhotoDTO != null) {
            removeOldPhoto(photos, BACK_PIC);
            photos.add(backInsurancePhotoDTO);
        }

        closeDialog();
    }

    private void closeDialog() {
        dismiss();
        if (callback != null) {
            callback.onInsuranceEdited(demographicDTO, false);

        }
    }

    private void initializeScanArea(View view) {
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this,
                CarePayCameraPreview.CameraType.SCAN_DOC);
        documentScannerAdapter = new DocumentScannerAdapter(getContext(), view, mediaScannerPresenter,
                getApplicationMode().getApplicationType());


        View lastCaptureView = view.findViewById(MediaScannerPresenter.captureViewId);
        if (lastCaptureView != null) {
            mediaScannerPresenter.setCaptureView(lastCaptureView);
        }

        if (hasFrontImage || hasBackImage) {
            DemographicInsurancePayloadDTO payloadDTO = new DemographicInsurancePayloadDTO();
            if (frontInsurancePhotoDTO != null) {
                payloadDTO.getInsurancePhotos().add(frontInsurancePhotoDTO);
                documentScannerAdapter.setFrontRescan();
            }
            if (backInsurancePhotoDTO != null) {
                payloadDTO.getInsurancePhotos().add(backInsurancePhotoDTO);
                documentScannerAdapter.setBackRescan();
            }
            documentScannerAdapter.setInsuranceDocumentsFromData(payloadDTO);
        } else if (editedIndex != NEW_INSURANCE) {
            documentScannerAdapter.setInsuranceDocumentsFromData(demographicDTO.getPayload()
                    .getDemographics().getPayload().getInsurances().get(editedIndex));
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
        return mediaScannerPresenter != null && mediaScannerPresenter
                .handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
            DemographicInsurancePhotoDTO photoDTO = new DemographicInsurancePhotoDTO();
            photoDTO.setDelete(false);
            photoDTO.setInsurancePhoto(filePath);
            photoDTO.setNewPhoto(true);
            int page;
            if (view.getId() == documentScannerAdapter.getFrontImageId()) {
                page = FRONT_PIC;
                hasFrontImage = true;
                frontInsurancePhotoDTO = photoDTO;
            } else {
                page = BACK_PIC;
                hasBackImage = true;
                backInsurancePhotoDTO = photoDTO;
            }
            photoDTO.setPage(page);
        }
        checkIfEnableButton();
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

    }

    private void getInsuranceDropdownLists() {
        InsuranceModelProperties insuranceModelProperties = demographicDTO.getMetadata()
                .getNewDataModel().getDemographic().getInsurances().getProperties().getItems()
                .getInsuranceModel().getInsuranceModelProperties();
        OnSelectionChangeCallback selectProviderCallback = new OnSelectionChangeCallback() {
            @Override
            public void onSelectionChange(DemographicsOption demographicsOption) {
                findViewById(R.id.health_insurance_plans).setVisibility(View.GONE);
                selectedPlanTextView.setVisibility(View.VISIBLE);

                if (!demographicsOption.getName().equals(selectedProviderOption.getName())) {
                    //reset the plan dropdown
                    selectedPlanOption = new DemographicsOption();
                    selectedPlanTextView.setText(Label.getLabel("demographics_choose"));
                    selectedPlanTextView.setOnClickListener(
                            getSelectOptionsListener(((DemographicsInsuranceOption) demographicsOption)
                                            .getPayerPlans(),
                                    getDefaultOnOptionsSelectedListener(selectedPlanTextView,
                                            selectedPlanOption, null),
                                    Label.getLabel("demographics_documents_title_select_plan")));
                }

                if (demographicsOption.getName().toLowerCase().equals("other")) {
                    otherProviderEditText.setVisibility(View.VISIBLE);
                    otherProviderEditText.requestFocus();
                    selectedProviderTextView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            selectedProviderOption = new DemographicsInsuranceOption();
                        }
                    }, 100);
                } else {
                    otherProviderEditText.setVisibility(View.GONE);
                }
                findViewById(R.id.providerRequired).setVisibility(View.GONE);
                checkIfEnableButton();

            }
        };

        selectedProviderTextView.setOnClickListener(
                getSelectOptionsListener(insuranceModelProperties.getInsuranceProvider().getOptions(),
                        getDefaultOnOptionsSelectedListener(selectedProviderTextView,
                                selectedProviderOption, selectProviderCallback),
                        Label.getLabel("demographics_documents_title_select_provider")));

        selectedPlanTextView.setOnClickListener(
                getSelectOptionsListener(selectedProviderOption.getPayerPlans(),
                        getDefaultOnOptionsSelectedListener(selectedPlanTextView, selectedPlanOption, null),
                        Label.getLabel("demographics_documents_title_select_plan")));

        selectedTypeTextView.setOnClickListener(
                getSelectOptionsListener(insuranceModelProperties.getInsuranceType().getOptions(),
                        getDefaultOnOptionsSelectedListener(selectedTypeTextView, selectedTypeOption, null),
                        Label.getLabel("demographics_insurance_type_label")));


        selectedRelationshipTextView.setOnClickListener(
                getSelectOptionsListener(insuranceModelProperties.getRelationship().getOptions(),
                        getDefaultOnOptionsSelectedListener(selectedRelationshipTextView,
                                selectedRelationshipOption, new OnSelectionChangeCallback() {
                                    @Override
                                    public void onSelectionChange(DemographicsOption demographicsOption) {
                                        selectedRelationshipOption = demographicsOption;
                                        if (demographicsOption.getLabel().toLowerCase().equals("self")) {
                                            isDataHolderSelf = true;
                                            manageBoDAndGenderVisibility(isDataHolderSelf);
                                            policyFirstNameHolderInput.setError(null);
                                            policyFirstNameHolderInput.setErrorEnabled(false);
                                            policyLastNameHolderInput.setError(null);
                                            policyLastNameHolderInput.setErrorEnabled(false);
                                        } else {
                                            isDataHolderSelf = false;
                                            manageBoDAndGenderVisibility(isDataHolderSelf);
                                        }
                                        checkIfEnableButton();
                                    }
                                }),
                        Label.getLabel("demographics_insurance_relationship_label")));


        selectedGenderTextView.setOnClickListener(
                getSelectOptionsListener(insuranceModelProperties.getPolicyHolderGender().getOptions(),
                        getDefaultOnOptionsSelectedListener(selectedGenderTextView,
                                selectedGenderOption, new OnSelectionChangeCallback() {
                                    @Override
                                    public void onSelectionChange(DemographicsOption demographicsOption) {
                                        genderRequiredTextView.setVisibility(View.GONE);
                                    }
                                }),
                        Label.getLabel("demographics_review_gender")));

    }

    private void manageBoDAndGenderVisibility(boolean isDataHolderSelf) {
        dateOfBirthContainer.setVisibility(isDataHolderSelf ? View.GONE : View.VISIBLE);
        genderContainer.setVisibility(isDataHolderSelf ? View.GONE : View.VISIBLE);
        findViewById(R.id.divider).setVisibility(isDataHolderSelf ? View.GONE : View.VISIBLE);
    }


    private void setTextListeners() {
        final String defaultError = Label.getLabel("demographics_required_field_msg");
        cardNumber.addTextChangedListener(getValidInputTextWatcher(cardNumberInput, false, defaultError));
        groupNumber.addTextChangedListener(getValidInputTextWatcher(groupNumberInput, false, defaultError));
        policyFirstNameHolder.addTextChangedListener(getValidInputTextWatcher(policyFirstNameHolderInput,
                false, defaultError));
        policyLastNameHolder.addTextChangedListener(getValidInputTextWatcher(policyLastNameHolderInput,
                false, defaultError));
        policyBirthDateHolder.addTextChangedListener(dateInputFormatter);
        policyBirthDateHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())
                        && dateOfBirthContainer.getVisibility() == View.VISIBLE) {
                    policyBirthDateHolderInput.setErrorEnabled(true);
                    policyBirthDateHolderInput.setError(defaultError);

                } else {
                    policyBirthDateHolderInput.setError(null);
                    policyBirthDateHolderInput.setErrorEnabled(false);
                }
                checkIfEnableButton();
            }
        });

        otherProviderEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 || selectedProviderOption.getName() == null) {
                    selectedProviderOption.setName(editable.toString());
                    selectedPlanOption.setLabel(selectedProviderOption.getLabel());
                }
                checkIfEnableButton();
            }
        });

    }

    private void setChangeFocusListeners() {
        cardNumber.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(cardNumberInput, null));
        groupNumber.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(groupNumberInput, null));
        policyFirstNameHolder.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(policyFirstNameHolderInput, null));
        policyLastNameHolder.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(policyLastNameHolderInput, null));
        policyBirthDateHolder.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(policyBirthDateHolderInput, null));
    }

    private void setActionListeners() {
        cardNumber.setOnEditorActionListener(getEditorActionListener(groupNumber));
        groupNumber.setOnEditorActionListener(getEditorActionListener(policyFirstNameHolder));
        policyFirstNameHolder.setOnEditorActionListener(getEditorActionListener(policyLastNameHolder));
        policyLastNameHolder.setOnEditorActionListener(getEditorActionListener(null));
        policyBirthDateHolder.setOnEditorActionListener(getEditorActionListener(null));
    }

    private TextWatcher getValidInputTextWatcher(final TextInputLayout inputLayout,
                                                 final boolean requiredField,
                                                 final String errorMessage) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString()) && requiredField) {
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError(errorMessage);

                } else {
                    inputLayout.setError(null);
                    inputLayout.setErrorEnabled(false);
                }
                checkIfEnableButton();
            }
        };
    }

    private TextView.OnEditorActionListener getEditorActionListener(final TextView nextFocus) {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT && nextFocus != null) {
                    nextFocus.requestFocus();
                    return true;
                } else if (actionId == EditorInfo.IME_ACTION_DONE || nextFocus == null) {
                    textView.clearFocus();
                    SystemUtil.hideSoftKeyboard(getContext(), textView);
                }
                return false;
            }
        };
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

    private void checkIfEnableButton() {
        boolean isValid = validateForm(false);
        saveInsuranceButton.setSelected(isValid);
        saveInsuranceButton.setClickable(isValid);
    }

    private boolean validateForm(boolean userInteraction) {
        if (StringUtil.isNullOrEmpty(selectedProviderOption.getName())) {
            return false;
        }

        if (!isDataHolderSelf) {
            if (StringUtil.isNullOrEmpty(policyFirstNameHolder.getText().toString().trim())) {
                if (userInteraction) {
                    policyFirstNameHolderInput.setErrorEnabled(true);
                    policyFirstNameHolderInput.setError(Label.getLabel("demographics_required_field_msg"));
                }
                return false;
            }
            if (StringUtil.isNullOrEmpty(policyLastNameHolder.getText().toString().trim())) {
                if (userInteraction) {
                    policyLastNameHolderInput.setErrorEnabled(true);
                    policyLastNameHolderInput.setError(Label.getLabel("demographics_required_field_msg"));
                }
                return false;
            }
            if (StringUtil.isNullOrEmpty(policyBirthDateHolder.getText().toString().trim())) {
                if (userInteraction) {
                    policyBirthDateHolderInput.setErrorEnabled(true);
                    policyBirthDateHolderInput.setError(Label.getLabel("demographics_required_field_msg"));
                }
                return false;
            }

            if (!StringUtil.isNullOrEmpty(policyBirthDateHolder.getText().toString().trim())) {
                String dateValidationResult = DateUtil
                        .getDateOfBirthValidationResultMessage(policyBirthDateHolder.getText().toString().trim());
                if (dateValidationResult != null) {
                    if (userInteraction) {
                        policyBirthDateHolderInput.setErrorEnabled(true);
                        policyBirthDateHolderInput.setError(dateValidationResult);
                    }
                    return false;
                }
            }
            if (StringUtil.isNullOrEmpty(selectedGenderOption.getName())
                    || "Choose".equals(selectedGenderOption.getName())) {
                if (userInteraction) {
                    genderRequiredTextView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        }
        return true;
    }


    private View.OnClickListener getSelectOptionsListener(final List<? extends DemographicsOption> options,
                                                          final OnOptionSelectedListener listener,
                                                          final String title) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(getContext(), options, title, listener);
            }
        };
    }

    private OnOptionSelectedListener getDefaultOnOptionsSelectedListener(final TextView textView,
                                                                         final DemographicsOption storeOption,
                                                                         final OnSelectionChangeCallback callback) {
        return new OnOptionSelectedListener() {
            @Override
            public void onOptionSelected(DemographicsOption option) {
                if (callback != null) {
                    callback.onSelectionChange(option);
                }


                if (textView != null) {
                    textView.setText(option.getLabel());
                }

                storeOption.setLabel(option.getLabel());
                storeOption.setName(option.getName());

                if (getView() != null) {
                    checkIfEnableButton();
                }
            }
        };
    }

    private void showChooseDialog(Context context,
                                  List<? extends DemographicsOption> options,
                                  String title,
                                  final OnOptionSelectedListener listener) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        // add cancel button
        dialog.setNegativeButton(Label.getLabel("demographics_cancel_label"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pos) {
                dialogInterface.dismiss();
            }
        });

        // create dialog layout
        View customView = LayoutInflater.from(context).inflate(R.layout.alert_list_layout, null, false);
        dialog.setView(customView);
        TextView titleTextView = (TextView) customView.findViewById(R.id.title_view);
        titleTextView.setText(title);
        titleTextView.setVisibility(View.VISIBLE);


        // create the adapter
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomOptionsAdapter customOptionsAdapter = new CustomOptionsAdapter(context, options);
        listView.setAdapter(customOptionsAdapter);


        final AlertDialog alert = dialog.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();

        // set item click listener
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long row) {
                DemographicsOption selectedOption = (DemographicsOption) adapterView
                        .getAdapter().getItem(position);
                if (listener != null) {
                    listener.onOptionSelected(selectedOption);
                }
                alert.dismiss();
            }
        };
        listView.setOnItemClickListener(clickListener);
    }

    private void removeOldPhoto(List<DemographicInsurancePhotoDTO> photos, int page) {
        Iterator<DemographicInsurancePhotoDTO> iterator = photos.iterator();
        while (iterator.hasNext()) {
            DemographicInsurancePhotoDTO photoDTO = iterator.next();
            if (photoDTO.getPage() == page) {
                iterator.remove();
            }
        }
    }

    private interface OnOptionSelectedListener {
        void onOptionSelected(DemographicsOption option);
    }

    private interface OnSelectionChangeCallback {
        void onSelectionChange(DemographicsOption demographicsOption);
    }

    protected TextWatcher dateInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatDateOfBirth(editable, lastLength);
        }
    };

}