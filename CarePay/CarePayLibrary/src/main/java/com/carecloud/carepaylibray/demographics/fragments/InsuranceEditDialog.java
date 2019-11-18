package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomOptionsAdapter;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
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
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.DTOInterface;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.marcok.stepprogressbar.StepProgressBar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.BACK_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.FRONT_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_BACK_DTO;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_FRONT_DTO;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_HAS_BACK;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_HAS_FRONT;

public class InsuranceEditDialog extends BaseDialogFragment implements MediaViewInterface, DTOInterface {

    public static final String EDITED_INDEX = "EditedIndex";
    public static final String IS_PATIENT_MODE = "IsPatientMode";
    public static final String IS_CHECK_IN = "IsCheckIn";
    public static final String KEY_POLICY_HOLDER_SELF = "self";
    private static final String KEY_POLICY_HOLDER_SELF_ES = "yo";
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
    private EditText planEditText;
    private Button noInsuranceButton;

    private boolean hadInsurance;
    private boolean isPatientMode;
    private boolean isDataHolderSelf;
    private boolean isCheckin;
    private boolean isProviderOther;

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

    @Override
    public DTO getDto() {
        return demographicDTO;
    }


    public interface InsuranceEditDialogListener {
        void onInsuranceEdited(DemographicDTO demographicDTO, boolean proceed);

        void goOneStepBack();

        void showRemovePrimaryInsuranceDialog(ConfirmationCallback callback, DialogInterface.OnCancelListener cancelListener);
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

//        setRetainInstance(true);
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

        StepProgressBar stepProgressBar = view.findViewById(R.id.stepProgressBarCheckin);
        stepProgressBar.setNumDots(5);
        stepProgressBar.setCurrentProgressDot(4);

        ViewGroup contentLayout = view.findViewById(R.id.checkinDemographicsContentLayout);
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

        if (callback instanceof CheckinFlowCallback) {
            CheckinFlowCallback checkinFlowCallback = (CheckinFlowCallback) callback;
            checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, checkinFlowCallback.getTotalSteps(), CheckinFlowCallback.INSURANCE);
            checkinFlowCallback.setCurrentStep(CheckinFlowCallback.INSURANCE);
        }

