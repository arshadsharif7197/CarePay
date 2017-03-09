package com.carecloud.carepay.patient.demographics.fragments.viewpager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import com.carecloud.carepaylibray.utils.ValidationHelper;

import com.smartystreets.api.us_zipcode.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsoco_user on 9/2/2016.
 * Fragment for on-boarding demographics address.
 */
public class DemographicsAddressFragment extends BaseFragment {

    View view;

    private LinearLayout rootLayout;

    private TextInputLayout firstNameInputLayout;
    private TextInputLayout middleNameInputLayout;
    private TextInputLayout lastNameInputLayout;
    private TextInputLayout phNoTextInputLayout;
    private TextInputLayout address1TextInputLayout;
    private TextInputLayout address2TextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout zipCodeTextInputLayout;

    private EditText phoneNumberEditText;
    private EditText zipCodeEditText;
    private EditText address1EditText;
    private EditText address2EditText;
    private EditText cityEditText;
    private EditText firstNameText;
    private EditText middleNameText;
    private EditText lastNameText;
    private TextView firstNameReqHint;
    private TextView lastNameReqHint;
    private TextView header;
    private TextView subheader;
    private Button   nextButton;

    private AutoCompleteTextView stateAutoCompleteTextView;
    private String stateAbbr = null;
    private City smartyStreetsResponse;

    private boolean isFirstNameEmpty;
    private boolean isMiddleNameEmpty;
    private boolean isLastNameEmpty;
    private boolean isPhoneEmpty;
    private boolean isAddressEmpty;
    private boolean isCityEmpty;
    private boolean isStateEmtpy;
    private boolean isZipEmpty;
    private boolean isNextVisible = false;

