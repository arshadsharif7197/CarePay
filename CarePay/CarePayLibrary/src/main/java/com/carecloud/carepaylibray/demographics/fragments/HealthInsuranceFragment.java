package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jorge on 07/02/17.
 */
public class HealthInsuranceFragment extends CheckInDemographicsBaseFragment implements
        InsuranceLineItemsListAdapter.OnInsuranceEditClickListener,
        InsuranceEditDialog.InsuranceEditDialogListener {

    private static final ImageCaptureHelper.CameraType CAMERA_TYPE = ImageCaptureHelper.CameraType.CUSTOM_CAMERA;
    private static final ImageCaptureHelper.ImageShape IMAGE_SHAPE = ImageCaptureHelper.ImageShape.RECTANGULAR;
    public static final String IS_FRONT_SCAN = "isFrontScan";

    private DemographicDTO demographicDTO;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;

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

    private ImageView healthInsuranceFrontPhotoView;
    private ImageView healthInsuranceBackPhotoView;

    private ImageCaptureHelper scannerFront;
    private ImageCaptureHelper scannerBack;
    protected ImageCaptureHelper imageCaptureHelper;

    private boolean isCardNumberEmpty;
    private boolean isGroupNumberEmpty;
    private boolean isFrontScan;
    private InsuranceEditDialog insuranceEditDialog;

    private InsuranceDocumentScannerListener callback;
    private String frontImageAsBase64;
    private String backImageAsBase64;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (InsuranceDocumentScannerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement InsuranceDocumentScannerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public interface InsuranceDocumentScannerListener {
        void navigateToParentFragment();

        void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model);

        void captureImage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());

        initDTOs();
        initActiveSection(view);
        initLabels();

        checkIfEnableButton(view);
        SystemUtil.hideSoftKeyboard(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeInsuranceList();
    }

    @Override
    public void onResume(){
        super.onResume();
        stepProgressBar.setCurrentProgressDot(4);
        checkInNavListener.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 5);
        checkInNavListener.setCurrentStep(5);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_FRONT_SCAN, isFrontScan);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // keep the fragment and all its data across screen rotation
        setRetainInstance(true);
        if(savedInstanceState != null){
            isFrontScan = savedInstanceState.getBoolean(IS_FRONT_SCAN);
        }
    }

    @Override
    public void onDestroy() {
        onInsuranceEdited();
        super.onDestroy();
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

    private void initLabels() {
        // Set Labels
        selectedProvider.setText(Label.getLabel("demographics_choose"));
        selectedPlan.setText(Label.getLabel("demographics_choose"));
        selectedType.setText(Label.getLabel("demographics_choose"));

        scanFrontButton.setText(Label.getLabel("demographics_insurance_take_front_photo"));
        scanBackButton.setText(Label.getLabel("demographics_insurance_take_back_photo"));
    }

    /**
     * enable or disable sections
     * @param view main view
     */
    public void initActiveSection(final View view) {
        addAnotherButton = (Button) view.findViewById(R.id.health_insurance_add_another);
        addAnotherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View addAnotherButton) {
                insuranceEditDialog = new InsuranceEditDialog(getActivity(), null, demographicDTO,
                        HealthInsuranceFragment.this);
                insuranceEditDialog.show();
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

        healthInsuranceFrontPhotoView = (ImageView) view.findViewById(R.id.health_insurance_front_photo);
        scanFrontButton = (Button) view.findViewById(R.id.take_front_photo_button);
        scannerFront = new ImageCaptureHelper(getActivity(), healthInsuranceFrontPhotoView);
        scanFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View frontButtonView) {
                captureInsuranceFrontImage();
            }
        });

        healthInsuranceBackPhotoView = (ImageView) view.findViewById(R.id.health_insurance_back_photo);
        scanBackButton = (Button) view.findViewById(R.id.take_back_photo_button);
        scannerBack = new ImageCaptureHelper(getActivity(), healthInsuranceBackPhotoView);
        scanBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View backButtonView) {
                captureInsuranceBackImage();
            }
        });

        Button doNotHaveOne = (Button) view.findViewById(R.id.health_insurance_dont_have_button);
        doNotHaveOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                openNextFragment(demographicDTO, true);
            }
        });

        Button addNewButton = (Button) view.findViewById(R.id.health_insurance_add_new_button);
        doNotHaveOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                // TO-DO Add New Stuff
