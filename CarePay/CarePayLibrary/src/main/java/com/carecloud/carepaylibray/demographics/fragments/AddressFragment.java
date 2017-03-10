package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.CustomPopupNotification;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.carecloud.carepaylibray.utils.payeezysdk.firstdata.domain.v2.Address;
import com.smartystreets.api.us_zipcode.City;

import java.util.Arrays;

import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddressFragmentListener} interface
 * to handle interaction events.
 * Use the {@link AddressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddressFragment extends CheckInDemographicsBaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private EditText cityEditText;
    private TextView stateEditText;

    private AddressFragmentListener addressFragmentListener;

    public AddressFragment() {
        // Required empty public constructor
    }


    private View.OnClickListener editStateListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {

            showDialogState(AddressUtil.states, globalLabelsMetaDTO.getDemographicsTitleSelectState(), "cancelLabel");

        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressFragment newInstance(String param1, String param2) {
        AddressFragment fragment = new AddressFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        return mainView;

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
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
                if (!bool) { // for SmartyStreets
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

                    //((EditText) views.findViewById(R.id.cityId)).setText(smartyStreetsResponse.getCity());
                    cityEditText.setText(smartyStreetsResponse.getCity());


                    stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    stateEditText.setText(stateAbbr);
                    //((TextView) views.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).setText(stateAbbr);

                    //(TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)

                    /*cityEditText.setText(smartyStreetsResponse.getCity());

                    stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    stateEditText.setText(stateAbbr);*/
                }
            }


        }.execute(zipcode);
    }

    public void formatEditText(final View mainView){

   /*     final EditText address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        final TextInputLayout address1Label=(TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
*/

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





   /*     setTextListener(addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.address1.validations.get(0).getErrorMessage(),
                R.id.address1TextInputLayout, R.id.addressEditTextId, mainView);
*/


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
                    final String lastNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.address1.validations.get(0).getErrorMessage();
                    ((TextInputLayout) mainView.findViewById(R.id.address1TextInputLayout)).setError(lastNameError);
                    ((TextInputLayout) mainView.findViewById(R.id.address1TextInputLayout)).setErrorEnabled(true);
                }
                checkIfEnableButton(mainView);
            }
        });



     /*   final EditText zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);
        final TextInputLayout zipcodeLabel = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
*/

 /*       setTextListener(addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.address1.validations.get(0).getErrorMessage(),
                R.id.zipCodeTextInputLayout, R.id.zipCodeId, view);
*/




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
                    final String zipcodeError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.zipcode.validations.get(0).getErrorMessage();
                    ( (TextInputLayout) mainView.findViewById(R.id.zipCodeTextInputLayout)).setError(zipcodeError);
                    ((TextInputLayout) mainView.findViewById(R.id.zipCodeTextInputLayout)).setErrorEnabled(true);
                }

                StringUtil.autoFormatZipcode(editable, prevLen);
                checkIfEnableButton(mainView);
            }
        });


    /*    final EditText cityEditText = (EditText) view.findViewById(R.id.cityId);
        final TextInputLayout cityLabel = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
*/


 /*       setTextListener(addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.address1.validations.get(0).getErrorMessage(),
                R.id.cityTextInputLayout, R.id.cityId, mainView);
*/


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

    private void setEditTexts(View view) {

        ((TextInputLayout) view.findViewById(R.id.address1TextInputLayout)).setTag(addressMetaDTO.properties.address1.getLabel());

    /*

        address1EditText.setTag(address1Label);


        address2Label.setTag(addressMetaDTO.properties.address2.getLabel());
        address2EditText.setTag(address2Label);


        zipcodeLabel.setTag(addressMetaDTO.properties.zipcode.getLabel());
        zipCodeEditText.setTag(zipcodeLabel);


        cityLabel.setTag(addressMetaDTO.properties.city.getLabel());
        cityEditText.setTag(cityLabel);

        stateLabel.setText(addressMetaDTO.properties.state.getLabel().toUpperCase());


        setChangeFocusListeners();*/
    }


    private void initViewFromModels(View view) {

        if (demographicAddressPayloadDTO != null) {

            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getAddress1())) {
              //  ((EditText) view.findViewById(R.id.addressEditTextId)).setText(demographicAddressPayloadDTO.getAddress1());
                // ((EditText) view.findViewById(R.id.addressEditTextId)).requestFocus();

                String adress1 = demographicAddressPayloadDTO.getAddress1();
                initTextInputLayoutValue(adress1, R.id.addressEditTextId, view);

                /*
                            String firstName = demographicPersDetailsPayloadDTO.getFirstName();
            initTextInputLayoutValue(firstName, R.id.reviewdemogrFirstNameEdit, view);

                 */


            }

            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getAddress2())) {
                ((EditText) view.findViewById(R.id.addressEditText2Id)).setText(demographicAddressPayloadDTO.getAddress2());
                ((EditText) view.findViewById(R.id.addressEditText2Id)).requestFocus();
            }

            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getCity()) || !((EditText) view.findViewById(R.id.cityId)).getText().toString().isEmpty()) {
                ((EditText) view.findViewById(R.id.cityId)).setText(demographicAddressPayloadDTO.getCity());
                ((EditText) view.findViewById(R.id.cityId)).requestFocus();
            }

