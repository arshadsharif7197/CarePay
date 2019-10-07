package com.carecloud.carepaylibray.demographics.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsAddressSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.smartystreets.api.us_zipcode.City;

/**
 * A simple {@link CheckInDemographicsBaseFragment} subclass.
 */
public class AddressFragment extends CheckInDemographicsBaseFragment {

    private DemographicAddressPayloadDTO demographicAddressPayloadDTO;
    private EditText cityEditText;
    private EditText stateEditText;
    private DemographicsOption selectedState = new DemographicsOption();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (demographicDTO.getPayload().getDemographics() != null) {
            demographicAddressPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getAddress();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initialiseUIFields(view);
        initViews(view);
        checkIfEnableButton(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        stepProgressBar.setCurrentProgressDot(CheckinFlowCallback.ADDRESS - 1);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, checkinFlowCallback.getTotalSteps(), CheckinFlowCallback.ADDRESS);
        checkinFlowCallback.setCurrentStep(CheckinFlowCallback.ADDRESS);
    }

    private void initViews(View view) {
        DemographicPayloadDTO demographicPayload = demographicDTO.getPayload().getDemographics().getPayload();
        DemographicsAddressSection addressSection = dataModel.getDemographic().getAddress();

        TextInputLayout address2InputLayout = view.findViewById(R.id.address2TextInputLayout);
        final EditText address2EditText = view.findViewById(R.id.addressEditText2Id);
        setUpField(address2InputLayout, address2EditText,
                addressSection.getProperties().getAddress2().isDisplayed(),
                demographicPayload.getAddress().getAddress2(),
                addressSection.getProperties().getAddress2().isRequired(),
                null);
        address2EditText.setEnabled(!StringUtil.isNullOrEmpty(demographicPayload.getAddress().getAddress1()));

        final TextInputLayout addressInputLayout = view.findViewById(R.id.address1TextInputLayout);
        final EditText addressEditText = view.findViewById(R.id.addressEditTextId);
        setUpField(addressInputLayout, addressEditText,
                addressSection.getProperties().getAddress1().isDisplayed(),
                demographicPayload.getAddress().getAddress1(),
                addressSection.getProperties().getAddress1().isRequired(),
                view.findViewById(R.id.addressLine1Required));
        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())) {
                    address2EditText.setEnabled(false);
                    address2EditText.setText("");
                    address2EditText.getOnFocusChangeListener().onFocusChange(address2EditText,
                            !StringUtil.isNullOrEmpty(address2EditText.getText().toString().trim()));
                } else {
                    address2EditText.setEnabled(true);
                }
            }
        });

        TextInputLayout zipCodeInputLayout = view.findViewById(R.id.zipCodeTextInputLayout);
        EditText zipCode = view.findViewById(R.id.zipCodeId);
        setUpField(zipCodeInputLayout, zipCode,
                addressSection.getProperties().getZipcode().isDisplayed(),
                StringUtil.formatZipCode(demographicPayload.getAddress().getZipcode()),
                addressSection.getProperties().getZipcode().isRequired(),
                view.findViewById(R.id.zipCodeRequired));
        zipCode.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeInputLayout, getZipCodeFocusListener(zipCode)));
        zipCode.addTextChangedListener(zipInputFormatter);


        TextInputLayout cityInputLayout = view.findViewById(R.id.cityTextInputLayout);
        cityEditText = view.findViewById(R.id.cityId);
        setUpField(cityInputLayout, cityEditText,
                addressSection.getProperties().getCity().isDisplayed(),
                demographicPayload.getAddress().getCity(),
                addressSection.getProperties().getCity().isRequired(),
                view.findViewById(R.id.cityRequired));


        TextInputLayout stateInputLayout = view.findViewById(R.id.stateTextInputLayout);
        stateEditText = view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView);
        String state = demographicPayload.getAddress().getState();
        setUpField(stateInputLayout, stateEditText,
                addressSection.getProperties().getState().isDisplayed(),
                state,
                addressSection.getProperties().getState().isRequired(),
                view.findViewById(R.id.stateRequired));
        stateEditText.setOnClickListener(
                getSelectOptionsListener(addressSection.getProperties().getState().getOptions(),
                        getDefaultOnOptionsSelectedListener(stateEditText, selectedState,
                                view.findViewById(R.id.stateRequired)),
                        Label.getLabel("demographics_documents_title_select_state")));

        initSelectableInput(stateEditText, selectedState, state,
                view.findViewById(R.id.stateRequired),
                addressSection.getProperties().getState().getOptions());
        stateEditText.getOnFocusChangeListener().onFocusChange(stateEditText,
                !StringUtil.isNullOrEmpty(stateEditText.getText().toString().trim()));
    }

    private void initialiseUIFields(View view) {
        setHeaderTitle(Label.getLabel("demographics_address_section"),
                Label.getLabel("demographics_address_heading"),
                Label.getLabel("demographics_address_subheading"),
                view);
        initNextButton(view);

    }

    @Override
    protected DemographicDTO updateDemographicDTO(View view) {

        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        updatableDemographicDTO.setPayload(new DemographicPayloadResponseDTO());
        updatableDemographicDTO.getPayload().setDemographics(new DemographicPayloadInfoDTO());
        updatableDemographicDTO.getPayload().getDemographics().setPayload(new DemographicPayloadDTO());

        String address1 = ((EditText) view.findViewById(R.id.addressEditTextId)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(address1)) {
            demographicAddressPayloadDTO.setAddress1(address1);
        }

        String address2 = ((EditText) view.findViewById(R.id.addressEditText2Id)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(address2)) {
            demographicAddressPayloadDTO.setAddress2(address2);
        }

        String zipCode = ((EditText) view.findViewById(R.id.zipCodeId)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(zipCode)) {
            // 'de-format' before saving to model
            demographicAddressPayloadDTO.setZipcode(StringUtil.revertZipToRawFormat(zipCode));
        }

        String city = ((EditText) view.findViewById(R.id.cityId)).getText().toString().trim();
        if (!StringUtil.isNullOrEmpty(city)) {
            demographicAddressPayloadDTO.setCity(city);
        }
        String state = selectedState.getName();
        if (!StringUtil.isNullOrEmpty(state)) {
            demographicAddressPayloadDTO.setState(state);
        }

        updatableDemographicDTO.getPayload().getDemographics().getPayload().setAddress(demographicAddressPayloadDTO);
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        return updatableDemographicDTO;
    }

    @Override
    protected void replaceTranslatedOptionsValues() {

    }

    @Override
    protected boolean passConstraints(View view) {
        try {
            String address1Value = ((EditText) view.findViewById(R.id.addressEditTextId)).getText().toString();
            if (validateField(view, dataModel.getDemographic().getAddress().getProperties()
                            .getAddress1().isRequired(), address1Value, R.id.address1Container,
                    R.id.address1TextInputLayout, isUserAction())) return false;

            String address2Value = ((EditText) view.findViewById(R.id.addressEditText2Id)).getText().toString();
            if (validateField(view, dataModel.getDemographic().getAddress().getProperties()
                            .getAddress2().isRequired(), address2Value, R.id.address2Container,
                    R.id.address2TextInputLayout, isUserAction())) return false;

            EditText zipCode = view.findViewById(R.id.zipCodeId);
            String zipCodeValue = zipCode.getText().toString();
            if (validateField(view, dataModel.getDemographic().getAddress().getProperties()
                            .getZipcode().isRequired(), zipCodeValue, R.id.zipCodeContainer,
                    R.id.zipCodeTextInputLayout, isUserAction())) return false;

            TextInputLayout zipLayout = view.findViewById(R.id.zipCodeTextInputLayout);
            if (zipLayout.getVisibility() == View.VISIBLE &&
                    !StringUtil.isNullOrEmpty(zipCode.getText().toString().trim()) &&
                    !ValidationHelper.isValidString(zipCode.getText().toString().trim(), ValidationHelper.ZIP_CODE_PATTERN)) {
                setFieldError(zipLayout, Label.getLabel("demographics_zip_code_validation_msg"), isUserAction());
                if (isUserAction()) {
                    showErrorViews(true, (ViewGroup) view.findViewById(R.id.zipCodeContainer));
                }
                return false;
            } else {
                unsetFieldError(zipLayout);
            }

            String cityValue = ((EditText) view.findViewById(R.id.cityId)).getText().toString();
            if (validateField(view, dataModel.getDemographic().getAddress().getProperties()
                            .getCity().isRequired(), cityValue, R.id.cityContainer,
                    R.id.cityTextInputLayout, isUserAction())) return false;

            String stateValue = selectedState.getName();
            if (validateField(view, dataModel.getDemographic().getAddress().getProperties()
                            .getState().isRequired(), stateValue, R.id.stateContainer,
                    R.id.stateTextInputLayout, isUserAction())) return false;

            return true;
        } finally {
            setUserAction(false);
        }
    }

    private View.OnFocusChangeListener getZipCodeFocusListener(final EditText zipCode) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    getCityAndState(zipCode.getText().toString());
                }
            }
        };
    }

    /**
     * Background task to call smarty streets zip code lookup.
     * The response is a com.smartystreets.api.us_zipcode.City object,
     * that contains city, mailableCity, stateAbbreviation and state.
     */
    private void getCityAndState(String zipcode) {

        new AsyncTask<String, Void, City>() {

            @Override
            protected City doInBackground(String... params) {
                return AddressUtil.getCityAndStateByZipCode(params[0]);
            }

            @Override
            protected void onPostExecute(City city) {
                super.onPostExecute(city);
                if (city != null) {

                    cityEditText.setText(city.getCity());
                    String stateAbbr = city.getStateAbbreviation();
                    stateEditText.setText(stateAbbr);
                    selectedState.setLabel(stateAbbr);
                    selectedState.setName(stateAbbr);
                    if (getView() != null) {
                        checkIfEnableButton(getView());
                    }
                }
            }

        }.execute(zipcode);
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review_demographic_address;
    }
}
