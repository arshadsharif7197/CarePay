package com.carecloud.carepay.practice.library.payments.fragments;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialog;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientAddNewCreditCardFragment extends Fragment implements
        SimpleDatePickerDialog.OnDateSetListener{

    private TextInputLayout creditCardNoTextInput;
    private TextInputLayout verificationCodeTextInput;
    private EditText creditCardNoEditText;
    private EditText verificationCodeEditText;
    private TextView expirationDateTextView;
    private TextView pickDateTextView;
    private CheckBox saveCardOnFileCheckBox;
    private Button nextButton;

    private static final char SPACE_CHAR = ' ';
    private static final String SPACE_STRING = String.valueOf(SPACE_CHAR);
    private static final int GROUPSIZE = 4;
    ArrayList<String> listOfPattern=new ArrayList<>();
    private PaymentsModel paymentsModel;

    /**
     * Breakdown of this regexp:
     * ^             - Start of the string
     * (\\d{4}\\s)*  - A group of four digits, followed by a whitespace, e.g. "1234 ". Zero or more times.
     * \\d{0,4}      - Up to four (optional) digits.
     * (?<!\\s)$     - End of the string, but NOT with a whitespace just before it.
     * Example of matching strings:
     *  - "2304 52"
     *  - "2304"
     *  - ""
     */
    private final String regexp = "^(\\d{4}\\s)*\\d{0,4}(?<!\\s)$";
    private boolean isUpdating = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View addNewCreditCardView = inflater.inflate(R.layout.fragment_add_new_credit_card,
                container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            paymentsModel = (PaymentsModel) arguments.getSerializable(CarePayConstants.INTAKE_BUNDLE);
        }
        Toolbar toolbar = (Toolbar) addNewCreditCardView.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentNewCreditCard());
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        initilizeViews(addNewCreditCardView);
        setTypefaces();
        setTextWatchers();


        String ptVisa = "^4[0-9]{6,}$";
        listOfPattern.add(ptVisa);
        String ptMasterCard = "^5[1-5][0-9]{5,}$";
        listOfPattern.add(ptMasterCard);
        String ptAmeExp = "^3[47][0-9]{5,}$";
        listOfPattern.add(ptAmeExp);


        return addNewCreditCardView;
    }

    private void setTextWatchers() {

        creditCardNoEditText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                validateCreditCardDetails();
            }

            @Override
            public void afterTextChanged(Editable str) {
                String originalString = str.toString();

                if (isUpdating || originalString.matches(regexp)) {

                    for(String p:listOfPattern){
                        if(originalString.matches(p)){
                            Log.d("DEBUG", "afterTextChanged : MC");
                            break;
                        }
                    }
                    //DRAW LINE FOR MATCHING CREDIT CARD NAME
                    return;
                }


                isUpdating = true;


                LinkedList<Integer> spaceIndices = new LinkedList <>();
                for (int index = originalString.indexOf(SPACE_CHAR); index >= 0; index = originalString.indexOf(SPACE_CHAR, index + 1)) {
                    spaceIndices.offerLast(index);
                }


                Integer spaceIndex;
                while (!spaceIndices.isEmpty()) {
                    spaceIndex = spaceIndices.removeLast();
                    str.delete(spaceIndex, spaceIndex + 1);
                }


                for(int i = 0; ((i + 1) * GROUPSIZE + i) < str.length(); i++) {
                    str.insert((i + 1) * GROUPSIZE + i, SPACE_STRING);
                }

                int cursorPos = creditCardNoEditText.getSelectionStart();
                if (cursorPos > 0 && str.charAt(cursorPos - 1) == SPACE_CHAR) {
                    creditCardNoEditText.setSelection(cursorPos - 1);
                }

                isUpdating = false;

            }
        });


        verificationCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                validateCreditCardDetails();
            }

            @Override
            public void afterTextChanged(Editable str) {

            }
        });



    }

    private void initilizeViews(View view) {
        creditCardNoTextInput = (TextInputLayout)view.findViewById(R.id.creditCardNoTextInputLayout);
        creditCardNoTextInput.setTag(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentCreditCardNumber());
        creditCardNoEditText = (EditText) view.findViewById(R.id.creditCardNoEditText);
        creditCardNoEditText.setHint(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentCreditCardNumber());
        creditCardNoEditText.setTag(creditCardNoTextInput);

        verificationCodeTextInput = (TextInputLayout) view.findViewById(R.id.verificationCodeTextInputLayout);
        verificationCodeTextInput.setTag(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentVerificationNumber());
        verificationCodeEditText = (EditText) view.findViewById(R.id.verificationCodeEditText);
        verificationCodeEditText.setHint(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentVerificationNumber());
        verificationCodeEditText.setTag(verificationCodeTextInput);

        expirationDateTextView = (TextView) view.findViewById(R.id.expirationDateTextView);
        expirationDateTextView.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentExpirationDate());
        pickDateTextView = (TextView) view.findViewById(R.id.pickDateTextView);
        pickDateTextView.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentPickDate());
        pickDateTextView.setOnClickListener(pickDateListener);

        saveCardOnFileCheckBox = (CheckBox) view.findViewById(R.id.saveCardOnFileCheckBox);
        saveCardOnFileCheckBox.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentSaveCardOnFile());
        nextButton = (Button) view.findViewById(R.id.nextButton);
        nextButton.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentNextButton());
        nextButton.setOnClickListener(nextButtonListener);

        setChangeFocusListeners();
        setActionListeners();

        creditCardNoEditText.clearFocus();
        verificationCodeEditText.clearFocus();

        nextButton.setEnabled(false);
        nextButton.setClickable(false);
    }

    private void setTypefaces() {
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), creditCardNoEditText);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), verificationCodeEditText);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), expirationDateTextView);

        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), creditCardNoTextInput);
        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), verificationCodeTextInput);

        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), nextButton);

        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), saveCardOnFileCheckBox);
    }

    private void setChangeFocusListeners() {
        creditCardNoEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean flag) {
                if (flag) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, flag);
            }
        });
        verificationCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean flag) {
                if (flag) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, flag);
            }
        });
    }

    private void setActionListeners() {
        creditCardNoEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
                if (action == EditorInfo.IME_ACTION_NEXT) {
                    verificationCodeEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        verificationCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
                if (action == EditorInfo.IME_ACTION_DONE) {
                    verificationCodeEditText.clearFocus();
                    pickDateTextView.requestFocus();
                    SystemUtil.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });
    }

    private View.OnClickListener pickDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            displaySimpleDatePickerDialogFragment();
        }
    };

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
              //  Add New Credit Card details here - to be done after POST body available
