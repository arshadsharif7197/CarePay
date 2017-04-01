package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
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
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.DemographicMetadataPropertiesInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.marcok.stepprogressbar.StepProgressBar;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class InsuranceEditDialog extends BaseDialogFragment implements CarePayCameraCallback {

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

    private CarePayTextView selectedProviderTextView;
    private CarePayTextView selectedPlan;

    private CarePayTextView selectedType;
    private ImageView healthInsuranceFrontPhotoView;
    private ImageView healthInsuranceBackPhotoView;

    private boolean isCardNumberEmpty;
    private boolean isGroupNumberEmpty;
    private boolean isFrontScan;
    private boolean hadInsurance;

    private String frontImageAsBase64;
    private String backImageAsBase64;

    private InsuranceEditDialogListener callback;
    private CarePayCameraReady carePayCameraReady;
    private int editedIndex;
    private String selectedProvider;
    private EditText otherProviderEditText;

    public interface InsuranceEditDialogListener {
        void onInsuranceEdited(DemographicDTO demographicDTO);

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        hadInsurance = hasInsurance();
        if (hadInsurance) {
            return inflater.inflate(R.layout.dialog_add_edit_insurance, container, false);
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

        if(hasInsurance()) {
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
        selectedPlan = (CarePayTextView) findViewById(R.id.health_insurance_choose_plans);
        selectedType = (CarePayTextView) findViewById(R.id.health_insurance_types);

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

        Button saveInsuranceButton = (Button) findViewById(R.id.save_insurance_changes);

        setValues();

        if (editedIndex == NEW_INSURANCE) {
            ((Button) findViewById(R.id.take_front_photo_button)).setText(
                    Label.getLabel("demographics_insurance_take_front_photo"));
            ((Button) findViewById(R.id.take_back_photo_button)).setText(
                    Label.getLabel("demographics_insurance_take_back_photo"));

            selectedType.setText(Label.getLabel("demographics_choose"));

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
            ((CarePayTextView) findViewById(R.id.toolbar_title)).setText(demographicInsurancePayload.getInsurancePlan());
            findViewById(R.id.health_insurance_plans).setVisibility(View.GONE);
            selectedProviderTextView.setText(demographicInsurancePayload.getInsuranceProvider());
            selectedPlan.setText(demographicInsurancePayload.getInsurancePlan());
            selectedType.setText(demographicInsurancePayload.getInsuranceType());
            cardNumber.setText(demographicInsurancePayload.getInsuranceMemberId());
            cardNumber.getOnFocusChangeListener().onFocusChange(cardNumber, false);
            groupNumber.setText(demographicInsurancePayload.getInsuranceGroupId());
            groupNumber.getOnFocusChangeListener().onFocusChange(groupNumber, false);

            findViewById(R.id.remove_insurance_entry).setOnClickListener(removeButtonListener);
        }

        saveInsuranceButton.setOnClickListener(saveButtonListener);
        // TO-DO: Need to know what fields are required
        saveInsuranceButton.setEnabled(true);
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
                    callback.onInsuranceEdited(null);
                }
            }
        };
    }


    View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View saveChanges) {
            DemographicInsurancePayloadDTO demographicInsurancePayloadDTO;
            if (editedIndex == NEW_INSURANCE) {
                demographicInsurancePayloadDTO = new DemographicInsurancePayloadDTO();
                demographicDTO.getPayload().getDemographics().getPayload().getInsurances().add(demographicInsurancePayloadDTO);
            } else {
                demographicInsurancePayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(editedIndex);
            }

            List<DemographicInsurancePhotoDTO> photos = demographicInsurancePayloadDTO.getInsurancePhotos();
            if (photos.isEmpty()) {
                photos.add(new DemographicInsurancePhotoDTO());
            }

            if (photos.size() == 1) {
                photos.add(new DemographicInsurancePhotoDTO());
            }

            demographicInsurancePayloadDTO.setInsuranceProvider(selectedProvider);
            demographicInsurancePayloadDTO.setInsurancePlan(selectedPlan.getText().toString());
            demographicInsurancePayloadDTO.setInsuranceType(selectedType.getText().toString());
            demographicInsurancePayloadDTO.setInsuranceMemberId(cardNumber.getText().toString());
            demographicInsurancePayloadDTO.setInsuranceGroupId(groupNumber.getText().toString());

            photos = demographicInsurancePayloadDTO.getInsurancePhotos();
            if (frontImageAsBase64 != null) {
                photos.get(0).setInsurancePhoto(frontImageAsBase64);
            }

            if (backImageAsBase64 != null) {
                photos.get(1).setInsurancePhoto(backImageAsBase64);
            }

            closeDialog();
        }
    };


    private void closeDialog() {
        dismiss();
        if (callback != null) {
            callback.onInsuranceEdited(demographicDTO);

            if (!hadInsurance) {
                callback.goOneStepBack();
            }
        }
    }

    private void initializeScanArea() {
        healthInsuranceFrontPhotoView = (ImageView) findViewById(R.id.health_insurance_front_photo);
        healthInsuranceBackPhotoView = (ImageView) findViewById(R.id.health_insurance_back_photo);

        if (editedIndex != NEW_INSURANCE) {
            List<DemographicInsurancePhotoDTO> photos = demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(editedIndex).getInsurancePhotos();
            if (!photos.isEmpty()) {
                setInsurancePhoto(healthInsuranceFrontPhotoView, photos.get(0));

                if (photos.size() > 1) {
                    setInsurancePhoto(healthInsuranceBackPhotoView, photos.get(1));
                }
            }
        }

        Button scanFrontButton = (Button) findViewById(R.id.take_front_photo_button);
        scanFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View frontButtonView) {
                if (callback != null) {
                    isFrontScan = true;
                    carePayCameraReady.captureImage(InsuranceEditDialog.this);
                }
            }
        });

        Button scanBackButton = (Button) findViewById(R.id.take_back_photo_button);
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

    private void setInsurancePhoto(ImageView imageView, DemographicInsurancePhotoDTO photoDTO) {
        if (photoDTO == null || photoDTO.getInsurancePhoto() == null) {
            return;
        }

        String photo = photoDTO.getInsurancePhoto();
        if (URLUtil.isValidUrl(photo)) {
            Picasso.with(getContext()).load(photo).fit().centerCrop().into(imageView);
        } else {
            imageView.setImageBitmap(SystemUtil.convertStringToBitmap(photo));
        }
    }

    private void setValues() {
        DemographicMetadataEntityInsurancesDTO insurancesMetaDTO = demographicDTO.getMetadata().getDataModels().getDemographic().getInsurances();
        if (insurancesMetaDTO != null) {
            DemographicMetadataPropertiesInsuranceDTO properties = insurancesMetaDTO.getProperties()
                    .getItems().getInsurance().getProperties();

            // Providers
            List<MetadataOptionDTO> providerList = properties.getInsuranceProvider().getOptions();
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

            // Plans
            List<MetadataOptionDTO> planList = properties.getInsurancePlan().getOptions();
            final String[] plans = new String[planList.size()];
            for (int i = 0; i < planList.size(); i++) {
                plans[i] = planList.get(i).getLabel();
            }
            selectedPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View providerView) {
                    showAlertDialogWithListView(plans, "Choose Plan", "Cancel", PLANS);
                }
            });

            // Types
            List<MetadataOptionDTO> typeList = properties.getInsuranceType().getOptions();
            final String[] types = new String[typeList.size()];
            for (int i = 0; i < typeList.size(); i++) {
                types[i] = typeList.get(i).getLabel();
            }
            selectedType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View providerView) {
                    showAlertDialogWithListView(types, "Choose Type", "Cancel", TYPES);
                }
            });
        }
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
                        if ("other".equals(dataArray[position].toLowerCase())) {
                            otherProviderEditText.setVisibility(View.VISIBLE);
                            otherProviderEditText.requestFocus();

                        } else {
                            otherProviderEditText.setVisibility(View.GONE);
                            selectedProvider = dataArray[position];
                        }
                        selectedProviderTextView.setText(dataArray[position]);
                        selectedPlan.setVisibility(View.VISIBLE);
                        break;

                    case PLANS:
                        selectedPlan.setText(dataArray[position]);
                        break;

                    case TYPES:
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
        String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
        ImageView target;

        if (isFrontScan) {
            frontImageAsBase64 = imageAsBase64;
            target = healthInsuranceFrontPhotoView;
        } else {
            backImageAsBase64 = imageAsBase64;
            target = healthInsuranceBackPhotoView;
        }

        ImageCaptureHelper.setCapturedImageToTargetView(getContext(), target, bitmap, CAMERA_TYPE, IMAGE_SHAPE);
    }

    private boolean hasInsurance() {
        return !demographicDTO.getPayload().getDemographics().getPayload().getInsurances().isEmpty();
    }
}