package com.carecloud.carepay.patient.consentforms.fragments;

import android.annotation.TargetApi;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.patient.consentforms.ConsentActivity;
import com.carecloud.carepay.patient.consentforms.FormData;
import com.carecloud.carepay.patient.consentforms.interfaces.IFragmentCallback;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDataModelDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization.ConsentFormMinorDateofBirthDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization.ConsentFormMinorFirstNameDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization.ConsentFormMinorGenderDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization.ConsentFormMinorLastNameDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;


public class ConsentForm2Fragment extends Fragment {

    Date date = new Date();
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView contentTextView;
    private TextView content2TextView;
    private TextView dateTextView;
    private TextView dobTextView;
    private TextView chooseGenderTextView;
    private TextView minorDOB;
    private TextView minorInformation;
    private TextInputLayout minorFirstNameTextView;
    private TextInputLayout minorLastNameTextView;
    private Button signButton;
    private boolean isGenderSelected;
    private boolean isDatePicked;
    private IFragmentCallback fragmentCallback;
    private ScrollView consentFormScrollView;
    private EditText minorFirstNameEditText;
    private EditText minorLastNameEditText;
    private List<String> gender = new ArrayList<String>();
    private TextView minorGender;
    private ConsentFormDTO consentFormDTO;
    private ConsentFormDataModelDTO consentFormDataModelDTO;
    private ConsentFormLabelsDTO consentFormLabelsDTO;
    private ConsentFormMinorFirstNameDTO consentFormMinorFirstNameDTO;
    private ConsentFormMinorLastNameDTO consentFormMinorLastNameDTO;
    private ConsentFormMinorDateofBirthDTO consentFormMinorDateofBirthDTO;
    private ConsentFormMinorGenderDTO consentFormMinorGenderDTO;
    private AppointmentsPayloadDTO appointmentsPayloadDTO;
    private String firstNameLabel;
    private String lastNameLabel;
    private String genderLabel;
    private String minordobLabel;
    private String nextButtonLabel;
    private String maleLabel;
    private String femaleLabel;
    private String selectGenderLabel;
    private String selectDateLabel;
    private String minorInformationLabel;
    private String providerName;
    private String patienFirstName;
    private String patientLastName;
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            String dob = new StringBuilder().append(month + 1).append("/").append(dayOfMonth).append("/")
                    .append(year).toString();
            dobTextView.setText(dob);
            isDatePicked = !(dob.equals(R.string.pick_date));
            dobTextView.setTag(dob);
        }
    };
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.signButton && fragmentCallback != null) {
                fragmentCallback.signButtonClicked();
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.consent2_form_layout, container, false);

        consentFormDTO=((ConsentActivity)getActivity()).getConsentFormDTO();

        titleTextView = (TextView) view.findViewById(R.id.titleTv);
        descriptionTextView = (TextView) view.findViewById(R.id.descriptionTv);
        contentTextView = (TextView) view.findViewById(R.id.contentTv);
        chooseGenderTextView = (TextView) view.findViewById(R.id.choose_genderTextView);
        content2TextView = (TextView) view.findViewById(R.id.content2Tv);
        dateTextView = (TextView) view.findViewById(R.id.dateTv);
        signButton = (Button) view.findViewById(R.id.signButton);
        signButton.setEnabled(false);
        minorDOB = (TextView) view.findViewById(R.id.minor_dateofbirth);
        minorGender = (TextView) view.findViewById(R.id.minor_gender);
        minorInformation = (TextView) view.findViewById(R.id.minor_information);
        minorFirstNameTextView = (TextInputLayout) view.findViewById(R.id.text_input_layout);
        minorLastNameTextView = (TextInputLayout) view.findViewById(R.id.minorLastName);
        minorFirstNameEditText = (EditText) view.findViewById(R.id.minorFirstNameET);
        minorLastNameEditText = (EditText) view.findViewById(R.id.minorLastNameET);
        dobTextView = (TextView) view.findViewById(R.id.dobET);
        consentFormScrollView = (ScrollView) view.findViewById(R.id.consentform_scrollView);
        initViewFromModels();
        getLabels();
        setTypefaces(view);
        setEditTexts();
        onClickListners();
        setEnableNextButtonOnFullScroll();


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = null;

        if (context instanceof ConsentActivity) {
            activity = (Activity) context;
            try {
                fragmentCallback = (IFragmentCallback) activity;
            } catch (Exception e) {
                Log.e("Error", " Exception");
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
        signButton.setText(formData.getButtonLabel());
        initviews();
    }

    private void showAlertDialogWithListview(final List<String> genderArray, String title) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int listener) {
                dialogInterface.dismiss();
            }
        });
        View customView = LayoutInflater.from(getActivity()).inflate(
                R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter alertAdapter = new CustomAlertAdapter(getActivity(), gender);
        listView.setAdapter(alertAdapter);
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long ll) {
                chooseGenderTextView.setText(consentFormMinorGenderDTO.getOptions().get(position).getName().toUpperCase());

                alert.dismiss();
            }
        });
    }

    private void onClickListners() {
        dobTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog datepickerDialog = new DatePickerDialog(getActivity(),
                        myDateListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.DAY_OF_MONTH));
                datepickerDialog.show();
                datepickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            }
        });

        chooseGenderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickListener) {
                showAlertDialogWithListview(gender, "Gender Select");
            }
        });

    }

    private void initViewFromModels() {

        consentFormDTO = ((ConsentActivity) getActivity())
                .getConsentFormDTO();

        consentFormLabelsDTO = ((ConsentActivity) getActivity())
                .getConsentFormLabelsDTO();

        appointmentsPayloadDTO = ((ConsentActivity) getActivity()).getAppointmentsPayloadDTO();


    }

    private void getLabels() {

        consentFormMinorFirstNameDTO = consentFormDTO.getMetadata().getDataModels().getPost()
                .getConsentForAuthorization().getProperties().getMinorFirstName();

        consentFormMinorLastNameDTO = consentFormDTO.getMetadata().getDataModels().getPost()
                .getConsentForAuthorization().getProperties().getMinorLastName();

        consentFormMinorGenderDTO = consentFormDTO.getMetadata().getDataModels().getPost()
                .getConsentForAuthorization().getProperties().getMinorGender();

        consentFormMinorDateofBirthDTO = consentFormDTO.getMetadata().getDataModels().getPost()
                .getConsentForAuthorization().getProperties().getMinorDateOfBirth();

        firstNameLabel = consentFormMinorFirstNameDTO.getLabel();
        lastNameLabel = consentFormMinorLastNameDTO.getLabel();
        nextButtonLabel = consentFormLabelsDTO.getSignAuthorizationFormTitle().toUpperCase();
        genderLabel = consentFormMinorGenderDTO.getLabel();
        minordobLabel = consentFormMinorDateofBirthDTO.getLabel();
        selectGenderLabel = consentFormLabelsDTO.getSelectGenderLabel();
        minorInformationLabel = consentFormLabelsDTO.getMinorsInformation();
        selectDateLabel = consentFormLabelsDTO.getSelectDateLabel();
        int genderlistsize = consentFormMinorGenderDTO.getOptions().size();

        for (int i = 0; i < genderlistsize; i++) {
            gender.add(i, consentFormMinorGenderDTO.getOptions().get(i).getName());
        }

    }

    private void initviews() {
        minorFirstNameEditText.setHint(firstNameLabel);
        minorLastNameEditText.setHint(lastNameLabel);
        minorGender.setText(genderLabel);
        dobTextView.setText(selectDateLabel);
        minorDOB.setText(minordobLabel);
        minorInformation.setText(minorInformationLabel);
    }

    private void setEditTexts() {

        minorFirstNameTextView.setTag(firstNameLabel);
        minorFirstNameEditText.setTag(minorFirstNameTextView);

        minorLastNameTextView.setTag(lastNameLabel);
        minorLastNameEditText.setTag(minorLastNameTextView);

        setChangeFocusListeners();
    }

    private void setChangeFocusListeners() {

        minorFirstNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {

                if (bool) {
                    // change hint to all caps
                    SystemUtil.showSoftKeyboard(getActivity());
                } else {
                    SystemUtil.handleHintChange(view, bool);
                }
            }
        });


        minorLastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    // change hint to all caps
                    SystemUtil.showSoftKeyboard(getActivity());
                } else {
                    SystemUtil.handleHintChange(view, bool);
                }
            }

        });

    }

    private void setEnableNextButtonOnFullScroll() {
        // enable next button on scrolling all the way to the bottom
        consentFormScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = consentFormScrollView.getChildAt(consentFormScrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (consentFormScrollView.getHeight() + consentFormScrollView.getScrollY()));

                if (diff == 0) {
                    signButton.setEnabled(true);
                }
            }
        });
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), titleTextView);
        setProximaNovaRegularTypeface(getActivity(), descriptionTextView);
        setProximaNovaRegularTypeface(getActivity(), contentTextView);
        setProximaNovaRegularTypeface(getActivity(), content2TextView);
        setProximaNovaRegularTypeface(getActivity(), dateTextView);

        setProximaNovaSemiboldTypeface(getActivity(), minorInformation);
        setProximaNovaRegularTypeface(getActivity(), minorFirstNameEditText);
        setProximaNovaRegularTypeface(getActivity(), minorLastNameEditText);

        setProximaNovaSemiboldTypeface(getActivity(), dobTextView);
        setProximaNovaSemiboldTypeface(getActivity(), chooseGenderTextView);
        setProximaNovaRegularTypeface(getActivity(), minorDOB);
        setProximaNovaRegularTypeface(getActivity(), minorGender);
    }

}