//            PaymentCreditCardsPayloadDTO creditCardsPayloadDTO = new PaymentCreditCardsPayloadDTO();
//            Gson gson = new Gson();
//            String body = gson.toJson(creditCardsPayloadDTO);
//            Map<String, String> queryMap = new HashMap<>();
//            queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
//            queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
//            queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());
//            queryMap.put("patient_id", paymentsModel.getPaymentPayload().getPatientBalances().get(0).getMetadata().getPatientId());
//            TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getAddCreditCard();
//
//            WorkflowServiceHelper.getInstance().execute(transitionDTO, addNewCreditCardCallback, body,queryMap,WorkflowServiceHelper.getPreferredLanguageHeader());
        }
    };

    private WorkflowServiceCallback addNewCreditCardCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            //PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onDateSet(int year, int monthOfYear) {
        pickDateTextView.setText(DateUtil.getInstance().formatMonthYear(year, monthOfYear));
        validateCreditCardDetails();
    }

    private void displaySimpleDatePickerDialogFragment() {
        SimpleDatePickerDialogFragment datePickerDialogFragment;
        DateUtil instance = DateUtil.getInstance();
        datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance( instance.getYear(),
                instance.getMonth());
        datePickerDialogFragment.setOnDateSetListener(this);
        datePickerDialogFragment.show(getChildFragmentManager(), null);
    }

    private boolean validateCreditCardDetails(){
        if(!(creditCardNoEditText.getText().toString().length()==12)){
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            return false;
        }
        if(!(verificationCodeEditText.getText().toString().length()>0)){
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            return false;
        }
        if(!pickDateTextView.getText().toString().equalsIgnoreCase(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentPickDate())){
            nextButton.setEnabled(true);
            nextButton.setClickable(true);
            return true;
        } else {
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            return false;
        }
    }
}