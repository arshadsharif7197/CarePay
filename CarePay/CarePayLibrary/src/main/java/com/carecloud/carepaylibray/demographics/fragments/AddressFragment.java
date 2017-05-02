package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
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

import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;

/**
 * A simple {@link CheckInDemographicsBaseFragment} subclass.
 */
public class AddressFragment extends CheckInDemographicsBaseFragment {

    private static final String TAG = AddressFragment.class.getSimpleName();

    private DemographicDTO demographicDTO;
    private DemographicMetadataEntityAddressDTO addressMetaDTO;
    private DemographicAddressPayloadDTO demographicAddressPayloadDTO;

    private City smartyStreetsResponse;
    private String stateAbbr = null;

    private boolean isAddressEmpty;
    private boolean isCityEmpty;
    private boolean isZipEmpty;

    private EditText cityEditText;
    private TextView stateEditText;

    private View.OnClickListener editStateListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            showDialogState(AddressUtil.states, Label.getLabel("demographics_documents_title_select_state"),
                    Label.getLabel("demographics_cancel_label"));
        }
    };

    public AddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        addressMetaDTO = demographicDTO.getMetadata().getDataModels().getDemographic().getAddress();
        if (demographicDTO.getPayload().getDemographics() != null) {
            demographicAddressPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getAddress();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView  = super.onCreateView(inflater, container, savedInstanceState);
        initialiseUIFields(mainView);
        formatEditText(mainView);

        initViewFromModels(mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        hideSoftKeyboard(getActivity());
    }

    @Override
    public void onResume(){
        super.onResume();
        stepProgressBar.setCurrentProgressDot(1);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 2);
        checkinFlowCallback.setCurrentStep(2);
    }


    private boolean checkFormatedFields(View view) {

        boolean isZipValid = isZipCodeValid(view);
        if (!isZipValid) {
            view.findViewById(R.id.zipCodeId).requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Background task to call smarty streets zip code lookup.
     * The response is a com.smartystreets.api.us_zipcode.City object,
     * that contains city, mailableCity, stateAbbreviation and state.
     */
    private void getCityAndState(final View views, String zipcode) {

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
                    stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    stateEditText.setText(stateAbbr);
                    checkIfEnableButton(getView());
                }
            }

        }.execute(zipcode);
    }

    /**
     * Format edit text
     */
    public void formatEditText(final View mainView){
        TextInputLayout addressInputLayout = (TextInputLayout) mainView.findViewById(R.id.address1TextInputLayout);
        EditText address = (EditText) mainView.findViewById(R.id.addressEditTextId);
        address.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(addressInputLayout, null));

        TextInputLayout address2InputLayout = (TextInputLayout) mainView.findViewById(R.id.address2TextInputLayout);
        mainView.findViewById(R.id.addressEditText2Id).setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(address2InputLayout, null));

        TextInputLayout zipCodeInputLayout = (TextInputLayout) mainView.findViewById(R.id.zipCodeTextInputLayout);
        EditText zipCode = (EditText) mainView.findViewById(R.id.zipCodeId);
        zipCode.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeInputLayout, new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    getCityAndState(mainView, ((EditText) mainView.findViewById(R.id.zipCodeId)).getText().toString());
                }
            }
        }));

        TextInputLayout cityInputLayout = (TextInputLayout) mainView.findViewById(R.id.cityTextInputLayout);
        EditText city = (EditText) mainView.findViewById(R.id.cityId);
        city.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(cityInputLayout, null));


        TextInputLayout stateInputLayout = (TextInputLayout) mainView.findViewById(R.id.stateTextInputLayout);
        EditText state = (EditText) mainView.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView);
        state.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(cityInputLayout, null));

        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isAddressEmpty = StringUtil.isNullOrEmpty(((EditText) mainView.findViewById(R.id.addressEditTextId)).getText().toString());
                if (!isAddressEmpty) {
                    ((TextInputLayout) mainView.findViewById(R.id.address1TextInputLayout)).setError(null);
                    ((TextInputLayout) mainView.findViewById(R.id.address1TextInputLayout)).setErrorEnabled(false);
                } else {
                    try {
                        final String lastNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.getProperties().getAddress1().getValidations().get(0).getErrorMessage();
                        ((TextInputLayout) mainView.findViewById(R.id.address1TextInputLayout)).setError(lastNameError);
                        ((TextInputLayout) mainView.findViewById(R.id.address1TextInputLayout)).setErrorEnabled(true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                checkIfEnableButton(mainView);
            }
        });

        zipCode.addTextChangedListener(new TextWatcher() {
            int prevLen = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {
                prevLen = charSequence.length();
                ((EditText) mainView.findViewById(R.id.zipCodeId)).setSelection(charSequence.length());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String zip = ((EditText) mainView.findViewById(R.id.zipCodeId)).getText().toString();

                isZipEmpty = StringUtil.isNullOrEmpty(zip);
                if (!isZipEmpty) {
                    ( (TextInputLayout) mainView.findViewById(R.id.zipCodeTextInputLayout)).setError(null);
                    ( (TextInputLayout) mainView.findViewById(R.id.zipCodeTextInputLayout)).setErrorEnabled(false);
                } else {
                    try {
                        final String zipcodeError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.getProperties().getZipcode().getValidations().get(0).getErrorMessage();
                        ( (TextInputLayout) mainView.findViewById(R.id.zipCodeTextInputLayout)).setError(zipcodeError);
                        ((TextInputLayout) mainView.findViewById(R.id.zipCodeTextInputLayout)).setErrorEnabled(true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                StringUtil.autoFormatZipcode(editable, prevLen);
                checkIfEnableButton(mainView);
            }
        });

        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isCityEmpty = StringUtil.isNullOrEmpty(cityEditText.getText().toString());
                if (!isCityEmpty) {
                    ((TextInputLayout) mainView.findViewById(R.id.cityTextInputLayout)).setError(null);
                    ((TextInputLayout) mainView.findViewById(R.id.cityTextInputLayout)).setErrorEnabled(false);
                } else {
                    final String lastNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.getProperties().getCity().getValidations().get(0).getErrorMessage();
                    ((TextInputLayout) mainView.findViewById(R.id.cityTextInputLayout)).setError(lastNameError);
                    ((TextInputLayout) mainView.findViewById(R.id.cityTextInputLayout)).setErrorEnabled(true);
                }

                checkIfEnableButton(mainView);
            }
        });
    }

    /**
     * Validate zip code
     */
    private boolean isZipCodeValid(View view) {
        String zipCode = ((EditText) view.findViewById(R.id.zipCodeId)).getText().toString();
        if (StringUtil.isNullOrEmpty(zipCode)) {
            return true;
        }
        // apply validate from backend
        return ValidationHelper.applyPatternValidationToWrappedEdit(((EditText) view.findViewById(R.id.zipCodeId)),
                ((TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout)),
                addressMetaDTO.getProperties().getZipcode(),
                null);
    }

    /**
     * init view from models
     */
    private void initViewFromModels(View view) {

        if (demographicAddressPayloadDTO != null) {

            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getAddress1())) {
                String address1 = demographicAddressPayloadDTO.getAddress1();
                initializeInputLayoutValue(address1, R.id.addressEditTextId, view);
            }else{
                isAddressEmpty = true;
            }

            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getAddress2())) {
                initializeInputLayoutValue(demographicAddressPayloadDTO.getAddress2(), R.id.addressEditText2Id, view);
            }

            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getCity()) || !((EditText) view.findViewById(R.id.cityId)).getText().toString().isEmpty()) {
                initializeInputLayoutValue(demographicAddressPayloadDTO.getCity(), R.id.cityId, view);
            }else{
                isCityEmpty = true;
            }

            String state = demographicAddressPayloadDTO.getState();
            if (SystemUtil.isNotEmptyString(state) || !((TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).getText().toString().isEmpty()) {
//                ((TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).setText(state);
                initializeInputLayoutValue(demographicAddressPayloadDTO.getState(), R.id.reviewDemographicsStateAutoCompleteTextView, view);
            } else {
//                ((TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).setText(
//                        Label.getLabel("demographics_choose"));
                initializeInputLayoutValue(Label.getLabel("demographics_choose"), R.id.reviewDemographicsStateAutoCompleteTextView, view);
            }

            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getZipcode())) {
                initializeInputLayoutValue(demographicAddressPayloadDTO.getZipcode(), R.id.zipCodeId, view);
            }else{
                isZipEmpty = true;
            }
        } else {
            Log.v(TAG, "Demographic adress is empty ");
        }

        checkIfEnableButton(view);
    }

    /**
     * Init text input layout
     */
    private void initializeInputLayoutValue(String value, int textEditableId, View view){
        EditText editText = (EditText) view.findViewById(textEditableId);
        if (SystemUtil.isNotEmptyString(value)) {
            editText.setText(value);
            editText.getOnFocusChangeListener().onFocusChange(editText, true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Show dialog state
     */
    private void showDialogState(final String[] dataArray, String title, String cancelLabel) {
        SystemUtil.showChooseDialog(
                getActivity(),
                dataArray,
                title,
                cancelLabel,
                ((TextView) findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)),

                new SystemUtil.OnClickItemCallback() {
                    @Override
                    public void executeOnClick(TextView destination, String selectedOption) {
                        ((TextView) findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).setText(selectedOption);
                        checkIfEnableButton(getView());
                    }
                });
    }

    /**
     * Initialize ui fields
     */
    private void initialiseUIFields(View view) {
        setHeaderTitle(Label.getLabel("demographics_address_section"),
                Label.getLabel("demographics_address_heading"),
                Label.getLabel("demographics_address_subheading"),
                view);
        initNextButton(view);

        cityEditText = (EditText) view.findViewById(R.id.cityId);
        stateEditText = (TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView);

        stateEditText.setOnClickListener(editStateListener);
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
        String state = ((TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).getText().toString();
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
        boolean isStateValid = ! Label.getLabel("demographics_choose").equals(((TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).getText().toString());
        return !isZipEmpty && !isAddressEmpty && !isCityEmpty && isStateValid && checkFormatedFields(view);
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review_demographic_address;
    }
}
