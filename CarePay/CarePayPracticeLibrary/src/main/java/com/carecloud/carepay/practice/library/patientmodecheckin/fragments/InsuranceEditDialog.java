package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataInsuranceOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.DemographicMetadataPropertiesInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.marcok.stepprogressbar.StepProgressBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import static com.carecloud.carepaylibray.utils.ImageCaptureHelper.getBitmapFileUrl;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class InsuranceEditDialog extends BaseDialogFragment implements CarePayCameraCallback {
    private static final int FRONT_PIC = 1;
    private static final int BACK_PIC = 2;
    private static final String HTTP = "http";

    private static final ImageCaptureHelper.CameraType CAMERA_TYPE = ImageCaptureHelper.CameraType.CUSTOM_CAMERA;
    private static final ImageCaptureHelper.ImageShape IMAGE_SHAPE = ImageCaptureHelper.ImageShape.RECTANGULAR;
    public static final String EDITED_INDEX = "EditedIndex";
    public static final int NEW_INSURANCE = -1;
    private static final int PROVIDERS = 0;
    private static final int PLANS = 1;
    private static final int TYPES = 2;

    private DemographicDTO demographicDTO;

    private TextInputLayout cardNumberInput;
    private TextInputLayout groupNumberInput;
    private EditText cardNumber;
    private EditText groupNumber;
    private Button saveInsuranceButton;

    private Button scanFrontButton;
    private Button scanBackButton;

    private CarePayTextView selectedProviderTextView;
    private CarePayTextView selectedPlanTextView;
    private CarePayTextView selectedTypeTextView;

    private ImageView healthInsuranceFrontPhotoView;
    private ImageView healthInsuranceBackPhotoView;

    private boolean isCardNumberEmpty;
    private boolean isGroupNumberEmpty;
    private boolean isFrontScan;
    private boolean hadInsurance;

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

    private Handler handler;

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
                                                  Integer editedIndex) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        args.putInt(EDITED_INDEX, editedIndex == null ? NEW_INSURANCE : editedIndex);
        DtoHelper.bundleDto(args, demographicDTO);

        InsuranceEditDialog dialog = new InsuranceEditDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (InsuranceEditDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement InsuranceEditDialogListener");
        }

        try {
            carePayCameraReady = (CarePayCameraReady) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CarePayCameraReady");
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
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, arguments);

        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        hadInsurance = hasInsurance();
        if (hadInsurance) {
            View view =  inflater.inflate(R.layout.dialog_add_edit_insurance, container, false);

            hideKeyboardOnViewTouch(view.findViewById(R.id.dialog_content_layout));
            hideKeyboardOnViewTouch(view.findViewById(R.id.container_main));
            return view;
        }

        View view = inflater.inflate(R.layout.fragment_review_demographic_base, container, false);

        StepProgressBar stepProgressBar = (StepProgressBar) view.findViewById(R.id.stepProgressBarCheckin);
        stepProgressBar.setCumulativeDots(true);
        stepProgressBar.setNumDots(5);

        View child = inflater.inflate(R.layout.add_edit_insurance_view, null);
        ((ViewGroup) view.findViewById(R.id.checkinDemographicsContentLayout)).addView(child);

        inflateToolbarViews(view);

        hideKeyboardOnViewTouch(view);
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

        TextView textView = (TextView) view.findViewById(R.id.checkinDemographicsHeaderLabel);
        textView.setText(Label.getLabel("demographics_insurance_label"));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (hasInsurance()) {
            findViewById(R.id.edit_insurance_close_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View closeButton) {
                    closeDialog();
                }
            });
        }
        View container = view.findViewById(R.id.container_main);
        hideKeyboardOnViewTouch(container);

        selectedProviderTextView = (CarePayTextView) findViewById(R.id.health_insurance_providers);
        selectedPlanTextView = (CarePayTextView) findViewById(R.id.health_insurance_choose_plans);
        selectedTypeTextView = (CarePayTextView) findViewById(R.id.health_insurance_types);

        cardNumberInput = (TextInputLayout) findViewById(R.id.health_insurance_card_number_layout);
        cardNumber = (EditText) findViewById(R.id.health_insurance_card_number);
        otherProviderEditText = (EditText) findViewById(R.id.otherProviderEditText);

        String cardNumberHint = Label.getLabel("demographics_insurance_card_number");
        cardNumberInput.setTag(cardNumberHint);
        cardNumber.setHint(cardNumberHint);
        cardNumber.setTag(cardNumberInput);

        groupNumberInput = (TextInputLayout) findViewById(R.id.health_insurance_group_number_layout);
        groupNumber = (EditText) findViewById(R.id.health_insurance_group_number);

        String groupNumberHint = Label.getLabel("demographics_insurance_group_number");
        groupNumberInput.setTag(groupNumberHint);
        groupNumber.setHint(groupNumberHint);
        groupNumber.setTag(groupNumberInput);

        setTextListeners();
        setChangeFocusListeners();
        setActionListeners();

        cardNumber.clearFocus();
        groupNumber.clearFocus();

        initializeScanArea();

        saveInsuranceButton = (Button) findViewById(R.id.save_insurance_changes);

        getInsuranceDropdownLists();

        if (editedIndex == NEW_INSURANCE) {
            ((Button) findViewById(R.id.take_front_photo_button)).setText(
                    Label.getLabel("demographics_insurance_take_front_photo"));
            ((Button) findViewById(R.id.take_back_photo_button)).setText(
                    Label.getLabel("demographics_insurance_take_back_photo"));

            selectedTypeTextView.setText(Label.getLabel("demographics_choose"));

            if (hasInsurance()) {
                disappearViewById(R.id.remove_insurance_entry);
                ((CarePayTextView) findViewById(R.id.toolbar_title)).setText(
                        Label.getLabel("practice_checkin_demogr_ins_add_new_button_label"));
            } else {
                showViewById(R.id.check_in_demographics_left_button);
                findViewById(R.id.check_in_demographics_left_button).setOnClickListener(getNoInsuranceListener());
                saveInsuranceButton = (Button) findViewById(R.id.checkinDemographicsNextButton);
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
            ((CarePayTextView) findViewById(R.id.toolbar_title)).setText(selectedProvider+(selectedPlan!=null?" "+selectedPlan:""));
            findViewById(R.id.health_insurance_plans).setVisibility(View.GONE);

            selectedType = demographicInsurancePayload.getInsuranceType();
            selectedTypeTextView.setText(selectedType);

            cardNumber.setText(demographicInsurancePayload.getInsuranceMemberId());
            cardNumber.getOnFocusChangeListener().onFocusChange(cardNumber, false);
            groupNumber.setText(demographicInsurancePayload.getInsuranceGroupId());
            groupNumber.getOnFocusChangeListener().onFocusChange(groupNumber, false);

            findViewById(R.id.remove_insurance_entry).setOnClickListener(removeButtonListener);
        }

        saveInsuranceButton.setOnClickListener(saveButtonListener);
        // TO-DO: Need to know what fields are required
        validateForm();
    }

    private View.OnClickListener removeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View saveChanges) {
            if (editedIndex != NEW_INSURANCE) {
                demographicDTO.getPayload().getDemographics().getPayload().getInsurances().remove(editedIndex);
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
            boolean hasInsurance = hasInsurance();


            DemographicInsurancePayloadDTO demographicInsurancePayloadDTO;
            if (editedIndex == NEW_INSURANCE) {
                demographicInsurancePayloadDTO = new DemographicInsurancePayloadDTO();
                demographicDTO.getPayload().getDemographics().getPayload().getInsurances().add(demographicInsurancePayloadDTO);
            } else {
                demographicInsurancePayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(editedIndex);
            }

            List<DemographicInsurancePhotoDTO> photos = demographicInsurancePayloadDTO.getInsurancePhotos();

            demographicInsurancePayloadDTO.setInsuranceProvider(selectedProvider);
            demographicInsurancePayloadDTO.setInsurancePlan(selectedPlan);
            demographicInsurancePayloadDTO.setInsuranceType(selectedType != null ? selectedType : typeList.get(0).getLabel());

            demographicInsurancePayloadDTO.setInsuranceMemberId(cardNumber.getText().toString());
            demographicInsurancePayloadDTO.setInsuranceGroupId(groupNumber.getText().toString());

            photos = demographicInsurancePayloadDTO.getInsurancePhotos();
            if (frontInsurancePhotoDTO != null) {
                photos.add(frontInsurancePhotoDTO);
            }

            if (backInsurancePhotoDTO != null) {
                photos.add(backInsurancePhotoDTO);
            }

            if (hasInsurance) {
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

            if (!hadInsurance) {
                callback.goOneStepBack();
            }
        }
    }

    private void initializeScanArea() {
        healthInsuranceFrontPhotoView = (ImageView) findViewById(R.id.health_insurance_front_photo);
        healthInsuranceBackPhotoView = (ImageView) findViewById(R.id.health_insurance_back_photo);
        healthInsuranceFrontPhotoView.measure(0,0);
        healthInsuranceBackPhotoView.measure(0,0);

        if (editedIndex != NEW_INSURANCE) {
            String frontPic = null;
            String backPic = null;
            List<DemographicInsurancePhotoDTO> insurancePhotoDTOs = demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(editedIndex).getInsurancePhotos();
            if(insurancePhotoDTOs!=null && !insurancePhotoDTOs.isEmpty()){
                for(DemographicInsurancePhotoDTO photoDTO: insurancePhotoDTOs){
                    if(photoDTO.getPage() == FRONT_PIC){
                        frontPic = photoDTO.getInsurancePhoto();
                    }
                    if(photoDTO.getPage() == BACK_PIC){
                        backPic = photoDTO.getInsurancePhoto();
                    }
                }
            }

            if (!StringUtil.isNullOrEmpty(frontPic)) {
                if(frontPic.startsWith(HTTP)) {
                    setInsurancePhoto(healthInsuranceFrontPhotoView, frontPic);
                }else{//must be BASE64
                    Bitmap bitmap = SystemUtil.convertStringToBitmap(frontPic);
                    File file = ImageCaptureHelper.getBitmapFileUrl(getContext(), bitmap, "idFront");
                    setInsurancePhoto(healthInsuranceFrontPhotoView, file, healthInsuranceFrontPhotoView.getMeasuredWidth(), healthInsuranceFrontPhotoView.getMeasuredHeight());
                }
            }

            if (!StringUtil.isNullOrEmpty(backPic)) {
                if(backPic.startsWith(HTTP)) {
                    setInsurancePhoto(healthInsuranceBackPhotoView, backPic);
                }else{//must be BASE64
                    Bitmap bitmap = SystemUtil.convertStringToBitmap(backPic);
                    File file = ImageCaptureHelper.getBitmapFileUrl(getContext(), bitmap, "idBack");
                    setInsurancePhoto(healthInsuranceBackPhotoView, file, healthInsuranceBackPhotoView.getMeasuredWidth(), healthInsuranceBackPhotoView.getMeasuredHeight());
                }
            }

        }

        scanFrontButton = (Button) findViewById(R.id.take_front_photo_button);
        scanFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View frontButtonView) {
                if (callback != null) {
                    isFrontScan = true;
                    carePayCameraReady.captureImage(InsuranceEditDialog.this);
                }
            }
        });

        scanBackButton = (Button) findViewById(R.id.take_back_photo_button);
        scanBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View frontButtonView) {
                if (callback != null) {
                    isFrontScan = false;
                    carePayCameraReady.captureImage(InsuranceEditDialog.this);
                }
            }
        });
    }

    private void setInsurancePhoto(final ImageView imageView, String photoUrl){
        imageView.measure(0,0);
        final int width = imageView.getMeasuredWidth();
        final int height = imageView.getMeasuredHeight();

        Picasso.with(getContext()).load(photoUrl)
                .resize(width, height)
                .centerInside()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .transform(new RoundedCornersTransformation(8, 0))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                R.drawable.icn_placeholder_document));
                    }
                });
    }

    private void setInsurancePhoto(final ImageView imageView, File file, int width, int height){
        Picasso.with(getContext()).load(file)
                .resize(width, height)
                .centerInside()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .transform(new RoundedCornersTransformation(8, 0))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                R.drawable.icn_placeholder_document));
                    }
                });
    }


    private void getInsuranceDropdownLists() {
        DemographicMetadataEntityInsurancesDTO insurancesMetaDTO = demographicDTO.getMetadata().getDataModels().getDemographic().getInsurances();
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
                        otherProviderEditText = (EditText) findViewById(R.id.otherProviderEditText);
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
                selectedProvider = editable.toString();
                validateForm();
            }
        });

    }

    private void setChangeFocusListeners() {
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard((Activity) getContext());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        };
        cardNumber.setOnFocusChangeListener(onFocusChangeListener);
        groupNumber.setOnFocusChangeListener(onFocusChangeListener);
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
     * @param bitmap scanned insurance
     */
    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        if (bitmap != null) {
            final Bitmap rotateBitmap = ImageCaptureHelper.rotateBitmap(bitmap, ImageCaptureHelper.getOrientation());


            final ImageView imageView;
            String tempFile;
            final int page;

            if(isFrontScan){
                scanFrontButton.setText(Label.getLabel("demographics_insurance_retake_front_photo"));
                imageView = healthInsuranceFrontPhotoView;
                tempFile = "idFront";
                page = FRONT_PIC;
            }else{
                scanBackButton.setText(Label.getLabel("demographics_insurance_retake_back_photo"));
                imageView = healthInsuranceBackPhotoView;
                tempFile = "idBack";
                page = BACK_PIC;
            }

            int width = imageView.getWidth();
            int height = imageView.getHeight();
            imageView.getLayoutParams().width = width;
            imageView.getLayoutParams().height = height;

            File file = getBitmapFileUrl(getContext(), rotateBitmap, tempFile);
            setInsurancePhoto(imageView, file, width, height);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    // save from image
                    String imageAsBase64 = SystemUtil.convertBitmapToString(rotateBitmap, Bitmap.CompressFormat.JPEG, 90);
                    DemographicInsurancePhotoDTO photoDTO = new DemographicInsurancePhotoDTO();
                    photoDTO.setInsurancePhoto(imageAsBase64); // create the image dto
                    photoDTO.setPage(page);
                    photoDTO.setDelete(false);
                    if(isFrontScan){
                        frontInsurancePhotoDTO = photoDTO;
                    }else{
                        backInsurancePhotoDTO = photoDTO;
                    }
                }
            });


        }

    }

    private boolean hasInsurance() {
        return !demographicDTO.getPayload().getDemographics().getPayload().getInsurances().isEmpty();
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
