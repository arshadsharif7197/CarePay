package com.carecloud.carepaylibray.demographics.fragments;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataInsuranceOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.DemographicMetadataPropertiesInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.marcok.stepprogressbar.StepProgressBar;

import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.BACK_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.FRONT_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_BACK_DTO;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_FRONT_DTO;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_HAS_BACK;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.KEY_HAS_FRONT;

import java.util.Arrays;
import java.util.List;

public class InsuranceEditDialog extends BaseDialogFragment implements MediaViewInterface {

    public static final String EDITED_INDEX = "EditedIndex";
    public static final String IS_PATIENT_MODE = "IsPatientMode";
    public static final int NEW_INSURANCE = -1;
    private static final int PROVIDERS = 0;
    private static final int PLANS = 1;
    private static final int TYPES = 2;

    private DemographicDTO demographicDTO;
    private MediaScannerPresenter mediaScannerPresenter;
    private DocumentScannerAdapter documentScannerAdapter;

    private boolean hasFrontImage = false;
    private boolean hasBackImage = false;

    private TextInputLayout cardNumberInput;
    private TextInputLayout groupNumberInput;
    private EditText cardNumber;
    private EditText groupNumber;
    private Button saveInsuranceButton;

    private CarePayTextView selectedProviderTextView;
    private CarePayTextView selectedPlanTextView;
    private CarePayTextView selectedTypeTextView;

    private boolean isCardNumberEmpty;
    private boolean isGroupNumberEmpty;
    private boolean hadInsurance;
    private boolean isPatientMode;

    private DemographicInsurancePhotoDTO frontInsurancePhotoDTO;
    private DemographicInsurancePhotoDTO backInsurancePhotoDTO;

    private InsuranceEditDialogListener callback;
    private CarePayCameraReady carePayCameraReady;
    private int editedIndex;

    private String selectedProvider;
    private String selectedPlan;
    private String selectedType;

    private EditText otherProviderEditText;

    private List<MetadataInsuranceOptionDTO> providerList;
    private List<MetadataOptionDTO> typeList;


    public interface InsuranceEditDialogListener {
        void onInsuranceEdited(DemographicDTO demographicDTO, boolean proceed);

        void goOneStepBack();
    }

    /**
     * Creates a InsuranceEditDialog fragment
     *
     * @param demographicDTO Demographic DTO
     * @param editedIndex    index of the insurance being modified
     */
    public static InsuranceEditDialog newInstance(DemographicDTO demographicDTO,
                                                  Integer editedIndex,
                                                  boolean isPatientMode) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        args.putInt(EDITED_INDEX, editedIndex == null ? NEW_INSURANCE : editedIndex);
        args.putBoolean(IS_PATIENT_MODE, isPatientMode);
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

