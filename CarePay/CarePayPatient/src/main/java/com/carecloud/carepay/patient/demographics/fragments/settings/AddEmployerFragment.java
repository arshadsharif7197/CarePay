package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.demographics.dtos.payload.AddressDto;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepay.patient.demographics.interfaces.AddNewEmployerInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.AddressUtil;

/**
 * @author pjohnson on 5/10/17.
 */

public class AddEmployerFragment extends BaseFragment {


    private AddNewEmployerInterface callback;
    private String state;

    public static AddEmployerFragment newInstance() {
        return new AddEmployerFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (AddNewEmployerInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddNewEmployerInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_employer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarContainer);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("demographics_add_new_employer_title"));
        toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
        callback.setToolbar(toolbar);

        setUpUI(view);
    }

    private void setUpUI(View view) {
        view.findViewById(R.id.addressOptionalTextView).setVisibility(View.VISIBLE);

        final EditText address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        final EditText address2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);
        final EditText zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);
        final EditText cityEditText = (EditText) view.findViewById(R.id.cityId);
        AutoCompleteTextView stateAutoCompleteTextView = (AutoCompleteTextView) view
                .findViewById(com.carecloud.carepaylibrary.R.id.addNewCredidCardStateAutoCompleteTextView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.autocomplete_state_item, R.id.text1, AddressUtil.states);
        stateAutoCompleteTextView.setThreshold(1);
        stateAutoCompleteTextView.setAdapter(adapter);
        stateAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                state = adapter.getItem(position);
            }
        });

        final Button addEmployerButton = (Button) view.findViewById(R.id.addEmployerButton);
        final EditText employerNameEditText = (EditText) view.findViewById(R.id.employerNameEditText);
        employerNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    addEmployerButton.setEnabled(false);
                } else {
                    addEmployerButton.setEnabled(true);
                }
            }
        });
        addEmployerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressDto address = new AddressDto();
                address.setAddress1(address1EditText.getText().toString());
                address.setAddress2(address2EditText.getText().toString());
                address.setCity(cityEditText.getText().toString());
                address.setZipcode(zipCodeEditText.getText().toString());
                address.setState(state);
                EmployerDto employer = new EmployerDto();
                employer.setName(employerNameEditText.getText().toString());
                employer.setAddress(address);
                callback.addEmployer(employer);
            }
        });
    }
}
