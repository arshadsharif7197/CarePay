package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.DialogInterface;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
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
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsField;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

/**
 * Created by lmenendez on 5/22/17
 */

public abstract class DemographicsBaseSettingsFragment extends BaseFragment {

    protected abstract void checkIfEnableButton(boolean userInteraction);

    protected abstract boolean passConstraints(boolean isUserInteraction);

    protected void setVisibility(View view, boolean isDisplayed) {
        if (isDisplayed) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    protected boolean checkTextEmptyValue(int textEditableId, View view) {
        EditText editText = (EditText) view.findViewById(textEditableId);
        return StringUtil.isNullOrEmpty(editText.getText().toString());
    }

    protected void initSelectableInput(TextView textView, DemographicsOption storeOption,
                                       String value, View optional) {
        storeOption.setName(value);
        storeOption.setLabel(value);

        if (!StringUtil.isNullOrEmpty(value)) {
            textView.setText(storeOption.getLabel());
        }else if (optional != null) {
            optional.setVisibility(View.VISIBLE);
        }

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
            checkIfEnableButton(false);
        }
    };

    protected TextWatcher getValidateEmptyTextWatcher(final TextInputLayout inputLayout) {
        return new TextWatcher() {
            public int count;

            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
                this.count = sequence.length();
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())) {
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
                } else if (count == 0) {
                    unsetFieldError(inputLayout);
                }
                checkIfEnableButton(false);
            }
        };
    }

    protected TextWatcher getOptionalViewTextWatcher(final View optionalView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())) {
                    optionalView.setVisibility(View.VISIBLE);
                } else {
                    optionalView.setVisibility(View.GONE);
                }
                checkIfEnableButton(false);
            }
        };
    }

    protected TextWatcher clearValidationErrorsOnTextChange(final TextInputLayout inputLayout) {
        return new TextWatcher() {
            public int count;

            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
                this.count = sequence.length();
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!StringUtil.isNullOrEmpty(editable.toString()) && count == 0) {
                    unsetFieldError(inputLayout);
                }
                checkIfEnableButton(false);
            }
        };
    }

    protected TextWatcher ssnInputFormatter = new TextWatcher() {
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
            StringUtil.autoFormatSocialSecurityNumber(editable, lastLength);
        }
    };

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
            checkIfEnableButton(false);
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

    protected OnOptionSelectedListener getDefaultOnOptionsSelectedListener(final TextView textView,
                                                                           final DemographicsOption storeOption,
                                                                           final View optional) {
        return new OnOptionSelectedListener() {
            @Override
            public void onOptionSelected(DemographicsOption option) {
                if (textView != null) {
                    textView.setText(option.getLabel());
                }
                if (optional != null) {
                    optional.setVisibility(View.GONE);
                }
                storeOption.setLabel(option.getLabel());
                storeOption.setName(option.getName());
                checkIfEnableButton(false);

            }
        };
    }


    protected View.OnClickListener getSelectOptionsListener(final List<DemographicsOption> options,
                                                            final OnOptionSelectedListener listener,
                                                            final String title) {
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
                DemographicsOption selectedOption = (DemographicsOption) adapterView.getAdapter()
                        .getItem(position);
                if (listener != null) {
                    listener.onOptionSelected(selectedOption);
                }
                alert.dismiss();
            }
        };
        listView.setOnItemClickListener(clickListener);
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(DemographicsOption option);
    }

    protected void setUpDemographicField(View view, String value, DemographicsField demographicsField,
                                         int containerLayout, int inputLayoutId, int editTextId, int optionalViewId,
                                         DemographicsOption demographicsOption, String optionDialogTitle) {
        view.findViewById(containerLayout).setVisibility(demographicsField.isDisplayed() ? View.VISIBLE : View.GONE);
        final TextInputLayout inputLayout = (TextInputLayout) view.findViewById(inputLayoutId);
        final EditText editText = (EditText) view.findViewById(editTextId);
        editText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(inputLayout, null));
        editText.setText(value);
        editText.getOnFocusChangeListener().onFocusChange(editText,
                !StringUtil.isNullOrEmpty(editText.getText().toString().trim()));
        final View optionalView = view.findViewById(optionalViewId);
        optionalView.setVisibility(!demographicsField.isRequired()
                && StringUtil.isNullOrEmpty(value) ? View.VISIBLE : View.GONE);
        if (demographicsOption != null) {
            editText.setOnClickListener(getEditTextClickListener(demographicsField.getOptions(),
                    inputLayout, editText, optionalView,
                    demographicsOption, optionDialogTitle));
            demographicsOption.setName(editText.getText().toString());
            demographicsOption.setLabel(editText.getText().toString());
        } else if (demographicsField.isRequired()) {
            editText.addTextChangedListener(getValidateEmptyTextWatcher(inputLayout));
        } else {
            editText.addTextChangedListener(getOptionalViewTextWatcher(optionalView));
        }
    }

    protected View.OnClickListener getEditTextClickListener(List<DemographicsOption> options,
                                                            final TextInputLayout inputLayout,
                                                            final EditText editText,
                                                            final View optionalLabel,
                                                            final DemographicsOption demographicsOption,
                                                            final String dialogTitle) {
        return getSelectOptionsListener(options,
                new OnOptionSelectedListener() {
                    @Override
                    public void onOptionSelected(DemographicsOption option) {
                        if (demographicsOption != null) {
                            demographicsOption.setLabel(option.getLabel());
                            demographicsOption.setName(option.getName());
                            demographicsOption.setId(option.getId());
                        }
                        editText.setText(option.getLabel());
                        editText.getOnFocusChangeListener()
                                .onFocusChange(editText, !StringUtil.isNullOrEmpty(editText.getText().toString()));
                        inputLayout.setError(null);
                        inputLayout.setErrorEnabled(false);
                        if (optionalLabel != null) {
                            optionalLabel.setVisibility(View.GONE);
                        }
                        checkIfEnableButton(false);
                    }
                },
                dialogTitle);
    }

    protected void setUpField(TextInputLayout textInputLayout, EditText editText, boolean isVisible,
                              String value, boolean isRequired, View optionalView) {
        editText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(textInputLayout, null));
        setVisibility(textInputLayout, isVisible);
        editText.setText(StringUtil.captialize(value));
        editText.getOnFocusChangeListener().onFocusChange(editText,
                !StringUtil.isNullOrEmpty(editText.getText().toString().trim()));
        if (isRequired) {
            editText.addTextChangedListener(getValidateEmptyTextWatcher(textInputLayout));
        } else if (optionalView != null) {
            editText.addTextChangedListener(getOptionalViewTextWatcher(optionalView));
        }
        if (optionalView != null && !StringUtil.isNullOrEmpty(value)) {
            optionalView.setVisibility(View.GONE);
        }
    }

    protected View.OnClickListener selectEndOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText editText = (EditText) view;
            editText.setSelection(editText.length());
        }
    };


    protected boolean validateField(View view, boolean isRequired, String value,
                                    int inputLayoutId, boolean shouldRequestFocus) {
        if (isRequired && StringUtil.isNullOrEmpty(value)) {
            if (shouldRequestFocus) {
                setDefaultError(view, inputLayoutId, shouldRequestFocus);
            }
            return true;
        }
        return false;
    }

    protected void setDefaultError(View baseView, int id, boolean shouldRequestFocus) {
        setFieldError(baseView, id, Label.getLabel("demographics_required_validation_msg"), shouldRequestFocus);
    }

    protected void setFieldError(View baseView, int id, String error, boolean shouldRequestFocus) {
        TextInputLayout inputLayout = (TextInputLayout) baseView.findViewById(id);
        setFieldError(inputLayout, error, shouldRequestFocus);
    }

    protected void setFieldError(TextInputLayout inputLayout, boolean shouldRequestFocus) {
        setFieldError(inputLayout, Label.getLabel("demographics_required_validation_msg"), shouldRequestFocus);
    }

    protected void setFieldError(TextInputLayout inputLayout, String error, boolean shouldRequestFocus) {
        if (inputLayout != null) {
            if (!inputLayout.isErrorEnabled()) {
                inputLayout.setErrorEnabled(true);
                inputLayout.setError(error);
            }
            if (shouldRequestFocus) {
                inputLayout.clearFocus();
                inputLayout.requestFocus();
            }
        }

    }

    protected void unsetFieldError(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

}
