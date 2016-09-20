package com.carecloud.carepaylibray.consentforms.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.interfaces.ConsentActivity;
import com.carecloud.carepaylibray.consentforms.interfaces.FormData;
import com.carecloud.carepaylibray.consentforms.interfaces.IFragmentCallback;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;

import java.util.Arrays;
import java.util.Calendar;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;


public class ConsentForm2Fragment extends Fragment {

    private TextView titleTextView, descriptionTextView, contentTextView, content2Tv, dateTv,
            dobTextView;
    private Button signButton;
    Spinner genderspinner;
    private IFragmentCallback fragmentCallback;
    private EditText minorFirstNameEditText, minorLastNameEditText, genderEditText;
    private String[] state = { "Male", "Female"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.consent2_form_layout, container, false);


        titleTextView = (TextView) view.findViewById(R.id.titleTv);
        descriptionTextView = (TextView) view.findViewById(R.id.descriptionTv);
        contentTextView = (TextView) view.findViewById(R.id.contentTv);
        content2Tv = (TextView) view.findViewById(R.id.content2Tv);
        dateTv = (TextView) view.findViewById(R.id.dateTv);
        signButton = (Button) view.findViewById(R.id.signButton);
        minorFirstNameEditText = (EditText) view.findViewById(R.id.minorFirstNameET);
        minorLastNameEditText = (EditText) view.findViewById(R.id.minorLastNameET);
        dobTextView = (TextView) view.findViewById(R.id.dobET);
        genderspinner = (Spinner)view.findViewById(R.id.genderspinner);
        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, state);
        adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderspinner.setAdapter(adapter_state);
        setTypefaces(view);

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
        content2Tv.setText(formData.getContent2());
        dateTv.setText(formData.getDate());

        dobTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(getActivity(), myDateListener, cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }


    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        genderspinner.setSelection(position);
        String selState = (String) genderspinner.getSelectedItem();
    }


    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            dobTextView.setText(new StringBuilder().append(i1).append("/").append(i2).append("/")
                    .append(i));
        }
    };

    private void showAlertDialogWithListview(final String[] genderArray, String title) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                        genderEditText.setText("Male");
                        break;
                    case 1:
                        genderEditText.setText("Feale");
                        break;

                }
                alert.dismiss();
            }
        });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.signButton) {
                if (fragmentCallback != null)
                    fragmentCallback.signButtonClicked();
            }
        }
    };


    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(),(TextView) view.findViewById(R.id.titleTv));
        setProximaNovaRegularTypeface(getActivity(),(TextView) view.findViewById(R.id.descriptionTv));
        setProximaNovaRegularTypeface(getActivity(),(TextView) view.findViewById(R.id.contentTv));
        setProximaNovaSemiboldTypeface(getActivity(),(TextView) view.findViewById(R.id.minor_information));
        setProximaNovaRegularTypeface(getActivity(),(EditText) view.findViewById(R.id.minorFirstNameET));
        setProximaNovaRegularTypeface(getActivity(),(EditText) view.findViewById(R.id.minorLastNameET));
        setProximaNovaSemiboldTypeface(getActivity(),(TextView) view.findViewById(R.id.dobET));
        setProximaNovaSemiboldTypeface(getActivity(),(TextView) view.findViewById(R.id.choose_genderTextView));
        setProximaNovaRegularTypeface(getActivity(),(TextView) view.findViewById(R.id.minor_dateofbirth));
        setProximaNovaRegularTypeface(getActivity(),(TextView) view.findViewById(R.id.minor_gender));
    }

}