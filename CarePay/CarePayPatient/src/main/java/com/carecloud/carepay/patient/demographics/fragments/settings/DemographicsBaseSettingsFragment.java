package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.CustomOptionsAdapter;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * Created by lmenendez on 5/22/17
 */

public abstract class DemographicsBaseSettingsFragment extends BaseFragment {

    protected abstract void checkIfEnableButton();

    protected abstract boolean passConstraints();

    protected void setVisibility(View view, boolean isDisplayed){
        if(isDisplayed){
            view.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.GONE);
        }
    }

    protected boolean checkTextEmptyValue(int textEditableId, View view) {
        EditText editText = (EditText) view.findViewById(textEditableId);
        return StringUtil.isNullOrEmpty(editText.getText().toString());
    }

    protected void initSelectableInput(TextView textView, DemographicsOption storeOption, String value, View optional){
        storeOption.setName(value);
        storeOption.setLabel(value);

        if(StringUtil.isNullOrEmpty(value)){
            value = Label.getLabel("demographics_choose");
            if(optional!=null) {
                optional.setVisibility(View.VISIBLE);
            }
        }
        textView.setText(value);

    }

    protected TextWatcher zipInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatZipcode(editable, lastLength);
        }
    };

    protected TextWatcher getValidateEmptyTextWatcher(final TextInputLayout inputLayout){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(StringUtil.isNullOrEmpty(editable.toString())){
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
                }else{
                    inputLayout.setError(null);
                }
                checkIfEnableButton();
            }
        };
    }

    protected TextWatcher clearValidationErrorsOnTextChange(final TextInputLayout inputLayout){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!StringUtil.isNullOrEmpty(editable.toString())) {
                    inputLayout.setError(null);
                    inputLayout.setErrorEnabled(false);
                }
            }
        };
    }

    protected TextWatcher phoneInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatPhone(editable, lastLength);
        }
    };

    protected TextWatcher dateInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatDateOfBirth(editable, lastLength);
        }
    };

    protected OnOptionSelectedListener getDefaultOnOptionsSelectedListener(final TextView textView, final DemographicsOption storeOption, final View optional){
        return new OnOptionSelectedListener() {
            @Override
            public void onOptionSelected(DemographicsOption option) {
                if(textView!=null){
                    textView.setText(option.getLabel());
                }
                if(optional != null){
                    optional.setVisibility(View.GONE);
                }
                storeOption.setLabel(option.getLabel());
                storeOption.setName(option.getName());
                checkIfEnableButton();

            }
        };
    }


    protected View.OnClickListener getSelectOptionsListener(final List<DemographicsOption> options, final OnOptionSelectedListener listener, final String title) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(getContext(), options, title, listener);
            }
        };
    }


    private void showChooseDialog(Context context,
                                  List<DemographicsOption> options,
                                  String title,
                                  final OnOptionSelectedListener listener) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        // add cancel button
        dialog.setNegativeButton(Label.getLabel("demographics_cancel_label"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pos) {
                dialogInterface.dismiss();
            }
        });

        // create dialog layout
        View customView = LayoutInflater.from(context).inflate(R.layout.alert_list_layout, null, false);
        dialog.setView(customView);
        TextView titleTextView = (TextView) customView.findViewById(R.id.title_view);
        titleTextView.setText(title);
        titleTextView.setVisibility(View.VISIBLE);


        // create the adapter
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomOptionsAdapter customOptionsAdapter = new CustomOptionsAdapter(context, options);
        listView.setAdapter(customOptionsAdapter);


        final AlertDialog alert = dialog.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();

        // set item click listener
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long row) {
                DemographicsOption selectedOption = (DemographicsOption) adapterView.getAdapter().getItem(position);
                if (listener != null) {
                    listener.onOptionSelected(selectedOption);
                }
                alert.dismiss();
            }
        };
        listView.setOnItemClickListener(clickListener);
    }

    public interface OnOptionSelectedListener{
        void  onOptionSelected(DemographicsOption option);
    }

}