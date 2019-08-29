package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;


/**
 * Created by jorge on 27/02/17
 */

public class PersonalInfoFragment extends CheckInDemographicsBaseFragment implements MediaViewInterface {

    private Button buttonChangeCurrentPhoto;
    boolean hasNewImage = false;
    private MediaScannerPresenter mediaScannerPresenter;
    private String base64ProfileImage;
    private DemographicsOption selectedPhoneType = new DemographicsOption();

    private EditText phoneNumberTypeEditText;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        demographicDTO = checkinFlowCallback.getDemographicDTO();// DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        dataModel = demographicDTO.getMetadata().getNewDataModel();
        preventNavBack = getArguments().getBoolean(PREVENT_NAV_BACK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        initialiseUIFields(view);
        initViews(view);
        initCameraViews(view);
        checkIfEnableButton(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        stepProgressBar.setCurrentProgressDot(CheckinFlowCallback.PERSONAL_INFO - 1);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, checkinFlowCallback.getTotalSteps(), CheckinFlowCallback.PERSONAL_INFO);
        checkinFlowCallback.setCurrentStep(CheckinFlowCallback.PERSONAL_INFO);
    }

    private void initialiseUIFields(View view) {
        setHeaderTitle(
                Label.getLabel("demographics_review_peronsonalinfo_section"),
                Label.getLabel("demographics_personal_info_heading"),
                Label.getLabel("demographics_personal_info_subheading"),
                view);
        initNextButton(view);
    }