//                DemographicDTO demographicDTO = updateDemographicDTO(view);
//                openNextFragment(demographicDTO, true);
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

        setHeaderTitle(Label.getLabel("demographics_insurance_label"), view);
        initNextButton(null, view, View.GONE);

        Button nextButton = (Button) view.findViewById(R.id.checkinDemographicsNextButton);
        nextButton.setText(Label.getLabel("demographics_review_go_to_consent"));
    }

    private void initializeInsuranceList() {
        if (!demographicDTO.getPayload().getDemographics().getPayload().getInsurances().isEmpty()) {

            setInsuranceListAdapter();
            showViewById(R.id.checkinDemographicsNextButton);
            showViewById(R.id.health_insurance_list_view);
            disappearViewById(R.id.no_health_insurance_view);

        } else {

            disappearViewById(R.id.checkinDemographicsNextButton);
            disappearViewById(R.id.health_insurance_list_view);
            showViewById(R.id.no_health_insurance_view);

        }
    }

    private void selectImage(ImageCaptureHelper imageCaptureHelper) {
        this.imageCaptureHelper = imageCaptureHelper;

        if (callback != null) {
            callback.captureImage();
        }
    }

    private void updateModelAndViewsAfterScan(ImageCaptureHelper scanner, Bitmap bitmap) { // license has been scanned
        if (bitmap == null) {
            return;
        }

        String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
        ImageView target = null;

        if (scanner == scannerFront) {
            // change button caption to 'rescan'
            scanFrontButton.setText(Label.getLabel("demographics_insurance_retake_front_photo"));
            // save from image
            frontImageAsBase64 = imageAsBase64;
            target = healthInsuranceFrontPhotoView;
        } else if (scanner == scannerBack) {
            // change button caption to 'rescan'
            scanBackButton.setText(Label.getLabel("demographics_insurance_retake_back_photo"));
            backImageAsBase64 = imageAsBase64;
            target = healthInsuranceBackPhotoView;
        }

        ImageCaptureHelper.setCapturedImageToTargetView(getContext(), target, bitmap, CAMERA_TYPE, IMAGE_SHAPE);
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

    protected void setInsuranceListAdapter() {
        RecyclerView recyclerView = ((RecyclerView) findViewById(R.id.available_health_insurance_list));
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new InsuranceLineItemsListAdapter(getContext(), demographicDTO, this));
        }
    }

    @Override
    public void onEditInsuranceClicked(DemographicInsurancePayloadDTO lineItem) {
        insuranceEditDialog = new InsuranceEditDialog(getActivity(), lineItem, demographicDTO, this);
        insuranceEditDialog.show();
    }

    private void initDTOs() {
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());

        insurancesMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.insurances;
    }

    @Override
    public void onInsuranceEdited() {
        if (insuranceEditDialog != null) {
            insuranceEditDialog.dismiss();
            insuranceEditDialog = null;
        }

        initializeInsuranceList();
    }

    @Override
    public void captureInsuranceFrontImage() {
        isFrontScan = true;
        selectImage(scannerFront);
    }

    @Override
    public void captureInsuranceBackImage() {
        isFrontScan = false;
        selectImage(scannerBack);
    }

    @Override
    public void imageCaptured(Bitmap bitmap) {
        if (insuranceEditDialog == null) {
            updateModelAndViewsAfterScan(imageCaptureHelper, bitmap);
        } else {
            insuranceEditDialog.updateModelAndViewsAfterScan(bitmap, isFrontScan);
        }
    }
}