    private DemographicAddressPayloadDTO            addressDTO;
    private DemographicPersDetailsPayloadDTO        persDetailsDTO;
    private DemographicMetadataEntityAddressDTO     addressMetaDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private DemographicLabelsDTO                    globalLabelsMetaDTO;
    private DemographicsAddressFragmentListener demographicsAddressFragmentListener;
    private String[] states;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            demographicsAddressFragmentListener = (DemographicsAddressFragment.DemographicsAddressFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fetch global labels from DTO
        globalLabelsMetaDTO = ((DemographicsActivity) getActivity()).getLabelsDTO();

        // create the view
        view = inflater.inflate(R.layout.fragment_demographics_address, container, false);

        // used to detect soft keyboard hide/show and thus toggle the next button visible/invisible
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = view.getRootView().getHeight() - view.getHeight();
                if (heightDiff > SystemUtil.dpToPx(getActivity(), 200)) { // if more than 200 dp, it's probably a keyboard...
                    if (isNextVisible) {
                        nextButton.setVisibility(View.GONE);
                        isNextVisible = false;
                    }
                } else {
                    if (!isNextVisible) {
                        nextButton.setVisibility(View.VISIBLE);
                        isNextVisible = true;
                    }
                }
            }
        });
        rootLayout = (LinearLayout) view.findViewById(R.id.demographicsAddressRootContainer);

        isFirstNameEmpty = true;
        isMiddleNameEmpty = true;
        isLastNameEmpty = true;
        isAddressEmpty = true;
        isZipEmpty = true;
        isCityEmpty = true;
        isStateEmtpy = true;
        isPhoneEmpty = true;

        initModels();
        initialiseUIFields();

        // enable/disable next button
        nextButton.setEnabled(!isFirstNameEmpty && !isLastNameEmpty);

        return view;
    }

    /**
     * Interface to implement scrolling on view pager
     */
    public interface DemographicsAddressFragmentListener{
        void enableScroll(boolean myString);
    }

    /**
     * Inits the UI components; to manipulate the change of the hint to caps when floating,
     * set the textinputlayout as the tag of the corresponding edittext and the hint as the
     * the tag of the textinputlayout
     */
    private void initialiseUIFields() {
        header = (TextView) view.findViewById(R.id.addressHeading);
        header.setText(globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsAddressHeader());
        subheader = (TextView) view.findViewById(R.id.addressSubHeading);
        subheader.setText(globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsAddressSubheader());

        String reqLabel = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsRequired();
        firstNameReqHint = (TextView) view.findViewById(R.id.demogrAddressFirstNameReqHint);
        firstNameReqHint.setText(reqLabel);

        lastNameReqHint = (TextView) view.findViewById(R.id.demogrAddressLastNameReqHint);
        lastNameReqHint.setText(reqLabel);

        String hint;

        hint = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.firstName.getLabel();
        firstNameText = (EditText) view.findViewById(R.id.demogrAddressFirstNameEdit);
        firstNameInputLayout = (TextInputLayout) view.findViewById(R.id.demogrAddressFirstNameTextInput);
        firstNameInputLayout.setTag(hint);
        firstNameText.setTag(firstNameInputLayout);
        firstNameText.setHint(hint);

        hint = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.middleName.getLabel();
        middleNameText = (EditText) view.findViewById(R.id.demogrAddressMiddleNameEdit);
        middleNameInputLayout = (TextInputLayout) view.findViewById(R.id.demogrAddressMiddleNameTextInput);
        middleNameInputLayout.setTag(hint);
        middleNameText.setTag(middleNameInputLayout);
        middleNameText.setHint(hint);

        hint = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.lastName.getLabel();
        lastNameText = (EditText) view.findViewById(R.id.demogrAddressLastNameEdit);
        lastNameInputLayout = (TextInputLayout) view.findViewById(R.id.demogrAddressLastNameTextInput);
        lastNameInputLayout.setTag(hint);
        lastNameText.setTag(lastNameInputLayout);
        lastNameText.setHint(hint);

        hint = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.address1.getLabel();
        address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        address1TextInputLayout = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        address1TextInputLayout.setTag(hint);
        address1EditText.setTag(address1TextInputLayout);
        address1EditText.setHint(hint);

        hint = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.address2.getLabel();
        address2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);
        address2TextInputLayout = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        address2TextInputLayout.setTag(hint);
        address2EditText.setTag(address2TextInputLayout);
        address2EditText.setHint(hint);

        hint = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.zipcode.getLabel();
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);
        zipCodeTextInputLayout = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        zipCodeTextInputLayout.setTag(hint);
        zipCodeEditText.setTag(zipCodeTextInputLayout);
        zipCodeEditText.setHint(hint);

        hint = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.city.getLabel();
        cityEditText = (EditText) view.findViewById(R.id.cityId);
        cityTextInputLayout = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        cityTextInputLayout.setTag(hint);
        cityEditText.setTag(cityTextInputLayout);
        cityEditText.setHint(hint);

        hint = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.state.getLabel();
        stateAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.stateAutoCompleteTextView);
        stateTextInputLayout = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
        stateTextInputLayout.setTag(hint);
        stateAutoCompleteTextView.setTag(stateTextInputLayout);
        stateAutoCompleteTextView.setHint(hint);
        getOptions();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                                                R.layout.autocomplete_state_item,
                                                                R.id.text1,
                                                                states);
        stateAutoCompleteTextView.setThreshold(1);
        stateAutoCompleteTextView.setAdapter(adapter);
        stateAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stateAbbr = adapter.getItem(position);
            }
        });

        hint = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.phone.getLabel();
        phoneNumberEditText = (EditText) view.findViewById(R.id.phNoEditText);
        phNoTextInputLayout = (TextInputLayout) view.findViewById(R.id.phNoTextInputLayout);
        phNoTextInputLayout.setTag(hint);
        phoneNumberEditText.setTag(phNoTextInputLayout);
        phoneNumberEditText.setHint(hint);

        // get label from global metadata
        nextButton = (Button) view.findViewById(R.id.demographicsAddressNextButton);
        String nextLabel = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsNext();
        nextButton.setText(nextLabel);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkReadyForNext()) { // if all valid...
                    // update personal details
                    persDetailsDTO.setFirstName(firstNameText.getText().toString());
                    persDetailsDTO.setMiddleName(middleNameText.getText().toString());
                    persDetailsDTO.setLastName(lastNameText.getText().toString());
                    // update the addressDTO with values from UI
                    addressDTO.setAddress1(address1EditText.getText().toString());
                    addressDTO.setAddress2(address2EditText.getText().toString());
                    String formattedZipCode = zipCodeEditText.getText().toString();
                    addressDTO.setZipcode(StringUtil.revertZipToRawFormat(formattedZipCode));
                    addressDTO.setCity(cityEditText.getText().toString());
                    addressDTO.setState(stateAutoCompleteTextView.getText().toString());
                    // eliminate '-' from the phone number
                    String formattedPhoneNum = phoneNumberEditText.getText().toString();
                    addressDTO.setPhone(StringUtil.revertToRawPhoneFormat(formattedPhoneNum));

                    ((DemographicsActivity) getActivity()).setAddressModel(addressDTO); // sent the addressDTO to the activity
                    ((DemographicsActivity) getActivity()).setDetailsModel(persDetailsDTO); // sent the modelDetails to the activity

                    ((DemographicsActivity) getActivity()).setCurrentItem(1, true);
                }
            }
        });

        setTypefaces(view);
        setFocusChangeListeners();
        setEditActionListeners();
        setTextWachers();

        // populate views
        populateViewsWithData();
    }

    private void getOptions() {
        // init states
        if (addressMetaDTO != null
                && addressMetaDTO.properties != null
                && addressMetaDTO.properties.state != null) {
            List<MetadataOptionDTO> optionDTOs = addressMetaDTO.properties.state.options;
            List<String> statesStrings = new ArrayList<>();
            for (MetadataOptionDTO optionDTO : optionDTOs) {
                statesStrings.add(optionDTO.getLabel());
            }
            states = statesStrings.toArray(new String[0]);
        } else {
            states = new String[1];
            states[0] = CarePayConstants.NOT_DEFINED;
        }
    }

    /**
     * Init the models (DTOs) for this screen
     */
    public void initModels() {
        addressDTO = ((DemographicsActivity) getActivity()).getAddressModel();
        if (addressDTO == null) {
            addressDTO = new DemographicAddressPayloadDTO();
        }
        persDetailsDTO = ((DemographicsActivity) getActivity()).getDetailsDTO();
        if (persDetailsDTO == null) {
            persDetailsDTO = new DemographicPersDetailsPayloadDTO();
        }
        addressMetaDTO = DtoHelper.getConvertedDTO(DemographicMetadataEntityAddressDTO.class, getArguments());
    }

    private void populateViewsWithData() {
        if (persDetailsDTO != null) {
            // populate the views
            String firstName = persDetailsDTO.getFirstName();
            if (!StringUtil.isNullOrEmpty(firstName)) {
                firstNameText.setText(firstName);
                firstNameText.requestFocus();
                isFirstNameEmpty = false;
            }

            String middleName = persDetailsDTO.getMiddleName();
            if (!StringUtil.isNullOrEmpty(middleName)) {
                middleNameText.setText(middleName);
                middleNameText.requestFocus();
                isMiddleNameEmpty = false;
            }

            String lastName = persDetailsDTO.getLastName();
            if (!StringUtil.isNullOrEmpty(lastName)) {
                lastNameText.setText(lastName);
                lastNameText.requestFocus();
                isLastNameEmpty = false;
            }
        }
        if (addressDTO != null) {
            String address1 = addressDTO.getAddress1();
            if (!StringUtil.isNullOrEmpty(address1)) {
                address1EditText.setText(address1);
                address1EditText.requestFocus();
            }

            String address2 = addressDTO.getAddress2();
            if (!StringUtil.isNullOrEmpty(address2)) {
                address2EditText.setText(address2);
                address2EditText.requestFocus();
            }

            String zip = addressDTO.getZipcode();
            if (!StringUtil.isNullOrEmpty(zip)) {
                zipCodeEditText.setText(StringUtil.formatZipCode(zip));
                zipCodeEditText.requestFocus();
            }

            String city = addressDTO.getCity();
            if (!StringUtil.isNullOrEmpty(city)) {
                cityEditText.setText(city);
                cityEditText.requestFocus();
            }

            String state = addressDTO.getState();
            if (!StringUtil.isNullOrEmpty(state)) {
                stateAutoCompleteTextView.setText(state);
                stateAutoCompleteTextView.requestFocus();
            }

            String phone = addressDTO.getPhone();
            if (!StringUtil.isNullOrEmpty(phone) && phone.length() == 10) {
                // expected as xxxxxxxxxx; convert to xxx-xxx-xxxx
                phoneNumberEditText.setText(StringUtil.formatPhoneNumber(phone));
                phoneNumberEditText.requestFocus();
            }
        }
        rootLayout.requestFocus();
    }

    private void setTextWachers() {
        firstNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isFirstNameEmpty = StringUtil.isNullOrEmpty(firstNameText.getText().toString());
                if (!isFirstNameEmpty) {
                    firstNameInputLayout.setError(null);
                    firstNameInputLayout.setErrorEnabled(false);
                }
                enableNextButton();
            }
        });
        lastNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isLastNameEmpty = StringUtil.isNullOrEmpty(lastNameText.getText().toString());
                if (!isLastNameEmpty) {
                    lastNameInputLayout.setError(null);
                    lastNameInputLayout.setErrorEnabled(false);
                }
                enableNextButton();
            }
        });
        address1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String addr1 = address1EditText.getText().toString();
                isAddressEmpty = StringUtil.isNullOrEmpty(addr1);
                if (!isAddressEmpty) {
                    address1TextInputLayout.setError(null);
                    address1TextInputLayout.setErrorEnabled(false);
                    addressDTO.setAddress1(addr1);
                }
            }
        });

        zipCodeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zipCodeEditText.setSelection(zipCodeEditText.length());
            }
        });
        zipCodeEditText.addTextChangedListener(new TextWatcher() {
            int prevLen = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {
                prevLen = charSequence.length();
                zipCodeEditText.setSelection(charSequence.length());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String zip = zipCodeEditText.getText().toString();
                isZipEmpty = StringUtil.isNullOrEmpty(zip);
                if (!isZipEmpty) {
                    zipCodeTextInputLayout.setError(null);
                    zipCodeTextInputLayout.setErrorEnabled(false);
                    addressDTO.setZipcode(zip);
                }

                StringUtil.autoFormatZipcode(editable, prevLen);
            }
        });
        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String city = cityEditText.getText().toString();
                isCityEmpty = StringUtil.isNullOrEmpty(city);
                if (!isCityEmpty) {
                    cityTextInputLayout.setError(null);
                    cityTextInputLayout.setErrorEnabled(false);
                    addressDTO.setCity(city);
                }
            }
        });
        stateAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String state = stateAutoCompleteTextView.getText().toString();
                isStateEmtpy = StringUtil.isNullOrEmpty(state);
                if (!isStateEmtpy) {
                    stateTextInputLayout.setError(null);
                    stateTextInputLayout.setErrorEnabled(false);
                    stateAbbr = state;
                    addressDTO.setState(state);
                }
            }
        });

        // place the cursor on the last char when clicked
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
                isPhoneEmpty = StringUtil.isNullOrEmpty(phone);
                if (!isPhoneEmpty) {
                    phNoTextInputLayout.setError(null);
                    phNoTextInputLayout.setErrorEnabled(false);
                    addressDTO.setPhone(phone);
                }
                // auto-format as typing
                StringUtil.autoFormatPhone(phonenumber, len);
            }
        });
    }

    /**
     * Enable next button and view pager
     */
    private void enableNextButton() {
        boolean areAllReqNonEmpty = !(isFirstNameEmpty || isLastNameEmpty);
        nextButton.setEnabled(areAllReqNonEmpty);
        demographicsAddressFragmentListener.enableScroll(areAllReqNonEmpty);
    }

    private void setEditActionListeners() {
        firstNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    lastNameText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        middleNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    middleNameText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        lastNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    address1EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        address1EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    address2EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        address2EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    zipCodeEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        zipCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    cityEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        cityEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    stateAutoCompleteTextView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        stateAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    phNoTextInputLayout.requestFocus();
                    return true;
                }
                return false;
            }
        });

        phoneNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_DONE) {
                    phoneNumberEditText.clearFocus();
                    rootLayout.requestFocus();
                    SystemUtil.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Helper to set focus change listener in order to toggle hint strings to caps
     */
    private void setFocusChangeListeners() {
        firstNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) { // show the keyboard
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
        middleNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) { // show the keyboard
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
        lastNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
        address1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });

        address2EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });

        zipCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
                if (!hasFocus) { // for SmartyStreets
                    getCityAndState(zipCodeEditText.getText().toString());
                }
            }
        });

        cityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });

        stateAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });

        phoneNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
    }

    private boolean checkState() {
        return ValidationHelper.applyIsInOptionsValidationToWrappedEdit(stateAutoCompleteTextView,
                                                                 stateTextInputLayout,
                                                                 addressMetaDTO.properties.state,
                                                                 null);
    }

    private boolean checkCity() {
        if (isCityEmpty) {
            return true;
        }

        // apply validate from backend
        boolean isValidFormat = ValidationHelper.applyPatternValidationToWrappedEdit(cityEditText,
                                                                                     cityTextInputLayout,
                                                                                     addressMetaDTO.properties.city,
                                                                                     null);
        if (!isValidFormat) {
            return false;
        }

        return true;
    }

    private boolean checkPhoneNumber() {
        // not require validate
        if (isPhoneEmpty) {
            return true;
        }
        // apply validate from backend
        boolean isValidFormat = ValidationHelper.applyPatternValidationToWrappedEdit(
                phoneNumberEditText,
                phNoTextInputLayout,
                addressMetaDTO.properties.phone, null);

        return isValidFormat;
    }

    private boolean checkZip() {
        String zipCode = zipCodeEditText.getText().toString();
        if (StringUtil.isNullOrEmpty(zipCode)) {
            return true;
        }
        // apply validate from backend
        boolean isValidFormat = ValidationHelper.applyPatternValidationToWrappedEdit(zipCodeEditText,
                                                                                     zipCodeTextInputLayout,
                                                                                     addressMetaDTO.properties.zipcode,
                                                                                     null);
        return isValidFormat;
    }

    private void setTypefaces(View view) {
        setProximaNovaSemiboldTypeface(getActivity(), firstNameReqHint);
        setProximaNovaSemiboldTypeface(getActivity(), lastNameReqHint);
        setGothamRoundedMediumTypeface(getActivity(), header);
        setProximaNovaRegularTypeface(getActivity(), subheader);

        // for each text input test if its edit has text and set the font accordingly
        if (!StringUtil.isNullOrEmpty(firstNameText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), firstNameInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), firstNameInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), firstNameText);

        if (!StringUtil.isNullOrEmpty(middleNameText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), middleNameInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), middleNameInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), middleNameText);

        if (!StringUtil.isNullOrEmpty(lastNameText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), lastNameInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), lastNameInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), lastNameText);

        if (!StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), zipCodeTextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), zipCodeTextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), zipCodeEditText);

        if (!StringUtil.isNullOrEmpty(address1EditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), address1TextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), address1TextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), address1EditText);

        if (!StringUtil.isNullOrEmpty(address2EditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), address2TextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), address2TextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), address2EditText);

        if (!StringUtil.isNullOrEmpty(cityEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), cityTextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), cityTextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), cityEditText);

        if (!StringUtil.isNullOrEmpty(stateAutoCompleteTextView.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), stateTextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), stateTextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), stateAutoCompleteTextView);

        if (!StringUtil.isNullOrEmpty(phoneNumberEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), phNoTextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), phNoTextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), phoneNumberEditText);

        setGothamRoundedMediumTypeface(getActivity(), nextButton);
    }

    private boolean checkReadyForNext() {
        boolean isPhoneValid = checkPhoneNumber();
        // for non-required field, check validity only if non-empty
        if (!isPhoneValid) {
            phoneNumberEditText.requestFocus();
        }

        boolean isStateValid = checkState();
        if (!isStateValid) {
            stateAutoCompleteTextView.requestFocus();
        }

        boolean isCityValid = checkCity();
        if (!isCityValid) {
            cityEditText.requestFocus();
        }

        boolean isZipValid = checkZip();
        if (!isZipValid) {
            zipCodeEditText.requestFocus();
        }

        return isPhoneValid && isStateValid && isCityValid && isZipValid;
    }

    public void setPersDetailsMetaDTO(DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO) {
        this.persDetailsMetaDTO = persDetailsMetaDTO;
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

                    stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    stateAutoCompleteTextView.setText(stateAbbr);
                }
            }
        }.execute(zipcode);
    }
}