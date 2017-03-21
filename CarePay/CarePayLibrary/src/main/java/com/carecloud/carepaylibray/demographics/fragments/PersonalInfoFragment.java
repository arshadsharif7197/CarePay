package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
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
 * Created by jorge on 27/02/17.
 */

public class PersonalInfoFragment extends CheckInDemographicsBaseFragment {

    private DemographicDTO demographicDTO;
    private UpdateProfilePictureListener profilePicturelistener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        initialiseUIFields(view);
        setTypefaces(view);
        formatEditText(view);
        initViewFromModels(view);
        checkIfEnableButton(view);
        (view.findViewById(R.id.toolbar_layout)).setVisibility(View.INVISIBLE);
        stepProgressBar.setCurrentProgressDot(0);
        checkInNavListener.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 1);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        profilePicturelistener.loadPictureFragment();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SystemUtil.hideSoftKeyboard(getActivity());
                ((ScrollView)view.findViewById(R.id.reviewdemographicsPersonalContainer)).smoothScrollTo(0,0);
            }
        }, 300);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            profilePicturelistener = (UpdateProfilePictureListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement UpdateProfilePictureListener");
        }
    }

    private void formatEditText(final View view) {
        final DemographicMetadataEntityAddressDTO addressMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.address;
        final DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.personalDetails;
        setTextListener(persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.firstName.validations.get(0).getErrorMessage(),
                R.id.reviewdemogrFirstNameTextInput, R.id.reviewdemogrFirstNameEdit, view);
        setTextFocusListener(R.id.reviewdemogrFirstNameEdit,view);
        setTextListener(persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.lastName.validations.get(0).getErrorMessage(),
                R.id.reviewdemogrLastNameTextInput, R.id.reviewdemogrLastNameEdit, view);
        setTextFocusListener(R.id.reviewdemogrLastNameEdit,view);

        final TextInputLayout doblabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrDOBTextInput);
        final EditText dobEditText = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dobEditText.setSelection(dobEditText.length());
            }
        });

        dobEditText.addTextChangedListener(new TextWatcher() {
            int prevLen = 0;

            @Override
            public void beforeTextChanged(CharSequence dob, int start, int count, int after) {
                prevLen = dob.length();
            }

            @Override
            public void onTextChanged(CharSequence dob, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String dob = dobEditText.getText().toString();
                if (!StringUtil.isNullOrEmpty(dob)) {
                    doblabel.setErrorEnabled(false);
                    doblabel.setError(null);
                }
                StringUtil.autoFormatDateOfBirth(editable, prevLen);
                checkIfEnableButton(view);
            }
        });
        dobEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
        setTextFocusListener(R.id.revewidemogrDOBEdit,view);

        final TextInputLayout phoneNumberLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        final EditText phoneNumberEditText = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        setTextFocusListener(R.id.reviewgrdemoPhoneNumberEdit,view);

        dobEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT || inputType == EditorInfo.IME_ACTION_DONE) {
                    SystemUtil.hideSoftKeyboard(getActivity());
                    dobEditText.clearFocus();
                    phoneNumberEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });



        phoneNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumberEditText.setSelection(phoneNumberEditText.length());
            }
        });

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            int len = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {
                len = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable phonenumber) {
                String phone = phoneNumberEditText.getText().toString();
                boolean isPhoneEmpty = StringUtil.isNullOrEmpty(phone);
                if (!isPhoneEmpty) {
                    phoneNumberLabel.setError(null);
                    phoneNumberLabel.setErrorEnabled(false);
                } else {
                    final String phoneNumberError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.phone.validations.get(0).getErrorMessage();
                    phoneNumberLabel.setError(phoneNumberError);
                    phoneNumberLabel.setErrorEnabled(true);
                }
                // auto-format as typing
                StringUtil.autoFormatPhone(phonenumber, len);
                checkIfEnableButton(view);
            }
        });


        setTextFocusListener(R.id.reviewdemogrMiddleNameEdit,view);

    }

    private void setTextListener(final String message, final int layOutTextLabel, final int textEditableId, final View view){
        final TextInputLayout textLayout = (TextInputLayout) view.findViewById(layOutTextLabel);
        final EditText editText = (EditText) view.findViewById(textEditableId);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean isTextEmpty = checkTextEmptyValue(textEditableId, view);
                if (!isTextEmpty) {
                    textLayout.setError(null);
                    textLayout.setErrorEnabled(false);
                } else {;
                    textLayout.setError(message);
                    textLayout.setErrorEnabled(true);
                }
                checkIfEnableButton(view);
            }
        });

    }

    private void setTextFocusListener(final int textEditableId, final View view){
        final EditText editText = (EditText) view.findViewById(textEditableId);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
    }


    private void setTypefaces(View view) {

        setLabelStyle( R.id.reviewdemogrFirstNameTextInput, R.id.reviewdemogrFirstNameEdit, view);

        setLabelStyle( R.id.reviewdemogrMiddleNameTextInputLayout, R.id.reviewdemogrMiddleNameEdit, view);

        setLabelStyle( R.id.reviewdemogrLastNameTextInput, R.id.reviewdemogrLastNameEdit, view);

        setLabelStyle( R.id.reviewdemogrDOBTextInput, R.id.revewidemogrDOBEdit, view);

        setLabelStyle(R.id.reviewdemogrPhoneNumberTextInput, R.id.reviewgrdemoPhoneNumberEdit, view);
    }

    private void initialiseUIFields(View view){
        DemographicLabelsDTO globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();
        DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.personalDetails;
        setHeaderTitle(globalLabelsMetaDTO.getDemographicsReviewPeronsonalinfoSection(), view);

        String label = persDetailsMetaDTO.properties.firstName.getLabel();
        initTextLabel(label, R.id.reviewdemogrFirstNameTextInput, R.id.reviewdemogrFirstNameEdit, view);

        label = persDetailsMetaDTO.properties.middleName.getLabel();
        initTextLabel(label, R.id.reviewdemogrMiddleNameTextInputLayout, R.id.reviewdemogrMiddleNameEdit, view);

        label = persDetailsMetaDTO.properties.lastName.getLabel();
        initTextLabel(label, R.id.reviewdemogrLastNameTextInput, R.id.reviewdemogrLastNameEdit, view);

        label = persDetailsMetaDTO.properties.lastName.getLabel();
        initTextLabel(label, R.id.reviewdemogrDOBTextInput, R.id.revewidemogrDOBEdit, view);

        DemographicMetadataEntityAddressDTO addressMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.address;
        label = addressMetaDTO.properties.phone.getLabel();
        initTextLabel(label, R.id.reviewdemogrPhoneNumberTextInput, R.id.reviewgrdemoPhoneNumberEdit, view);

        TextView optinalLabelTextView = (TextView) view.findViewById(R.id.reviewdemogrMiddleNameOptionalLabel);
        optinalLabelTextView.setText(globalLabelsMetaDTO.getDemographicsDetailsOptionalHint());

        TextView dateformatLabelTextView = (TextView) view.findViewById(R.id.dobformatlabel);
        dateformatLabelTextView.setText(globalLabelsMetaDTO.getDemographicsDetailsDobHint());

        initNextButton(null, view);
    }

    private void setLabelStyle(int layOutTextLabel, int textEditableId, View view) {
        TextInputLayout textLayout = (TextInputLayout) view.findViewById(layOutTextLabel);
        EditText editText = (EditText) view.findViewById(textEditableId);
        if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
            SystemUtil.setProximaNovaExtraboldTypefaceInput(getActivity(), textLayout);
        } else {
            SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), textLayout);
        }
    }

    private void initTextLabel(String label, int layOutTextLabel, int textEditableId, View view){
        TextInputLayout textLayout = (TextInputLayout) view.findViewById(layOutTextLabel);
        textLayout.setTag(label);
        EditText editText = (EditText) view.findViewById(textEditableId);
        editText.setTag(textLayout);
        editText.setHint(label);
    }

    private void initViewFromModels(View view) {
        DemographicAddressPayloadDTO demographicAddressPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getAddress();
        if (demographicAddressPayloadDTO != null){

            String phonenumber = demographicAddressPayloadDTO.getPhone();
            if(SystemUtil.isNotEmptyString(phonenumber)) {
                phonenumber = StringUtil.formatPhoneNumber(phonenumber);
            }
            initTextInputLayoutValue(phonenumber, R.id.reviewgrdemoPhoneNumberEdit, view);
        }

        PatientModel demographicPersDetailsPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
        if (demographicPersDetailsPayloadDTO != null) {

            //Personal Details

            String lastName = demographicPersDetailsPayloadDTO.getLastName();
            initTextInputLayoutValue(lastName, R.id.reviewdemogrLastNameEdit, view);

            String middleName = demographicPersDetailsPayloadDTO.getMiddleName();
            initTextInputLayoutValue(middleName, R.id.reviewdemogrMiddleNameEdit, view);

            String datetime = demographicPersDetailsPayloadDTO.getDateOfBirth();
            EditText dobEditText = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
            if (datetime != null) {
                String dateOfBirthString = DateUtil.getInstance().setDateRaw(datetime).toStringWithFormatMmSlashDdSlashYyyy();
                dobEditText.setText(dateOfBirthString);
                dobEditText.requestFocus();
            }

            String firstName = demographicPersDetailsPayloadDTO.getFirstName();
            initTextInputLayoutValue(firstName, R.id.reviewdemogrFirstNameEdit, view);
        }

    }

    private void initTextInputLayoutValue(String value, int textEditableId, View view){
        EditText editText = (EditText) view.findViewById(textEditableId);
        if (SystemUtil.isNotEmptyString(value)) {
            editText.setText(value);
            editText.requestFocus();

        }
    }

    private boolean isPhoneNumberValid(TextInputLayout phoneNumberLabel, EditText phoneNumberEditText) {
        DemographicMetadataEntityAddressDTO addressMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.address;

        final String phoneError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.phone.validations.get(0).getErrorMessage();
        final String phoneValidation = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : ((String) addressMetaDTO.properties.phone.validations.get(0).value);

        String phone = phoneNumberEditText.getText().toString();
            if (!StringUtil.isNullOrEmpty(phone)
                    && !ValidationHelper.isValidString(phone.trim(), phoneValidation)) {
                phoneNumberLabel.setErrorEnabled(true);
                phoneNumberLabel.setError(phoneError);
                //phoneNumberEditText.requestFocus();
                return false;
            }
         phoneNumberLabel.setError(null);
         phoneNumberLabel.setErrorEnabled(false);

        return true;
    }

    private boolean isDateOfBirthValid(TextInputLayout doblabel, EditText dobEditText) {
        DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.personalDetails;
        final String errorMessage = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.dateOfBirth.validations.get(0).getErrorMessage();
        String dob = dobEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(dob)) {
            boolean isValid = DateUtil.isValidateStringDateOfBirth(dob);
            doblabel.setErrorEnabled(!isValid);
            doblabel.setError(isValid ? null : errorMessage);
            return isValid;
        }
        return true;
    }

    private boolean checkFormatedFields(View view) {
        TextInputLayout phoneNumberLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        EditText phoneNumberEditText = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        boolean isPhoneValid = isPhoneNumberValid(phoneNumberLabel, phoneNumberEditText);
        if (!isPhoneValid) {
            //phoneNumberEditText.requestFocus();
            return false;
        }
        TextInputLayout doblabel = (TextInputLayout) view.findViewById( R.id.reviewdemogrDOBTextInput);
        EditText dobEditText = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        boolean isdobValid = isDateOfBirthValid(doblabel, dobEditText);
        if (!isdobValid) {
            //dobEditText.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected boolean passConstraints(View view) {
        boolean isPhoneEmpty = checkTextEmptyValue(R.id.reviewgrdemoPhoneNumberEdit, view);
        boolean isFirstNameEmpty = checkTextEmptyValue(R.id.reviewdemogrFirstNameEdit, view);
        boolean isLastNameEmpty = checkTextEmptyValue(R.id.reviewdemogrLastNameEdit, view);
        boolean isdobEmpty = checkTextEmptyValue(R.id.revewidemogrDOBEdit, view);

        return !isPhoneEmpty && !isdobEmpty && !isFirstNameEmpty && !isLastNameEmpty && checkFormatedFields(view);
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
            demographicPersDetailsPayloadDTO.setDateOfBirth(
                    DateUtil.getInstance().setDateRaw(dateOfBirth).toStringWithFormatYyyyDashMmDashDd()
            );
        }

        String profileImage = profilePicturelistener.getProfilePicture();
        if (!StringUtil.isNullOrEmpty(profileImage)){
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

    public interface UpdateProfilePictureListener{
        public String getProfilePicture();

        public void loadPictureFragment();
    }
}