/*
            if (SystemUtil.isNotEmptyString(demographicAddressPayloadDTO.getState()) || !((TextView) view.findViewById(R.id.stateTextInputLayout)).getText().toString().isEmpty()) {
                ((TextView) view.findViewById(R.id.stateTextInputLayout)).setText(demographicAddressPayloadDTO.getState());
            } else {
                ((TextView) view.findViewById(R.id.stateTextInputLayout)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }
*/

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


    private void initTextInputLayoutValue(String value, int textEditableId, View view){
        EditText editText = (EditText) view.findViewById(textEditableId);
        if (SystemUtil.isNotEmptyString(value)) {
            editText.setText(value);
            editText.requestFocus();

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (addressFragmentListener != null) {
            addressFragmentListener.onFragmentInteraction(uri);
        }
    }


    public void navigateToPersonalInfoFragment(){
        if(addressFragmentListener !=null){
            addressFragmentListener.navigateToPersonalInformationFragment();
        }
    }

    public void navigateToDemographicsFragment(){
        if(addressFragmentListener != null){
            addressFragmentListener.navigateToDemographicsFragment();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddressFragmentListener) {
            addressFragmentListener = (AddressFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        addressFragmentListener = null;
    }


    public void onClick(View view) {

        if (view == findViewById(R.id.buttonAddDemographicInfo)){
            //   openNewFragment();
            if (isAllFieldsValid() && checkFormatedFields(view)) {
                findViewById(R.id.buttonAddDemographicInfo).setEnabled(false);
                // update the model
                updateModels();
                // post the changes
//                postUpdates();
                // next
               // openNextFragment();
                // hide the keyboard
                SystemUtil.hideSoftKeyboard(getActivity());
            } else {
/*
                CustomPopupNotification popup = new CustomPopupNotification(getActivity(), getActivity().getWindow().getCurrentFocus(),
                        globalLabelsMetaDTO.getDemographicsMissingInformation(), CustomPopupNotification.TYPE_ERROR_NOTIFICATION);
                popup.showPopWindow();
*/
            }
        }

        if (view == findViewById(R.id.addressEditTextId)) {
            //
            showAlertDialogWithListview(AddressUtil.states, globalLabelsMetaDTO.getDemographicsTitleSelectState(), "cancelLabel", 4);

            showDialogState(AddressUtil.states, globalLabelsMetaDTO.getDemographicsTitleSelectState(), "cancelLabel");

        }

    }

    private void  updateModels(){

    }



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
                    }
                });
    }


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





    public void setTypefaces(View view) {

        setLabelStyle( R.id.address1TextInputLayout, R.id.addressEditTextId, view);

 /*       if (!StringUtil.isNullOrEmpty(((EditText) view.findViewById(R.id.addressEditTextId)).getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), ((TextInputLayout) view.findViewById(R.id.address1TextInputLayout)));
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), ((TextInputLayout) view.findViewById(R.id.address1TextInputLayout)));
        }
*/


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

    private void initTextLabel(String label, int layOutTextLabel, int textEditableId, View view){
        TextInputLayout textLayout = (TextInputLayout) view.findViewById(layOutTextLabel);
        textLayout.setTag(label);
        EditText editText = (EditText) view.findViewById(textEditableId);
        editText.setTag(textLayout);
        editText.setHint(label);
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


    private void initialiseUIFields(View view) {
        //globalLabelsMetaDTO.getDemographicsAddressHeader()

        setHeaderTitle(globalLabelsMetaDTO.getDemographicsAddressSection(), view);
        initNextButton(globalLabelsMetaDTO.getDemographicsReviewNextButton(), null, view);

        //String label = persDetailsMetaDTO.properties.firstName.getLabel();

        cityEditText = (EditText) view.findViewById(R.id.cityId);
        stateEditText = (TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView);
/*
        address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        address2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);

        stateEditText = (TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView);

        address1Label = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        address2Label = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        zipcodeLabel = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        cityLabel = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        stateLabel = (TextView) view.findViewById(R.id.stateTextInputLayout);
*/



        ((EditText) view.findViewById(R.id.addressEditTextId)).setHint(addressMetaDTO.properties.address1.getLabel());
        ((EditText) view.findViewById(R.id.addressEditText2Id)).setHint(addressMetaDTO.properties.address2.getLabel());
        ((EditText) view.findViewById(R.id.cityId)).setHint(addressMetaDTO.properties.city.getLabel());
        ((EditText) view.findViewById(R.id.zipCodeId)).setHint(addressMetaDTO.properties.zipcode.getLabel());

        (view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).setOnClickListener(editStateListener);


        String label = addressMetaDTO.properties.address1.getLabel();
        //initTextLabel(label, R.id.address1TextInputLayout, R.id.addressEditTextId, view);


        //view.findViewById(R.id.address1TextInputLayout).setTag(addressMetaDTO.properties.address1.getLabel());

        //view.findViewById(R.id.addressEditTextId).setTag((view.findViewById(R.id.address1TextInputLayout)));

 /*       ((TextInputLayout) view.findViewById(R.id.address2TextInputLayout)).setTag(addressMetaDTO.properties.address2.getLabel());
        ((EditText) view.findViewById(R.id.addressEditText2Id)).setTag(((TextInputLayout) view.findViewById(R.id.address2TextInputLayout)));
*/
/*
        ((TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout)).setTag(addressMetaDTO.properties.zipcode.getLabel());
        ((EditText) view.findViewById(R.id.zipCodeId)).setTag(((TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout)));
*/
/*

        ((TextInputLayout) view.findViewById(R.id.cityTextInputLayout)).setTag(addressMetaDTO.properties.city.getLabel());
        ((EditText) view.findViewById(R.id.cityId)).setTag(((TextInputLayout) view.findViewById(R.id.cityTextInputLayout)));

        ((TextView) view.findViewById(R.id.stateTextInputLayout)).setText(addressMetaDTO.properties.state.getLabel().toUpperCase());

        ((TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).setOnClickListener(editStateListener);
*/

    }

/*
    public void checkIfEnableButton() {
        checkIfDisableButton(false);
    }


    public void checkIfDisableButton(boolean isDisabled) {
        enableButton(isAllFieldsValid() && !isDisabled);
    }
*/

    /**
     * Enable or disable main button
     *
     * @param isEnabled is Button enabled
     */
    public void enableButton(boolean isEnabled) {
        if (isPractice) {
            //buttonConfirmData.setBackground(ContextCompat.getDrawable(getContext(), isEnabled ? R.drawable.bg_green_overlay : R.drawable.bg_silver_overlay));
            //buttonConfirmData.setPadding(20, 0, 20, 0);
        } else {
            //buttonConfirmData.setBackground(ContextCompat.getDrawable(getContext(), isEnabled ? R.drawable.language_button_selector : R.drawable.button_light_gray_bg));
        }
        //buttonConfirmData.setEnabled(isEnabled);
    }

    @Override
    protected DemographicDTO updateDemographicDTO(View view) {

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
        demographicDTO.getPayload().getDemographics().getPayload().setAddress(demographicAddressPayloadDTO);
        return demographicDTO;
    }

    private boolean isAllFieldsValid() {

        boolean isStateValid =   true;  // !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(((EditText) findViewById(R.id.reviewDemographicsStateAutoCompleteTextView)).getText().toString());

        return !isZipEmpty && !isAddressEmpty && !isCityEmpty && isStateValid;

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface AddressFragmentListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void navigateToPersonalInformationFragment();

        void navigateToDemographicsFragment();

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
