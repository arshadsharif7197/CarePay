package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomOptionsAdapter;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsField;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsInsuranceField;
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
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.marcok.stepprogressbar.StepProgressBar;

import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.BACK_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.FRONT_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_BACK_DTO;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_FRONT_DTO;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_HAS_BACK;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_HAS_FRONT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InsuranceEditDialog extends BaseDialogFragment implements MediaViewInterface {

    public static final String EDITED_INDEX = "EditedIndex";
    public static final String IS_PATIENT_MODE = "IsPatientMode";
    public static final String IS_CHECK_IN = "IsCheckIn";
    public static final String KEY_POLICY_HOLDER_SELF = "self";
    public static final String KEY_PROVIDER_OTHER = "other";

    public static final int NEW_INSURANCE = -1;

    private DemographicDTO demographicDTO;
    private MediaScannerPresenter mediaScannerPresenter;
    private DocumentScannerAdapter documentScannerAdapter;

    private boolean hasFrontImage = false;
    private boolean hasBackImage = false;

    private Button saveInsuranceButton;
    private EditText otherProviderEditText;
    private View otherProviderLayout;
    private ScrollView scrollView;

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
        if(!hadInsurance && !isPatientMode && isCheckin) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
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

        otherProviderEditText = (EditText) view.findViewById(R.id.otherProviderEditText);
        otherProviderLayout = view.findViewById(R.id.otherProviderLayout);
        scrollView = (ScrollView) view.findViewById(R.id.demographicsScrollView);

        if (getDialog() != null || (hadInsurance && !isPatientMode) || !isCheckin) {
            saveInsuranceButton = (Button) findViewById(R.id.save_insurance_changes);
        } else {
            saveInsuranceButton = (Button) findViewById(R.id.checkinDemographicsNextButton);
        }

        defaultType = demographicDTO.getMetadata().getNewDataModel().getDemographic()
                .getInsurances().getProperties().getItems()
                .getInsuranceModel().getInsuranceModelProperties().getInsuranceType()
                .getOptions().get(0).getName();

        if (editedIndex == NEW_INSURANCE) {
            ((Button) findViewById(R.id.demogrDocsFrontScanButton)).setText(
                    Label.getLabel("demographics_insurance_take_front_photo"));
            ((Button) findViewById(R.id.demogrDocsBackScanButton)).setText(
                    Label.getLabel("demographics_insurance_take_back_photo"));


            if (hasInsurance() && (getDialog() != null || !isPatientMode) || !isCheckin) {
                disappearViewById(R.id.remove_insurance_entry);
                ((CarePayTextView) findViewById(R.id.toolbar_title)).setText(
                        Label.getLabel("practice_checkin_demogr_ins_add_new_button_label"));
                saveInsuranceButton.setText(Label.getLabel("demographics.insuranceEdit.button.label.newInsurance"));
            } else {
                showViewById(R.id.check_in_demographics_left_button);
                findViewById(R.id.check_in_demographics_left_button).setOnClickListener(getNoInsuranceListener());
                saveInsuranceButton.setText(Label.getLabel("practice_checkin_demogr_ins_add_new_button_label"));
            }

            isDataHolderSelf = true;
            initInsuranceData(view, new DemographicInsurancePayloadDTO());
        } else {
            DemographicInsurancePayloadDTO demographicInsurancePayload = demographicDTO.getPayload()
                    .getDemographics().getPayload().getInsurances().get(editedIndex);
            initInsuranceData(view, demographicInsurancePayload);

            findViewById(R.id.remove_insurance_entry).setOnClickListener(removeButtonListener);
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


    private void initInsuranceData(final View view, final DemographicInsurancePayloadDTO demographicInsurancePayload) {
        final InsuranceModelProperties insuranceModelProperties = demographicDTO.getMetadata()
                .getNewDataModel().getDemographic().getInsurances().getProperties().getItems()
                .getInsuranceModel().getInsuranceModelProperties();

        final TextInputLayout planInputLayout = (TextInputLayout) view.findViewById(R.id.healthInsurancePlanInputLayout);
        final EditText planEditText = (EditText) view.findViewById(R.id.health_insurance_plans);
        final DemographicsField planField = insuranceModelProperties.getInsurancePlan();

        String selectedProvider = demographicInsurancePayload.getInsuranceProvider();
        TextInputLayout providerInputLayout = (TextInputLayout) view.findViewById(R.id.healthInsuranceProvidersInputLayout);
        final EditText providerEditText = (EditText) view.findViewById(R.id.health_insurance_providers);
        DemographicsInsuranceField providerField = insuranceModelProperties.getInsuranceProvider();
        setUpDemographicField(view, selectedProvider, providerField.isDisplayed(), providerField.isRequired(),
                providerField.getOptions(), null, providerInputLayout, providerEditText, null,
                selectedProviderOption, Label.getLabel("demographics_documents_title_select_provider"), new OnOptionSelectedListener() {
                    @Override
                    public void onOptionSelected(DemographicsOption selectedOption) {
                        DemographicsInsuranceOption insuranceOption = (DemographicsInsuranceOption) selectedOption;
                        providerEditText.setText(insuranceOption.getName());

                        enableDependentFields(view,
                                new int[]{R.id.healthInsurancePlanInputLayout,
                                        R.id.health_insurance_card_number_layout,
                                        R.id.health_insurance_group_number_layout},
                                true);

                        //reset the plan dropdown
                        selectedPlanOption = new DemographicsOption();
                        setUpDemographicField(view, null, planField.isDisplayed(), planField.isRequired(),
                                insuranceOption.getPayerPlans(), R.id.healthInsurancePlanLayout,
                                planInputLayout, planEditText, null, selectedPlanOption,
                                Label.getLabel("demographics_documents_title_select_plan"), null);

                        if (selectedOption.getName().toLowerCase().equals(KEY_PROVIDER_OTHER)) {
                            otherProviderLayout.setVisibility(View.VISIBLE);
                            otherProviderEditText.requestFocus();
                            view.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    selectedProviderOption = new DemographicsInsuranceOption();
                                }
                            }, 100);
                        } else {
                            otherProviderLayout.setVisibility(View.GONE);
                        }
                    }
                });

        setProviderOptionsPlans(insuranceModelProperties);
        enableDependentFields(view,
                new int[]{R.id.healthInsurancePlanInputLayout,
                        R.id.health_insurance_card_number_layout,
                        R.id.health_insurance_group_number_layout},
                !StringUtil.isNullOrEmpty(selectedProvider));

        String selectedPlan = demographicInsurancePayload.getInsurancePlan();
        setUpDemographicField(view, selectedPlan, planField.isDisplayed(), planField.isRequired(),
                selectedProviderOption.getPayerPlans(), R.id.healthInsurancePlanLayout,
                planInputLayout, planEditText, null, selectedPlanOption,
                Label.getLabel("demographics_documents_title_select_plan"), null);

        String selectedType = StringUtil.captialize(demographicInsurancePayload.getInsuranceType());
        setUpDemographicField(view, selectedType, insuranceModelProperties.getInsuranceType(),
                R.id.healthInsuranceTypeLayout, R.id.healthInsuranceTypeInputLayout,
                R.id.health_insurance_types, null,
                selectedTypeOption, Label.getLabel("demographics_insurance_type_label"));

        String selectedRelationship = demographicInsurancePayload.getRelationship();
        if(StringUtil.isNullOrEmpty(selectedRelationship) && isDataHolderSelf){
            selectedRelationship = StringUtil.captialize(KEY_POLICY_HOLDER_SELF);
        }
        DemographicsField relationshipField = insuranceModelProperties.getRelationship();
        TextInputLayout relationshipInputLayout = (TextInputLayout) view.findViewById(R.id.healthInsuranceRelationshipInputLayout);
        EditText relationshipEditText = (EditText) view.findViewById(R.id.health_insurance_relationship);
        setUpDemographicField(view, selectedRelationship, relationshipField.isDisplayed(),
                relationshipField.isRequired(), relationshipField.getOptions(),
                R.id.healthInsuranceRelationshipLayout, relationshipInputLayout,
                relationshipEditText, null,
                selectedRelationshipOption, Label.getLabel("demographics_insurance_relationship_label"),
                new OnOptionSelectedListener() {
                    @Override
                    public void onOptionSelected(DemographicsOption option) {
                        isDataHolderSelf = selectedRelationshipOption.getLabel().toLowerCase().equals(KEY_POLICY_HOLDER_SELF);
//                        setupExtraFields(view, demographicInsurancePayload, insuranceModelProperties);
                        checkIfEnableButton();
                        enableDependentFields(view,
                                new int[]{R.id.health_insurance_policy_first_name_holder_layout,
                                        R.id.health_insurance_policy_last_name_holder_layout,
                                        R.id.health_insurance_policy_birth_date_holder_layout,
                                        R.id.healthInsuranceGenderInputLayout},
                                !isDataHolderSelf);

                    }
                });

        if(editedIndex != NEW_INSURANCE) {
            isDataHolderSelf = selectedRelationshipOption.getLabel().toLowerCase().trim().equals(KEY_POLICY_HOLDER_SELF);
        }
        setupExtraFields(view, demographicInsurancePayload, insuranceModelProperties);
        enableDependentFields(view,
                new int[]{R.id.health_insurance_policy_first_name_holder_layout,
                        R.id.health_insurance_policy_last_name_holder_layout,
                        R.id.health_insurance_policy_birth_date_holder_layout,
                        R.id.healthInsuranceGenderInputLayout},
                !isDataHolderSelf);



        String memberId = demographicInsurancePayload.getInsuranceMemberId();
        setUpDemographicField(view, memberId, insuranceModelProperties.getInsuranceMemberId(),
                null, R.id.health_insurance_card_number_layout,
                R.id.health_insurance_card_number, null, null, null);

        String groupId = demographicInsurancePayload.getInsuranceGroupId();
        setUpDemographicField(view, groupId, insuranceModelProperties.getInsuranceGroupId(),
                null, R.id.health_insurance_group_number_layout,
                R.id.health_insurance_group_number, null, null, null);

        String firstName = demographicInsurancePayload.getPolicyFirstNameHolder();
        setUpDemographicField(view, firstName, insuranceModelProperties.getPolicyHolder(),
                null, R.id.health_insurance_policy_first_name_holder_layout,
                R.id.health_insurance_policy_first_name_holder, null, null, null);
        TextInputLayout firstNameInputLayout = (TextInputLayout) view.findViewById(R.id.health_insurance_policy_first_name_holder_layout);
        EditText firstNameEditText = (EditText) view.findViewById(R.id.health_insurance_policy_first_name_holder);
        firstNameEditText.addTextChangedListener(getValidateOptionalFields(firstNameInputLayout));


        String lastName = demographicInsurancePayload.getPolicyLastNameHolder();
        setUpDemographicField(view, lastName, insuranceModelProperties.getPolicyHolder(),
                null, R.id.health_insurance_policy_last_name_holder_layout,
                R.id.health_insurance_policy_last_name_holder, null, null, null);
        TextInputLayout lastNameInputLayout = (TextInputLayout) view.findViewById(R.id.health_insurance_policy_last_name_holder_layout);
        EditText lastNameEditText = (EditText) view.findViewById(R.id.health_insurance_policy_last_name_holder);
        lastNameEditText.addTextChangedListener(getValidateOptionalFields(lastNameInputLayout));

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

        String title = (selectedProvider != null ? selectedProvider : "") + (selectedPlan != null ? " " + selectedPlan : "");
        if(!StringUtil.isNullOrEmpty(title)) {
            ((TextView) view.findViewById(R.id.toolbar_title)).setText(title);
        }

    }

    private void enableDependentFields(View view, int[] fields, boolean enabled){
        for(int field : fields){
            View target = view.findViewById(field);
            if(target instanceof TextInputLayout){
                TextInputLayout inputLayout = (TextInputLayout) target;
                inputLayout.setError(null);
                inputLayout.setErrorEnabled(false);
                EditText editText = inputLayout.getEditText();
                if(editText != null && !enabled && editText.getOnFocusChangeListener() != null) {
                    editText.getOnFocusChangeListener().onFocusChange(editText, true);
                    editText.setText(null);
                    editText.getOnFocusChangeListener().onFocusChange(editText, false);
                }
            }
            target.setEnabled(enabled);
        }
    }

    private void setupExtraFields(View view, DemographicInsurancePayloadDTO demographicInsurancePayload,
                                  InsuranceModelProperties insuranceModelProperties){
        String dob = demographicInsurancePayload.getPolicyDateOfBirthHolder();
        final TextInputLayout dobTextInputLayout = (TextInputLayout) view.findViewById(R.id.health_insurance_policy_birth_date_holder_layout);
        EditText dobEditText = (EditText) view.findViewById(R.id.health_insurance_policy_birth_date_holder);
        setUpDemographicField(view, dob, true, !isDataHolderSelf, new ArrayList<DemographicsOption>(),
                R.id.health_insurance_policy_birth_date_holder_layout, dobTextInputLayout, dobEditText,
                null, null, null, null);
        dobEditText.addTextChangedListener(dateInputFormatter);

        String selectedGender = demographicInsurancePayload.getGender();
        TextInputLayout genderInputLayout = (TextInputLayout) view.findViewById(R.id.healthInsuranceGenderInputLayout);
        EditText genderEditText = (EditText) view.findViewById(R.id.health_insurance_gender);
        setUpDemographicField(view, selectedGender, true, !isDataHolderSelf,
                insuranceModelProperties.getPolicyHolderGender().getOptions(), R.id.healthInsuranceGenderLayout,
                genderInputLayout, genderEditText, null,
                selectedGenderOption, Label.getLabel("demographics_review_gender"), null);

    }

    private void setProviderOptionsPlans(InsuranceModelProperties insuranceModelProperties) {
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
        View view = getView();
        if(view == null){
            return;
        }

        DemographicInsurancePayloadDTO demographicInsurancePayloadDTO;
        if (editedIndex == NEW_INSURANCE) {
            demographicInsurancePayloadDTO = new DemographicInsurancePayloadDTO();
            demographicDTO.getPayload().getDemographics().getPayload().getInsurances()
                    .add(demographicInsurancePayloadDTO);
        } else {
            demographicInsurancePayloadDTO = demographicDTO.getPayload().getDemographics()
                    .getPayload().getInsurances().get(editedIndex);
        }

        demographicInsurancePayloadDTO.setInsuranceProvider(selectedProviderOption.getName().trim());
        demographicInsurancePayloadDTO.setInsurancePlan(selectedPlanOption.getName().trim());
        demographicInsurancePayloadDTO.setInsuranceType(!StringUtil.isNullOrEmpty(selectedTypeOption.getName())
                ? selectedTypeOption.getName().trim() : defaultType);

        String cardNumber = ((TextView) view.findViewById(R.id.health_insurance_card_number)).getText().toString();
        demographicInsurancePayloadDTO.setInsuranceMemberId(cardNumber.trim());

        String groupNumber = ((TextView) view.findViewById(R.id.health_insurance_group_number)).getText().toString();
        demographicInsurancePayloadDTO.setInsuranceGroupId(groupNumber.trim());

        demographicInsurancePayloadDTO.setRelationship(selectedRelationshipOption.getName().trim());

        String firstName = ((TextView) view.findViewById(R.id.health_insurance_policy_first_name_holder)).getText().toString();
        demographicInsurancePayloadDTO.setPolicyFirstNameHolder(firstName.trim());

        String lastName = ((TextView) view.findViewById(R.id.health_insurance_policy_last_name_holder)).getText().toString();
        demographicInsurancePayloadDTO.setPolicyLastNameHolder(lastName.trim());

        if(!isDataHolderSelf) {
            String dob = ((TextView) view.findViewById(R.id.health_insurance_policy_birth_date_holder)).getText().toString();
            demographicInsurancePayloadDTO.setPolicyDateOfBirthHolder(dob.trim());

            demographicInsurancePayloadDTO.setGender(selectedGenderOption.getName());
        }

        setupImageBase64();

        List<DemographicInsurancePhotoDTO> photos = demographicInsurancePayloadDTO.getInsurancePhotos();
        if(frontInsurancePhotoDTO != null || backInsurancePhotoDTO != null){
            //Log new Insurance Doc
            MixPanelUtil.logEvent(getString(R.string.event_add_insurance_doc), getString(R.string.param_is_checkin), isCheckin);
        }

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
        View view = getView();
        if(view == null){
            return false;
        }

        if (StringUtil.isNullOrEmpty(selectedProviderOption.getName())) {
            if (userInteraction) {
                showErrorViews(true, (ViewGroup) view.findViewById(R.id.healthInsuranceProvidersLayout));
            }
            return false;
        }else{
            showErrorViews(false, (ViewGroup) view.findViewById(R.id.healthInsuranceProvidersLayout));
        }

        if (!isDataHolderSelf) {

            String firstName = ((TextView) view.findViewById(R.id.health_insurance_policy_first_name_holder)).getText().toString();
            TextInputLayout policyFirstNameHolderInput = (TextInputLayout) view.findViewById(R.id.health_insurance_policy_first_name_holder_layout);
            if (StringUtil.isNullOrEmpty(firstName.trim())) {
                if (userInteraction) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.healthInsuranceFirstNameContainer));
                    policyFirstNameHolderInput.setErrorEnabled(true);
                    policyFirstNameHolderInput.setError(Label.getLabel("demographics_required_field_msg"));
                }
                return false;
            }else{
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.healthInsuranceFirstNameContainer));
                policyFirstNameHolderInput.setError(null);
                policyFirstNameHolderInput.setErrorEnabled(false);
            }


            String lastName = ((TextView) view.findViewById(R.id.health_insurance_policy_last_name_holder)).getText().toString();
            TextInputLayout policyLastNameHolderInput = (TextInputLayout) view.findViewById(R.id.health_insurance_policy_last_name_holder_layout);
            if (StringUtil.isNullOrEmpty(lastName.trim())) {
                if (userInteraction) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.healthInsuranceLastNameContainer));
                    policyLastNameHolderInput.setErrorEnabled(true);
                    policyLastNameHolderInput.setError(Label.getLabel("demographics_required_field_msg"));
                }
                return false;
            }else{
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.healthInsuranceLastNameContainer));
                policyLastNameHolderInput.setError(null);
                policyLastNameHolderInput.setErrorEnabled(false);
            }

            String dob = ((TextView) view.findViewById(R.id.health_insurance_policy_birth_date_holder)).getText().toString();
            TextInputLayout policyBirthDateHolderInput = (TextInputLayout) view.findViewById(R.id.health_insurance_policy_birth_date_holder_layout);
            if (StringUtil.isNullOrEmpty(dob.trim())) {
                if (userInteraction) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.dateOfBirthContainer));
                    policyBirthDateHolderInput.setErrorEnabled(true);
                    policyBirthDateHolderInput.setError(Label.getLabel("demographics_required_field_msg"));
                }
                return false;
            }else{
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.dateOfBirthContainer));
                policyBirthDateHolderInput.setError(null);
                policyBirthDateHolderInput.setErrorEnabled(false);
            }


            if (StringUtil.isNullOrEmpty(selectedGenderOption.getName()) || "Choose".equals(selectedGenderOption.getName())) {
                if (userInteraction) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.healthInsuranceGenderLayout));
                }
                return false;
            }else{
                showErrorViews(false, (ViewGroup) view.findViewById(R.id.healthInsuranceGenderLayout));
            }

            if (!StringUtil.isNullOrEmpty(dob.trim())) {
                String dateValidationResult = DateUtil
                        .getDateOfBirthValidationResultMessage(dob.trim());
                if (dateValidationResult != null) {
                    if (userInteraction) {
                        policyBirthDateHolderInput.setErrorEnabled(true);
                        policyBirthDateHolderInput.setError(dateValidationResult);
                    }
                    return false;
                }else{
                    policyBirthDateHolderInput.setError(null);
                    policyBirthDateHolderInput.setErrorEnabled(false);
                }
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
            checkIfEnableButton();
        }
    };

    private void setUpDemographicField(View view, String value, DemographicsField demographicsField,
                                       Integer containerLayout, int inputLayoutId, int editTextId, Integer optionalViewId,
                                       DemographicsOption demographicsOption, String optionDialogTitle) {
        if(demographicsField == null){
            demographicsField = new DemographicsField();
        }
        TextInputLayout inputLayout = (TextInputLayout) view.findViewById(inputLayoutId);
        EditText editText = (EditText) view.findViewById(editTextId);
        setUpDemographicField(view, value, demographicsField.isDisplayed(), demographicsField.isRequired(),
                demographicsField.getOptions(), containerLayout, inputLayout, editText,
                optionalViewId, demographicsOption, optionDialogTitle, null);

    }

    private void setUpDemographicField(View view, String value, boolean displayed,
                                       boolean required, List<? extends DemographicsOption> options,
                                       Integer containerLayout, TextInputLayout inputLayout, EditText editText, Integer optionalViewId,
                                       DemographicsOption demographicsOption, String optionDialogTitle,
                                       OnOptionSelectedListener optionalListener) {
        if(containerLayout != null) {
            view.findViewById(containerLayout).setVisibility(displayed ? View.VISIBLE : View.GONE);
        }
        editText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(inputLayout, null));
        editText.setText(value);
        editText.getOnFocusChangeListener().onFocusChange(editText,
                !StringUtil.isNullOrEmpty(editText.getText().toString().trim()));
        View optionalView = null;
        if(optionalViewId != null) {
            optionalView = view.findViewById(optionalViewId);
            optionalView.setVisibility(!required && StringUtil.isNullOrEmpty(value) ? View.VISIBLE : View.GONE);
        }
        if (demographicsOption != null) {
            editText.setOnClickListener(getEditTextClickListener(options, inputLayout, editText,
                    optionalView, demographicsOption, optionDialogTitle, optionalListener));
            demographicsOption.setName(editText.getText().toString());
            demographicsOption.setLabel(editText.getText().toString());
        } else if (required) {
            editText.addTextChangedListener(getValidateOptionalFields(inputLayout));
        } else if (optionalView != null) {
            editText.addTextChangedListener(getOptionalViewTextWatcher(optionalView));
        }
    }


    private View.OnClickListener getEditTextClickListener(List<? extends DemographicsOption> options,
                                                          final TextInputLayout inputLayout,
                                                          final EditText editText,
                                                          final View optionalLabel,
                                                          final DemographicsOption demographicsOption,
                                                          final String dialogTitle,
                                                          final OnOptionSelectedListener optionalListener) {
        return getSelectOptionsListener(options,
                new OnOptionSelectedListener() {
                    @Override
                    public void onOptionSelected(DemographicsOption option) {
                        if (demographicsOption != null) {
                            demographicsOption.setLabel(option.getLabel());
                            demographicsOption.setName(option.getName());
                            demographicsOption.setId(option.getId());
                        }
                        editText.setText(option.getLabel());
                        editText.getOnFocusChangeListener()
                                .onFocusChange(editText, !StringUtil.isNullOrEmpty(editText.getText().toString()));
                        inputLayout.setError(null);
                        inputLayout.setErrorEnabled(false);
                        if(optionalLabel != null) {
                            optionalLabel.setVisibility(View.GONE);
                        }
                        checkIfEnableButton();

                        if(optionalListener != null){
                            optionalListener.onOptionSelected(option);
                        }
                    }
                },
                dialogTitle);
    }


    private TextWatcher getValidateOptionalFields(final TextInputLayout inputLayout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString()) && !isDataHolderSelf) {
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
                } else {
                    inputLayout.setError(null);
                }
                checkIfEnableButton();
            }
        };
    }

    private TextWatcher getOptionalViewTextWatcher(final View optionalView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())) {
                    optionalView.setVisibility(View.VISIBLE);
                } else {
                    optionalView.setVisibility(View.GONE);
                }
                checkIfEnableButton();
            }
        };
    }

    protected void showErrorViews(boolean isError, ViewGroup container) {
        final String TAG_ERROR_HIDE_INV = getString(R.string.tag_demographics_error_hide_inv);
        final String TAG_ERROR_HIDE_GONE = getString(R.string.tag_demographics_error_hide_gone);
        final String TAG_ERROR_SHOW_INV = getString(R.string.tag_demographics_error_show_inv);
        final String TAG_ERROR_SHOW_GONE = getString(R.string.tag_demographics_error_show_gone);
        final String TAG_ERROR_COLOR = getString(R.string.tag_demographics_error_color);

        for (int i = 0; i < container.getChildCount(); i++) {
            final View view = container.getChildAt(i);
            if (view instanceof ViewGroup) {
                showErrorViews(isError, (ViewGroup) view);
            }
            if (view.getTag() instanceof String) {
                String tag = (String) view.getTag();
                if (tag != null) {
                    if (isError) {
                        if (tag.equals(TAG_ERROR_HIDE_GONE)) {
                            view.setVisibility(View.GONE);
                        } else if (tag.equals(TAG_ERROR_HIDE_INV)) {
                            view.setVisibility(View.INVISIBLE);
                        } else if (tag.equals(TAG_ERROR_SHOW_GONE) || tag.equals(TAG_ERROR_SHOW_INV)) {
                            view.setVisibility(View.VISIBLE);
                        }else if (tag.equals(TAG_ERROR_COLOR)){
                            view.setSelected(true);
                            if(view instanceof TextInputLayout){
                                EditText editText = ((TextInputLayout) view).getEditText();
                                editText.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.remove_red), PorterDuff.Mode.SRC_IN);
                            }
                        }
                        Rect rect = new Rect();
                        view.getGlobalVisibleRect(rect);
                        if(rect.top > 0){
                            scrollView.scrollBy(0, rect.top-scrollView.getTop());
                        }else {
                            scrollView.scrollBy(0, rect.top);
                        }
                    } else {
                        if (tag.equals(TAG_ERROR_SHOW_GONE)) {
                            view.setVisibility(View.GONE);
                        } else if (tag.equals(TAG_ERROR_SHOW_INV)) {
                            view.setVisibility(View.INVISIBLE);
                        } else if (tag.equals(TAG_ERROR_HIDE_GONE) || tag.equals(TAG_ERROR_HIDE_INV)) {
                            view.setVisibility(View.VISIBLE);
                        } else if (tag.equals(TAG_ERROR_COLOR)){
                            view.setSelected(false);
                        }
                    }
                }
            }

        }

    }

}