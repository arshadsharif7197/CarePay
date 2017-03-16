package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
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

import java.util.Arrays;

import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;

/**
 * A simple {@link CheckInDemographicsBaseFragment} subclass.
 */
public class AddressFragment extends CheckInDemographicsBaseFragment {

    private static final String TAG = AddressFragment.class.getSimpleName();

    private DemographicDTO demographicDTO;
    private DemographicMetadataEntityAddressDTO addressMetaDTO;
    private DemographicAddressPayloadDTO demographicAddressPayloadDTO;
    private DemographicLabelsDTO globalLabelsMetaDTO;


    private City smartyStreetsResponse;
    private String stateAbbr = null;
    private boolean isPractice;

    private boolean isAddressEmpty;
    private boolean isCityEmpty;
    private boolean isZipEmpty;

    private EditText cityEditText;
    private TextView stateEditText;
    private View.OnClickListener editStateListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {

            showDialogState(AddressUtil.states, globalLabelsMetaDTO.getDemographicsTitleSelectState(), globalLabelsMetaDTO.getDemographicsCancelLabel());

        }
    };


    public AddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View mainView  = super.onCreateView(inflater, container, savedInstanceState);

        isPractice = getApplicationMode().getApplicationType().equals(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);

        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        addressMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.address;
        globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();
        if (demographicDTO.getPayload().getDemographics() != null) {
            demographicAddressPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getAddress();
        }

        initialiseUIFields(mainView);
        formatEditText(mainView);
        setTypefaces(mainView);

        initViewFromModels(mainView);
        stepProgressBar.setCurrentProgressDot(1);
        checkInNavListener.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 2);
        return mainView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        hideSoftKeyboard(getActivity());
    }





    private boolean checkFormatedFields(View view) {

        boolean isZipValid = isZipCodeValid(view);
        if (!isZipValid) {
            ((EditText) view.findViewById(R.id.zipCodeId)).requestFocus();
            return false;
        }


        return true;
    }


    protected void setChangeFocusListeners(View view) {

        ((EditText) view.findViewById(R.id.zipCodeId)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
                if (!hasFocus) { // for SmartyStreets
                    getCityAndState(view, ((EditText) view.findViewById(R.id.zipCodeId)).getText().toString());
                }
            }
        });

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

                }
            }


        }.execute(zipcode);
    }

    /**
     * Format edit text
     */
    public void formatEditText(final View mainView){


        ((TextInputLayout) mainView.findViewById(R.id.address1TextInputLayout)).setTag(addressMetaDTO.properties.address1.getLabel());
        ((EditText) mainView.findViewById(R.id.addressEditTextId)).setTag( ((TextInputLayout) mainView.findViewById(R.id.address1TextInputLayout)));


        ((TextInputLayout) mainView.findViewById(R.id.address2TextInputLayout)).setTag(addressMetaDTO.properties.address2.getLabel());
        ((EditText) mainView.findViewById(R.id.addressEditText2Id)).setTag(((TextInputLayout) mainView.findViewById(R.id.address2TextInputLayout)));


        ((TextInputLayout) mainView.findViewById(R.id.zipCodeTextInputLayout)).setTag(addressMetaDTO.properties.zipcode.getLabel());
        ((EditText) mainView.findViewById(R.id.zipCodeId)).setTag(((TextInputLayout) mainView.findViewById(R.id.zipCodeTextInputLayout)));


        ((TextInputLayout) mainView.findViewById(R.id.cityTextInputLayout)).setTag(addressMetaDTO.properties.city.getLabel());
        ((EditText) mainView.findViewById(R.id.cityId)).setTag(((TextInputLayout) mainView.findViewById(R.id.cityTextInputLayout)));

        ((TextView) mainView.findViewById(R.id.stateTextInputLayout)).setText(addressMetaDTO.properties.state.getLabel().toUpperCase());

        ((EditText) mainView.findViewById(R.id.addressEditTextId)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        ((EditText) mainView.findViewById(R.id.addressEditText2Id)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        ((EditText) mainView.findViewById(R.id.zipCodeId)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
                if (!bool) { // for SmartyStreets
                    getCityAndState(mainView, ((EditText) mainView.findViewById(R.id.zipCodeId)).getText().toString());
                }
            }
        });

        ((EditText) mainView.findViewById(R.id.cityId)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });




        ((EditText) mainView.findViewById(R.id.addressEditTextId)).addTextChangedListener(new TextWatcher() {
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
                        final String lastNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.address1.validations.get(0).getErrorMessage();
                        ((TextInputLayout) mainView.findViewById(R.id.address1TextInputLayout)).setError(lastNameError);
                        ((TextInputLayout) mainView.findViewById(R.id.address1TextInputLayout)).setErrorEnabled(true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                checkIfEnableButton(mainView);
            }
        });



        ((EditText) mainView.findViewById(R.id.zipCodeId)).addTextChangedListener(new TextWatcher() {
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
                        final String zipcodeError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.zipcode.validations.get(0).getErrorMessage();
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



        ((EditText) mainView.findViewById(R.id.cityId)).addTextChangedListener(new TextWatcher() {
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
                    final String lastNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.city.validations.get(0).getErrorMessage();
                    ((TextInputLayout) mainView.findViewById(R.id.cityTextInputLayout)).setError(lastNameError);
                    ((TextInputLayout) mainView.findViewById(R.id.cityTextInputLayout)).setErrorEnabled(true);
                }

                checkIfEnableButton(mainView);
            }
        });


        setChangeFocusListeners(mainView);

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
                addressMetaDTO.properties.zipcode,
                null);
    }


    /**
     * Set text listener
     */
    private void addTextListener(final String message, final int layOutTextLabel, final int textEditableId, final View view){
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

    /**
     * Set edit text
     */
    private void addEditTexts(View view) {

        ((TextInputLayout) view.findViewById(R.id.address1TextInputLayout)).setTag(addressMetaDTO.properties.address1.getLabel());

    }


    /**
     * init view from models
     */
    private void initViewFromModels(View view) {

        if (demographicAddressPayloadDTO != null) {

            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getAddress1())) {

                String adress1 = demographicAddressPayloadDTO.getAddress1();
                initializeInputLayoutValue(adress1, R.id.addressEditTextId, view);

            }

            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getAddress2())) {
                ((EditText) view.findViewById(R.id.addressEditText2Id)).setText(demographicAddressPayloadDTO.getAddress2());
                ((EditText) view.findViewById(R.id.addressEditText2Id)).requestFocus();
            }

            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getCity()) || !((EditText) view.findViewById(R.id.cityId)).getText().toString().isEmpty()) {
                ((EditText) view.findViewById(R.id.cityId)).setText(demographicAddressPayloadDTO.getCity());
                ((EditText) view.findViewById(R.id.cityId)).requestFocus();
            }


            String state = demographicAddressPayloadDTO.getState();
            if (SystemUtil.isNotEmptyString(state) || !((TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).getText().toString().isEmpty()) {
                ((TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).setText(state);
            } else {
                ((TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }

            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getZipcode())) {
                ((EditText) view.findViewById(R.id.zipCodeId)).setText(demographicAddressPayloadDTO.getZipcode());
                ((EditText) view.findViewById(R.id.zipCodeId)).requestFocus();
            }


        } else {
            Log.v(TAG, "Demographic adress is empty ");
        }

    }


    /**
     * Init text input layout
     */
    private void initializeInputLayoutValue(String value, int textEditableId, View view){
        EditText editText = (EditText) view.findViewById(textEditableId);
        if (SystemUtil.isNotEmptyString(value)) {
            editText.setText(value);
            editText.requestFocus();

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
     * Show alert dialog
     */
    private void showAlertDialogWithListview(final String[] dataArray, String title, String cancelLabel, final int selectedDataArray) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton(cancelLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int listener) {
                dialogInterface.dismiss();
            }
        });
        View customView = LayoutInflater.from(getActivity()).inflate(
                R.layout.alert_list_layout, (ViewGroup) getView(), false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter alertAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(dataArray));
        listView.setAdapter(alertAdapter);
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long listener) {
                switch (selectedDataArray) {

                    case 4:
                        stateAbbr = dataArray[position];
                        ((TextView) findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).setText(stateAbbr);
                        break;
                    default:
                        break;
                }
                checkIfEnableButton(view);
                alert.dismiss();
            }
        });
    }




    /**
     * Set type faces
     */
    public void setTypefaces(View view) {

        setLabelStyle( R.id.address1TextInputLayout, R.id.addressEditTextId, view);


        if (!StringUtil.isNullOrEmpty(((EditText) view.findViewById(R.id.addressEditTextId)).getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), ((TextInputLayout) view.findViewById(R.id.address1TextInputLayout)));
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), ((TextInputLayout) view.findViewById(R.id.address1TextInputLayout)));
        }


        if (!StringUtil.isNullOrEmpty(((EditText) view.findViewById(R.id.addressEditText2Id)).getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), ((TextInputLayout) view.findViewById(R.id.address2TextInputLayout)));
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), ((TextInputLayout) view.findViewById(R.id.address2TextInputLayout)));
        }
        if (!StringUtil.isNullOrEmpty(((EditText) view.findViewById(R.id.zipCodeId)).getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), ((TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout)));
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), ((TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout)));
        }

        if (!StringUtil.isNullOrEmpty(((EditText) view.findViewById(R.id.cityId)).getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), ((TextInputLayout) view.findViewById(R.id.cityTextInputLayout)));
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), ((TextInputLayout) view.findViewById(R.id.cityTextInputLayout)));
        }

        setProximaNovaExtraboldTypeface(getActivity(), ((TextView) view.findViewById(R.id.stateTextInputLayout)));


    }

    /**
     * Init text labels
     */
    private void initTextLabel(String label, int layOutTextLabel, int textEditableId, View view){
        TextInputLayout textLayout = (TextInputLayout) view.findViewById(layOutTextLabel);
        textLayout.setTag(label);
        EditText editText = (EditText) view.findViewById(textEditableId);
        editText.setTag(textLayout);
        editText.setHint(label);
    }

    /**
     * set label style
     */
    private void setLabelStyle(int layOutTextLabel, int textEditableId, View view) {
        TextInputLayout textLayout = (TextInputLayout) view.findViewById(layOutTextLabel);
        EditText editText = (EditText) view.findViewById(textEditableId);
        if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
            SystemUtil.setProximaNovaExtraboldTypefaceInput(getActivity(), textLayout);
        } else {
            SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), textLayout);
        }
    }


    /**
     * Initialize ui fields
     */
    private void initialiseUIFields(View view) {


        setHeaderTitle(globalLabelsMetaDTO.getDemographicsAddressSection(), view);
        initNextButton(globalLabelsMetaDTO.getDemographicsReviewNextButton(), null, view);


        cityEditText = (EditText) view.findViewById(R.id.cityId);
        stateEditText = (TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView);


        TextView optinalLabelTextView = (TextView) view.findViewById(R.id.demogrAddressOptionalLabel);
        optinalLabelTextView.setText(globalLabelsMetaDTO.getDemographicsDetailsOptionalHint());



        ((EditText) view.findViewById(R.id.addressEditTextId)).setHint(addressMetaDTO.properties.address1.getLabel());
        ((EditText) view.findViewById(R.id.addressEditText2Id)).setHint(addressMetaDTO.properties.address2.getLabel());
        ((EditText) view.findViewById(R.id.cityId)).setHint(addressMetaDTO.properties.city.getLabel());
        ((EditText) view.findViewById(R.id.zipCodeId)).setHint(addressMetaDTO.properties.zipcode.getLabel());

        (view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).setOnClickListener(editStateListener);


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
        boolean isStateValid = ! globalLabelsMetaDTO.getDemographicsChooseLabel().equals(((TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).getText().toString());
        return !isZipEmpty && !isAddressEmpty && !isCityEmpty && isStateValid && checkFormatedFields(view);
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review_demographic_address;
    }



}
