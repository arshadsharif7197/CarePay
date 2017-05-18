package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;


/**
 * Created by jorge on 27/02/17
 */

public class PersonalInfoFragment extends CheckInDemographicsBaseFragment {

    private DemographicDTO demographicDTO;
    private UpdateProfilePictureListener callback;
    private DemographicDataModel dataModel;

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
        checkIfEnableButton(view);
    }

    @Override
    public void onStart(){
        super.onStart();
        callback.loadPictureFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        stepProgressBar.setCurrentProgressDot(0);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 1);
        checkinFlowCallback.setCurrentStep(1);
    }

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (UpdateProfilePictureListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement UpdateProfilePictureListener");
        }
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
        firstName.getOnFocusChangeListener().onFocusChange(firstName, !StringUtil.isNullOrEmpty(firstName.getText().toString()));
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getFirstName().isRequired()) {
            firstName.addTextChangedListener(getValidateEmptyTextWatcher(firstNameLayout));
        }


        TextInputLayout lastNameLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrLastNameTextInput);
        EditText lastName = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        lastName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(lastNameLayout, null));
        setVisibility(lastNameLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getLastName().isDisplayed());
        lastName.setText(demographicPayload.getPersonalDetails().getLastName());
        lastName.getOnFocusChangeListener().onFocusChange(lastName, !StringUtil.isNullOrEmpty(lastName.getText().toString()));
        if(dataModel.getDemographic().getPersonalDetails().getProperties().getLastName().isRequired()) {
            lastName.addTextChangedListener(getValidateEmptyTextWatcher(lastNameLayout));
        }


        TextInputLayout middleNameLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrMiddleNameTextInputLayout);
        EditText middleName = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        middleName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(middleNameLayout, null));
        setVisibility(middleNameLayout, dataModel.getDemographic().getPersonalDetails().getProperties().getMiddleName().isDisplayed());
        middleName.setText(demographicPayload.getPersonalDetails().getMiddleName());
        middleName.getOnFocusChangeListener().onFocusChange(middleName, !StringUtil.isNullOrEmpty(middleName.getText().toString()));
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


    private TextWatcher dateInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            lastLength = s.length();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            StringUtil.autoFormatDateOfBirth(s, lastLength);
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
        EditText dateOfBirth = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        if(!StringUtil.isNullOrEmpty(dateOfBirth.getText().toString()) && !DateUtil.isValidateStringDateOfBirth(dateOfBirth.getText().toString())) {
            TextInputLayout dateBirthLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrDOBTextInput);
            dateBirthLayout.setErrorEnabled(true);
            dateBirthLayout.setError(Label.getLabel("demographics_date_validation_msg"));
            return false;
        }

        EditText phoneNumber = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        String pattern = "\\d{3}-\\d{3}-\\d{4}";
        if(!StringUtil.isNullOrEmpty(phoneNumber.getText().toString()) && !ValidationHelper.isValidString(phoneNumber.getText().toString().trim(), pattern)){
            TextInputLayout phoneLayout = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
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

        PatientModel demographicPersDetailsPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();

        if (demographicPersDetailsPayloadDTO == null) {
            demographicPersDetailsPayloadDTO = new PatientModel();
        }


        EditText firstNameText = (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit);
        String firstName = firstNameText.getText().toString();
        if (!StringUtil.isNullOrEmpty(firstName)) {
            demographicPersDetailsPayloadDTO.setFirstName(firstName);
        }
        EditText middleNameText = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        String middleName = middleNameText.getText().toString();
        if (!StringUtil.isNullOrEmpty(middleName)) {
            demographicPersDetailsPayloadDTO.setMiddleName(middleName);
        }
        EditText lastNameText = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        String lastName = lastNameText.getText().toString();
        if (!StringUtil.isNullOrEmpty(lastName)) {
            demographicPersDetailsPayloadDTO.setLastName(lastName);
        }
        EditText dobEditText = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        String dateOfBirth = dobEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(dateOfBirth)) {
            // the date is DateUtil as
            demographicPersDetailsPayloadDTO.setDateOfBirth(DateUtil.getInstance().setDateRaw(dateOfBirth).toStringWithFormatYyyyDashMmDashDd());
        }

        String profileImage = callback.getProfilePicture();
        if (!StringUtil.isNullOrEmpty(profileImage)) {
            demographicPersDetailsPayloadDTO.setProfilePhoto(profileImage);
        }

        updatableDemographicDTO.getPayload().getDemographics().getPayload().setPersonalDetails(demographicPersDetailsPayloadDTO);

        DemographicAddressPayloadDTO demographicAddressPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getAddress();
        // save address
        if (demographicAddressPayloadDTO == null) {
            demographicAddressPayloadDTO = new DemographicAddressPayloadDTO();
        }
        EditText phoneNumberEditText = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        String phoneNumber = phoneNumberEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(phoneNumber)) {
            // 'de-format' before saving to model
            demographicAddressPayloadDTO.setPhone(StringUtil.revertToRawPhoneFormat(phoneNumber));
        }
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setAddress(demographicAddressPayloadDTO);
        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        return updatableDemographicDTO;
    }

    public interface UpdateProfilePictureListener {
        String getProfilePicture();

        void loadPictureFragment();
    }
}
