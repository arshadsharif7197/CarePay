package com.carecloud.carepaylibray.consentforms.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.interfaces.ConsentActivity;
import com.carecloud.carepaylibray.consentforms.interfaces.FormData;
import com.carecloud.carepaylibray.consentforms.interfaces.IFragmentCallback;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.Arrays;
import java.util.Calendar;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTextInputLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;


public class ConsentForm2Fragment extends Fragment {

    private TextView titleTextView, descriptionTextView, contentTextView, content2TextView, dateTextView,
            dobTextView, chooseGenderTextView;
    private TextInputLayout minorFirstNameTextView;
    private TextInputLayout minorLastNameTextView;
    private Button signButton;
    private boolean isGenderSelected, isDatePicked;
    private IFragmentCallback fragmentCallback;
    private ScrollView consentFormScrollView;
    private EditText minorFirstNameEditText, minorLastNameEditText;
    private String[] gender = {"Male", "Female"};
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            String dob = new StringBuilder().append(i1).append("/").append(i2).append("/")
                    .append(i).toString();
            dobTextView.setText(dob);
                isDatePicked = !(dob.equals(R.string.pick_date));
            dobTextView.setTag(dob);
        }
    };
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.signButton && fragmentCallback != null) {
                fragmentCallback.signButtonClicked();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.consent2_form_layout, container, false);


        titleTextView = (TextView) view.findViewById(R.id.titleTv);
        descriptionTextView = (TextView) view.findViewById(R.id.descriptionTv);
        contentTextView = (TextView) view.findViewById(R.id.contentTv);
        chooseGenderTextView = (TextView) view.findViewById(R.id.choose_genderTextView);
        content2TextView = (TextView) view.findViewById(R.id.content2Tv);
        dateTextView = (TextView) view.findViewById(R.id.dateTv);
        signButton = (Button) view.findViewById(R.id.signButton);
        signButton.setEnabled(false);
        minorFirstNameTextView = (TextInputLayout) view.findViewById(R.id.text_input_layout);
        minorLastNameTextView = (TextInputLayout) view.findViewById(R.id.minorLastName);
        minorFirstNameEditText = (EditText) view.findViewById(R.id.minorFirstNameET);
        minorLastNameEditText = (EditText) view.findViewById(R.id.minorLastNameET);
        dobTextView = (TextView) view.findViewById(R.id.dobET);
        consentFormScrollView = (ScrollView) view.findViewById(R.id.consentform_scrollView);
        setTypefaces(view);
        setTextListeners();
        onClickListners();
        getButtonEnabled();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a = null;

        if (context instanceof ConsentActivity) {
            a = (Activity) context;
            try {
                fragmentCallback = (IFragmentCallback) a;
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (signButton != null) {
            signButton.setOnClickListener(clickListener);
        }

        FormData formData = (FormData) getArguments().getSerializable(CarePayConstants.FORM_DATA);

        titleTextView.setText(formData.getTitle());
        descriptionTextView.setText(formData.getDescription());
        contentTextView.setText(formData.getContent());
        content2TextView.setText(formData.getContent2());
        dateTextView.setText(formData.getDate());

    }

    private void showAlertDialogWithListview(final String[] genderArray, String title) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        View customView = LayoutInflater.from(getActivity()).inflate(
                R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter mAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(genderArray));
        listView.setAdapter(mAdapter);
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        chooseGenderTextView.setText("Male");
                        isGenderSelected = true;
                        break;
                    case 1:
                        chooseGenderTextView.setText("Female");
                        isGenderSelected = true;
                        break;

                }
                alert.dismiss();
            }
        });
    }

    private void onClickListners(){
        dobTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog datepickerdailog = new DatePickerDialog(getActivity(), myDateListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
                datepickerdailog.show();
                minorLastNameEditText.clearFocus();
                minorFirstNameEditText.clearFocus();
            }
        });

        chooseGenderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogWithListview(gender, "Gender Select");
            }
        });

    }

    private void setTextListeners() {
        minorFirstNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String hint = "Minor's First Name";
                String hintCaps = hint.toUpperCase();
                if (hasFocus) {
                    // change hint to all caps
                    minorFirstNameTextView.setHint(hintCaps);
                    setProximaNovaSemiboldTextInputLayout(getActivity(), minorFirstNameTextView);
                } else {
                    if (StringUtil.isNullOrEmpty(minorFirstNameEditText.getText().toString())) {
                        // change hint to lower
                        minorFirstNameTextView.setHint(hint);

                    } else {
                        minorFirstNameEditText.setHint(hint);
                    }
                }
            }

        });
        minorLastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String hint = "Minor's Last Name";
                String hintCaps = hint.toUpperCase();
                if (hasFocus) {
                    // change hint to all caps
                    minorLastNameTextView.setHint(hintCaps);
                    setProximaNovaSemiboldTextInputLayout(getActivity(), minorLastNameTextView);
                } else {
                    if (StringUtil.isNullOrEmpty(minorLastNameEditText.getText().toString())) {
                        // change hint to lower
                        minorLastNameTextView.setHint(hint);

                    } else {
                        minorLastNameEditText.setHint(hint);
                    }
                }

            }

        });
    }

    private void getButtonEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            consentFormScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    View view = (View) consentFormScrollView.getChildAt(consentFormScrollView.getChildCount() - 1);
                    int diff = (view.getBottom() - (consentFormScrollView.getHeight() + consentFormScrollView.getScrollY()));

                    if (diff == 0
                            && !StringUtil.isNullOrEmpty(minorLastNameEditText.getText().toString())
                            && !StringUtil.isNullOrEmpty(minorLastNameEditText.getText().toString())
                            && isDatePicked
                            && isGenderSelected) {
                        signButton.setEnabled(true);
                    }
                }
            });

        }
    }


    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.titleTv));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.descriptionTv));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.contentTv));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.minor_information));
        setProximaNovaRegularTypeface(getActivity(), minorFirstNameEditText);
        setProximaNovaRegularTypeface(getActivity(), minorLastNameEditText);
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.dobET));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.choose_genderTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.minor_dateofbirth));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.minor_gender));
    }

}
