package com.carecloud.carepaylibray.demographics.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsAddressSection;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.smartystreets.api.us_zipcode.City;

/**
 * A simple {@link CheckInDemographicsBaseFragment} subclass.
 */
public class AddressFragment extends CheckInDemographicsBaseFragment {

    private DemographicDTO demographicDTO;
    private DemographicDataModel dataModel;
    private DemographicAddressPayloadDTO demographicAddressPayloadDTO;

    private City smartyStreetsResponse;

    private EditText cityEditText;
    private TextView stateEditText;

    private DemographicsOption selectedState = new DemographicsOption();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        dataModel = demographicDTO.getMetadata().getNewDataModel();

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
    public void onResume(){
        super.onResume();
        stepProgressBar.setCurrentProgressDot(1);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 2);
        checkinFlowCallback.setCurrentStep(2);
    }


    /**
     * Background task to call smarty streets zip code lookup.
     * The response is a com.smartystreets.api.us_zipcode.City object,
     * that contains city, mailableCity, stateAbbreviation and state.
     */
    private void getCityAndState(String zipcode) {

        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                smartyStreetsResponse = AddressUtil.getCityAndStateByZipCode(params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                if (smartyStreetsResponse != null) {

                    cityEditText.setText(smartyStreetsResponse.getCity());
                    String stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    stateEditText.setText(stateAbbr);
                    if(getView()!=null) {
                        checkIfEnableButton(getView());
                    }
                }
            }

        }.execute(zipcode);
    }

    private void initViews(View view){
        DemographicPayloadDTO demographicPayload = demographicDTO.getPayload().getDemographics().getPayload();
        DemographicsAddressSection addressSection = dataModel.getDemographic().getAddress();

        TextInputLayout addressInputLayout = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        EditText address = (EditText) view.findViewById(R.id.addressEditTextId);
        address.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(addressInputLayout, null));
        setVisibility(addressInputLayout, addressSection.getProperties().getAddress1().isDisplayed());
        address.setText(demographicPayload.getAddress().getAddress1());
        address.getOnFocusChangeListener().onFocusChange(address, !StringUtil.isNullOrEmpty(address.getText().toString()));
        if(addressSection.getProperties().getAddress1().isRequired()){
            address.addTextChangedListener(getValidateEmptyTextWatcher(addressInputLayout));
        }


        TextInputLayout address2InputLayout = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        EditText address2 = (EditText) view.findViewById(R.id.addressEditText2Id);
        address2.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(address2InputLayout, null));
        setVisibility(address2InputLayout, addressSection.getProperties().getAddress2().isDisplayed());
        address2.setText(demographicPayload.getAddress().getAddress2());
        address2.getOnFocusChangeListener().onFocusChange(address2, !StringUtil.isNullOrEmpty(address2.getText().toString()));
        if(addressSection.getProperties().getAddress2().isRequired()){
            address2.addTextChangedListener(getValidateEmptyTextWatcher(address2InputLayout));
            View address2Optional = view.findViewById(R.id.demogrAddressOptionalLabel);
            address2Optional.setVisibility(View.GONE);
        }


        TextInputLayout zipCodeInputLayout = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        EditText zipCode = (EditText) view.findViewById(R.id.zipCodeId);
        zipCode.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeInputLayout, getZipCodeFocusListener(zipCode)));
        setVisibility(zipCodeInputLayout, addressSection.getProperties().getZipcode().isDisplayed());
        zipCode.addTextChangedListener(zipInputFormatter);
        zipCode.setText(demographicPayload.getAddress().getZipcode());
        zipCode.getOnFocusChangeListener().onFocusChange(zipCode, !StringUtil.isNullOrEmpty(zipCode.getText().toString()));
        if(addressSection.getProperties().getZipcode().isRequired()){
            zipCode.addTextChangedListener(getValidateEmptyTextWatcher(zipCodeInputLayout));
        }else{
            zipCode.addTextChangedListener(clearValidationErrorsOnTextChange(zipCodeInputLayout));
        }


        TextInputLayout cityInputLayout = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        cityEditText = (EditText) view.findViewById(R.id.cityId);
        cityEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(cityInputLayout, null));
        setVisibility(cityInputLayout, addressSection.getProperties().getCity().isDisplayed());
        cityEditText.setText(demographicPayload.getAddress().getCity());
        cityEditText.getOnFocusChangeListener().onFocusChange(cityEditText, !StringUtil.isNullOrEmpty(cityEditText.getText().toString()));
        if(addressSection.getProperties().getCity().isRequired()){
            cityEditText.addTextChangedListener(getValidateEmptyTextWatcher(cityInputLayout));
        }


        TextInputLayout stateInputLayout = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
        stateEditText = (EditText) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView);
        stateEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(stateInputLayout, null));
        setVisibility(stateInputLayout, addressSection.getProperties().getState().isDisplayed());
        stateEditText.setOnClickListener(
                getSelectOptionsListener(addressSection.getProperties().getState().getOptions(),
                getDefaultOnOptionsSelectedListener(stateEditText, selectedState),
                Label.getLabel("demographics_documents_title_select_state")));

        String state = demographicPayload.getAddress().getState();
        initSelectableInput(stateEditText, selectedState, state);
        stateEditText.getOnFocusChangeListener().onFocusChange(stateEditText, true);
    }


    private TextWatcher zipInputFormatter = new TextWatcher() {
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
            StringUtil.autoFormatZipcode(editable, lastLength);
        }
    };


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

        String address1 = ((EditText) view.findViewById(R.id.addressEditTextId)).getText().toString();
        if (!StringUtil.isNullOrEmpty(address1)) {
            demographicAddressPayloadDTO.setAddress1(address1);
        }

        String address2 = ((EditText) view.findViewById(R.id.addressEditText2Id)).getText().toString();
        if (!StringUtil.isNullOrEmpty(address2)) {
            demographicAddressPayloadDTO.setAddress2(address2);
        }

        String zipCode =  ((EditText) view.findViewById(R.id.zipCodeId)).getText().toString();
        if (!StringUtil.isNullOrEmpty(zipCode)) {
            // 'de-format' before saving to model
            demographicAddressPayloadDTO.setZipcode(StringUtil.revertZipToRawFormat(zipCode));
        }

        String city = ((EditText) view.findViewById(R.id.cityId)).getText().toString();
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
    protected boolean passConstraints(View view) {
        if(dataModel.getDemographic().getAddress().getProperties().getAddress1().isRequired()
                && checkTextEmptyValue(R.id.addressEditTextId, view)){
            return false;
        }
        if(dataModel.getDemographic().getAddress().getProperties().getAddress2().isRequired()
                && checkTextEmptyValue(R.id.addressEditText2Id, view)){
            return false;
        }
        if(dataModel.getDemographic().getAddress().getProperties().getZipcode().isRequired()
                && checkTextEmptyValue(R.id.zipCodeId, view)){
            return false;
        }
        if(dataModel.getDemographic().getAddress().getProperties().getCity().isRequired()
                && checkTextEmptyValue(R.id.cityId, view)){
            return false;
        }
        if(dataModel.getDemographic().getAddress().getProperties().getState().isRequired()
                && StringUtil.isNullOrEmpty(selectedState.getName())){
            return false;
        }

        EditText zipCode = (EditText) view.findViewById(R.id.zipCodeId);
        if(!StringUtil.isNullOrEmpty(zipCode.getText().toString()) && !ValidationHelper.isValidString(zipCode.getText().toString(), ValidationHelper.ZIP_CODE_PATTERN)){
            TextInputLayout zipLayout = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
            zipLayout.setErrorEnabled(true);
            zipLayout.setError(Label.getLabel("demographics_zip_code_validation_msg"));
            return false;
        }

        return true;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review_demographic_address;
    }
}
