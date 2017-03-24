package com.carecloud.carepaylibray.demographics.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.DemographicMetadataPropertiesInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Arrays;
import java.util.List;

public class InsuranceEditDialog extends Dialog {

    private Context context;
    private DemographicDTO demographicDTO;
    private DemographicInsurancePayloadDTO lineItem;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;

    private TextInputLayout cardNumberInput;
    private TextInputLayout groupNumberInput;
    private EditText cardNumber;
    private EditText groupNumber;

    private CarePayTextView selectedProvider;
    private CarePayTextView selectedPlan;
    private CarePayTextView selectedType;

    private boolean isInEditMode;
    private boolean isCardNumberEmpty;
    private boolean isGroupNumberEmpty;

    /**
     * Constructor
     * @param context context
     * @param lineItem Insurance item
     */
    @SuppressWarnings("ConstantConditions")
    public InsuranceEditDialog(Context context, DemographicInsurancePayloadDTO lineItem,
                               DemographicDTO demographicDTO, boolean isInEditMode) {
        super(context);
        this.context = context;
        this.lineItem = lineItem;
        this.demographicDTO = demographicDTO;
        this.isInEditMode = isInEditMode;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_edit_insurance);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.55);
        getWindow().setAttributes(params);

        initViews();
        initLabels();
    }

    private void initLabels() {
        ((CarePayTextView) findViewById(R.id.edit_insurance_cancel_label)).setText(
                Label.getLabel("demographics_review_edit_insurance_close"));
        ((CarePayTextView) findViewById(R.id.health_insurance_provider_label)).setText(
                Label.getLabel("demographics_documents_title_select_provider"));
        ((CarePayTextView) findViewById(R.id.health_insurance_plan_label)).setText(
                Label.getLabel("demographics_documents_title_select_plan"));
        ((CarePayTextView) findViewById(R.id.health_insurance_type_label)).setText(
                Label.getLabel("demographics_insurance_type_label"));
        ((CarePayTextView) findViewById(R.id.health_insurance_plans)).setText(
                Label.getLabel("demographics_documents_choose_plan"));

        if (lineItem != null && isInEditMode) {
            ((CarePayTextView) findViewById(R.id.toolbar_title)).setText(lineItem.getInsurancePlan());
            selectedProvider.setText(lineItem.getInsuranceProvider());
            selectedPlan.setText(lineItem.getInsurancePlan());
            selectedType.setText(lineItem.getInsuranceType());

            cardNumber.setText(lineItem.getInsuranceMemberId());
            cardNumber.getOnFocusChangeListener().onFocusChange(cardNumber, false);
            groupNumber.setText(lineItem.getInsuranceGroupId());
            groupNumber.getOnFocusChangeListener().onFocusChange(groupNumber, false);

            findViewById(R.id.remove_insurance_entry).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.remove_insurance_entry)).setText(
                    Label.getLabel("demographics_documents_remove"));
            ((Button) findViewById(R.id.take_front_photo_button)).setText(
                    Label.getLabel("demographics_insurance_retake_front_photo"));
            ((Button) findViewById(R.id.take_back_photo_button)).setText(
                    Label.getLabel("demographics_insurance_retake_back_photo"));
        } else {
            findViewById(R.id.remove_insurance_entry).setVisibility(View.GONE);
            ((CarePayTextView) findViewById(R.id.toolbar_title)).setText(
                    Label.getLabel("practice_checkin_demogr_ins_add_new_button_label"));
            ((Button) findViewById(R.id.take_front_photo_button)).setText(
                    Label.getLabel("demographics_insurance_take_front_photo"));
            ((Button) findViewById(R.id.take_back_photo_button)).setText(
                    Label.getLabel("demographics_insurance_take_back_photo"));

            selectedProvider.setText(Label.getLabel("demographics_choose"));
            selectedPlan.setText(Label.getLabel("demographics_choose"));
            selectedType.setText(Label.getLabel("demographics_choose"));
        }

        ((Button) findViewById(R.id.save_insurance_changes)).setText(
                Label.getLabel("demographics_save_changes_button"));
    }

    private void initViews() {
        findViewById(R.id.edit_insurance_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View closeButton) {
                dismiss();
            }
        });

        selectedProvider = (CarePayTextView) findViewById(R.id.health_insurance_providers);
        selectedPlan = (CarePayTextView) findViewById(R.id.health_insurance_choose_plans);
        selectedType = (CarePayTextView) findViewById(R.id.health_insurance_types);

        cardNumberInput = (TextInputLayout) findViewById(R.id.health_insurance_card_number_layout);
        cardNumber = (EditText) findViewById(R.id.health_insurance_card_number);

        String cardNumberHint = Label.getLabel("demographics_insurance_card_number");
        cardNumberInput.setTag(cardNumberHint);
        cardNumber.setHint(cardNumberHint);
        cardNumber.setTag(cardNumberInput);

        groupNumberInput = (TextInputLayout) findViewById(R.id.health_insurance_group_number_layout);
        groupNumber = (EditText) findViewById(R.id.health_insurance_group_number);

        String groupNumberHint = Label.getLabel("demographics_insurance_group_number");
        groupNumberInput.setTag(groupNumberHint);
        groupNumber.setHint(groupNumberHint);
        groupNumber.setTag(groupNumberInput);

        setTextListeners();
        setChangeFocusListeners();
        setActionListeners();

        cardNumber.clearFocus();
        groupNumber.clearFocus();

        findViewById(R.id.save_insurance_changes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View saveChanges) {

            }
        });

        if (!isInEditMode) {
            setValues();
        }
    }

    private void setValues() {
        insurancesMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.insurances;
        if (insurancesMetaDTO != null) {
            DemographicMetadataPropertiesInsuranceDTO properties = insurancesMetaDTO.properties.items.insurance.properties;

            // Providers
            List<MetadataOptionDTO> providerList = properties.insuranceProvider.options;
            final String[] providers = new String[providerList.size()];
            for (int i = 0; i < providerList.size(); i++) {
                providers[i] = providerList.get(i).getLabel();
            }
            selectedProvider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View providerView) {
                    showAlertDialogWithListView(providers, "Choose Provider", "Cancel", 0);
                }
            });

            // Plans
            List<MetadataOptionDTO> planList = properties.insurancePlan.options;
            final String[] plans = new String[planList.size()];
            for (int i = 0; i < planList.size(); i++) {
                plans[i] = planList.get(i).getLabel();
            }
            selectedPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View providerView) {
                    showAlertDialogWithListView(plans, "Choose Plan", "Cancel", 1);
                }
            });

            // Types
            List<MetadataOptionDTO> typeList = properties.insuranceType.options;
            final String[] types = new String[typeList.size()];
            for (int i = 0; i < typeList.size(); i++) {
                types[i] = typeList.get(i).getLabel();
            }
            selectedType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View providerView) {
                    showAlertDialogWithListView(types, "Choose Type", "Cancel", 2);
                }
            });
        }
    }

    @SuppressLint("InflateParams")
    private void showAlertDialogWithListView(final String[] dataArray, String title,
                                             String cancelLabel, final int index) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);

        dialog.setNegativeButton(cancelLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int listener) {
                dialogInterface.dismiss();
            }
        });

        View customView = LayoutInflater.from(context).inflate(R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter alertAdapter = new CustomAlertAdapter((Activity) context, Arrays.asList(dataArray));
        listView.setAdapter(alertAdapter);
        dialog.setView(customView);

        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long listener) {
                switch (index) {
                    case 0:
                        selectedProvider.setText(dataArray[position]);
                        selectedPlan.setVisibility(View.VISIBLE);
                        findViewById(R.id.health_insurance_plans).setVisibility(View.GONE);
                        break;

                    case 1:
                        selectedPlan.setText(dataArray[position]);
                        break;

                    case 2:
                        selectedType.setText(dataArray[position]);
                        break;
                    default:
                        break;
                }
                alert.dismiss();
            }
        });
    }

    private void setTextListeners() {
        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isCardNumberEmpty = StringUtil.isNullOrEmpty(cardNumber.getText().toString());
                if (!isCardNumberEmpty) { // clear the error
                    cardNumberInput.setError(null);
                    cardNumberInput.setErrorEnabled(false);
                }
            }
        });

        groupNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isGroupNumberEmpty = StringUtil.isNullOrEmpty(groupNumber.getText().toString());
                if (!isGroupNumberEmpty) { // clear the error
                    groupNumberInput.setError(null);
                    groupNumberInput.setErrorEnabled(false);
                }
            }
        });
    }

    private void setChangeFocusListeners() {
        cardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard((Activity) context);
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        groupNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard((Activity) context);
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
    }

    private void setActionListeners() {
        cardNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    groupNumber.requestFocus();
                    return true;
                }
                return false;
            }
        });

        groupNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    groupNumber.clearFocus();
                    SystemUtil.hideSoftKeyboard((Activity) context);
                    return true;
                }
                return false;
            }
        });
    }
}