        try {
            if (context instanceof DemographicsView) {
                carePayCameraReady = ((DemographicsView) context).getPresenter();
            } else {
                carePayCameraReady = (CarePayCameraReady) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CarePayCameraReady");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null || carePayCameraReady == null) {
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
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, arguments);
        if(savedInstanceState != null){
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
    public void onSaveInstanceState(Bundle icicle){
        if(frontInsurancePhotoDTO != null) {
            icicle.putString(KEY_FRONT_DTO, DtoHelper.getStringDTO(frontInsurancePhotoDTO));
        }
        if(backInsurancePhotoDTO != null) {
            icicle.putString(KEY_BACK_DTO, DtoHelper.getStringDTO(backInsurancePhotoDTO));
        }
        icicle.putBoolean(KEY_HAS_FRONT, hasFrontImage);
        icicle.putBoolean(KEY_HAS_BACK, hasBackImage);
        super.onSaveInstanceState(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        hadInsurance = hasInsurance();
        if (getDialog() != null || (hadInsurance && !isPatientMode)) {
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
        stepProgressBar.setCumulativeDots(true);
        stepProgressBar.setNumDots(5);
        stepProgressBar.setCurrentProgressDot(4);

        View child = inflater.inflate(R.layout.add_edit_insurance_view, null);
        ((ViewGroup) view.findViewById(R.id.checkinDemographicsContentLayout)).addView(child);

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
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                if (callback != null) {
                    callback.goOneStepBack();
                }
            }
        });

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

        selectedProviderTextView = (CarePayTextView) findViewById(R.id.health_insurance_providers);
        selectedPlanTextView = (CarePayTextView) findViewById(R.id.health_insurance_choose_plans);
        selectedTypeTextView = (CarePayTextView) findViewById(R.id.health_insurance_types);

        cardNumberInput = (TextInputLayout) findViewById(R.id.health_insurance_card_number_layout);
        cardNumber = (EditText) findViewById(R.id.health_insurance_card_number);
        otherProviderEditText = (EditText) findViewById(R.id.otherProviderEditText);

        groupNumberInput = (TextInputLayout) findViewById(R.id.health_insurance_group_number_layout);
        groupNumber = (EditText) findViewById(R.id.health_insurance_group_number);

        setTextListeners();
        setChangeFocusListeners();
        setActionListeners();


        if (getDialog() != null || (hadInsurance && !isPatientMode)) {
            saveInsuranceButton = (Button) findViewById(R.id.save_insurance_changes);
        } else {
            saveInsuranceButton = (Button) findViewById(R.id.checkinDemographicsNextButton);
        }

        getInsuranceDropdownLists();

        if (editedIndex == NEW_INSURANCE) {
            ((Button) findViewById(R.id.demogrDocsFrontScanButton)).setText(
                    Label.getLabel("demographics_insurance_take_front_photo"));
            ((Button) findViewById(R.id.demogrDocsBackScanButton)).setText(
                    Label.getLabel("demographics_insurance_take_back_photo"));

            selectedTypeTextView.setText(Label.getLabel("demographics_choose"));

            if (hasInsurance() && (getDialog() != null || !isPatientMode)) {
                disappearViewById(R.id.remove_insurance_entry);
                ((CarePayTextView) findViewById(R.id.toolbar_title)).setText(
                        Label.getLabel("practice_checkin_demogr_ins_add_new_button_label"));
            } else {
                showViewById(R.id.check_in_demographics_left_button);
                findViewById(R.id.check_in_demographics_left_button).setOnClickListener(getNoInsuranceListener());
                saveInsuranceButton.setText(Label.getLabel("practice_checkin_demogr_ins_add_new_button_label"));
            }
        } else {
            DemographicInsurancePayloadDTO demographicInsurancePayload = demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(editedIndex);
            selectedProvider = demographicInsurancePayload.getInsuranceProvider();
            selectedProviderTextView.setText(selectedProvider);
            getInsurancePlans(findInsuranceProvider(selectedProvider));

            selectedPlan = demographicInsurancePayload.getInsurancePlan();
            selectedPlanTextView.setText(selectedPlan);
            selectedPlanTextView.setVisibility(View.VISIBLE);
            ((CarePayTextView) findViewById(R.id.toolbar_title)).setText(selectedProvider + (selectedPlan != null ? " " + selectedPlan : ""));
            findViewById(R.id.health_insurance_plans).setVisibility(View.GONE);

            selectedType = demographicInsurancePayload.getInsuranceType();
            selectedTypeTextView.setText(selectedType);

            cardNumber.setText(demographicInsurancePayload.getInsuranceMemberId());
            if (!StringUtil.isNullOrEmpty(demographicInsurancePayload.getInsuranceMemberId()) && cardNumber.getOnFocusChangeListener() != null) {
                cardNumber.getOnFocusChangeListener().onFocusChange(cardNumber, false);
            }
            groupNumber.setText(demographicInsurancePayload.getInsuranceGroupId());
            if (!StringUtil.isNullOrEmpty(demographicInsurancePayload.getInsuranceGroupId()) && groupNumber.getOnFocusChangeListener() != null) {
                groupNumber.getOnFocusChangeListener().onFocusChange(groupNumber, false);
            }

            findViewById(R.id.remove_insurance_entry).setOnClickListener(removeButtonListener);
        }

        initializeScanArea(view);

        saveInsuranceButton.setOnClickListener(saveButtonListener);
        // TO-DO: Need to know what fields are required
        validateForm();
    }

    private View.OnClickListener removeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View saveChanges) {
            if (editedIndex != NEW_INSURANCE) {
                demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(editedIndex).setDeleted(true);
            }
            closeDialog();
        }
    };

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
            final boolean hasInsurance = hasInsurance();

            DemographicInsurancePayloadDTO demographicInsurancePayloadDTO;
            if (editedIndex == NEW_INSURANCE) {
                demographicInsurancePayloadDTO = new DemographicInsurancePayloadDTO();
                demographicDTO.getPayload().getDemographics().getPayload().getInsurances().add(demographicInsurancePayloadDTO);
            } else {
                demographicInsurancePayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(editedIndex);
            }

            demographicInsurancePayloadDTO.setInsuranceProvider(selectedProvider);
            demographicInsurancePayloadDTO.setInsurancePlan(selectedPlan);
            demographicInsurancePayloadDTO.setInsuranceType(selectedType != null ? selectedType : typeList.get(0).getLabel());

            demographicInsurancePayloadDTO.setInsuranceMemberId(cardNumber.getText().toString());
            demographicInsurancePayloadDTO.setInsuranceGroupId(groupNumber.getText().toString());

            setupImageBase64();

            List<DemographicInsurancePhotoDTO> photos = demographicInsurancePayloadDTO.getInsurancePhotos();
            if (frontInsurancePhotoDTO != null) {
                photos.add(frontInsurancePhotoDTO);
            }

            if (backInsurancePhotoDTO != null) {
                photos.add(backInsurancePhotoDTO);
            }

            if (hasInsurance && getDialog() != null
                    || getApplicationMode().getApplicationType().equals(ApplicationMode.ApplicationType.PATIENT)) {
                closeDialog();
            } else {
                callback.onInsuranceEdited(demographicDTO, true);
            }
        }
    };


    private void closeDialog() {
        dismiss();
        if (callback != null) {
            callback.onInsuranceEdited(demographicDTO, false);

            if (callback != null && (!hadInsurance || !isPatientMode)) {
                callback.goOneStepBack();
            }
        }
    }

    private void initializeScanArea(View view) {
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this);
        documentScannerAdapter = new DocumentScannerAdapter(getContext(), view, mediaScannerPresenter, getApplicationMode().getApplicationType());


        View lastCaptureView = view.findViewById(MediaScannerPresenter.captureViewId);
        if (lastCaptureView != null) {
            mediaScannerPresenter.setCaptureView(lastCaptureView);
        }

        if(hasFrontImage || hasBackImage){
            DemographicInsurancePayloadDTO payloadDTO = new DemographicInsurancePayloadDTO();
            if(frontInsurancePhotoDTO != null){
                payloadDTO.getInsurancePhotos().add(frontInsurancePhotoDTO);
                documentScannerAdapter.setFrontRescan();
            }
            if(backInsurancePhotoDTO != null){
                payloadDTO.getInsurancePhotos().add(backInsurancePhotoDTO);
                documentScannerAdapter.setBackRescan();
            }
            documentScannerAdapter.setInsuranceDocumentsFromData(payloadDTO);
        }else if (editedIndex != NEW_INSURANCE) {
            documentScannerAdapter.setInsuranceDocumentsFromData(demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(editedIndex));
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
            DemographicInsurancePhotoDTO photoDTO = new DemographicInsurancePhotoDTO();
            photoDTO.setDelete(false);
            photoDTO.setInsurancePhoto(filePath);
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
        validateForm();
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
        String filePath;

        if (frontInsurancePhotoDTO != null) {
            filePath = frontInsurancePhotoDTO.getInsurancePhoto();
            frontInsurancePhotoDTO.setInsurancePhoto(documentScannerAdapter.getBase64(filePath));
        }
        if (backInsurancePhotoDTO != null) {
            filePath = backInsurancePhotoDTO.getInsurancePhoto();
            backInsurancePhotoDTO.setInsurancePhoto(documentScannerAdapter.getBase64(filePath));
        }

        //lets clear the existing insurance pics that will be replaced
        if (editedIndex != NEW_INSURANCE) {
            List<DemographicInsurancePhotoDTO> photoDTOList = demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(editedIndex).getInsurancePhotos();
            for (DemographicInsurancePhotoDTO photoDTO : photoDTOList) {
                if ((photoDTO.getPage() == FRONT_PIC && hasFrontImage) || (photoDTO.getPage() == BACK_PIC && hasBackImage)) {
                    photoDTO.setDelete(true);
                }
            }
        }
    }


    private void getInsuranceDropdownLists() {
        DemographicMetadataEntityInsurancesDTO insurancesMetaDTO = demographicDTO.getMetadata().getNewDataModel().getDemographic().getInsurances();
        if (insurancesMetaDTO != null) {
            DemographicMetadataPropertiesInsuranceDTO properties = insurancesMetaDTO.getProperties()
                    .getItems().getInsurance().getProperties();

            // Providers
            providerList = properties.getInsuranceProvider().getOptions();
            final String[] providers = new String[providerList.size()];
            for (int i = 0; i < providerList.size(); i++) {
                providers[i] = providerList.get(i).getLabel();
            }
            selectedProviderTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View providerView) {
                    showAlertDialogWithListView(providers, "Choose Provider", "Cancel", PROVIDERS);
                }
            });

            // Types
            typeList = properties.getInsuranceType().getOptions();
            final String[] types = new String[typeList.size()];
            for (int i = 0; i < typeList.size(); i++) {
                types[i] = typeList.get(i).getLabel();
            }
            selectedTypeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View providerView) {
                    showAlertDialogWithListView(types, "Choose Type", "Cancel", TYPES);
                }
            });
        }
    }

    private void getInsurancePlans(MetadataInsuranceOptionDTO selectedInsurance) {
        if (selectedInsurance == null) {
            return;
        }

        // Plans
        List<MetadataOptionDTO> planList = selectedInsurance.getPayerPlans();
        final String[] plans = new String[planList.size()];
        for (int i = 0; i < planList.size(); i++) {
            plans[i] = planList.get(i).getLabel();
        }
        selectedPlanTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View providerView) {
                showAlertDialogWithListView(plans, "Choose Plan", "Cancel", PLANS);
            }
        });

    }

    private MetadataInsuranceOptionDTO findInsuranceProvider(String name) {
        if (name == null) {
            return null;
        }
        for (MetadataInsuranceOptionDTO provider : providerList) {
            if (provider.getLabel().toLowerCase().equals(name.toLowerCase()) ||
                    provider.getName().toLowerCase().equals(name.toLowerCase())) {
                return provider;
            }
        }
        return null;
    }

    @SuppressLint("InflateParams")
    private void showAlertDialogWithListView(final String[] dataArray, String title,
                                             String cancelLabel, final int index) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(title);

        dialog.setNegativeButton(cancelLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int listener) {
                dialogInterface.dismiss();
            }
        });

        View customView = LayoutInflater.from(getContext()).inflate(R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter alertAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(dataArray));
        listView.setAdapter(alertAdapter);
        dialog.setView(customView);

        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long listener) {
                switch (index) {
                    case PROVIDERS:
                        findViewById(R.id.health_insurance_plans).setVisibility(View.GONE);

                        if (!dataArray[position].equals(selectedProvider)) {
                            selectedPlan = null;
                            selectedPlanTextView.setText(Label.getLabel("demographics_choose"));
                        }

                        if ("other".equals(dataArray[position].toLowerCase())) {
                            otherProviderEditText.setVisibility(View.VISIBLE);
                            otherProviderEditText.requestFocus();
                            selectedProvider = null;
                        } else {
                            otherProviderEditText.setVisibility(View.GONE);
                            selectedProvider = dataArray[position];
                        }
                        selectedProviderTextView.setText(dataArray[position]);
                        getInsurancePlans(providerList.get(position));
                        selectedPlanTextView.setVisibility(View.VISIBLE);
                        break;

                    case PLANS:
                        selectedPlan = dataArray[position];
                        selectedPlanTextView.setText(selectedPlan);
                        break;

                    case TYPES:
                        selectedType = dataArray[position];
                        selectedTypeTextView.setText(selectedType);
                        break;
                    default:
                        break;
                }
                alert.dismiss();
                validateForm();
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
                validateForm();
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
                validateForm();
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
                if (editable.length() > 0 || selectedProvider == null) {
                    selectedProvider = editable.toString();
                }
                validateForm();
            }
        });

    }

    private void setChangeFocusListeners() {
        cardNumber.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(cardNumberInput, null));
        groupNumber.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(groupNumberInput, null));
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

    private void validateForm() {
        boolean isValid = true;

        if (StringUtil.isNullOrEmpty(selectedProvider)) {
            isValid = false;
        }

//        if(StringUtil.isNullOrEmpty(selectedPlan)){
//            isValid = false;
//        }
//
//        if(StringUtil.isNullOrEmpty(selectedType)){
//            isValid = false;
//        }
//
//        if(StringUtil.isNullOrEmpty(cardNumber.getText().toString())){
//            isValid = false;
//        }
//
//        if(StringUtil.isNullOrEmpty(groupNumber.getText().toString())){
//            isValid = false;
//        }

        saveInsuranceButton.setEnabled(isValid);
    }
}