    private void initViews(View view) {
        DemographicPayloadDTO demographicPayload = demographicDTO.getPayload().getDemographics().getPayload();

        setUpField((TextInputLayout) view.findViewById(R.id.reviewdemogrFirstNameTextInput),
                (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit),
                dataModel.getDemographic().getPersonalDetails().getProperties().getFirstName().isDisplayed(),
                demographicPayload.getPersonalDetails().getFirstName(),
                dataModel.getDemographic().getPersonalDetails().getProperties().getFirstName().isRequired(),
                view.findViewById(R.id.firstNameRequired));

        setUpField((TextInputLayout) view.findViewById(R.id.reviewdemogrMiddleNameTextInputLayout),
                (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit),
                dataModel.getDemographic().getPersonalDetails().getProperties().getMiddleName().isDisplayed(),
                demographicPayload.getPersonalDetails().getMiddleName(),
                dataModel.getDemographic().getPersonalDetails().getProperties().getMiddleName().isRequired(),
                view.findViewById(R.id.middleNameRequired));

        setUpField((TextInputLayout) view.findViewById(R.id.reviewdemogrLastNameTextInput),
                (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit),
                dataModel.getDemographic().getPersonalDetails().getProperties().getLastName().isDisplayed(),
                demographicPayload.getPersonalDetails().getLastName(),
                dataModel.getDemographic().getPersonalDetails().getProperties().getLastName().isRequired(),
                view.findViewById(R.id.lastNameRequired));

        TextInputLayout dateBirthInputLayout = view.findViewById(R.id.reviewdemogrDOBTextInput);
        final EditText dateOfBirthEditText = view.findViewById(R.id.revewidemogrDOBEdit);
        boolean isDOBRequired = dataModel.getDemographic().getPersonalDetails().getProperties().getDateOfBirth().isRequired();
        setUpField(dateBirthInputLayout, dateOfBirthEditText,
                dataModel.getDemographic().getPersonalDetails().getProperties().getDateOfBirth().isDisplayed(),
                demographicPayload.getPersonalDetails().getFormattedDateOfBirth(), isDOBRequired,
                view.findViewById(R.id.dobRequired));
        if (!isDOBRequired) {
            dateOfBirthEditText.addTextChangedListener(clearValidationErrorsOnTextChange(dateBirthInputLayout));
        }
        dateOfBirthEditText.addTextChangedListener(dateInputFormatter);
        dateOfBirthEditText.setOnClickListener(selectEndOnClick);

        TextInputLayout phoneNumberInputLayout = view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        final EditText phoneNumberEditText = view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        boolean phoneNumberRequired = dataModel.getDemographic().getAddress().getProperties().getPhone().isRequired();
        setUpField(phoneNumberInputLayout, phoneNumberEditText,
                dataModel.getDemographic().getAddress().getProperties().getPhone().isDisplayed(),
                StringUtil.formatPhoneNumber(demographicPayload.getAddress().getPhone()), phoneNumberRequired,
                view.findViewById(R.id.phoneNumberRequired));
        if (!phoneNumberRequired) {
            phoneNumberEditText.addTextChangedListener(clearValidationErrorsOnTextChange(phoneNumberInputLayout));
        }
        phoneNumberEditText.addTextChangedListener(phoneInputFormatter);
        phoneNumberEditText.setOnClickListener(selectEndOnClick);


        TextInputLayout phoneTypeInputLayout = view.findViewById(R.id.phoneTypeInputLayout);
        phoneNumberTypeEditText = view.findViewById(R.id.phoneTypeEditText);
        phoneNumberTypeEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(phoneTypeInputLayout, null));
        phoneNumberTypeEditText.setOnClickListener(getSelectOptionsListener(dataModel
                        .getDemographic().getAddress().getProperties().getPhoneType().getOptions(),
                getDefaultOnOptionsSelectedListener(phoneNumberTypeEditText, selectedPhoneType,
                        view.findViewById(R.id.phoneNumberTypeRequired)),
                Label.getLabel("phone_type_label")));
        initSelectableInput(phoneNumberTypeEditText, selectedPhoneType,
                demographicPayload.getAddress().getPhoneNumberType(),
                view.findViewById(R.id.phoneNumberTypeRequired),
                dataModel.getDemographic().getAddress().getProperties().getPhoneType().getOptions());

    }

    private void initCameraViews(View view) {
        ImageView profileImage = view.findViewById(R.id.DetailsProfileImage);
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this, profileImage,
                CarePayCameraPreview.CameraType.CAPTURE_PHOTO);

        buttonChangeCurrentPhoto = view.findViewById(R.id.changeCurrentPhotoButton);
        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        if (isCloverDevice) {
            buttonChangeCurrentPhoto.setVisibility(View.INVISIBLE);
        } else {
            buttonChangeCurrentPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isPatientApp = getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT;
                    mediaScannerPresenter.selectImage(isPatientApp);
                }
            });
        }

        if (demographicDTO != null) {
            String profilePicURL = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails().getProfilePhoto();
            if (!StringUtil.isNullOrEmpty(profilePicURL)) {
                displayProfileImage(profilePicURL, profileImage);
            }
        }

    }

    @Override
    protected boolean passConstraints(View view) {

        try {
            String firstNameValue = ((EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit)).getText().toString();
            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getFirstName().isRequired(), firstNameValue, R.id.firstNameContainer,
                    R.id.reviewdemogrFirstNameTextInput, isUserAction())) return false;

            String middleNameValue = ((EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit)).getText().toString();
            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getMiddleName().isRequired(), middleNameValue, R.id.middleNameContainer,
                    R.id.reviewdemogrMiddleNameTextInputLayout, isUserAction())) return false;

            String lastNameValue = ((EditText) view.findViewById(R.id.reviewdemogrLastNameEdit)).getText().toString();
            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getLastName().isRequired(), lastNameValue, R.id.lastNameContainer,
                    R.id.reviewdemogrLastNameTextInput, isUserAction())) return false;

            EditText dateOfBirth = view.findViewById(R.id.revewidemogrDOBEdit);
            String dobValue = dateOfBirth.getText().toString();
            if (validateField(view, dataModel.getDemographic().getPersonalDetails().getProperties()
                            .getDateOfBirth().isRequired(), dobValue, R.id.dobContainer,
                    R.id.reviewdemogrDOBTextInput, isUserAction())) return false;

            //This validation is required regardless of whether fields are required
            TextInputLayout dateBirthLayout = view.findViewById(R.id.reviewdemogrDOBTextInput);
            if (dateBirthLayout.getVisibility() == View.VISIBLE &&
                    !StringUtil.isNullOrEmpty(dateOfBirth.getText().toString().trim())) {
                String dateValidationResult = DateUtil
                        .getDateOfBirthValidationResultMessage(dateOfBirth.getText().toString().trim());
                if (dateValidationResult != null) {
                    setFieldError(dateBirthLayout, dateValidationResult, isUserAction());
                    if (isUserAction()) {
                        showErrorViews(true, (ViewGroup) view.findViewById(R.id.dobContainer));
                    }
                    return false;
                } else {
                    unsetFieldError(dateBirthLayout);
                }
            } else {
                unsetFieldError(dateBirthLayout);
            }

            EditText phoneNumber = view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
            String phoneValue = phoneNumber.getText().toString();
            if (validateField(view, dataModel.getDemographic().getAddress().getProperties()
                            .getPhone().isRequired(), phoneValue, R.id.phoneNumberContainer,
                    R.id.reviewdemogrPhoneNumberTextInput, isUserAction())) return false;

            TextInputLayout phoneLayout = view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
            if (phoneLayout.getVisibility() == View.VISIBLE &&
                    !StringUtil.isNullOrEmpty(phoneNumber.getText().toString().trim()) &&
                    !ValidationHelper.isValidString(phoneNumber.getText().toString().trim(), ValidationHelper.PHONE_NUMBER_PATTERN)) {
                setFieldError(phoneLayout, Label.getLabel("demographics_phone_number_validation_msg"), isUserAction());
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.phoneNumberContainer));
                }
                return false;
            } else {
                unsetFieldError(phoneLayout);
            }

            if (validateField(view, dataModel.getDemographic().getAddress().getProperties()
                            .getPhoneType().isRequired(), selectedPhoneType.getName(), R.id.phoneNumberTypeContainer,
                    R.id.phoneTypeInputLayout, isUserAction())) {
                return false;
            } else {
                unsetFieldError((TextInputLayout) view.findViewById(R.id.phoneTypeInputLayout));
            }

            return true;
        } finally {
            setUserAction(false);
        }
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review_demographic_personal;
    }

    @Override
    protected DemographicDTO updateDemographicDTO(View view) {
        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        updatableDemographicDTO.setPayload(new DemographicPayloadResponseDTO());
        updatableDemographicDTO.getPayload().setDemographics(new DemographicPayloadInfoDTO());
        updatableDemographicDTO.getPayload().getDemographics().setPayload(new DemographicPayloadDTO());

        setupImageBase64();
        PatientModel demographicPersDetailsPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();

        if (demographicPersDetailsPayloadDTO == null) {
            demographicPersDetailsPayloadDTO = new PatientModel();
        }


        EditText firstNameText = view.findViewById(R.id.reviewdemogrFirstNameEdit);
        String firstName = firstNameText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(firstName)) {
            demographicPersDetailsPayloadDTO.setFirstName(firstName);
        }
        EditText middleNameText = view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        String middleName = middleNameText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(middleName)) {
            demographicPersDetailsPayloadDTO.setMiddleName(middleName);
        }
        EditText lastNameText = view.findViewById(R.id.reviewdemogrLastNameEdit);
        String lastName = lastNameText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(lastName)) {
            demographicPersDetailsPayloadDTO.setLastName(lastName);
        }
        EditText dobEditText = view.findViewById(R.id.revewidemogrDOBEdit);
        String dateOfBirth = dobEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(dateOfBirth)) {
            // the date is DateUtil as
            demographicPersDetailsPayloadDTO.setDateOfBirth(DateUtil.getInstance().setDateRaw(dateOfBirth, true).toStringWithFormatYyyyDashMmDashDd());
        }

        if (!StringUtil.isNullOrEmpty(base64ProfileImage)) {
            demographicPersDetailsPayloadDTO.setProfilePhoto(base64ProfileImage);
        }

        updatableDemographicDTO.getPayload().getDemographics().getPayload().setPersonalDetails(demographicPersDetailsPayloadDTO);

        DemographicAddressPayloadDTO demographicAddressPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getAddress();
        // save address
        if (demographicAddressPayloadDTO == null) {
            demographicAddressPayloadDTO = new DemographicAddressPayloadDTO();
        }
        EditText phoneNumberEditText = view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(phoneNumber)) {
            // 'de-format' before saving to model
            demographicAddressPayloadDTO.setPhone(StringUtil.revertToRawFormat(phoneNumber));
        }

        String phoneType = selectedPhoneType.getName();
        if (!StringUtil.isNullOrEmpty(phoneType)) {
            demographicAddressPayloadDTO.setPhoneNumberType(phoneType);
        }

        updatableDemographicDTO.getPayload().getDemographics().getPayload().setAddress(demographicAddressPayloadDTO);
        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        return updatableDemographicDTO;
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
    public void setCapturedBitmap(final String filePath, View view) {
        if (filePath != null) {
            displayProfileImage(filePath, view);

            PatientModel demographicsPersonalDetails = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
            demographicsPersonalDetails.setProfilePhoto(filePath);
            hasNewImage = true;

        }
    }

    void displayProfileImage(final String filePath, View view) {
        final ImageView imageView = (ImageView) view;

        imageView.measure(0, 0);
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        final int width = Math.max(imageView.getMeasuredWidth(), lp.width);
        final int height = Math.max(imageView.getMeasuredHeight(), lp.height);

        File file = new File(filePath);
        Uri fileUri;
        if (file.exists()) {
            fileUri = Uri.fromFile(file);
        } else {
            //check if we have a base64 image instead of an URI
            Bitmap bitmap = SystemUtil.convertStringToBitmap(filePath);
            if (bitmap != null) {
                File temp = ImageCaptureHelper.getBitmapFileUrl(getContext(), bitmap, "temp_" + System.currentTimeMillis());
                fileUri = Uri.fromFile(temp);
            } else {
                fileUri = Uri.parse(filePath);
            }
        }

        Picasso.with(getContext()).load(fileUri)
                .placeholder(R.drawable.icn_placeholder_user_profile_png)
                .resize(width, height)
                .centerCrop()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .transform(new CircleImageTransform())
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        buttonChangeCurrentPhoto.setText(Label.getLabel("demographics_take_another_picture_button_title"));
                    }

                    @Override
                    public void onError() {
                        imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                R.drawable.icn_placeholder_user_profile_png));

                    }
                });
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
        PatientModel demographicsPersonalDetails = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
        if (hasNewImage) {
            String filePath = demographicsPersonalDetails.getProfilePhoto();
            File file = new File(filePath);
            Bitmap bitmap = null;
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(filePath);
            } else {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(filePath));
                } catch (IOException ioe) {
                    //do nothing
                }
            }

            if (bitmap != null) {
                base64ProfileImage = SystemUtil.convertBitmapToString(
                        SystemUtil.getScaledBitmap(bitmap, CarePayConstants.IMAGE_QUALITY_MAX_PX),
                        Bitmap.CompressFormat.JPEG, 90);
                hasNewImage = false;
            }
        }
    }

    @Override
    public void afterLanguageChanged(DemographicDTO demographicDTO) {
        super.afterLanguageChanged(demographicDTO);
        dataModel = demographicDTO.getMetadata().getNewDataModel();
    }

    @Override
    protected void replaceTranslatedOptionsValues() {
        initSelectableInput(phoneNumberTypeEditText, selectedPhoneType,
                selectedPhoneType.getName(), null,
                dataModel.getDemographic().getAddress().getProperties().getPhoneType().getOptions());
    }
}
