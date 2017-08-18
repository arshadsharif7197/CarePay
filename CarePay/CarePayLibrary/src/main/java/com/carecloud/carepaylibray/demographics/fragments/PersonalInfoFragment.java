package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
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

    private DemographicDTO demographicDTO;
    private DemographicDataModel dataModel;

    private Button buttonChangeCurrentPhoto;
    boolean hasNewImage = false;
    private MediaScannerPresenter mediaScannerPresenter;
    private String base64ProfileImage;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
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
        stepProgressBar.setCurrentProgressDot(0);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 1);
        checkinFlowCallback.setCurrentStep(1);
    }


    private void initialiseUIFields(View view) {
        setHeaderTitle(
                Label.getLabel("demographics_review_peronsonalinfo_section"),
                Label.getLabel("demographics_personal_info_heading"),
                Label.getLabel("demographics_personal_info_subheading"),
                view);
        initNextButton(view);
    }


    private void initViews(View view){
        DemographicPayloadDTO demographicPayload = demographicDTO.getPayload().getDemographics().getPayload();

        TextInputLayout firstNameLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrFirstNameTextInput);
        EditText firstName = (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit);
        firstName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(firstNameLayout, null));
        setVisibility(firstNameLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getFirstName().isDisplayed());
        firstName.setText(demographicPayload.getPersonalDetails().getFirstName());
        firstName.getOnFocusChangeListener().onFocusChange(firstName,
                !StringUtil.isNullOrEmpty(firstName.getText().toString().trim()));
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getFirstName().isRequired()) {
            firstName.addTextChangedListener(getValidateEmptyTextWatcher(firstNameLayout));
        }


        TextInputLayout lastNameLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrLastNameTextInput);
        EditText lastName = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        lastName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(lastNameLayout, null));
        setVisibility(lastNameLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getLastName().isDisplayed());
        lastName.setText(demographicPayload.getPersonalDetails().getLastName());
        lastName.getOnFocusChangeListener().onFocusChange(lastName,
                !StringUtil.isNullOrEmpty(lastName.getText().toString().trim()));
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getLastName().isRequired()) {
            lastName.addTextChangedListener(getValidateEmptyTextWatcher(lastNameLayout));
        }


        TextInputLayout middleNameLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrMiddleNameTextInputLayout);
        EditText middleName = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        middleName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(middleNameLayout, null));
        setVisibility(middleNameLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getMiddleName().isDisplayed());
        middleName.setText(demographicPayload.getPersonalDetails().getMiddleName());
        middleName.getOnFocusChangeListener().onFocusChange(middleName,
                !StringUtil.isNullOrEmpty(middleName.getText().toString().trim()));
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getMiddleName().isRequired()) {
            middleName.addTextChangedListener(getValidateEmptyTextWatcher(middleNameLayout));
            View middleNameOptional = view.findViewById(R.id.reviewdemogrMiddleNameOptionalLabel);
            middleNameOptional.setVisibility(View.GONE);
        }


        TextInputLayout dateBirthLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrDOBTextInput);
        final EditText dateOfBirth = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        dateOfBirth.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(dateBirthLayout, null));
        setVisibility(dateBirthLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getDateOfBirth().isDisplayed());
        dateOfBirth.addTextChangedListener(dateInputFormatter);
        dateOfBirth.setOnClickListener(selectEndOnClick);

        String dateString = demographicPayload.getPersonalDetails().getFormattedDateOfBirth();
        dateOfBirth.setText(dateString);
        dateOfBirth.getOnFocusChangeListener().onFocusChange(dateOfBirth, !StringUtil.isNullOrEmpty(dateString));
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getDateOfBirth().isRequired()) {
            dateOfBirth.addTextChangedListener(getValidateEmptyTextWatcher(dateBirthLayout));
        }else{
            dateOfBirth.addTextChangedListener(clearValidationErrorsOnTextChange(dateBirthLayout));
        }


        TextInputLayout phoneNumberLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        EditText phoneNumber = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        phoneNumber.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(phoneNumberLayout, null));
        setVisibility(phoneNumberLayout, dataModel.getDemographic().getAddress().getProperties().getPhone().isDisplayed());
        phoneNumber.addTextChangedListener(phoneInputFormatter);
        phoneNumber.setOnClickListener(selectEndOnClick);

        String phoneNumberString = demographicPayload.getAddress().getPhone();
        phoneNumber.setText(StringUtil.formatPhoneNumber(phoneNumberString));
        phoneNumber.getOnFocusChangeListener().onFocusChange(phoneNumber, !StringUtil.isNullOrEmpty(phoneNumberString));
        if(dataModel.getDemographic().getAddress().getProperties().getPhone().isRequired()) {
            phoneNumber.addTextChangedListener(getValidateEmptyTextWatcher(phoneNumberLayout));
        }else {
            phoneNumber.addTextChangedListener(clearValidationErrorsOnTextChange(phoneNumberLayout));
        }

    }

    private void initCameraViews(View view){
        ImageView profileImage = (ImageView) view.findViewById(R.id.DetailsProfileImage);
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this, profileImage,
                CarePayCameraPreview.CameraType.CAPTURE_PHOTO);

        buttonChangeCurrentPhoto = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        if(isCloverDevice){
            buttonChangeCurrentPhoto.setVisibility(View.INVISIBLE);
        }else {
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
            if(!StringUtil.isNullOrEmpty(profilePicURL)) {
                displayProfileImage(profilePicURL, profileImage);
            }
        }

    }


    private TextWatcher dateInputFormatter = new TextWatcher() {
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


    private View.OnClickListener selectEndOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText editText = (EditText) view;
            editText.setSelection(editText.length());
        }
    };



    @Override
    protected boolean passConstraints(View view) {
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getFirstName().isRequired()
                && checkTextEmptyValue(R.id.reviewdemogrFirstNameEdit, view)) {
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getLastName().isRequired()
                && checkTextEmptyValue(R.id.reviewdemogrLastNameEdit, view)){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getMiddleName().isRequired()
                && checkTextEmptyValue(R.id.reviewdemogrMiddleNameEdit, view)){
            return false;
        }
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getDateOfBirth().isRequired()
                && checkTextEmptyValue(R.id.revewidemogrDOBEdit, view)) {
            return false;
        }
        if(dataModel.getDemographic().getAddress().getProperties().getPhone().isRequired()
                && checkTextEmptyValue(R.id.reviewgrdemoPhoneNumberEdit, view)) {
            return false;
        }

        //This validation is required regardless of whether fields are required
        TextInputLayout dateBirthLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrDOBTextInput);
        EditText dateOfBirth = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        if(dateBirthLayout.getVisibility() == View.VISIBLE &&
                !StringUtil.isNullOrEmpty(dateOfBirth.getText().toString().trim()) &&
                !DateUtil.isValidateStringDateOfBirth(dateOfBirth.getText().toString().trim())) {
            dateBirthLayout.setErrorEnabled(true);
            dateBirthLayout.setError(Label.getLabel("demographics_date_validation_msg"));
            return false;
        }

        TextInputLayout phoneLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        EditText phoneNumber = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        if(phoneLayout.getVisibility() == View.VISIBLE &&
                !StringUtil.isNullOrEmpty(phoneNumber.getText().toString().trim()) &&
                !ValidationHelper.isValidString(phoneNumber.getText().toString().trim(), ValidationHelper.PHONE_NUMBER_PATTERN)){
            phoneLayout.setErrorEnabled(true);
            phoneLayout.setError(Label.getLabel("demographics_phone_number_validation_msg"));
            return false;
        }

        return true;
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


        EditText firstNameText = (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit);
        String firstName = firstNameText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(firstName)) {
            demographicPersDetailsPayloadDTO.setFirstName(firstName);
        }
        EditText middleNameText = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        String middleName = middleNameText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(middleName)) {
            demographicPersDetailsPayloadDTO.setMiddleName(middleName);
        }
        EditText lastNameText = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        String lastName = lastNameText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(lastName)) {
            demographicPersDetailsPayloadDTO.setLastName(lastName);
        }
        EditText dobEditText = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        String dateOfBirth = dobEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(dateOfBirth)) {
            // the date is DateUtil as
            demographicPersDetailsPayloadDTO.setDateOfBirth(DateUtil.getInstance().setDateRaw(dateOfBirth).toStringWithFormatYyyyDashMmDashDd());
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
        EditText phoneNumberEditText = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(phoneNumber)) {
            // 'de-format' before saving to model
            demographicAddressPayloadDTO.setPhone(StringUtil.revertToRawPhoneFormat(phoneNumber));
        }
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setAddress(demographicAddressPayloadDTO);
        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        return updatableDemographicDTO;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(mediaScannerPresenter!=null){
            mediaScannerPresenter.handleRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!handleActivityResult(requestCode, resultCode, data)){
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mediaScannerPresenter!=null && mediaScannerPresenter.handleActivityResult(requestCode, resultCode, data);
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

    void displayProfileImage(final String filePath, View view){
        final ImageView imageView = (ImageView) view;

        imageView.measure(0,0);
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        final int width = Math.max(imageView.getMeasuredWidth(), lp.width);
        final int height = Math.max(imageView.getMeasuredHeight(), lp.height);

        File file = new File(filePath);
        Uri fileUri;
        if(file.exists()){
            fileUri = Uri.fromFile(file);
        }else{
            //check if we have a base64 image instead of an URI
            Bitmap bitmap = SystemUtil.convertStringToBitmap(filePath);
            if(bitmap!=null) {
                File temp = ImageCaptureHelper.getBitmapFileUrl(getContext(), bitmap, "temp_"+System.currentTimeMillis());
                fileUri = Uri.fromFile(temp);
            }else {
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
        if(hasNewImage){
            String filePath = demographicsPersonalDetails.getProfilePhoto();
            File file = new File(filePath);
            Bitmap bitmap = null;
            if(file.exists()) {
                bitmap = BitmapFactory.decodeFile(filePath);
            }else{
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(filePath));
                }catch (IOException ioe){
                    //do nothing
                }
            }

            if(bitmap != null){
                base64ProfileImage = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
                hasNewImage = false;
            }
        }
    }

}
