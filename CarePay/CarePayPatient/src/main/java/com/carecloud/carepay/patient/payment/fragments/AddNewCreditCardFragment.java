package com.carecloud.carepay.patient.payment.fragments;


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

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialog;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialogFragment;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewCreditCardFragment extends Fragment implements
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
    ArrayList<String> listOfPattern=new ArrayList<String>();

    /**
     * Breakdown of this regexp:
     * ^             - Start of the string
     * (\\d{4}\\s)*  - A group of four digits, followed by a whitespace, e.g. "1234 ". Zero or more times.
     * \\d{0,4}      - Up to four (optional) digits.
     * (?<!\\s)$     - End of the string, but NOT with a whitespace just before it.
     *
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

        Toolbar toolbar = (Toolbar) addNewCreditCardView.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(R.string.new_credit_card);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String originalString = s.toString();

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


                LinkedList<Integer> spaceIndices = new LinkedList <Integer>();
                for (int index = originalString.indexOf(SPACE_CHAR); index >= 0; index = originalString.indexOf(SPACE_CHAR, index + 1)) {
                    spaceIndices.offerLast(index);
                }


                Integer spaceIndex = null;
                while (!spaceIndices.isEmpty()) {
                    spaceIndex = spaceIndices.removeLast();
                    s.delete(spaceIndex, spaceIndex + 1);
                }


                for(int i = 0; ((i + 1) * GROUPSIZE + i) < s.length(); i++) {
                    s.insert((i + 1) * GROUPSIZE + i, SPACE_STRING);
                }

                int cursorPos = creditCardNoEditText.getSelectionStart();
                if (cursorPos > 0 && s.charAt(cursorPos - 1) == SPACE_CHAR) {
                    creditCardNoEditText.setSelection(cursorPos - 1);
                }

                isUpdating = false;



            }
        });


        verificationCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    private void initilizeViews(View view) {
        creditCardNoTextInput = (TextInputLayout)view.findViewById(R.id.creditCardNoTextInputLayout);
        creditCardNoTextInput.setTag(getString(R.string.credit_card_number));
        creditCardNoEditText = (EditText) view.findViewById(R.id.creditCardNoEditText);
        creditCardNoEditText.setTag(creditCardNoTextInput);

        verificationCodeTextInput = (TextInputLayout) view.findViewById(R.id.verificationCodeTextInputLayout);
        verificationCodeTextInput.setTag(getString(R.string.verification_code));
        verificationCodeEditText = (EditText) view.findViewById(R.id.verificationCodeEditText);
        verificationCodeEditText.setTag(verificationCodeTextInput);

        expirationDateTextView = (TextView) view.findViewById(R.id.expirationDateTextView);
        pickDateTextView = (TextView) view.findViewById(R.id.pickDateTextView);
        pickDateTextView.setOnClickListener(pickDateListener);

        saveCardOnFileCheckBox = (CheckBox) view.findViewById(R.id.saveCardOnFileCheckBox);
        nextButton = (Button) view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(nextButtonListener);

        setChangeFocusListeners();
        setActionListeners();

        creditCardNoEditText.clearFocus();
        verificationCodeEditText.clearFocus();
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
            // ToDo : Add New Credit Card action here - to be done
        }
    };

    @Override
    public void onDateSet(int year, int monthOfYear) {
        pickDateTextView.setText(DateUtil.getInstance().formatMonthYear(year, monthOfYear));
    }

    private void displaySimpleDatePickerDialogFragment() {
        SimpleDatePickerDialogFragment datePickerDialogFragment;
        DateUtil instance = DateUtil.getInstance();
        datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance( instance.getYear(),
                instance.getMonth());
        datePickerDialogFragment.setOnDateSetListener(this);
        datePickerDialogFragment.show(getChildFragmentManager(), null);
    }
}
