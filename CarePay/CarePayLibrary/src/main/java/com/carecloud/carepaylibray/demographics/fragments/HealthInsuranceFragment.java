package com.carecloud.carepaylibray.demographics.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.adapters.InsuranceLineItemsListAdapter;
import com.carecloud.carepaylibray.demographics.dialog.InsuranceEditDialog;
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
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.PermissionsUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jorge on 07/02/17.
 */
public class HealthInsuranceFragment extends CheckInDemographicsBaseFragment implements
        InsuranceLineItemsListAdapter.OnInsuranceEditClickListener,
        InsuranceEditDialog.OnSaveChangesListener {

    private List<DemographicInsurancePayloadDTO> insurancePayloadDTOs;
    private DemographicDTO demographicDTO;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO globalLabelsMetaDTO;

    private CarePayTextView selectedProvider;
    private CarePayTextView selectedPlan;
    private CarePayTextView selectedType;

    private TextInputLayout cardNumberInput;
    private TextInputLayout groupNumberInput;
    private EditText cardNumber;
    private EditText groupNumber;

    private Button addAnotherButton;
    private Button scanFrontButton;
    private Button scanBackButton;

    private ImageCaptureHelper scannerFront;
    private ImageCaptureHelper scannerBack;
    protected ImageCaptureHelper imageCaptureHelper;
    protected ImageCaptureHelper.CameraType cameraType;

    private boolean isCardNumberEmpty;
    private boolean isGroupNumberEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());

        initDTOs();
        initActiveSection(view);
        initLabels(view);
        initializePhotos();

        checkIfEnableButton(view);
        SystemUtil.hideSoftKeyboard(getActivity());

        stepProgressBar.setCurrentProgressDot(4);
        checkInNavListener.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 5);
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
        List<DemographicInsurancePayloadDTO> insuranceList = demographicDTO.getPayload()
                .getDemographics().getPayload().getInsurances();

        if (insuranceList.isEmpty()) {
            insuranceList = new ArrayList<>();
        }

        DemographicInsurancePayloadDTO insuranceDTO = new DemographicInsurancePayloadDTO();
        insuranceDTO.setInsuranceProvider(selectedProvider.getText().toString());
        insuranceDTO.setInsurancePlan(selectedPlan.getText().toString());
        insuranceDTO.setInsuranceType(selectedType.getText().toString());
        insuranceDTO.setInsuranceMemberId(cardNumber.getText().toString());
        insuranceDTO.setInsuranceGroupId(groupNumber.getText().toString());

        insuranceList.add(insuranceDTO);
        demographicDTO.getPayload().getDemographics().getPayload().setInsurances(insuranceList);
        return demographicDTO;
    }

    private void initLabels(View view) {
        // Set Labels
        ((TextView) view.findViewById(R.id.health_insurance_provider_label)).setText(
                Label.getLabel("demographics_documents_title_select_provider"));
        ((TextView) view.findViewById(R.id.health_insurance_plan_label)).setText(
                Label.getLabel("demographics_documents_title_select_plan"));
        ((TextView) view.findViewById(R.id.health_insurance_type_label)).setText(
                Label.getLabel("demographics_insurance_type_label"));
        ((TextView) view.findViewById(R.id.health_insurance_plans)).setText(
                Label.getLabel("demographics_documents_choose_plan"));

        selectedProvider.setText(Label.getLabel("demographics_choose"));
        selectedPlan.setText(Label.getLabel("demographics_choose"));
        selectedType.setText(Label.getLabel("demographics_choose"));

        scanFrontButton.setText(Label.getLabel("demographics_insurance_take_front_photo"));
        scanBackButton.setText(Label.getLabel("demographics_insurance_take_back_photo"));
        addAnotherButton.setText(Label.getLabel("practice_checkin_demogr_ins_add_another"));

        ((Button) view.findViewById(R.id.health_insurance_dont_have_button)).setText(
                Label.getLabel("practice_checkin_demogr_ins_dont_have_one_button_label"));
        ((Button) view.findViewById(R.id.health_insurance_add_new_button)).setText(
                Label.getLabel("practice_checkin_demogr_ins_add_new_button_label"));
    }

    /**
     * enable or disable sections
     * @param view main view
     */
    public void initActiveSection(final View view) {
        int visibility;
        if (insurancePayloadDTOs != null && !insurancePayloadDTOs.isEmpty()) {
            visibility = View.VISIBLE;
            fillDetailAdapter(view);
        } else {
            visibility = View.GONE;
            view.findViewById(R.id.health_insurance_list_view).setVisibility(View.GONE);
            view.findViewById(R.id.no_health_insurance_view).setVisibility(View.VISIBLE);
        }

        addAnotherButton = (Button) view.findViewById(R.id.health_insurance_add_another);
        addAnotherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View addAnotherButton) {
                InsuranceEditDialog dialog = new InsuranceEditDialog(getActivity(), null, demographicDTO,
                        false, HealthInsuranceFragment.this,
                        getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE
                        );
                dialog.show();
            }
        });

        selectedProvider = (CarePayTextView) view.findViewById(R.id.health_insurance_providers);
        selectedPlan = (CarePayTextView) view.findViewById(R.id.health_insurance_choose_plans);
        selectedType = (CarePayTextView) view.findViewById(R.id.health_insurance_types);

        cardNumberInput = (TextInputLayout) view.findViewById(R.id.health_insurance_card_number_layout);
        cardNumber = (EditText) view.findViewById(R.id.health_insurance_card_number);

        String cardNumberHint = Label.getLabel("demographics_insurance_card_number");
        cardNumberInput.setTag(cardNumberHint);
        cardNumber.setHint(cardNumberHint);
        cardNumber.setTag(cardNumberInput);

        groupNumberInput = (TextInputLayout) view.findViewById(R.id.health_insurance_group_number_layout);
        groupNumber = (EditText) view.findViewById(R.id.health_insurance_group_number);

        String groupNumberHint = Label.getLabel("demographics_insurance_group_number");
        groupNumberInput.setTag(groupNumberHint);
        groupNumber.setHint(groupNumberHint);
        groupNumber.setTag(groupNumberInput);

        ImageView imageFront = (ImageView) view.findViewById(R.id.health_insurance_front_photo);
        scanFrontButton = (Button) view.findViewById(R.id.take_front_photo_button);
        scannerFront = new ImageCaptureHelper(getActivity(), imageFront, globalLabelsMetaDTO);
        scanFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View frontButtonView) {
                selectImage(scannerFront, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
            }
        });

        ImageView imageBack = (ImageView) view.findViewById(R.id.health_insurance_back_photo);
        scanBackButton = (Button) view.findViewById(R.id.take_back_photo_button);
        scannerBack = new ImageCaptureHelper(getActivity(), imageBack, globalLabelsMetaDTO);
        scanBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View backButtonView) {
                selectImage(scannerBack, ImageCaptureHelper.CameraType.CUSTOM_CAMERA);
            }
        });

        Button doNotHaveOne = (Button) view.findViewById(R.id.health_insurance_dont_have_button);
        doNotHaveOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                DemographicDTO demographicDTO = updateDemographicDTO(view);
                openNextFragment(demographicDTO, true);
            }
        });

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
                    showAlertDialogWithListView(view, providers, "Choose Provider", "Cancel", 0);
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
                    showAlertDialogWithListView(view, plans, "Choose Plan", "Cancel", 1);
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
                    showAlertDialogWithListView(view, types, "Choose Type", "Cancel", 2);
                }
            });
        }

        setTextListeners();
        setChangeFocusListeners();
        setActionListeners();

        cardNumber.clearFocus();
        groupNumber.clearFocus();

        setHeaderTitle(Label.getLabel("demographics_insurance_label"),
                Label.getLabel("demographics_health_insurance_heading"),
                Label.getLabel("demographics_health_insurance_subheading"),
                view);

        initNextButton(null, view, visibility);

        Button nextButton = (Button) view.findViewById(R.id.checkinDemographicsNextButton);
        nextButton.setText(Label.getLabel("demographics_review_go_to_consent"));
    }

    private void selectImage(final ImageCaptureHelper imageCaptureHelper, final ImageCaptureHelper.CameraType cameraType) {
        this.imageCaptureHelper = imageCaptureHelper;
        this.cameraType = cameraType;
        // create the chooser dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(ImageCaptureHelper.chooseActionDlgTitle);
        if (cameraType == ImageCaptureHelper.CameraType.DEFAULT_CAMERA) {
            builder.setItems(ImageCaptureHelper.chooseActionDlOptions, dialogOnClickListener);
        } else {
            builder.setItems(ImageCaptureHelper.chooseActionDocumentDlOptions, dialogDocumentScanOnClickListener);
        }

        builder.show();
    }

    private DialogInterface.OnClickListener dialogDocumentScanOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            if (item == 0) { // "Take picture" chosen
                imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[0].toString());
                boolean result = PermissionsUtil.checkPermissionCamera(getActivity());
                if (result) {
                    // uncomment when camera activity
                    startActivityForResult(imageCaptureHelper.getCameraIntent(cameraType), ImageCaptureHelper.REQUEST_CAMERA);
                }
            } else if (item == 1) { // "Cancel"
                dialog.dismiss();
            }
        }
    };

    private DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            if (item == 0) { // "Take picture" chosen
                imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[0].toString());
                boolean result = PermissionsUtil.checkPermissionCamera(getActivity());
                if (result) {
                    // uncomment when camera activity
                    startActivityForResult(imageCaptureHelper.getCameraIntent(cameraType), ImageCaptureHelper.REQUEST_CAMERA);
                }
            } else if (item == 1) {  // "Select from Gallery" chosen
                imageCaptureHelper.setCameraType(null);
                imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[1].toString());
                boolean result = PermissionsUtil.checkPermission(getActivity());
                if (result) {
                    startActivityForResult(imageCaptureHelper.galleryIntent(), ImageCaptureHelper.SELECT_FILE);
                }
            } else if (item == 2) { // "Cancel"
                dialog.dismiss();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = null;
            if (requestCode == ImageCaptureHelper.SELECT_FILE) {
                bitmap = imageCaptureHelper.onSelectFromGalleryResult(data, ImageCaptureHelper.ImageShape.RECTANGULAR);
            } else if (requestCode == ImageCaptureHelper.REQUEST_CAMERA) {
                if (cameraType == ImageCaptureHelper.CameraType.CUSTOM_CAMERA) {
                    bitmap = imageCaptureHelper.onCaptureImageResult(ImageCaptureHelper.ImageShape.RECTANGULAR);
                    Log.v(LOG_TAG, "Orientation camera to: " + ImageCaptureHelper.getOrientation());
                } else {
                    bitmap = imageCaptureHelper.onCaptureImageResult(data, ImageCaptureHelper.ImageShape.RECTANGULAR);
                }
            }

//            hasImageChanged = bitmap != null;
            updateModelAndViewsAfterScan(imageCaptureHelper, bitmap);
        }
    }

    private void updateModelAndViewsAfterScan(ImageCaptureHelper scanner, Bitmap bitmap) { // license has been scanned

        List<DemographicInsurancePayloadDTO> insurances = demographicDTO.getPayload()
                .getDemographics().getPayload().getInsurances();

        if (bitmap != null && insurances != null) {
            if (scanner == scannerFront) {
                // change button caption to 'rescan'
                scanFrontButton.setText(Label.getLabel("demographics_insurance_retake_front_photo"));
                // save from image
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicInsurancePhotoDTO frontDTO = insurances.get(0).getInsurancePhotos().get(0);
                frontDTO.setInsurancePhoto(imageAsBase64); // create the image dto
            } else if (scanner == scannerBack) {
                // change button caption to 'rescan'
                scanBackButton.setText(Label.getLabel("demographics_insurance_retake_back_photo"));
                String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
                DemographicInsurancePhotoDTO backDTO = insurances.get(0).getInsurancePhotos().get(1);
                backDTO.setInsurancePhoto(imageAsBase64); // create the image dto
            }
        }
    }

    private void initializePhotos() {
        List<DemographicInsurancePhotoDTO> insurancePhotos;
        List<DemographicInsurancePayloadDTO> insurances = demographicDTO.getPayload()
                .getDemographics().getPayload().getInsurances();

        if (!insurances.isEmpty()) {
            insurancePhotos = insurances.get(0).getInsurancePhotos();
            if (insurancePhotos == null) { // create the list of photos (front and back) if null
                insurancePhotos = new ArrayList<>();
                // create two empty photos DTOs
                DemographicInsurancePhotoDTO frontPhoto = new DemographicInsurancePhotoDTO();
                DemographicInsurancePhotoDTO backPhoto = new DemographicInsurancePhotoDTO();
                frontPhoto.setInsurancePhoto("");
                backPhoto.setInsurancePhoto("");
                insurancePhotos.add(frontPhoto);
                insurancePhotos.add(backPhoto);
                insurances.get(0).setInsurancePhotos(insurancePhotos);
            } else {
                if (insurancePhotos.size() == 0) {
                    // create two empty photos DTOs
                    DemographicInsurancePhotoDTO frontPhoto = new DemographicInsurancePhotoDTO();
                    DemographicInsurancePhotoDTO backPhoto = new DemographicInsurancePhotoDTO();
                    frontPhoto.setInsurancePhoto("");
                    backPhoto.setInsurancePhoto("");
                    insurancePhotos.add(frontPhoto);
                    insurancePhotos.add(backPhoto);
                } else if (insurancePhotos.size() == 1) {
                    DemographicInsurancePhotoDTO backPhoto = new DemographicInsurancePhotoDTO();
                    backPhoto.setInsurancePhoto("");
                    insurancePhotos.add(1, backPhoto); // create the second
                }
            }
        } else {
            insurancePhotos = new ArrayList<>();
            // create two empty photos DTOs
            DemographicInsurancePhotoDTO frontPhoto = new DemographicInsurancePhotoDTO();
            DemographicInsurancePhotoDTO backPhoto = new DemographicInsurancePhotoDTO();
            frontPhoto.setInsurancePhoto("");
            backPhoto.setInsurancePhoto("");
            insurancePhotos.add(frontPhoto);
            insurancePhotos.add(backPhoto);
        }

//        DemographicInsurancePayloadDTO payloadDTO = new DemographicInsurancePayloadDTO();
//        payloadDTO.setInsurancePhotos(insurancePhotos);
//        insurances.add(payloadDTO);
    }

    private void showAlertDialogWithListView(final View parentView, final String[] dataArray, String title,
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
            @SuppressWarnings("deprecation")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long listener) {
                switch (index) {
                    case 0:
                        selectedProvider.setText(dataArray[position]);
                        selectedPlan.setVisibility(View.VISIBLE);
                        parentView.findViewById(R.id.health_insurance_plans).setVisibility(View.GONE);
                        break;

                    case 1:
                        selectedPlan.setText(dataArray[position]);
                        break;

                    case 2:
                        selectedType.setText(dataArray[position]);
                        break;
                    default:
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

    protected void fillDetailAdapter(View view) {
        view.findViewById(R.id.health_insurance_list_view).setVisibility(View.VISIBLE);
        view.findViewById(R.id.no_health_insurance_view).setVisibility(View.GONE);

        RecyclerView detailsListRecyclerView = ((RecyclerView) view.findViewById(R.id.available_health_insurance_list));
        detailsListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        InsuranceLineItemsListAdapter adapter = new InsuranceLineItemsListAdapter(this.getContext(),
                demographicDTO, insurancePayloadDTOs, this);
        detailsListRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onEditInsuranceClicked(DemographicInsurancePayloadDTO lineItem) {
        InsuranceEditDialog dialog = new InsuranceEditDialog(getActivity(), lineItem, demographicDTO,
                true, HealthInsuranceFragment.this,
                getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE
                );
        dialog.show();
    }

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        try {
//            documentCallback = (InsuranceDocumentScannerListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement InsuranceDocumentScannerListener");
//        }
    }

    @Override
    public void onSaveChangesClicked(DemographicDTO newRecord) {
        openNextFragment(newRecord, false);
    }

    public interface InsuranceDocumentScannerListener {
        void navigateToInsuranceDocumentFragment(int index, DemographicInsurancePayloadDTO model);

        void navigateToParentFragment();

        void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model);

        void disableMainButton(boolean isDisabled);
    }
}
