package com.carecloud.carepaylibray.consentforms.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.interfaces.ConsentActivity;
import com.carecloud.carepaylibray.consentforms.interfaces.FormData;
import com.carecloud.carepaylibray.consentforms.interfaces.IFragmentCallback;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;

import java.util.Arrays;
import java.util.Calendar;

import static com.carecloud.carepaylibray.utils.Utility.setTypefaceFromAssets;


public class ConsentForm2Fragment extends Fragment {

    private TextView titleTv, descriptionTv, contentTv, content2Tv, dateTv;
    private Button signButton;
    private IFragmentCallback fragmentCallback;
    private EditText minorFirstNameET, minorLastNameET, dobET, genderET;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.consent2_form_layout, container, false);



        titleTv = (TextView) view.findViewById(R.id.titleTv);
        descriptionTv = (TextView) view.findViewById(R.id.descriptionTv);
        contentTv = (TextView) view.findViewById(R.id.contentTv);
        content2Tv = (TextView) view.findViewById(R.id.content2Tv);
        dateTv = (TextView) view.findViewById(R.id.dateTv);
        signButton = (Button) view.findViewById(R.id.signButton);
        minorFirstNameET = (EditText) view.findViewById(R.id.minorFirstNameET);
        minorLastNameET = (EditText) view.findViewById(R.id.minorLastNameET);
        dobET = (EditText) view.findViewById(R.id.dobET);
        genderET = (EditText) view.findViewById(R.id.genderET);

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

        FormData formData = (FormData)getArguments().getSerializable(CarePayConstants.FORM_DATA);

        titleTv.setText(formData.getTitle());
        descriptionTv.setText(formData.getDescription());
        contentTv.setText(formData.getContent());
        content2Tv.setText(formData.getContent2());
        dateTv.setText(formData.getDate());

        genderET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogWithListview(new String[]{"Male", "Female"}, "Select Gender");
            }
        });

        dobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(getActivity(), myDateListener, cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            dobET.setText(new StringBuilder().append(i1).append("/").append(i2).append("/")
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
                        genderET.setText("Male");
                        break;
                    case 1:
                        genderET.setText("Feale");
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
}