        return view;
    }

    private void inflateToolbarViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        if (!hadInsurance && !isPatientMode && isCheckin) {
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

        TextView textView = view.findViewById(R.id.toolbar_title);
        textView.setText(StringUtil.capitalize(Label.getLabel("demographics_add_insurance_link")));
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

        otherProviderEditText = view.findViewById(R.id.otherProviderEditText);
        otherProviderLayout = view.findViewById(R.id.otherProviderLayout);
        scrollView = view.findViewById(R.id.demographicsScrollView);
        if (!isPatientMode) {
            noInsuranceButton = view.findViewById(R.id.noInsuranceButton);
        }

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
                ((CarePayTextView) findViewById(R.id.toolbar_title)).setText(StringUtil.capitalize(
                        Label.getLabel("demographics_add_insurance_link")));
                saveInsuranceButton.setText(Label.getLabel("demographics.insuranceEdit.button.label.newInsurance"));
            } else {
                showViewById(R.id.check_in_demographics_left_button);
                findViewById(R.id.check_in_demographics_left_button).setOnClickListener(getNoInsuranceListener());
                saveInsuranceButton.setText(Label.getLabel("practice_checkin_demogr_ins_add_new_button_label"));
            }

            isDataHolderSelf = true;
            initInsuranceData(view, new DemographicInsurancePayloadDTO());
            if (!isPatientMode) {
                noInsuranceButton.setVisibility(isCheckin ? View.VISIBLE : View.GONE);
                noInsuranceButton.setOnClickListener(noInsurance);
            }
        } else {
            DemographicInsurancePayloadDTO demographicInsurancePayload = demographicDTO.getPayload()
                    .getDemographics().getPayload().getInsurances().get(editedIndex);
            initInsuranceData(view, demographicInsurancePayload);

            findViewById(R.id.remove_insurance_entry).setOnClickListener(removeButtonListener);
            if (!isPatientMode) {
                noInsuranceButton.setVisibility(View.GONE);
            }
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

        final View otherPolicyHolderFields = findViewById(R.id.otherPolicyHolderFields);

        final TextInputLayout planInputLayout = view.findViewById(R.id.healthInsurancePlanInputLayout);
        planEditText = view.findViewById(R.id.health_insurance_plans);
        final DemographicsField planField = insuranceModelProperties.getInsurancePlan();
        boolean savedInsurance = !StringUtil.isNullOrEmpty(demographicInsurancePayload.getInsuranceId());

        final View otherProviderRequiredView = view.findViewById(R.id.otherProviderRequired);

        String selectedProvider = demographicInsurancePayload.getInsuranceProvider();
        TextInputLayout providerInputLayout = view.findViewById(R.id.healthInsuranceProvidersInputLayout);
        final EditText providerEditText = view.findViewById(R.id.health_insurance_providers);
        DemographicsInsuranceField providerField = insuranceModelProperties.getInsuranceProvider();
        setUpDemographicField(view, selectedProvider, providerField.isDisplayed(),
                providerField.getOptions(), null, providerInputLayout, providerEditText, R.id.healthInsuranceProviderRequired,
                selectedProviderOption, Label.getLabel("demographics_documents_title_select_provider"), new OnOptionSelectedListener() {
                    @Override
                    public void onOptionSelected(DemographicsOption selectedOption) {
                        DemographicsInsuranceOption insuranceOption = (DemographicsInsuranceOption) selectedOption;
                        providerEditText.setText(insuranceOption.getName());

                        enableDependentFields(view,
                                new int[]{R.id.healthInsurancePlanInputLayout},
                                true);

                        //reset the plan dropdown
                        selectedPlanOption = new DemographicsOption();
                        planEditText.getText().clear();
                        setUpDemographicField(view, null, planField.isDisplayed(),
                                insuranceOption.getPayerPlans(), R.id.healthInsurancePlanLayout,
                                planInputLayout, planEditText, null, selectedPlanOption,
                                Label.getLabel("demographics_documents_title_select_plan"), null, false);

                        isProviderOther = selectedOption.getName().toLowerCase().equals(KEY_PROVIDER_OTHER);
                        if (isProviderOther) {
                            planEditText.setOnClickListener(null);
                            planEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                            changeInputComponentType(planEditText, true);

                            otherProviderLayout.setVisibility(View.VISIBLE);
                            otherProviderRequiredView.setVisibility(View.VISIBLE);
                            otherProviderEditText.requestFocus();
                            otherProviderEditText.getText().clear();

                            view.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    selectedProviderOption.setLabel(null);
                                    selectedProviderOption.setName(null);
                                    selectedProviderOption.setId(null);
                                    checkIfEnableButton();
                                }
                            }, 100);
                        } else {
                            changeInputComponentType(planEditText, false);
                            otherProviderLayout.setVisibility(View.GONE);
                        }
                    }
                }, !StringUtil.isNullOrEmpty(selectedProvider) && savedInsurance);

        setProviderOptionsPlans(insuranceModelProperties);
        enableDependentFields(view,
                new int[]{R.id.healthInsurancePlanInputLayout},
                !StringUtil.isNullOrEmpty(selectedProvider));

        String selectedPlan = demographicInsurancePayload.getInsurancePlan();
        setUpDemographicField(view, selectedPlan, planField.isDisplayed(),
                selectedProviderOption.getPayerPlans(), R.id.healthInsurancePlanLayout,
                planInputLayout, planEditText, null, selectedPlanOption,
                Label.getLabel("demographics_documents_title_select_plan"), null,
                !StringUtil.isNullOrEmpty(selectedPlan) && savedInsurance);

        String selectedType = StringUtil.captialize(demographicInsurancePayload.getInsuranceType());
        setUpDemographicField(view, selectedType, insuranceModelProperties.getInsuranceType(),
                R.id.healthInsuranceTypeLayout, R.id.healthInsuranceTypeInputLayout,
                R.id.health_insurance_types, R.id.healthInsuranceTypeRequired,
                selectedTypeOption, Label.getLabel("demographics_insurance_type_label"), false);

        String selectedRelationship = demographicInsurancePayload.getRelationship();
        if (StringUtil.isNullOrEmpty(selectedRelationship) && isDataHolderSelf) {
            selectedRelationship = StringUtil.captialize(KEY_POLICY_HOLDER_SELF);
        }
        DemographicsField relationshipField = insuranceModelProperties.getRelationship();
        TextInputLayout relationshipInputLayout = view.findViewById(R.id.healthInsuranceRelationshipInputLayout);
        EditText relationshipEditText = view.findViewById(R.id.health_insurance_relationship);
        setUpDemographicField(view, selectedRelationship, relationshipField.isDisplayed(),
                relationshipField.getOptions(), R.id.healthInsuranceRelationshipLayout, relationshipInputLayout,
                relationshipEditText, null,
                selectedRelationshipOption, Label.getLabel("demographics_insurance_relationship_label"),
                new OnOptionSelectedListener() {
                    @Override
                    public void onOptionSelected(DemographicsOption option) {
                        String policyHolderRelation = selectedRelationshipOption.getName().toLowerCase();
                        isDataHolderSelf = policyHolderRelation.equals(KEY_POLICY_HOLDER_SELF) || policyHolderRelation.equals(KEY_POLICY_HOLDER_SELF_ES);
                        checkIfEnableButton();
                        otherPolicyHolderFields.setVisibility(isDataHolderSelf ? View.GONE : View.VISIBLE);
                        enableDependentFields(view,
                                new int[]{R.id.health_insurance_policy_first_name_holder_layout,
                                        R.id.health_insurance_policy_middle_name_holder_layout,
                                        R.id.health_insurance_policy_last_name_holder_layout,
                                        R.id.health_insurance_policy_birth_date_holder_layout,
                                        R.id.healthInsuranceGenderInputLayout},
                                !isDataHolderSelf);

                    }
                }, false);

        if (editedIndex != NEW_INSURANCE) {
            String policyHolderRelation = selectedRelationshipOption.getName().toLowerCase();
            isDataHolderSelf = policyHolderRelation.equals(KEY_POLICY_HOLDER_SELF) || policyHolderRelation.equals(KEY_POLICY_HOLDER_SELF_ES);
        }
        setupExtraFields(view, demographicInsurancePayload, insuranceModelProperties);
        enableDependentFields(view,
                new int[]{R.id.health_insurance_policy_first_name_holder_layout,
                        R.id.health_insurance_policy_middle_name_holder_layout,
                        R.id.health_insurance_policy_last_name_holder_layout,
                        R.id.health_insurance_policy_birth_date_holder_layout,
                        R.id.healthInsuranceGenderInputLayout},
                !isDataHolderSelf);


        String memberId = demographicInsurancePayload.getInsuranceMemberId();
        setUpDemographicField(view, memberId, insuranceModelProperties.getInsuranceMemberId(),
                null, R.id.health_insurance_card_number_layout,
                R.id.health_insurance_card_number, null, null, null,
                !StringUtil.isNullOrEmpty(memberId) && savedInsurance);

        String groupId = demographicInsurancePayload.getInsuranceGroupId();
        setUpDemographicField(view, groupId, insuranceModelProperties.getInsuranceGroupId(),
                null, R.id.health_insurance_group_number_layout,
                R.id.health_insurance_group_number, null, null, null,
                !StringUtil.isNullOrEmpty(groupId) && savedInsurance);

        String firstName = StringUtil.capitalize(demographicInsurancePayload.getPolicyFirstNameHolder());
        setUpDemographicField(view, firstName, insuranceModelProperties.getPolicyHolder(),
                null, R.id.health_insurance_policy_first_name_holder_layout,
                R.id.health_insurance_policy_first_name_holder, R.id.healthInsuranceFirstNameRequired,
                null, null, false);

        String middleName = StringUtil.capitalize(demographicInsurancePayload.getPolicyMiddleNameHolder());
        setUpDemographicField(view, middleName, insuranceModelProperties.getPolicyHolder(),
                null, R.id.health_insurance_policy_middle_name_holder_layout,
                R.id.health_insurance_policy_middle_name_holder, null,
                null, null, false);

        String lastName = StringUtil.capitalize(demographicInsurancePayload.getPolicyLastNameHolder());
        setUpDemographicField(view, lastName, insuranceModelProperties.getPolicyHolder(),
                null, R.id.health_insurance_policy_last_name_holder_layout,
                R.id.health_insurance_policy_last_name_holder, R.id.healthInsuranceLastNameRequired,
                null, null, false);

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
                    otherProviderRequiredView.setVisibility(View.GONE);
                } else {
                    selectedProviderOption.setLabel(null);
                    selectedProviderOption.setName(null);
                    selectedProviderOption.setId(null);
                    otherProviderRequiredView.setVisibility(View.VISIBLE);
                }
                checkIfEnableButton();
            }
        });

        String title = (selectedProvider != null ? selectedProvider : "") + (selectedPlan != null ? " " + selectedPlan : "");
        if (!StringUtil.isNullOrEmpty(title)) {
            ((TextView) view.findViewById(R.id.toolbar_title)).setText(title);
        }
        otherPolicyHolderFields.setVisibility(isDataHolderSelf ? View.GONE : View.VISIBLE);
    }

    /**
     * Changes the input component from dropdown to editText
     * @param view View
     * @param setToTextInput Boolean for component to be EditText
     */
    private void changeInputComponentType(EditText view, boolean setToTextInput) {
        view.setClickable(setToTextInput);
        view.setFocusableInTouchMode(setToTextInput);
        view.setCursorVisible(setToTextInput);
        view.setCompoundDrawablesWithIntrinsicBounds(null, null,
                setToTextInput ? null : getContext().getResources().getDrawable(R.drawable.icon_drop_down),
                null );
        if (setToTextInput) {
            view.requestFocus();
        } else {
            view.clearFocus();
        }
    }

    private void enableDependentFields(View view, int[] fields, boolean enabled) {
        for (int field : fields) {
            View target = view.findViewById(field);
            if (target instanceof TextInputLayout) {
                TextInputLayout inputLayout = (TextInputLayout) target;
                inputLayout.setError(null);
                inputLayout.setErrorEnabled(false);
                EditText editText = inputLayout.getEditText();
                if (editText != null && !enabled && editText.getOnFocusChangeListener() != null) {
                    editText.getOnFocusChangeListener().onFocusChange(editText, true);
                    editText.setText(null);
                    editText.getOnFocusChangeListener().onFocusChange(editText, false);
                }
            }
            target.setEnabled(enabled);
        }
    }


    private void setupExtraFields(View view, DemographicInsurancePayloadDTO demographicInsurancePayload,
                                  InsuranceModelProperties insuranceModelProperties) {
        String dob = demographicInsurancePayload.getFormattedPolicyDateOfBirthHolder();
        final TextInputLayout dobTextInputLayout = view.findViewById(R.id.health_insurance_policy_birth_date_holder_layout);
        EditText dobEditText = view.findViewById(R.id.health_insurance_policy_birth_date_holder);
        setUpDemographicField(view, dob, true, new ArrayList<DemographicsOption>(),
                R.id.health_insurance_policy_birth_date_holder_layout, dobTextInputLayout, dobEditText,
                R.id.healthInsuranceDateOfBirthRequired, null, null, null, false);
        dobEditText.addTextChangedListener(dateInputFormatter);

        String selectedGender = demographicInsurancePayload.getPolicyGenderHolder();
        TextInputLayout genderInputLayout = view.findViewById(R.id.healthInsuranceGenderInputLayout);
        EditText genderEditText = view.findViewById(R.id.health_insurance_gender);
        setUpDemographicField(view, selectedGender, true,
                insuranceModelProperties.getPolicyHolderGender().getOptions(), R.id.healthInsuranceGenderLayout,
                genderInputLayout, genderEditText, R.id.healthInsuranceGenderRequired,
                selectedGenderOption, Label.getLabel("demographics_review_gender"), null, false);

    }

    private void setProviderOptionsPlans(InsuranceModelProperties insuranceModelProperties) {
        for (DemographicsInsuranceOption insuranceOption : insuranceModelProperties
                .getInsuranceProvider().getOptions()) {
            if (insuranceOption.getName().equalsIgnoreCase(selectedProviderOption.getName().trim())) {
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
                DialogInterface.OnCancelListener cancelListener = null;
                if (getDialog() != null) {
                    cancelListener = new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            showDialog();
                        }
                    };
                }
                callback.showRemovePrimaryInsuranceDialog(new ConfirmationCallback() {
                    @Override
                    public void onConfirm() {
                        removeInsurance();
                    }
                }, cancelListener);
                hideDialog();
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
        if (view == null) {
            return;
        }

        if (isProviderOther && !StringUtil.isNullOrEmpty(planEditText.getText().toString())) {
            selectedPlanOption.setName(planEditText.getText().toString().trim());
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
        demographicInsurancePayloadDTO.setInsuranceType(selectedTypeOption.getName().trim());

        String cardNumber = ((TextView) view.findViewById(R.id.health_insurance_card_number)).getText().toString();
        demographicInsurancePayloadDTO.setInsuranceMemberId(cardNumber.trim());

        String groupNumber = ((TextView) view.findViewById(R.id.health_insurance_group_number)).getText().toString();
        demographicInsurancePayloadDTO.setInsuranceGroupId(groupNumber.trim());

        demographicInsurancePayloadDTO.setRelationship(selectedRelationshipOption.getName().trim());

        String firstName = ((TextView) view.findViewById(R.id.health_insurance_policy_first_name_holder)).getText().toString();
        demographicInsurancePayloadDTO.setPolicyFirstNameHolder(firstName.trim());

        String middleName = ((TextView) view.findViewById(R.id.health_insurance_policy_middle_name_holder)).getText().toString();
        demographicInsurancePayloadDTO.setPolicyMiddleNameHolder(middleName.trim());

        String lastName = ((TextView) view.findViewById(R.id.health_insurance_policy_last_name_holder)).getText().toString();
        demographicInsurancePayloadDTO.setPolicyLastNameHolder(lastName.trim());

        if (!isDataHolderSelf) {
            String dob = ((TextView) view.findViewById(R.id.health_insurance_policy_birth_date_holder)).getText().toString();
            demographicInsurancePayloadDTO.setPolicyDateOfBirthHolder(DateUtil.getInstance()
                    .setDateRaw(dob.trim(), true).toStringWithFormatYyyyDashMmDashDd());

            demographicInsurancePayloadDTO.setPolicyGenderHolder(selectedGenderOption.getName());
        }

        setupImageBase64();

        List<DemographicInsurancePhotoDTO> photos = demographicInsurancePayloadDTO.getInsurancePhotos();
        if (frontInsurancePhotoDTO != null || backInsurancePhotoDTO != null) {
            //Log new Insurance Doc
            CheckinFlowCallback checkinFlowCallback = null;
            if (callback instanceof CheckinFlowCallback) {
                checkinFlowCallback = (CheckinFlowCallback) callback;
            }
            if (checkinFlowCallback != null) {
                String[] params = {getString(R.string.param_is_checkin),
                        getString(R.string.param_practice_id),
                        getString(R.string.param_provider_id),
                        getString(R.string.param_location_id),
                        getString(R.string.param_appointment_id)
                };
                Object[] values = {isCheckin,
                        checkinFlowCallback.getAppointment().getMetadata().getPracticeId(),
                        checkinFlowCallback.getAppointment().getPayload().getProvider().getGuid(),
                        checkinFlowCallback.getAppointment().getPayload().getLocation().getGuid(),
                        checkinFlowCallback.getAppointment().getMetadata().getAppointmentId()
                };
                MixPanelUtil.logEvent(getString(R.string.event_add_insurance_doc), params, values);
            } else {
                MixPanelUtil.logEvent(getString(R.string.event_add_insurance_doc),
                        getString(R.string.param_is_checkin), isCheckin);
            }
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

    /**
     * Validate all the fields in the form and display errors
     * @param userInteraction User interaction
     * @return Boolean for all input data good
     */
    private boolean validateForm(boolean userInteraction) {
        View view = getView();
        if (view == null) {
            return false;
        }

        boolean error = false;

        if (validateError(StringUtil.isNullOrEmpty(selectedTypeOption.getName()),
                userInteraction, view.findViewById(R.id.healthInsuranceTypeLayout),
                (TextInputLayout)view.findViewById(R.id.healthInsuranceTypeInputLayout))) {
            error = true;
        }
        if (validateError(StringUtil.isNullOrEmpty(selectedProviderOption.getName()),
                userInteraction, isProviderOther ? view.findViewById(R.id.otherProviderLayout) : view.findViewById(R.id.healthInsuranceProvidersLayout),
                isProviderOther ? (TextInputLayout) view.findViewById(R.id.otherProviderInputLayout) :
                        (TextInputLayout) view.findViewById(R.id.healthInsuranceProvidersInputLayout))) {
            error = true;
        }
        if (!isDataHolderSelf) {
            String firstName = ((TextView) view.findViewById(R.id.health_insurance_policy_first_name_holder)).getText().toString().trim();
            if (validateError(StringUtil.isNullOrEmpty(firstName.trim()), userInteraction,
                    view.findViewById(R.id.healthInsuranceFirstNameContainer),
                    (TextInputLayout) view.findViewById(R.id.health_insurance_policy_first_name_holder_layout))) {
                error = true;
            }
            String lastName = ((TextView) view.findViewById(R.id.health_insurance_policy_last_name_holder)).getText().toString().trim();
            if (validateError(StringUtil.isNullOrEmpty(lastName.trim()), userInteraction,
                    view.findViewById(R.id.healthInsuranceLastNameContainer),
                    (TextInputLayout) view.findViewById(R.id.health_insurance_policy_last_name_holder_layout))) {
                error = true;
            }
            String dob = ((TextView) view.findViewById(R.id.health_insurance_policy_birth_date_holder)).getText().toString().trim();
            if (validateError(StringUtil.isNullOrEmpty(dob), userInteraction, view.findViewById(R.id.dateOfBirthContainer),
                    (TextInputLayout) view.findViewById(R.id.health_insurance_policy_birth_date_holder_layout))) {
                error = true;
            }
            if (validateError((StringUtil.isNullOrEmpty(selectedGenderOption.getName()) ||
                    "Choose".equals(selectedGenderOption.getName())),
                    userInteraction, view.findViewById(R.id.healthInsuranceGenderLayout),
                    (TextInputLayout) view.findViewById(R.id.healthInsuranceGenderInputLayout))) {
                error = true;
            }
            TextInputLayout policyBirthDateHolderInput = view.findViewById(R.id.health_insurance_policy_birth_date_holder_layout);
            if (!StringUtil.isNullOrEmpty(dob.trim())) {
                String dateValidationResult = DateUtil
                        .getDateOfBirthValidationResultMessage(dob.trim());
                if (dateValidationResult != null) {
                    if (userInteraction) {
                        policyBirthDateHolderInput.setErrorEnabled(true);
                        policyBirthDateHolderInput.setError(dateValidationResult);
                    }
                    error = true;
                } else {
                    policyBirthDateHolderInput.setError(null);
                    policyBirthDateHolderInput.setErrorEnabled(false);
                }
            }
        }
        if (error) { return false; }
        return true;
    }

    /**
     * Validate an individual field and set the error on the field
     * @param inputDataBad Boolean for input data verification check
     * @param userInteraction User interaction
     * @param container Container for error displays
     * @return Boolean error found
     */
    public boolean validateError(boolean inputDataBad, boolean userInteraction, View container, TextInputLayout inputLayout) {
        if (inputDataBad) {
            if (userInteraction) {
                showErrorViews(true, (ViewGroup) container);
                inputLayout.setErrorEnabled(true);
                inputLayout.setError(Label.getLabel("demographics_required_field_msg"));
            }
            return true;
        } else {
            showErrorViews(false, (ViewGroup) container);
            inputLayout.setError(null);
            inputLayout.setErrorEnabled(false);
        }
        return false;
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
        TextView titleTextView = customView.findViewById(R.id.title_view);
        titleTextView.setText(title);
        titleTextView.setVisibility(View.VISIBLE);

        // create the adapter
        ListView listView = customView.findViewById(R.id.dialoglist);
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

    /**
     * First time setup of individual field that uses ids for all UI components
     * instead of the component.
     * @param view View
     * @param keyName Key name.
     * @param demographicsField DemographicsField
     * @param containerLayout ContainerLayout id
     * @param inputLayoutId InputLayout id
     * @param editTextId EditText id
     * @param requiredViewId RequiredView id
     * @param demographicsOption DemographicsOption
     * @param optionDialogTitle Title for dialog
     * @param disableField Boolean for disabling input field
     */
    private void setUpDemographicField(View view, String keyName, DemographicsField demographicsField,
                                       Integer containerLayout, int inputLayoutId, int editTextId, Integer requiredViewId,
                                       DemographicsOption demographicsOption, String optionDialogTitle, boolean disableField) {
        if (demographicsField == null) {
            demographicsField = new DemographicsField();
        }
        TextInputLayout inputLayout = view.findViewById(inputLayoutId);
        EditText editText = view.findViewById(editTextId);
        setUpDemographicField(view, keyName, demographicsField.isDisplayed(),
                demographicsField.getOptions(), containerLayout, inputLayout, editText,
                requiredViewId, demographicsOption, optionDialogTitle, null, disableField);
    }

    private void setUpDemographicField(View view, String keyName, DemographicsField demographicsField,
                                       Integer containerLayout, int inputLayoutId, int editTextId, Integer requiredViewId,
                                       DemographicsOption demographicsOption, String optionDialogTitle) {
        if (demographicsField == null) {
            demographicsField = new DemographicsField();
        }
        TextInputLayout inputLayout = view.findViewById(inputLayoutId);
        EditText editText = view.findViewById(editTextId);
        setUpDemographicField(view, keyName, demographicsField.isDisplayed(),
                demographicsField.getOptions(), containerLayout, inputLayout, editText,
                requiredViewId, demographicsOption, optionDialogTitle, null, false);

    }

    /**
     * First time setup of the individual field
     * @param view View
     * @param keyName Key name.
     * @param displayed Boolean to display the view or hide.
     * @param options Pass in if there is multiple options for field, ex: Input dropdown with multiple options.
     * @param containerLayout ContainerLayout
     * @param inputLayout InputLayout
     * @param editText EditText
     * @param requiredViewId View of required label, used for removing and adding view when typed in field.
     * @param demographicsOption DemographicsOption
     * @param optionDialogTitle Title of dialog
     * @param requiredListener OnOptionSelectedListener.
     * @param disableField Boolean for disabling input field
     */
    private void setUpDemographicField(View view, String keyName, boolean displayed,
                                       List<? extends DemographicsOption> options,
                                       Integer containerLayout, TextInputLayout inputLayout, EditText editText, Integer requiredViewId,
                                       DemographicsOption demographicsOption, String optionDialogTitle,
                                       OnOptionSelectedListener requiredListener, boolean disableField) {
        if (containerLayout != null) {
            view.findViewById(containerLayout).setVisibility(displayed ? View.VISIBLE : View.GONE);
        }
        if (disableField) {
            editText.setEnabled(false);
        }
        editText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(inputLayout, null));
        if (demographicsOption != null ){//&& !StringUtil.isNullOrEmpty(demographicsOption.getName())) {
            initSelectableInput(editText, demographicsOption, keyName, (List<DemographicsOption>) options);
        } else {
            editText.setText(keyName);
        }
        editText.getOnFocusChangeListener().onFocusChange(editText,
                !StringUtil.isNullOrEmpty(editText.getText().toString().trim()));
        if (demographicsOption != null) {
            editText.setOnClickListener(getEditTextClickListener(options, inputLayout, editText,
                    demographicsOption, optionDialogTitle, requiredListener));
            demographicsOption.setName(editText.getText().toString());
            demographicsOption.setLabel(editText.getText().toString());
        }
        if (requiredViewId != null) {
            View requiredView = view.findViewById(requiredViewId);
            requiredView.setVisibility(StringUtil.isNullOrEmpty(keyName) ? View.VISIBLE : View.GONE);
            editText.addTextChangedListener(getRequiredViewTextWatcher(requiredView));
        }
    }

    protected void initSelectableInput(TextView textView, DemographicsOption storeOption,
                                       String storedName, List<DemographicsOption> options) {
        String key = storeOption.getName();
        if (StringUtil.isNullOrEmpty(key)) {
            key = storedName;
        }
        storeOption = getOptionByKey(options, key, storeOption);
        if (!StringUtil.isNullOrEmpty(storedName)) {
            textView.setText(storeOption.getLabel());
        }
    }

    private DemographicsOption getOptionByKey(List<DemographicsOption> options,
                                              String name,
                                              DemographicsOption storeOption) {
        for (DemographicsOption option : options) {
            if (option.getName().equals(name)) {
                storeOption.setName(option.getName());
                storeOption.setLabel(option.getLabel());
                return storeOption;
            }
        }
        storeOption.setName(name);
        storeOption.setLabel(name);
        return storeOption;
    }


    private View.OnClickListener getEditTextClickListener(List<? extends DemographicsOption> options,
                                                          final TextInputLayout inputLayout,
                                                          final EditText editText,
                                                          final DemographicsOption demographicsOption,
                                                          final String dialogTitle,
                                                          final OnOptionSelectedListener optionsListener) {
        return getSelectOptionsListener(options,
                new OnOptionSelectedListener() {
                    @Override
                    public void onOptionSelected(DemographicsOption option) {
                        demographicsOption.setLabel(option.getLabel());
                        demographicsOption.setName(option.getName());
                        demographicsOption.setId(option.getId());

                        editText.setText(option.getLabel());
                        editText.getOnFocusChangeListener()
                                .onFocusChange(editText, !StringUtil.isNullOrEmpty(editText.getText().toString()));
                        inputLayout.setError(null);
                        inputLayout.setErrorEnabled(false);
                        checkIfEnableButton();

                        if (optionsListener != null) {
                            optionsListener.onOptionSelected(option);
                        }
                    }
                },
                dialogTitle);
    }

    /**
     * Text watcher for views that contain required label, add/removes required label and
     * checks if button should be enabled
     * @param requiredView Required view
     * @return TextWatcher
     */
    private TextWatcher getRequiredViewTextWatcher(final View requiredView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                requiredView.setVisibility(StringUtil.isNullOrEmpty(editable.toString()) ? View.VISIBLE : View.GONE);
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
                        } else if (tag.equals(TAG_ERROR_COLOR)) {
                            if (view instanceof TextInputLayout) {
                                EditText editText = ((TextInputLayout) view).getEditText();
                                editText.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.remove_red), PorterDuff.Mode.SRC_IN);
                            }
                        }
                        Rect rect = new Rect();
                        view.getGlobalVisibleRect(rect);
                        if (rect.top > 0) {
                            scrollView.scrollBy(0, rect.top - scrollView.getTop());
                        } else {
                            scrollView.scrollBy(0, rect.top);
                        }
                    } else {
                        if (tag.equals(TAG_ERROR_SHOW_GONE)) {
                            view.setVisibility(View.GONE);
                        } else if (tag.equals(TAG_ERROR_SHOW_INV)) {
                            view.setVisibility(View.INVISIBLE);
                        } else if (tag.equals(TAG_ERROR_HIDE_GONE) || tag.equals(TAG_ERROR_HIDE_INV)) {
                            view.setVisibility(View.VISIBLE);
                        } else if (tag.equals(TAG_ERROR_COLOR)) {
                            view.setSelected(false);
                        }
                    }
                }
            }

        }

    }

    private View.OnClickListener noInsurance = new View.OnClickListener() {
        @Override
        public void onClick(View saveChanges) {
            closeDialog();
        }
    };

}